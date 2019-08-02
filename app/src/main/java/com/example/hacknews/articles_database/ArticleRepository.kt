package com.example.hacknews.articles_database

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread

class ArticleRepository(private val articleDao: ArticleDao) {

    //Observed LiveData will notify the observer when the data has changed.
    val allFavouriteArticles: LiveData<List<FavouriteArticle>> = articleDao.getAllFavouriteArticles()

    @WorkerThread
    suspend fun insert(product: FavouriteArticle) {
        articleDao.insert(product)
    }

    @WorkerThread
    suspend fun deleteFavouriteArticle(product: FavouriteArticle) {
        articleDao.deleteArticle(product)
    }

    @WorkerThread
    suspend fun deleteAll() {
        articleDao.deleteAll()
    }

    @WorkerThread
    suspend fun getAllFavouriteArticles(){
        articleDao.getAllFavouriteArticles()
    }
}