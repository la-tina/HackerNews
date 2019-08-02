package com.example.hacknews.article

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.news_list_row.view.*

class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    internal var title: TextView = itemView.news_row_title as TextView
    internal var author: TextView = itemView.news_row_author as TextView
    internal var score: TextView = itemView.news_row_score as TextView
    internal var comments: TextView = itemView.news_row_comments as TextView
    internal var time: TextView = itemView.news_row_time as TextView
    internal var id: TextView = itemView.news_row_id as TextView
    internal var shareIcon: ImageView = itemView.share_icon as ImageView
    internal var commentIcon: ImageView = itemView.comment_icon as ImageView
    val favouriteBorderImg: ImageView = itemView.favorite_border_icon as ImageView
    val favouriteImg: ImageView = itemView.favorite_icon as ImageView
}