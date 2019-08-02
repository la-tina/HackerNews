package com.example.hacknews.article

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hacknews.OnClickedListener
import com.example.hacknews.R
import com.example.hacknews.articles_database.ArticleViewModel
import com.example.hacknews.articles_database.FavouriteArticle
import kotlinx.android.synthetic.main.fragment_favourite_news.*


open class FavouriteNewsFragment : Fragment() {
    lateinit var onNavigationChangedListener: OnClickedListener
    private var listener: OnClickedListener? = null

    private val articleViewModel: ArticleViewModel by lazy {
        ViewModelProviders.of(this).get(ArticleViewModel(requireActivity().application)::class.java)
    }

    lateinit var favouriteNewsList: MutableList<Article>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_favourite_news, container, false)

    override fun onResume() {
        super.onResume()

        setupRecyclerView()
        if (!::favouriteNewsList.isInitialized || favouriteNewsList.size == 0) {
            setupEmptyView()
        }
    }

    private fun addArticleToDatabase(article: FavouriteArticle) {
        articleViewModel.insert(article)
    }

    private fun deleteArticleFromDatabase(article: FavouriteArticle) {
        articleViewModel.deleteFavouriteArticle(article)
    }

    private fun setupEmptyView() {
        val comments = favourite_recycler_view?.adapter
        if (comments?.itemCount == 0 || comments?.itemCount == null) {
            favourite_recycler_view.visibility = View.GONE
            empty_view_favourite.visibility = View.VISIBLE
            empty_view_favourite_text.visibility = View.VISIBLE
        } else {
            favourite_recycler_view.visibility = View.VISIBLE
            empty_view_favourite.visibility = View.GONE
            empty_view_favourite_text.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        favourite_recycler_view?.let { recyclerView ->
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            val favouriteArticleAdapter = FavouriteNewsAdapter(
                requireContext(),
                onNavigationChangedListener,
                ::startActivity,
                ::addArticleToDatabase,
                ::deleteArticleFromDatabase
            )
            recyclerView.adapter = favouriteArticleAdapter

            // Observer on the LiveData
            // The onChanged() method fires when the observed data changes and the activity is
            // in the foreground.

            articleViewModel.allFavouriteArticles.observe(this, Observer { products ->
                // Update the cached copy of the words in the adapter.
                products?.let {
                    favouriteArticleAdapter.setProducts(it)
                    setupEmptyView()
                }
            })
        }
    }

}
