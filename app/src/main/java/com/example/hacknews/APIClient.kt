package com.example.hacknews

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


private const val base_URL = "https://hacker-news.firebaseio.com/"

class ApiClient {
    private var retrofit: Retrofit? = null

    fun getClient(): Retrofit {
//        val interceptor = HttpLoggingInterceptor()
//        interceptor.level = HttpLoggingInterceptor.Level.BODY

        if (retrofit == null) {
            val ok = OkHttpClient.Builder()
                //.addInterceptor(interceptor)
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

    private fun getRetrofitWithRxJava(): Retrofit {
        if (retrofit == null) {
            val ok = OkHttpClient.Builder()
                //.addInterceptor(interceptor)
                .build()
            retrofit = Retrofit.Builder()
                .baseUrl(base_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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

    fun getApiServiceWithRx(): ApiInterface {
        val interface1 =  getRetrofitWithRxJava().create(ApiInterface::class.java)
        return interface1
    }
}