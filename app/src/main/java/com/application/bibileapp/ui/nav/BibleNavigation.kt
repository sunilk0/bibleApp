package com.application.bibileapp.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.application.bibileapp.ui.screen.BibleScreen
import com.application.bibileapp.ui.viewmodel.BibleViewModel

@Composable
fun BibleNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "home") {
        composable("home") {
            val viewModel: BibleViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
       BibleScreen(bibleViewModel = viewModel,navController = navController)

        }

        composable("second screen") {

        }
    }



}