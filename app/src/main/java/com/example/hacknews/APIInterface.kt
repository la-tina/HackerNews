package com.example.hacknews

import com.example.hacknews.article.ArticleResponse
import com.example.hacknews.comment.CommentResponse
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

    @GET("v0/item/{id}.json?print=pretty")
    fun getItem(@Path("id") id: Int): Call<CommentResponse>
}