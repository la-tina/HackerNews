package com.example.hacknews

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hacknews.article.NewNewsFragment
import com.example.hacknews.article.TheBestNewsFragment
import com.example.hacknews.article.TopNewsFragment
import kotlinx.android.synthetic.main.fragment_hacker_news.*


class HackerNewsFragment : Fragment() {

    lateinit var onNavigationChangedListener: OnClickedListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hacker_news, container, false)
    }

    override fun onResume() {
        super.onResume()
        setupViewPager()
    }

    override fun onPause() {
        super.onPause()
        childFragmentManager.fragments.forEach { fragment ->
            childFragmentManager.beginTransaction().remove(fragment).commit()
        }
    }

    private fun setupViewPager() {
        // Create an adapter that knows which fragment should be shown on each page
        val topNewsFragment = TopNewsFragment().apply {
            setOnNavigationChangedListener(onNavigationChangedListener)
        }

        val newNewsFragment = NewNewsFragment().apply {
            setOnNavigationChangedListener(onNavigationChangedListener)
        }

        val theBestNewsFragment = TheBestNewsFragment().apply {
            setOnNavigationChangedListener(onNavigationChangedListener)
        }

        val fragments = listOf(newNewsFragment, topNewsFragment, theBestNewsFragment)

        val adapter = HackerNewsTabAdapter(childFragmentManager, fragments)
        // Set the adapter onto the view pager
        hacker_news_viewpager.adapter = adapter

        // Give the TabLayout the ViewPager
        tab_layout_hacker_news.setupWithViewPager(hacker_news_viewpager)
    }
}
