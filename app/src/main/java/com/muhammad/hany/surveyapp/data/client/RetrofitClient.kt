package com.muhammad.hany.surveyapp.data.client

import com.muhammad.hany.surveyapp.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient(private val baseUrl: String = BuildConfig.BASE_URL){
    fun buildRetrofitInstance(moshi: Moshi): Retrofit {
        val logging = httpLoggingInterceptor()
        val httpClient = okhttpBuilder(logging)
        return Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(
                MoshiConverterFactory.create(moshi)
            ).client(httpClient.build())
            .build()
    }

    fun buildMoshiInstance(): Moshi {
        return Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

    private fun okhttpBuilder(logging: HttpLoggingInterceptor): OkHttpClient.Builder {
        return OkHttpClient
            .Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(logging)
    }

}