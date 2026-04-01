package com.application.bibileapp.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.bibileapp.data.model.BibleApiResponse
import com.application.bibileapp.data.repository.BibleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BibleViewModel @Inject constructor(
    private val bibleRepository: BibleRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<BibleUIState>(BibleUIState.Loading)
    val state = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow(savedStateHandle.get<String>("search_query") ?: "john 3:16")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        val reference: String? = savedStateHandle["reference"]
        if (reference != null) {
            // If we have a reference, fetch the chapter
            val chapterQuery = reference.substringBeforeLast(":")
            fetchVerses(chapterQuery)
        } else {
            // Initial fetch for home screen using the saved or default query
            fetchVerses(_searchQuery.value)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        savedStateHandle["search_query"] = query
    }

    fun fetchVerses(query: String) {
        viewModelScope.launch {
            _state.value = BibleUIState.Loading
            val result = bibleRepository.getVerses(query)
            when {
                result.isSuccess -> {
                    val data = result.getOrNull()
                    _state.value = BibleUIState.Success(data)
                }
                result.isFailure -> {
                    val error = result.exceptionOrNull()
                    _state.value = BibleUIState.Failure(error?.message ?: "Unknown error")
                }
            }
        }
    }
}

sealed interface BibleUIState {
    object Loading : BibleUIState
    data class Success(val apiResponse: BibleApiResponse?) : BibleUIState
    data class Failure(val message: String) : BibleUIState
}
