package com.application.bibileapp.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.application.bibileapp.ui.screen.BibleDetailScreen
import com.application.bibileapp.ui.screen.BibleScreen
import com.application.bibileapp.ui.screen.SplashScreen
import com.application.bibileapp.ui.viewmodel.BibleViewModel

@Composable
fun BibleNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController = navController)
        }

        composable("home") {
            val viewModel: BibleViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
            BibleScreen(bibleViewModel = viewModel, navController = navController)
        }

        composable(
            route = "detail/{reference}",
            arguments = listOf(navArgument("reference") { type = NavType.StringType })
        ) { backStackEntry ->
            val reference = backStackEntry.arguments?.getString("reference") ?: ""
            val viewModel: BibleViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
            BibleDetailScreen(reference = reference, bibleViewModel = viewModel, navController = navController)
        }

        composable("second screen") {

        }
    }
}
