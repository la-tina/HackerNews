package com.example.hacknews

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import android.view.ViewGroup
import com.example.hacknews.article.NewNewsFragment
import com.example.hacknews.article.TheBestNewsFragment
import com.example.hacknews.article.TopNewsFragment


class HackerNewsTabAdapter(fragmentManager: FragmentManager, val onNavigationChangedListener: OnClickedListener) :
    FragmentPagerAdapter(fragmentManager) {

    private val tabTitles = arrayOf("New", "Top", "The Best")
    private var fragmentTags: MutableMap<Int, String> = HashMap()

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }

    override fun getItem(position: Int): Fragment {
        Log.i("Fragment", "getItem called $position")
        return when (position) {
            0 -> NewNewsFragment().apply {
                Log.v("Fragment", "create NewNewsFragment")
                setOnNavigationChangedListener(onNavigationChangedListener)
            }
            1 -> TopNewsFragment().apply {
                Log.v("Fragment", "create TopNewsFragment")
                setOnNavigationChangedListener(onNavigationChangedListener)
            }
            2 -> TheBestNewsFragment().apply {
                Log.v("Fragment", "create TheBestNewsFragment")
                setOnNavigationChangedListener(onNavigationChangedListener)
            }
            else -> throw IllegalStateException("Entered else in when")
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val item = super.instantiateItem(container, position)

        if (item is Fragment) {
            // record the fragment tag here.
            val tag = item.tag
            fragmentTags[position] = tag!!
            return item
        } else throw IllegalStateException("asd")
    }

    fun getFragmentTags(): MutableMap<Int, String> {
        return fragmentTags
    }

    override fun getCount(): Int {
        return 3
    }
}