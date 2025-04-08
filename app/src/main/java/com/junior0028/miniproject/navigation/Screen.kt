package com.junior0028.miniproject.navigation

sealed class Screen(val route: String) {
    data object Home: Screen("mainScreen")
    data object Quiz: Screen("quizScreen")
}