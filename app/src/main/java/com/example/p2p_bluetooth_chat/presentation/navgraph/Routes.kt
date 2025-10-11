package com.example.p2p_bluetooth_chat.presentation.navgraph

sealed class Routes(
    val route: String
) {
    data object HomeScreen : Routes(route = "home_screen")
    data object ChatScreen : Routes(route = "chat_screen")
}