package com.example.hacknews.Comment

data class Comment(

    var commentId: Int?,
    val commentBy: String,
    val commentTime: Long,
    val commentText: String?,
    val parent: Int,
    val kids: List<Int>?,
    var type: String
)
