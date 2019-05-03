package com.example.hacknews

import android.support.v4.app.Fragment

abstract class HackerNewsTabFragment : Fragment()  {

    abstract fun setupRecyclerView()

    abstract fun setOnNavigationChangedListener(onNavigationChangedListener: OnNavigationChangedListener)

    protected var listener: OnNavigationChangedListener? = null
    
    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }
}