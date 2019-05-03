package com.example.hacknews

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hacknews.Article.Article
import com.example.hacknews.Article.ArticleResponse
import kotlinx.android.synthetic.main.fragment_new_news.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NewNewsFragment : HackerNewsTabFragment() {

    private var list: ArrayList<Article> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_new_news, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiInterface = ApiClient.getClient().create(ApiInterface::class.java)

        apiInterface?.newStories?.enqueue(object : Callback<List<Int>> {

            override fun onFailure(call: Call<List<Int>>, t: Throwable) {
            }

            override fun onResponse(call: Call<List<Int>>, response: Response<List<Int>>) {

                val newStories = response.body()
                for (i in 0..9) {

                    if (newStories != null) {
                        apiInterface.getArticle(newStories[i]).enqueue(object : Callback<ArticleResponse> {

                            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {

                            }

                            override fun onResponse(call: Call<ArticleResponse>, response: Response<ArticleResponse>) {

                                val title = response.body()?.title.toString()
                                val author = response.body()?.by.toString()
                                val score = response.body()?.score.toString()
                                val descendants = response.body()?.descendants.toString()
                                val type = response.body()?.type.toString()
                                val url = response.body()?.url.toString()
                                list.add(Article(title, url, author, score, descendants, type))

                                if (context != null) {
                                    val adapter = NewsAdapter(context!!, list)
                                    new_news_recycler_view.adapter = adapter
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        })
                    }
                }
            }
        })
    }

    override fun setOnNavigationChangedListener(onNavigationChangedListener: OnNavigationChangedListener) {
        listener = onNavigationChangedListener
    }

    override fun setupRecyclerView() {
        new_news_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        val newsAdapter = NewsAdapter(context!!, list)
        new_news_recycler_view.adapter = newsAdapter
    }
}
