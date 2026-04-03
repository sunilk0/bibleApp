package com.application.bibileapp.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.bibileapp.data.model.BibleApiResponse
import com.application.bibileapp.data.repository.BibleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BibleViewModel @Inject constructor(
    private val bibleRepository: BibleRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(BibleViewState())
    val uiState = _uiState.asStateFlow()

    init {
        val savedQuery = savedStateHandle.get<String>("search_query") ?: "john 3:16"
        _uiState.update { it.copy(searchQuery = savedQuery) }

        val reference: String? = savedStateHandle["reference"]
        if (reference != null) {
            onIntent(BibleIntent.LoadChapter(reference))
        } else {
            onIntent(BibleIntent.SearchVerse(savedQuery))
        }
    }

    fun onIntent(intent: BibleIntent) {
        when (intent) {
            is BibleIntent.UpdateSearchQuery -> {
                _uiState.update { it.copy(searchQuery = intent.query) }
                savedStateHandle["search_query"] = intent.query
            }
            is BibleIntent.SearchVerse -> {
                fetchVerses(intent.query)
            }
            is BibleIntent.LoadChapter -> {
                val chapterQuery = intent.reference.substringBeforeLast(":")
                fetchVerses(chapterQuery)
            }
        }
    }

    private fun fetchVerses(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(dataState = DataState.Loading) }
            val result = bibleRepository.getVerses(query)
            result.onSuccess { data ->
                _uiState.update { it.copy(dataState = DataState.Success(data)) }
            }.onFailure { error ->
                _uiState.update { it.copy(dataState = DataState.Failure(error.message ?: "Unknown error")) }
            }
        }
    }
}

// MVI State
data class BibleViewState(
    val searchQuery: String = "",
    val dataState: DataState = DataState.Loading
)

sealed interface DataState {
    object Loading : DataState
    data class Success(val apiResponse: BibleApiResponse?) : DataState
    data class Failure(val message: String) : DataState
}

// MVI Intents
sealed class BibleIntent {
    data class UpdateSearchQuery(val query: String) : BibleIntent()
    data class SearchVerse(val query: String) : BibleIntent()
    data class LoadChapter(val reference: String) : BibleIntent()
}
