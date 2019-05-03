package com.example.hacknews

import com.example.hacknews.Article.ArticleResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface ApiInterface {
    @get:GET("v0/topstories.json?print=pretty")
    val topStories: Call<List<Int>>

    @get:GET("v0/newstories.json?print=pretty")
    val newStories: Call<List<Int>>

    @get:GET("v0/beststories.json?print=pretty")
    val bestStories: Call<List<Int>>

    @GET("v0/item/{articleid}.json?print=pretty")
    fun getArticle(@Path("articleid") id: Int): Call<ArticleResponse>
}