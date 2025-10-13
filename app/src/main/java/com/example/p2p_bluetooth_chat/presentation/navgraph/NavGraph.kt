package com.example.p2p_bluetooth_chat.presentation.navgraph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.p2p_bluetooth_chat.bluetooth_utils.BleDevice
import com.example.p2p_bluetooth_chat.presentation.chat.ChatScreen
import com.example.p2p_bluetooth_chat.presentation.home.HomePage

@Composable
fun NavGraph(
    modifier: Modifier = Modifier
) {

    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HomeScreen.route) {
        composable(
            route = Routes.HomeScreen.route
        ) {
            HomePage(
                modifier = modifier
            ) { bleDevice ->
                navigateToChatScreen(navController, bleDevice)
            }
        }

        composable(
            route = Routes.ChatScreen.route
        ) {
            navController.previousBackStackEntry?.savedStateHandle?.get<BleDevice>("bleDevice")?.let { bleDevice ->
                // You can use the bleDevice object here if needed
                ChatScreen(
                    modifier = modifier,
                    bleDevice = bleDevice
                )
            }
        }
    }

}

fun navigateToChatScreen(navController: NavHostController, bleDevice: BleDevice) {
    navController.currentBackStackEntry?.savedStateHandle?.set("bleDevice", bleDevice)
    navController.navigate(Routes.ChatScreen.route)
}