package com.gerasimov_mobile_dev.techstore.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gerasimov_mobile_dev.techstore.TechStoreViewModel
import com.gerasimov_mobile_dev.techstore.ui.MainScreen
import kotlinx.coroutines.CoroutineScope
sealed class Screen(val route: String) {
    object Main : Screen("main")
}
@Composable
fun TechStoreNavGraph(
    navController: NavHostController,
    viewModelFactory: (String) -> TechStoreViewModel,
    scope: CoroutineScope
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
//            val viewModel = viewModelFactory("https://gerasimov-sa.github.io/techstore-web/")
            val viewModel = viewModelFactory("https://hop.uz/")
            MainScreen(viewModel = viewModel)

        }
    }
}