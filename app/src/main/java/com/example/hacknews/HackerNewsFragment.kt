package com.example.hacknews

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_hacker_news.*


class HackerNewsFragment : Fragment() {

    lateinit var onNavigationChangedListener: OnClickedListener

    lateinit var newsAdapter: HackerNewsTabAdapter

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
        val adapter = HackerNewsTabAdapter(childFragmentManager, onNavigationChangedListener)
        newsAdapter = adapter
        // Set the adapter onto the view pager
        hacker_news_viewpager.adapter = adapter

        // Give the TabLayout the ViewPager
        tab_layout_hacker_news.setupWithViewPager(hacker_news_viewpager)

        hacker_news_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            var first: Boolean = true

            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//                if (first) {
                    onPageSelected(position)
//                    first = false
//                }
            }

            override fun onPageSelected(position: Int) {
                val currentFragment =
                    adapter.instantiateItem(hacker_news_viewpager, position) as HackerNewsTabFragment

                currentFragment.loadStories()

                val inactiveFragmentsTags = adapter.getFragmentTags().minus(position)

                inactiveFragmentsTags.forEach { (_, tag) ->
                    val inactive = childFragmentManager.findFragmentByTag(tag) as? HackerNewsTabFragment
                    inactive?.stopLoading()
                }
            }
        })
    }
}

