package com.application.bibileapp.data.repository

import com.application.bibileapp.data.model.BibleApiResponse

interface BibleRepository {
    suspend fun getVerses(query:String): Result<BibleApiResponse>
}