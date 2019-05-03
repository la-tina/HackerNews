package com.example.hacknews

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity


class MainActivity : AppCompatActivity(), OnNavigationChangedListener {

    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            currentFragment = supportFragmentManager.getFragment(savedInstanceState, "FragmentName")
            restoreCurrentFragmentState()
        } else {
            setDefaultCurrentFragment()
        }

        if (currentFragment?.isAdded == false) {
            val getFragmentTag = getFragmentTag(currentFragment!!)

            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, currentFragment!!, getFragmentTag)
                .commit()
        }
    }

    override fun onNavigationChanged(tabNumber: Int) {
        when (tabNumber) {
            HACKER_NEWS_TAB -> openHackerNewsTab()
        }
    }

    private fun setDefaultCurrentFragment() {
        val fragment = HackerNewsFragment()
        currentFragment = fragment
        fragment.onNavigationChangedListener = this
    }

    override fun onBackPressed() {
        super.onBackPressed()
        restoreCurrentFragmentState()
    }

    private fun restoreCurrentFragmentState() {
        if (currentFragment is HackerNewsFragment)
            (currentFragment as HackerNewsFragment).onNavigationChangedListener = this
    }

    private fun getFragmentTag(fragment: Fragment): String =
        when (fragment) {
            is HackerNewsFragment -> hackerNewsTag
            else -> throw IllegalStateException()
        }


    private fun openHackerNewsTab() {
        val previouslyAddedHackerNewsFragment = supportFragmentManager.findFragmentByTag(hackerNewsTag)
        val fragment = (previouslyAddedHackerNewsFragment as? HackerNewsFragment) ?: HackerNewsFragment()

        fragment.onNavigationChangedListener = this

        openMainTab(fragment, hackerNewsTag)
    }

    private fun openMainTab(fragment: Fragment, tag: String) {
        currentFragment = fragment

        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(R.id.fragment_container, fragment, tag)
            .commit()
    }
}

interface OnNavigationChangedListener {
    fun onNavigationChanged(tabNumber: Int)
}
