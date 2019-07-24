package com.example.hacknews.article

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.hacknews.OnClickedListener
import com.example.hacknews.R
import kotlinx.android.synthetic.main.news_list_row.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class NewsAdapter(
    private val context: Context,
    private val list: MutableList<Article>,
    listener: OnClickedListener,
    private val startActivity: (Intent) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    var onNavigationChangedListener: OnClickedListener = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.news_list_row,
                parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.author.text = list[position].author
        holder.score.text = list[position].score
        if (list[position].descendants == "null") holder.comments.text = "0"
        else holder.comments.text = list[position].descendants
//        holder.type.text = list[position].type
//        holder.time.text = list[position].time

        convertTime(list[position].time, holder)

        holder.title.text = list[position].title

        setWebViewListener(holder, position)

        holder.comments.setOnClickListener {
            onNavigationChangedListener.onArticleCommentsClicked(list[position].id)
        }

        holder.commentIcon.setOnClickListener {
            onNavigationChangedListener.onArticleCommentsClicked(list[position].id)
        }

        holder.shareIcon.setOnClickListener {
            val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBody = list[position].url
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, holder.title.toString())
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Share via"))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun convertTime(time: String, holder: NewsViewHolder) {

        val format = SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss")

        val past = format.parse(time)

        val now = Date()
        val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
        val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time)
        val days = TimeUnit.MILLISECONDS.toDays(now.time - past.time)

        when {
            seconds < 60 -> holder.time.text = "$seconds seconds ago"
            minutes < 60 -> holder.time.text = "$minutes minutes ago"
            hours < 24 -> holder.time.text = "$hours hours ago"
            else -> holder.time.text = "$days days ago"
        }
    }

    private fun setWebViewListener(holder: NewsViewHolder, position: Int) {
        holder.title.setOnClickListener {

            val url = list[position].url
            val newsWebView = WebView(context)
            newsWebView.loadUrl(url)
            val alert = AlertDialog.Builder(context)
            newsWebView.webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    view?.loadUrl(url)
                    return true
                }
            }

            alert.setView(newsWebView)
            alert.setNegativeButton("Close") { dialog, _ -> dialog.dismiss() }
            val dialog = alert.create()

            dialog.show()
            val layoutParam = WindowManager.LayoutParams()

            layoutParam.copyFrom(dialog.window?.attributes)
            layoutParam.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParam.height = WindowManager.LayoutParams.MATCH_PARENT
            layoutParam.gravity = Gravity.CENTER
            dialog.window?.attributes = layoutParam

            val positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            val parent = positiveButton.parent as LinearLayout

            parent.gravity = Gravity.CENTER_HORIZONTAL
            val leftSpacer = parent.getChildAt(1)

            leftSpacer.visibility = View.GONE
        }
    }

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var title: TextView = itemView.news_row_title as TextView
        internal var author: TextView = itemView.news_row_author as TextView
        internal var score: TextView = itemView.news_row_score as TextView
        internal var comments: TextView = itemView.news_row_comments as TextView
        //        internal var type: TextView = itemView.news_row_type as TextView
        internal var time: TextView = itemView.news_row_time as TextView
        internal var id: TextView = itemView.news_row_id as TextView
        internal var shareIcon: ImageView = itemView.share_icon as ImageView
        internal var commentIcon: ImageView = itemView.comment_icon as ImageView
    }
}