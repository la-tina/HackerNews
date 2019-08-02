package com.example.hacknews.articles_database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import java.io.Serializable

@Entity(tableName = "FavouriteArticle")
data class FavouriteArticle(
    @ColumnInfo(name = "Title") val title: String,
    @ColumnInfo(name = "Url") var url: String,
    @ColumnInfo(name = "Author") var author: String,
    @ColumnInfo(name = "Score") var score: String,
    @ColumnInfo(name = "Descendants") var descendants: String,
    @ColumnInfo(name = "Type") var type: String,
    @ColumnInfo(name = "Time") var time: String,
    @ColumnInfo(name = "IsFavourite") var isFavourite: Boolean,
    @PrimaryKey
    @ColumnInfo(name = "Id") var id: Int
) : Serializable

@Dao
interface ArticleDao {

    @Insert
    fun insert(article: FavouriteArticle)

    @Query("DELETE FROM FavouriteArticle")
    fun deleteAll()

    @Delete
    fun deleteArticle(article: FavouriteArticle)

    @Query("SELECT * FROM FavouriteArticle ORDER BY id DESC")
    fun getAllFavouriteArticles(): LiveData<List<FavouriteArticle>>
}
