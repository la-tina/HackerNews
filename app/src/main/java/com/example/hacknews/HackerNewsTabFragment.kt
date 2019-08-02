package com.example.hacknews

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import com.example.hacknews.articles_database.ArticleViewModel
import com.example.hacknews.articles_database.FavouriteArticle

abstract class HackerNewsTabFragment : Fragment() {

    protected val articleViewModel: ArticleViewModel by lazy {
        ViewModelProviders.of(this).get(ArticleViewModel(requireActivity().application)::class.java)
    }

    abstract fun setupRecyclerView()

    abstract fun setOnNavigationChangedListener(onNavigationChangedListener: OnClickedListener)

    abstract fun setRefreshListener()

    abstract fun setUpLoadMoreListener()

    abstract fun loadStories()

    abstract fun stopLoading()

    protected fun addArticleToDatabase(article: FavouriteArticle) {
        articleViewModel.insert(article)
    }

    protected fun deleteArticleFromDatabase(article: FavouriteArticle) {
        articleViewModel.deleteFavouriteArticle(article)
    }

    protected var listener: OnClickedListener? = null

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
        setRefreshListener()
        setUpLoadMoreListener()
    }
}
