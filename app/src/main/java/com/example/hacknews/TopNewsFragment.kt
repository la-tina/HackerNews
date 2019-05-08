package com.example.hacknews

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hacknews.Article.Article
import com.example.hacknews.Article.ArticleResponse
import kotlinx.android.synthetic.main.fragment_new_news.*
import kotlinx.android.synthetic.main.fragment_top_news.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TopNewsFragment : HackerNewsTabFragment() {

    private var list: ArrayList<Article> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_top_news, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiInterface = ApiClient.getClient().create(ApiInterface::class.java)

        apiInterface?.topStories?.enqueue(object : Callback<List<Int>> {

            override fun onFailure(call: Call<List<Int>>, t: Throwable) {
            }

            override fun onResponse(call: Call<List<Int>>, response: Response<List<Int>>) {

                val topStories = response.body()
                for (i in 0..9) {

                    if (topStories != null) {
                        apiInterface.getArticle(topStories[i]).enqueue(object : Callback<ArticleResponse> {

                            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {

                            }

                            override fun onResponse(call: Call<ArticleResponse>, response: Response<ArticleResponse>) {

                                val title = response.body()?.title.toString()
                                val author = response.body()?.by.toString()
                                val score = response.body()?.score.toString()
                                val descendants = response.body()?.descendants.toString()
                                val type = response.body()?.type.toString()
                                val unixTime = response.body()?.time.toString()

                                val sdf = java.text.SimpleDateFormat("yy-MM-dd HH:mm")
                                val date = java.util.Date(unixTime.toLong() * 1000)
                                val time = sdf.format(date)

                                val url = response.body()?.url.toString()
                                list.add(Article(title, url, author, score, descendants, type, time))

                                if (context != null) {
                                    val adapter = NewsAdapter(context!!, list)
                                    top_news_recycler_view.adapter = adapter
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
        top_news_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        val newsAdapter = NewsAdapter(context!!, list)
        top_news_recycler_view.adapter = newsAdapter
    }
}
