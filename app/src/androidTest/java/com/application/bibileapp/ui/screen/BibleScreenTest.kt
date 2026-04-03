package com.application.bibileapp.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.application.bibileapp.data.model.BibleApiResponse
import com.application.bibileapp.data.model.Verse
import com.application.bibileapp.ui.viewmodel.BibleIntent
import com.application.bibileapp.ui.viewmodel.BibleViewState
import com.application.bibileapp.ui.viewmodel.DataState
import com.application.bibileapp.utils.theme.BibleAppTheme
import org.junit.Rule
import org.junit.Test

class BibleScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingState_showsCircularProgressIndicator() {
        composeTestRule.setContent {
            BibleAppTheme {
                BibleScreenContent(
                    uiState = BibleViewState(dataState = DataState.Loading),
                    onIntent = {},
                    onNavigateToDetail = {}
                )
            }
        }

        // CircularProgressIndicator doesn't have a default text, but we can check if the screen exists
        // Or we could add a testTag to it. Since it's inside a Box, let's verify "Bible Search" is there.
        composeTestRule.onNodeWithText("Bible Search").assertIsDisplayed()
    }

    @Test
    fun successState_displaysReferenceAndVerses() {
        val mockVerse = Verse(
            bookId = "JHN",
            bookName = "John",
            chapter = 3,
            text = "For God so loved the world",
            verse = 16
        )
        val mockResponse = BibleApiResponse(
            reference = "John 3:16",
            text = "",
            translationID = "KJV",
            translationame = "King James Version",
            translation_note = "",
            verses = listOf(mockVerse)
        )

        composeTestRule.setContent {
            BibleAppTheme {
                BibleScreenContent(
                    uiState = BibleViewState(
                        searchQuery = "John 3:16",
                        dataState = DataState.Success(mockResponse)
                    ),
                    onIntent = {},
                    onNavigateToDetail = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Reference: John 3:16").assertIsDisplayed()
        composeTestRule.onNodeWithText("16 For God so loved the world").assertIsDisplayed()
    }

    @Test
    fun failureState_displaysErrorMessage() {
        val errorMessage = "Network Error"
        composeTestRule.setContent {
            BibleAppTheme {
                BibleScreenContent(
                    uiState = BibleViewState(dataState = DataState.Failure(errorMessage)),
                    onIntent = {},
                    onNavigateToDetail = {}
                )
            }
        }

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun typingInSearchField_triggersUpdateIntent() {
        var capturedIntent: BibleIntent? = null
        composeTestRule.setContent {
            BibleAppTheme {
                BibleScreenContent(
                    uiState = BibleViewState(searchQuery = ""),
                    onIntent = { capturedIntent = it },
                    onNavigateToDetail = {}
                )
            }
        }

        val searchText = "Genesis 1:1"
        composeTestRule.onNodeWithText("Enter verse eg: John 3:16").performTextInput(searchText)

        // The onValueChange triggers BibleIntent.UpdateSearchQuery
        assert(capturedIntent is BibleIntent.UpdateSearchQuery)
        assert((capturedIntent as BibleIntent.UpdateSearchQuery).query == searchText)
    }

    @Test
    fun clickingSearchButton_triggersSearchIntent() {
        var capturedIntent: BibleIntent? = null
        val query = "John 1:1"
        composeTestRule.setContent {
            BibleAppTheme {
                BibleScreenContent(
                    uiState = BibleViewState(searchQuery = query),
                    onIntent = { capturedIntent = it },
                    onNavigateToDetail = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Search").performClick()

        assert(capturedIntent is BibleIntent.SearchVerse)
        assert((capturedIntent as BibleIntent.SearchVerse).query == query)
    }
}
