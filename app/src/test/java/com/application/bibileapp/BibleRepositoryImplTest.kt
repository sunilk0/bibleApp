@file:OptIn(ExperimentalCoroutinesApi::class)

package com.application.bibileapp.data.repository

import com.application.bibileapp.data.model.BibleApiResponse
import com.application.bibileapp.data.network.BibleApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BibleRepositoryImplTest {

    private lateinit var apiService: BibleApiService
    private lateinit var repository: BibleRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        repository = BibleRepositoryImpl(apiService)
    }

    @Test
    fun `getVerses returns success when api call succeeds`() = runTest {
        // Given
        val fakeResponse = BibleApiResponse(
            reference = "john 3:16",
            verses = emptyList(),
            text = "For God so loved the world",
            translationID = "",
            translationame = "",
            translation_note = ""
        )
        coEvery { apiService.fetchBible("john3%3A16") } returns fakeResponse

        // When
        val result = repository.getVerses("john3:16")

        // Then
        assertTrue(result.isSuccess)
        assertEquals("john 3:16", result.getOrNull()?.reference)
        coVerify(exactly = 1) { apiService.fetchBible("john3%3A16") }
    }

    @Test
    fun `getVerses returns failure when api call throws exception`() = runTest {
        // Given
        coEvery { apiService.fetchBible("john3%3A16") } throws RuntimeException("Network error")

        // When
        val result = repository.getVerses("john3:16")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { apiService.fetchBible("john3%3A16") }
    }
}
