package com.application.bibileapp.data.network

import com.application.bibileapp.data.model.BibleApiResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface BibleApiService {
    @GET
    suspend fun fetchBible(@Url query:String): BibleApiResponse
}