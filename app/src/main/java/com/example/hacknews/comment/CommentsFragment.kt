package com.example.hacknews.comment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hacknews.*
import com.example.hacknews.article.ArticleResponse
import kotlinx.android.synthetic.main.fragment_article_comments.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class CommentsFragment : Fragment() {

    lateinit var onNavigationChangedListener: OnClickedListener

    lateinit var articleTitle: String
    private val apiInterface: ApiInterface
        get() = ApiClient.getClient().create(ApiInterface::class.java)

    private var rootId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_article_comments, container, false)

    override fun onResume() {
        super.onResume()

        toolbarTop.setNavigationOnClickListener {
            fragmentManager?.popBackStackImmediate()
        }

        setupRecyclerView()

        val articleId = arguments?.getSerializable(ARTICLE_KEY) as Int
        rootId = articleId

        apiInterface.getArticle(articleId).enqueue(object : Callback<ArticleResponse> {

            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
            }

            override fun onResponse(call: Call<ArticleResponse>, response: Response<ArticleResponse>) {
                val article = response.body()
                articleTitle = article?.title!!
                if (article_title != null) article_title.text = articleTitle

                val articleKids = article.kids

                if (articleKids != null) {
                    for (commentId in articleKids) {
                        addChildren(0, commentId)
                    }
                } else {
                    setupEmptyView()
                }
            }
        })
    }

    @Synchronized
    fun addCommentToList(comment: Comment) {

        if (comment.commentId != null
            && comment.commentBy != "" && comment.commentTime != 0L && comment.commentText != null
            && comment.parent != -1 && comment.type != "" && comments_recycler_view != null
        ) {
            (comments_recycler_view.adapter as CommentsAdapter).addCommentsArrangement(comment)
        }

    }

    private fun addChildren(depth: Int, commentId: Int) {
        apiInterface.getItem(commentId).enqueue(object : Callback<CommentResponse> by emptyCallback {

            override fun onResponse(call: Call<CommentResponse>, response: Response<CommentResponse>) {
                //add comments to the commentsList
                val parentComment = buildCommentFromResponse(depth, response)

                if (parentComment != null) addCommentToList(parentComment)

                val commentKids = response.body()?.commentKids ?: return

                for (child in commentKids) {
                    addChildren(depth + 1, child)
                }
            }
        })
    }

    val emptyCallback = object : Callback<CommentResponse> {
        override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
        }

        override fun onResponse(call: Call<CommentResponse>, response: Response<CommentResponse>) {
        }
    }

    private fun buildCommentFromResponse(depth: Int, response: Response<CommentResponse>): Comment? =
        response.body()?.let { responseBody ->
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
        } else {
            comments_recycler_view.visibility = View.VISIBLE
            empty_view_comments.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        comments_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        comments_recycler_view.adapter = CommentsAdapter(context!!)
    }
}

