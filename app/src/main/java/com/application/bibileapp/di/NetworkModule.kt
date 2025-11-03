package com.application.bibileapp.di

import com.application.bibileapp.data.network.BibleApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule{
    const val BASE_URL = "https://bible-api.com/"
    val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient) : Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideBibleApi(retrofit: Retrofit) = retrofit.create(BibleApiService::class.java)

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val headerInterceptor = Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("X-Single-Chapter-Book-Matching", "indifferent")
                .build()
            chain.proceed(newRequest)
        }
        return OkHttpClient.Builder().addInterceptor(logging).addInterceptor(headerInterceptor).build()
    }

}