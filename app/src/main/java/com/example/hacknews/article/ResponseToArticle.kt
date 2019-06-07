package com.example.hacknews.article

fun ArticleResponse.toArticle(): Article {
    val id = id ?: -1
    val title = title.toString()
    val author = by.toString()
    val score = score.toString()
    val descendants = descendants.toString()
    val type = type.toString()
    val unixTime = time.toString()

    val simpleDataFormat = java.text.SimpleDateFormat("yy-MM-dd HH:mm")
    val date = java.util.Date(unixTime.toLong() * 1000)
    val time = simpleDataFormat.format(date)

    val url = url.toString()

    return Article(title, url, author, score, descendants, type, time, id)
}