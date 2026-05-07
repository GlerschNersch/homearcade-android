package com.homearcade.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.homearcade.android.data.local.AppPreferences
import com.homearcade.android.ui.screens.home.HomeScreen
import com.homearcade.android.ui.screens.library.LibraryScreen
import com.homearcade.android.ui.screens.player.PlayerScreen
import com.homearcade.android.ui.screens.settings.SettingsScreen
import com.homearcade.android.ui.screens.setup.SetupScreen
import javax.inject.Inject

@Composable
fun HomeArcadeNavGraph(prefs: AppPreferences = hiltViewModel<NavViewModel>().prefs) {
    val navController = rememberNavController()
    val isConfigured by prefs.isConfigured.collectAsState(initial = false)
    val start = if (isConfigured) Screen.Home.route else Screen.Setup.route

    NavHost(navController = navController, startDestination = start) {
        composable(Screen.Setup.route) {
            SetupScreen(onComplete = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Setup.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onSystemClick  = { systemId -> navController.navigate(Screen.Library.withSystem(systemId)) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
            )
        }
        composable(
            route = Screen.Library.route,
            arguments = listOf(navArgument("systemId") { type = NavType.StringType }),
        ) { back ->
            LibraryScreen(
                systemId    = back.arguments?.getString("systemId") ?: "",
                onRomClick  = { romId -> navController.navigate(Screen.Player.withRom(romId)) },
                onBack      = { navController.popBackStack() },
            )
        }
        composable(
            route = Screen.Player.route,
            arguments = listOf(navArgument("romId") { type = NavType.IntType }),
        ) { back ->
            PlayerScreen(
                romId  = back.arguments?.getInt("romId") ?: 0,
                onBack = { navController.popBackStack() },
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
