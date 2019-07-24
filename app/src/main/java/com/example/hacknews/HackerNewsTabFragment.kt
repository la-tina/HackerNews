package com.example.hacknews

import android.support.v4.app.Fragment

abstract class HackerNewsTabFragment : Fragment() {

    abstract fun setupRecyclerView()

    abstract fun setOnNavigationChangedListener(onNavigationChangedListener: OnClickedListener)

    abstract fun setRefreshListener()

    abstract fun setUpLoadMoreListener()

    abstract fun loadStories()

    abstract fun stopLoading()

    protected var listener: OnClickedListener? = null

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
        setRefreshListener()
        setUpLoadMoreListener()
    }
}
