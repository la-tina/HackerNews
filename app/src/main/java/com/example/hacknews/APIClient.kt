package com.example.hacknews

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ApiClient {

    private const val base_URL = "https://hacker-news.firebaseio.com/"

    private var retrofit: Retrofit? = null

    fun getClient(): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        if (retrofit == null) {
            val ok = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()
            retrofit = Retrofit.Builder()
                .baseUrl(base_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    ok.newBuilder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(
                        30,
                        TimeUnit.SECONDS
                    ).writeTimeout(30, TimeUnit.SECONDS).build()
                )
                .build()
        }

        return retrofit!!
    }
}