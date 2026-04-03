@file:OptIn(ExperimentalCoroutinesApi::class)

package com.application.bibileapp.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.application.bibileapp.data.model.BibleApiResponse
import com.application.bibileapp.data.repository.BibleRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BibleViewModelTest {

    private lateinit var repository: BibleRepository
    private lateinit var viewModel: BibleViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init with no reference triggers default search john 3 16`() = runTest {
        // Given
        val savedStateHandle = SavedStateHandle()
        val fakeResponse = BibleApiResponse(
            reference = "John 3:16",
            verses = emptyList(),
            text = "For God so loved the world",
            translationID = "KJV",
            translationame = "King James Version",
            translation_note = ""
        )
        coEvery { repository.getVerses("john 3:16") } returns Result.success(fakeResponse)

        // When
        viewModel = BibleViewModel(repository, savedStateHandle)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("john 3:16", state.searchQuery)
        assertTrue(state.dataState is DataState.Success)
        assertEquals("John 3:16", (state.dataState as DataState.Success).apiResponse?.reference)
    }

    @Test
    fun `init with reference triggers chapter load (LoadChapter intent)`() = runTest {
        // Given
        val savedStateHandle = SavedStateHandle(mapOf("reference" to "John 3:16"))
        val fakeResponse = BibleApiResponse(
            reference = "John 3",
            verses = emptyList(),
            text = "",
            translationID = "KJV",
            translationame = "King James Version",
            translation_note = ""
        )
        // ViewModel init calls LoadChapter which calls fetchVerses with "John 3"
        coEvery { repository.getVerses("John 3") } returns Result.success(fakeResponse)

        // When
        viewModel = BibleViewModel(repository, savedStateHandle)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.dataState is DataState.Success)
        assertEquals("John 3", (state.dataState as DataState.Success).apiResponse?.reference)
    }

    @Test
    fun `UpdateSearchQuery intent updates state and SavedStateHandle`() = runTest {
        // Given
        val savedStateHandle = SavedStateHandle()
        coEvery { repository.getVerses(any()) } returns Result.success(mockk(relaxed = true))
        viewModel = BibleViewModel(repository, savedStateHandle)

        // When
        viewModel.onIntent(BibleIntent.UpdateSearchQuery("Psalm 23"))

        // Then
        assertEquals("Psalm 23", viewModel.uiState.value.searchQuery)
        assertEquals("Psalm 23", savedStateHandle.get<String>("search_query"))
    }

    @Test
    fun `SearchVerse intent triggers loading and success states`() = runTest {
        // Given
        val savedStateHandle = SavedStateHandle()
        val fakeResponse = BibleApiResponse("Genesis 1:1", "", "", "", "", verses = emptyList())
        coEvery { repository.getVerses(any()) } returns Result.success(mockk(relaxed = true)) // for init
        viewModel = BibleViewModel(repository, savedStateHandle)
        
        coEvery { repository.getVerses("Genesis 1:1") } returns Result.success(fakeResponse)

        // When
        viewModel.onIntent(BibleIntent.SearchVerse("Genesis 1:1"))
        
        // Check loading state (might need to advance time carefully if testing immediate loading)
        // Note: fetchVerses updates to Loading immediately.
        
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.dataState is DataState.Success)
        assertEquals("Genesis 1:1", (state.dataState as DataState.Success).apiResponse?.reference)
    }

    @Test
    fun `SearchVerse intent handles repository failure`() = runTest {
        // Given
        val savedStateHandle = SavedStateHandle()
        coEvery { repository.getVerses(any()) } returns Result.success(mockk(relaxed = true)) // for init
        viewModel = BibleViewModel(repository, savedStateHandle)
        
        val errorMessage = "Network Error"
        coEvery { repository.getVerses("error") } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.onIntent(BibleIntent.SearchVerse("error"))
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.dataState is DataState.Failure)
        assertEquals(errorMessage, (state.dataState as DataState.Failure).message)
    }
}
