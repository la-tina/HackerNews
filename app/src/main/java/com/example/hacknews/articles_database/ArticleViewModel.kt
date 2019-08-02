package com.example.hacknews.articles_database

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ArticleViewModel(application: Application) : AndroidViewModel(application)
{
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: ArticleRepository

    val allFavouriteArticles: LiveData<List<FavouriteArticle>>

    init {
        val articleDao = ArticleRoomDatabase.getDatabase(
            application
        ).articleDao()
        repository = ArticleRepository(articleDao)
        allFavouriteArticles = repository.allFavouriteArticles
    }

    fun insert(article: FavouriteArticle) = scope.launch(Dispatchers.IO) {
        repository.insert(article)
    }

    fun deleteFavouriteArticle(article: FavouriteArticle) = scope.launch(Dispatchers.IO) {
        repository.deleteFavouriteArticle(article)
    }

    fun getAllFavouriteArticles() = scope.launch(Dispatchers.IO) {
        repository.getAllFavouriteArticles()
    }

        //when the ViewModel is no longer used
    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}