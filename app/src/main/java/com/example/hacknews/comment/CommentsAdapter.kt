package com.example.hacknews.comment

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.comment_row_root.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


internal class CommentsAdapter(
    context: Context
) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    private val comments: MutableList<Comment> = mutableListOf()
    private val commentColors = context.resources.getIntArray(com.example.hacknews.R.array.commentColors)

    fun addCommentsArrangement(comment: Comment) {
        val parentIndexInList = getIndexOfComment(comment.parent)
        if (parentIndexInList == -1) {
            comments.add(comment)
        } else {
            comments.add(parentIndexInList + 1, comment)
        }
        notifyDataSetChanged()
    }

    private fun getIndexOfComment(commentId: Int): Int {
        val comment = comments.firstOrNull { currentComment -> currentComment.commentId == commentId }
        return if (comment == null) return -1 else comments.indexOf(comment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun getItemViewType(position: Int): Int {
        return com.example.hacknews.R.layout.comment_row_root
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = comments[position]

        val list = item.commentText.toString()
            .replace("&#x27;", "'")
            .replace("<p>", "\n")
            .replace("&#x2F;", "/")
            .replace("&quot;", "\"")
            .replace("<i>", "")
            .replace("</i>", "")
            .replace("&gt;", ">")
            .replace("<a href=", " ")
            .replace("rel=", "\n")
            .replace("</a>", "\n")



        holder.comment.text = list
        val simpleDataFormat = SimpleDateFormat.getDateTimeInstance()
        val date = java.util.Date(item.commentTime * 1000)
        val time = simpleDataFormat.format(date)

        holder.user.text = item.commentBy

        convertTime(time, holder)

        val params = RelativeLayout.LayoutParams(holder.itemView.layoutParams)

        if (item.depth > commentColors.size) {
            holder.color?.setBackgroundColor(commentColors[commentColors.size - 2])
            holder.user.setTextColor(commentColors[commentColors.size - 2])
            holder.date.setTextColor(commentColors[commentColors.size - 2])
        } else {
            holder.color?.setBackgroundColor(commentColors[item.depth])
            holder.user.setTextColor(commentColors[item.depth])
            holder.date.setTextColor(commentColors[item.depth])
        }
        holder.itemView.layoutParams = params
        setRowPadding(holder, item)
    }

    private fun convertTime(time: String, holder: ViewHolder) {

        val format = SimpleDateFormat("dd MMM yyyy HH:mm:ss")

        val past = format.parse(time)

        val now = Date()
        val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
        val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time)
        val days = TimeUnit.MILLISECONDS.toDays(now.time - past.time)

        when {
            seconds < 60 -> holder.date.text = "$seconds seconds ago"
            minutes < 60 -> holder.date.text = "$minutes minutes ago"
            hours < 24 -> holder.date.text = "$hours hours ago"
            else -> holder.date.text = "$days days ago"
        }
    }

    private fun setRowPadding(holder: ViewHolder, item: Comment) {
        holder.commentLayout.setPadding(item.depth * 40, 0, 0, 0)
        Log.v("Tina", "Comment depth: ${item.depth} , padding ${item.depth + 20}")
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val user: TextView = itemView.user as TextView
        val date: TextView = itemView.date as TextView
        val color: View? = itemView.color as View
        val comment: TextView = itemView.comment as TextView
        var commentLayout: ConstraintLayout = itemView.comment_layout as ConstraintLayout
    }
}

