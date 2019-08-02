package com.example.hacknews.article

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.hacknews.*
import com.example.hacknews.articles_database.FavouriteArticle
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_the_best_news.*


class TheBestNewsFragment : HackerNewsTabFragment() {

    private val apiClient = ApiClient()

    private var list: ArrayList<Article> = ArrayList()

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val adapter by lazy {
        NewsAdapter(
            requireContext(),
            list,
            listener!!,
            ::startActivity,
            ::addArticleToDatabase,
            ::deleteArticleFromDatabase
        )
    }

    private val paginator = BehaviorProcessor.create<Int>()
    private var loading = false
    private var pageNumber = 1

    private var lastVisibleItem: Int = 0
    var totalItemCount: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_the_best_news, container, false)

    override fun onResume() {
        super.onResume()

        the_best_news_recycler_view.adapter = adapter
        Log.i("onResumed", "TheBestNewsFragment resumed")
    }

    override fun setRefreshListener() {
        // Adding Listener
        swipe_refresh_best?.setOnRefreshListener {
            // Your code here
            pageNumber = 1
            loadStories()
            Toast.makeText(context, "Refreshed", Toast.LENGTH_LONG).show()
            // To keep animation for 4 seconds
            Handler().postDelayed({
                // Stop animation (This will be after 3 seconds)
                swipe_refresh_best?.isRefreshing = false
            }, 5000) // Delay in millis
        }

        // Scheme colors for animation
        swipe_refresh_best.setColorSchemeResources(
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light,
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light
        )
    }

    override fun onStop() {
        super.onStop()
        Log.i("onStop", "BestNewsFragment")
        pageNumber = 1
        compositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun loadStories() {
        Log.i("loadStories", "BestNewsFragment load")
        val observable = apiClient.getApiServiceWithRx().bestStories()

        val disposable = observable
            .concatMap { articleIds ->
                val articleChunks = articleIds.chunked(10)
                Flowable.fromIterable(articleChunks).subscribeOn(Schedulers.io())
                    .zipWith(paginator, BiFunction { chunks: List<Int>, _: Int -> chunks }).toObservable()
                    .doOnNext { Log.i("Boo", "$it") }
            }
            .concatMap { articleIds ->
                Log.i("flatMap2", "articleIDs: $articleIds, Thread: ${Thread.currentThread()}")
                val requests =
                    articleIds.map { apiClient.getApiServiceWithRx().getArticle(it).subscribeOn(Schedulers.io()) }
                Observable.zip(requests) { responses ->
                    responses.toList() as List<ArticleResponse>
                }.sorted()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                Log.i("onNext best", "Thread: ${Thread.currentThread()}")
                loading = true
                progress_bar_best?.visibility = View.VISIBLE
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { articles ->
                    Log.i("subscribe best", "Thread: ${Thread.currentThread()}")
                    Log.i("subscribe best", articles.toString())
                    articles.forEach {
                        if (it.id != -1) list.add(it.toArticle())
                    }
                    progress_bar_best?.visibility = View.INVISIBLE
                    adapter.notifyDataSetChanged()
                    loading = false
                    swipe_refresh_best.isRefreshing = false
                }
                , { it.printStackTrace() }
                , { println("onComplete!") })

        compositeDisposable.add(disposable)
        paginator.onNext(pageNumber)
    }

    override fun setUpLoadMoreListener() {
        the_best_news_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int, dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)

                totalItemCount = the_best_news_recycler_view.layoutManager?.itemCount!!
                lastVisibleItem =
                    (the_best_news_recycler_view.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if (!loading && totalItemCount <= lastVisibleItem + VISIBLE_THRESHOLD) {
                    pageNumber++
                    paginator.onNext(pageNumber)
                    loading = true
                }
            }
        })
    }

    override fun stopLoading() {
        Log.i("TheBestNewsFragment", "stopLoading")
        pageNumber = 1
        compositeDisposable.clear()
    }

    override fun setOnNavigationChangedListener(onNavigationChangedListener: OnClickedListener) {
        listener = onNavigationChangedListener
    }

    override fun setupRecyclerView() {
        the_best_news_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        val newsAdapter = NewsAdapter(
            requireContext(),
            list,
            listener!!,
            ::startActivity,
            ::addArticleToDatabase,
            ::deleteArticleFromDatabase
        )
        the_best_news_recycler_view.adapter = newsAdapter

        articleViewModel.allFavouriteArticles.observe(this, Observer { articles ->
            // Update the cached copy of the words in the adapter.
            articles?.let {
                adapter.setFavouriteNews(it)
            }
        })
    }
}
