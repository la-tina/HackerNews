package com.example.hacknews

import android.support.v4.app.Fragment

abstract class HackerNewsTabFragment : Fragment() {

    abstract fun setupRecyclerView()

    abstract fun setOnNavigationChangedListener(onNavigationChangedListener: OnClickedListener)

    protected var listener: OnClickedListener? = null

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }
}