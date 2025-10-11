package com.example.p2p_bluetooth_chat.presentation.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.p2p_bluetooth_chat.presentation.home.viewmodels.HomePageViewModel

@Composable
fun HomePage(
    modifier: Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val homeViewModel: HomePageViewModel = hiltViewModel()
    val hasBluetoothPermission by homeViewModel.hasPermission.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        homeViewModel.setBluetoothPermission(permissions.all { it.value })
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver {
            _, event ->
            when (event) {
                androidx.lifecycle.Lifecycle.Event.ON_RESUME -> {
                    homeViewModel.hasBluetoothPermission(context)
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        homeViewModel.hasBluetoothPermission(context)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if(!hasBluetoothPermission) {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            Text(
                text = "We need Bluetooth Permissions to proceed",
            )
            Spacer(modifier = Modifier.height(5.dp))
            Button(
                onClick = {
                    if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                        launcher.launch(arrayOf(
                            android.Manifest.permission.BLUETOOTH_SCAN,
                            android.Manifest.permission.BLUETOOTH_ADVERTISE,
                            android.Manifest.permission.BLUETOOTH_CONNECT
                        ))
                    } else {
                        launcher.launch(arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ))
                    }
                }
            ) {
                Text(
                    text = "Grant Permissions"
                )
            }
        }
    } else {
        Text(
            text = "Hello Home Page!",
            modifier = modifier
        )
    }

}