package com.application.bibileapp.data.repository

import com.application.bibileapp.data.model.BibleApiResponse
import com.application.bibileapp.data.network.BibleApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class BibleRepositoryImpl @Inject constructor(private val api: BibleApiService) : BibleRepository {
    override suspend fun getVerses(query: String): Result<BibleApiResponse> {
        return try {
            //encode query
            val encodedQuery = withContext(Dispatchers.IO) { //use this as a dependency later.
                URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
            }
            val result = api.fetchBible(query = encodedQuery)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}