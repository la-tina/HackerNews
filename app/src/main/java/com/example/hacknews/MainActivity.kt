package com.example.hacknews

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.example.hacknews.Comment.CommentsFragment


class MainActivity : AppCompatActivity(), OnClickedListener {

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

    override fun onArticleCommentsClicked(articleId: Int) {
        openCommentsTab(articleId)
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

    private fun openCommentsTab(articleId: Int) {
        val previouslyAddedCommentsFragment = supportFragmentManager.findFragmentByTag(commentsTag)
        val fragment = (previouslyAddedCommentsFragment as? CommentsFragment) ?: CommentsFragment()

        fragment.onNavigationChangedListener = this

        val bundle = Bundle().apply { putSerializable(ARTICLE_KEY, articleId) }
        fragment.arguments = bundle
        openMainTab(fragment, commentsTag)
    }

    private fun openMainTab(fragment: Fragment, tag: String) {
        currentFragment = fragment

        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .addToBackStack("a")
            .replace(R.id.fragment_container, fragment, tag)
            .commit()
    }
}

interface OnClickedListener {
    fun onArticleCommentsClicked(articleId: Int)
}
