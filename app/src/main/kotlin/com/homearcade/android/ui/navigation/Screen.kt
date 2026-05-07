package com.homearcade.android.ui.navigation

sealed class Screen(val route: String) {
    data object Setup    : Screen("setup")
    data object Home     : Screen("home")
    data object Library  : Screen("library/{systemId}") {
        fun withSystem(systemId: String) = "library/$systemId"
    }
    data object Player   : Screen("player/{romId}") {
        fun withRom(romId: Int) = "player/$romId"
    }
    data object Settings : Screen("settings")
}
