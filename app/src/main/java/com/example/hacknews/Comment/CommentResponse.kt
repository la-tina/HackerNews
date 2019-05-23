package com.example.hacknews.Comment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommentResponse {

    @SerializedName("by")
    @Expose
    var commentBy: String? = null

    @SerializedName("time")
    @Expose
    var commentTime: Long? = null

    @SerializedName("text")
    @Expose
    var commentText: String? = null

    @SerializedName("url")
    @Expose
    var commentUrlString: String? = null

    @SerializedName("title")
    @Expose
    var commentTitle: String? = null

    @SerializedName("id")
    @Expose
    var commentId: Int? = null

    @SerializedName("parent")
    @Expose
    var commentParent: Int? = null

    @SerializedName("kids")
    @Expose
    var commentKids: List<Int>? = null

    @SerializedName("type")
    @Expose
    var commentType: String? = null
}