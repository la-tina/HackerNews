package com.example.hacknews.article

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.example.hacknews.ApiClient
import com.example.hacknews.OnClickedListener
import com.example.hacknews.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_search.*

class SearchStoriesFragment : Fragment() {

    lateinit var onNavigationChangedListener: OnClickedListener

    private val apiClient = ApiClient()
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var listener: OnClickedListener? = null

    private val storiesList: MutableList<Article> = mutableListOf()
    private val adapter by lazy { NewsAdapter(requireContext(), storiesList, listener!!, ::startActivity) }

    private var searchedString: String = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onStart() {
        super.onStart()
        listener = onNavigationChangedListener
        setupRecyclerView()
    }

    @Synchronized
    override fun onResume() {
        super.onResume()
        Log.i("onResume", "SearchStoriesFragment load")

        search_recycler_view.adapter = adapter

        search_button.setOnClickListener {
            Log.i("loadStories", "button clicked")
            storiesList.clear()
            hideKeyboard(requireActivity())

            if (search_edit_text != null && !search_edit_text.text.isBlank()) {
                searchedString = search_edit_text.text.toString()
                progress_bar_search?.visibility = View.VISIBLE
                adapter.notifyDataSetChanged()
                loadStories()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val f = activity.currentFocus
        if (null != f && null != f.windowToken && EditText::class.java.isAssignableFrom(f.javaClass))
            imm.hideSoftInputFromWindow(f.windowToken, 0)
        else
            activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private fun loadStories() {
        Log.i("loadStories", "SearchStoriesFragment load")
        val observableNew = apiClient.getApiServiceWithRx().newStories()
        val observableTop = apiClient.getApiServiceWithRx().topStories()
        val observableBest = apiClient.getApiServiceWithRx().bestStories()
        val disposable = observableNew
            .mergeWith(observableTop)
            .mergeWith(observableBest)
            .concatMap { articleIds ->
                Log.i("flatMap2", "articleIDs: $articleIds, Thread: ${Thread.currentThread()}")
                val requests =
                    articleIds.map {
                        apiClient.getApiServiceWithRx().getArticle(it).subscribeOn(Schedulers.io())
                    }
                Observable.zip(requests) { responses ->
                    responses.toList() as List<ArticleResponse>
                }.sorted()
            }
            .map { responses ->
                responses.filter {
                    it.title != null && it.type == "story" && it.title?.toLowerCase()!!.contains(
                        searchedString.toLowerCase()
                    )
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                Log.i("onNext filtered", "Thread: ${Thread.currentThread()}")
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { articles ->
                    Log.i("subscribe filtered", "Thread: ${Thread.currentThread()}")
                    Log.i("subscribe filtered", articles.toString())
                    articles.forEach {
                        if (it.id != -1) storiesList.add(it.toArticle())
                    }
                    adapter.notifyDataSetChanged()
                }
                , { it.printStackTrace() }
                , {
                    println("onComplete!")
                    progress_bar_search?.visibility = View.INVISIBLE
                })
        compositeDisposable.add(disposable)
    }

    private fun setupRecyclerView() {
        search_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        val filteredNewsAdapter = NewsAdapter(requireContext(), storiesList, listener!!, ::startActivity)
        search_recycler_view.adapter = filteredNewsAdapter
    }
}