package com.example.hacknews.article

data class Article(
    var title: String,
    var url: String,
    var author: String,
    var score: String,
    var descendants: String,
    var type: String,
    var time: String,
    var id: Int,
    var isFavourite: Boolean
)