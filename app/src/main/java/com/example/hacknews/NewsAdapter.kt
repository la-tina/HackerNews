package com.example.hacknews

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.TextView
import com.example.hacknews.Article.Article
import kotlinx.android.synthetic.main.news_list_row.view.*


class NewsAdapter(
    private val context: Context,
    private val list: ArrayList<Article>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(LayoutInflater.from(context).inflate(com.example.hacknews.R.layout.news_list_row,
            parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {

        holder.author.text = list[position].author
        holder.score.text = list[position].score
        holder.comments.text = list[position].descendants
        holder.type.text = list[position].type
        holder.title.text = list[position].title

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
        internal var type: TextView = itemView.news_row_type as TextView
    }
}