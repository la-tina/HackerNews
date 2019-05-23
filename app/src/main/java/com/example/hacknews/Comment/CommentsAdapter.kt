package com.example.hacknews.Comment

//import com.jakewharton.rxbinding2.view.RxView
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.hacknews.R
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.comment_row_root.view.*

internal class CommentsAdapter(
    private val items: ArrayList<Comment>,
    context: Context
) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    private val itemSubject: PublishSubject<Comment> = PublishSubject.create()
    private val depthMargin = context.resources.getDimension(R.dimen.padding_xs)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int =
        R.layout.comment_row_root

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]


        val list = items[position].commentText.toString()
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
        val simpleDataFormat = java.text.SimpleDateFormat("yy-MM-dd HH:mm")
        val date = java.util.Date(item.commentTime * 1000)
        val time = simpleDataFormat.format(date)
        holder.user.text = item.commentBy + " Posted on " + time
//        holder.color?.setBackgroundColor(colors[Math.max(0, (item.commentDepth % colors.size) - 1)])
        val params = RelativeLayout.LayoutParams(holder.itemView.layoutParams)
//        params.marginStart = (depthMargin * Math.max(0, Math.min(item.commentDepth, 5) - 1)).toInt()
        holder.itemView.layoutParams = params
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val user: TextView = itemView.user as TextView
        //        val color: View? = itemView.color
        val comment: TextView = itemView.comment as TextView
    }
}

