package com.example.hacknews

import android.support.v4.app.Fragment

abstract class HackerNewsTabFragment : Fragment() {

    val apiInterface: ApiInterface by lazy { ApiClient.getClient().create(ApiInterface::class.java) }

    abstract fun setupRecyclerView()

    abstract fun setOnNavigationChangedListener(onNavigationChangedListener: OnClickedListener)

    abstract fun getArticle(stories: List<Int>, i: Int)

    abstract fun addArticlesToAdapter()

    protected var listener: OnClickedListener? = null

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }
}
