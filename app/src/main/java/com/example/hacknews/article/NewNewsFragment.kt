package com.example.hacknews.article

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hacknews.*
import kotlinx.android.synthetic.main.fragment_new_news.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val MAX_COUNT = 499

class NewNewsFragment : HackerNewsTabFragment() {
    private var list: ArrayList<Article> = ArrayList()
    private var calls: MutableList<Call<ArticleResponse>> = mutableListOf()

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
                for (i in 0..MAX_COUNT) {
                    if (newStories != null) getArticle(newStories, i)
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        calls.forEach {
            it.cancel()
        }
    }

    override fun getArticle(stories: List<Int>, i: Int) {
        val call = apiInterface.getArticle(stories[i])
        call.enqueue(object : Callback<ArticleResponse> {
            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                Log.w("Call", "Failed")
            }

            override fun onResponse(call: Call<ArticleResponse>, response: Response<ArticleResponse>) {
                response.body()?.toArticle()?.let {
                    if (it.id != -1) list.add(it)
                }

                addArticlesToAdapter()
            }
        })
        calls.add(call)
    }

    override fun addArticlesToAdapter() {
        val adapter = NewsAdapter(requireContext(), list, listener!!, ::startActivity)
        new_news_recycler_view.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun setOnNavigationChangedListener(onNavigationChangedListener: OnClickedListener) {
        listener = onNavigationChangedListener
    }

    override fun setupRecyclerView() {
        new_news_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        val newsAdapter = NewsAdapter(requireContext(), list, listener!!, ::startActivity)
        new_news_recycler_view.adapter = newsAdapter
    }
}
