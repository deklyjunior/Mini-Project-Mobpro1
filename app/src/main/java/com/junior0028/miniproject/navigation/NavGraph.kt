package com.junior0028.miniproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.junior0028.miniproject.ui.theme.screen.QuizScreen
import com.junior0028.miniproject.ui.theme.screen.MainScreen

@Composable
fun SetupNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            MainScreen(navController = navController)
        }
        composable(Screen.Quiz.route) {
            QuizScreen(navController = navController)
        }
    }
}
