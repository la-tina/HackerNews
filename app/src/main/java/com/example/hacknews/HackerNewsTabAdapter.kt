package com.example.hacknews

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class HackerNewsTabAdapter(
    fragmentManager: FragmentManager,
    private var fragments: List<Fragment>
) : FragmentStatePagerAdapter(fragmentManager) {
    
    private val tabTitles = arrayOf("New", "Top", "The Best")

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}
