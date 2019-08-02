package com.example.hacknews.article

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.Toast
import com.example.hacknews.OnClickedListener
import com.example.hacknews.R
import com.example.hacknews.articles_database.FavouriteArticle
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class FavouriteNewsAdapter(
    private val context: Context,
    listener: OnClickedListener,
    private val startActivity: (Intent) -> Unit,
    val addArticleToDatabase: (FavouriteArticle) -> Unit,
    val deleteArticleFromDatabase: (FavouriteArticle) -> Unit
) : RecyclerView.Adapter<NewsViewHolder>() {

    var onNavigationChangedListener: OnClickedListener = listener
    private var articles: MutableList<FavouriteArticle> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.news_list_row,
                parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    internal fun setProducts(favouriteArticles: List<FavouriteArticle>) {
        this.articles.clear()
        this.articles.addAll(favouriteArticles)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.author.text = articles[position].author
        holder.score.text = articles[position].score
        if (articles[position].descendants == "null") holder.comments.text = "0"
        else holder.comments.text = articles[position].descendants

        convertTime(articles[position].time, holder)

        holder.title.text = articles[position].title

        setWebViewListener(holder, position)

        holder.comments.setOnClickListener {
            onNavigationChangedListener.onArticleCommentsClicked(articles[position].id)
        }

        holder.commentIcon.setOnClickListener {
            onNavigationChangedListener.onArticleCommentsClicked(articles[position].id)
        }

        holder.favouriteImg.visibility = View.VISIBLE
        holder.favouriteBorderImg.visibility = View.INVISIBLE

        holder.favouriteBorderImg.setOnClickListener {
            articles[position].isFavourite = true
            addArticleToDatabase(articles[position])
            holder.favouriteBorderImg.visibility = View.INVISIBLE
            holder.favouriteImg.visibility = View.VISIBLE
        }

        holder.favouriteImg.setOnClickListener {
            articles[position].isFavourite = false
            Toast.makeText(context, "Removed from favourites!", Toast.LENGTH_SHORT).show()
            deleteArticleFromDatabase(articles[position])
            holder.favouriteBorderImg.visibility = View.VISIBLE
            holder.favouriteImg.visibility = View.INVISIBLE
        }

        holder.shareIcon.setOnClickListener {
            val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBody = articles[position].url
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

            val url = articles[position].url
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
}


