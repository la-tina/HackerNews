package com.example.hacknews.Comment

//import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hacknews.*
import com.example.hacknews.Article.ArticleResponse
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_article_comments.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class CommentsFragment : Fragment() {

    lateinit var onNavigationChangedListener: OnClickedListener
    private var commentsList: ArrayList<Comment> = ArrayList()
    lateinit var articleTitle: String
    //private val colors = activity?.resources?.getIntArray(R.array.commentColors)!!.toList()

    //    private lateinit var apiInterface: ApiInterface
    private val subscriptions = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_article_comments, container, false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val articleId = arguments?.getSerializable(ARTICLE_KEY) as Int
        val apiInterface = ApiClient.getClient().create(ApiInterface::class.java)

        apiInterface?.getArticle(articleId)?.enqueue(object : Callback<ArticleResponse> {

            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
            }

            override fun onResponse(call: Call<ArticleResponse>, response: Response<ArticleResponse>) {
                val article = response.body()
                articleTitle = article?.title!!
                val articleKids = article?.kids
                if (articleKids != null) {
                    for (comment in articleKids) {
                        apiInterface.getItem(comment).enqueue(object : Callback<CommentResponse> {

                            override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                            }

                            override fun onResponse(call: Call<CommentResponse>, response: Response<CommentResponse>) {
                                val id = response.body()?.commentId
                                val author = response.body()?.commentBy
                                val time = response.body()?.commentTime
                                val text = response.body()?.commentText
                                val parent = response.body()?.commentParent
                                val kids = response.body()?.commentKids
                                val type = response.body()?.commentType
//                                val url = response.body()?.commentUrlString

                                if (id != null && author != null && time != null && text != null &&
                                    parent != null && kids != null && type != null
                                )
                                    commentsList.add(
                                        Comment(
                                            id,
                                            author,
                                            time,
                                            text,
                                            parent,
                                            kids,
                                            type
                                        )
                                    )

                                if (context != null) {

                                    article_title.text = articleTitle

                                    val adapter = CommentsAdapter(commentsList, context!!)
                                    comments_recycler_view.adapter = adapter
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        })

                    }
                }
            }

        })
    }

    override fun onResume() {
        super.onResume()
//        (activity as? AppCompatActivity)?.supportActionBar?.title = "title"

        toolbarTop.setNavigationOnClickListener {
            fragmentManager?.popBackStackImmediate()
        }

        setupRecyclerView()
    }

    override fun onAttach(context: Context) {
        retainInstance = true
        super.onAttach(context)
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        subscriptions.clear()
//    }

    private fun setupRecyclerView() {

        //val comments = mutableListOf<Comment>()
//        comments_recycler_view.isNestedScrollingEnabled = false
        comments_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        val commentsAdapter = CommentsAdapter(commentsList, context!!)
        comments_recycler_view.adapter = commentsAdapter
//        comments_recycler_view.itemAnimator = SlideInRightAnimator()

    }
}

