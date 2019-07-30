package com.example.hacknews.comment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import com.example.hacknews.ARTICLE_KEY
import com.example.hacknews.ApiClient
import com.example.hacknews.OnClickedListener
import com.example.hacknews.R
import com.example.hacknews.article.ArticleResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_article_comments.*


internal class CommentsFragment : Fragment() {

    lateinit var onNavigationChangedListener: OnClickedListener

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    lateinit var articleTitle: String

    lateinit var articleRes: ArticleResponse

    private val apiClient = ApiClient()

    private var articleKids: Int = 0
    private var rootId: Int = 0
    private var articleUrl: String = ""
    private var kids: Int = 0
    private var commentsKids: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_article_comments, container, false)

    override fun onResume() {
        super.onResume()

        Log.i("onResume", "on Resume")
        toolbarTop.setNavigationOnClickListener {
            fragmentManager?.popBackStackImmediate()
        }

        setupRecyclerView()

        val articleId = arguments?.getSerializable(ARTICLE_KEY) as Int
        rootId = articleId

        requestCurrentArticleData(articleId)

        setShareArticleListener()

        setWebViewListener()
    }

    override fun onStop() {
        super.onStop()
        kids = 0
        commentsKids = 0
        articleKids = 0

        comments_recycler_view.visibility = View.INVISIBLE
        Log.i("onStop", "on Stop")
        compositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        kids = 0
        commentsKids = 0
        articleKids = 0

        Log.i("onDestroy", "on Destroy")
        compositeDisposable.dispose()
    }

    private fun setWebViewListener() {
        articleTitleTextView.setOnClickListener {
            val url = articleUrl
            val newsWebView = WebView(context)
            newsWebView.loadUrl(url)
            val alert = AlertDialog.Builder(context)
            newsWebView.webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    view?.loadUrl(url)
                    return true
                }
            }

            alert.setView(newsWebView)
            alert.setNegativeButton("Close") { dialog, _ -> dialog.dismiss() }
            val dialog = alert.create()

            dialog.show()
            val layoutParam = WindowManager.LayoutParams()

            layoutParam.copyFrom(dialog.window?.attributes)
            layoutParam.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParam.height = WindowManager.LayoutParams.MATCH_PARENT
            layoutParam.gravity = Gravity.CENTER
            dialog.window?.attributes = layoutParam

            val positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            val parent = positiveButton.parent as LinearLayout

            parent.gravity = Gravity.CENTER_HORIZONTAL
            val leftSpacer = parent.getChildAt(1)

            leftSpacer.visibility = View.GONE
        }
    }

    private fun setShareArticleListener() {
        share_article.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBody = articleUrl
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, articleTitle)
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Share via"))
        }
    }

    private fun addCommentToList(comment: Comment) {
        if (comment.commentId != null
            && comment.commentBy != "" && comment.commentTime != 0L && comment.commentText != null
            && comment.parent != -1 && comment.type != "" && comments_recycler_view != null
        ) {
            (comments_recycler_view.adapter as CommentsAdapter).addCommentsArrangement(comment)

            if (kids == articleKids + commentsKids || kids == articleKids + commentsKids + 1
                || kids == articleKids + commentsKids - 1
            ) {
                progress_bar_comments.visibility = View.INVISIBLE
                comments_recycler_view.visibility = View.VISIBLE
            }
        }
    }

    private fun requestCurrentArticleData(articleId: Int) {
        val observable = apiClient.getApiServiceWithRx().getArticle(articleId)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                articleTitleTextView.text = it.title!!
                articleUrl = it.url!!
                if (it.kids == null) setupEmptyView()
                articleKids = it.kids?.size!!
            }
            .flatMap { article ->
                articleRes = article
                Observable.fromIterable(article.kids).subscribeOn(Schedulers.io())
            }
            .concatMapEager { articleKidId ->
                apiClient.getApiServiceWithRx().getItem(articleKidId).subscribeOn(Schedulers.io())
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { articleKid ->
                    articleTitle = articleRes.title!!
                    articleUrl = articleRes.url!!
                    if (articleTitleTextView != null) articleTitleTextView.text = articleTitle

                    progress_bar_comments.visibility = View.VISIBLE

                    if (articleKid != null) {
                        addChildren(0, articleKid.commentId!!)
                    }
                }
                , { println("onError!") }
                , { println("onComplete!") })

        compositeDisposable.add(observable)
    }

    private fun addChildren(depth: Int, commentId: Int) {
        compositeDisposable.add(apiClient.getApiServiceWithRx().getItem(commentId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { articleKid ->
                    kids++
                    //add comments to the commentsList
                    val parentComment = buildCommentFromResponse(depth, articleKid)

                    if (parentComment != null) addCommentToList(parentComment)

                    val commentKids = articleKid.commentKids

                    for (child in commentKids!!) {
                        commentsKids++
                        addChildren(depth + 1, child)
                    }
                }
                , { println("onError!") }
                , { println("onComplete!") })
        )
    }

    private fun buildCommentFromResponse(depth: Int, response: CommentResponse): Comment? =
        response.let { responseBody ->
            val id = responseBody.commentId
            val author = responseBody.commentBy ?: ""
            val time = responseBody.commentTime ?: 0L
            val text = responseBody.commentText
            val parent = responseBody.commentParent ?: -1
            val kids = responseBody.commentKids
            val type = responseBody.commentType ?: ""
            Comment(id, author, time, text, parent, kids, type, depth)
        }

    private fun setupEmptyView() {
        val comments = comments_recycler_view?.adapter
        if (comments?.itemCount == 0) {
            comments_recycler_view.visibility = View.GONE
            empty_view_comments.visibility = View.VISIBLE
            empty_view_comments_text.visibility = View.VISIBLE
        } else {
            comments_recycler_view.visibility = View.VISIBLE
            empty_view_comments.visibility = View.GONE
            empty_view_comments_text.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        comments_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        comments_recycler_view.adapter = CommentsAdapter(requireContext())
    }
}

