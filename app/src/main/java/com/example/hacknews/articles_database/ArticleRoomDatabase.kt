package com.example.hacknews.articles_database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [FavouriteArticle::class], version = 1)
abstract class ArticleRoomDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao

    //ProductRoomDatabase is singleton to prevent having multiple instances of the database opened at the same time.
    companion object {
        @Volatile
        private var INSTANCE: ArticleRoomDatabase? = null

        fun getDatabase(
            context: Context
        ): ArticleRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                // Create database here
                //create a RoomDatabase object in the application context from the ProductRoomDatabase class
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArticleRoomDatabase::class.java,
                    "Article_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

