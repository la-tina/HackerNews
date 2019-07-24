package com.example.hacknews

import com.example.hacknews.article.ArticleResponse
import com.example.hacknews.comment.CommentResponse
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface ApiInterface {
    @GET("v0/topstories.json?print=pretty")
    fun topStories(): Observable<List<Int>>

    @GET("v0/newstories.json?print=pretty")
    fun newStories(): Observable<List<Int>>

    @GET("v0/beststories.json?print=pretty")
    fun bestStories(): Observable<List<Int>>

    @GET("v0/item/{articleid}.json?print=pretty")
    fun getArticle(@Path("articleid") id: Int): Observable<ArticleResponse>

    @GET("v0/item/{id}.json?print=pretty")
    fun getItem(@Path("id") id: Int): Observable<CommentResponse>
}