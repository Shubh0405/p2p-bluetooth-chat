package com.example.p2p_bluetooth_chat.presentation.home

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.p2p_bluetooth_chat.presentation.home.viewmodels.HomePageViewModel
import com.example.p2p_bluetooth_chat.utils.enums.BleScanProgress

@SuppressLint("MissingPermission")
@Composable
fun HomePage(
    modifier: Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val homeViewModel: HomePageViewModel = hiltViewModel()
    val hasBluetoothPermission by homeViewModel.hasPermission.collectAsState()
    val bleScanState by homeViewModel.bleScanState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        homeViewModel.setBluetoothPermission(permissions.all { it.value })
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver {
            _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    homeViewModel.hasBluetoothPermission()
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        homeViewModel.hasBluetoothPermission()

        onDispose {
            homeViewModel.stopBleServices()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(hasBluetoothPermission) {
        if(hasBluetoothPermission) {
            homeViewModel.startBleServices()
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
                            android.Manifest.permission.BLUETOOTH_CONNECT,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
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
        Column(
            modifier = modifier.fillMaxSize()
        ) {
           if (bleScanState.progress == BleScanProgress.IN_PROGRESS) {
               Text(
                   text = "Scanning for devices...",
               )
           } else if (bleScanState.progress == BleScanProgress.COMPLETED) {
               Text(
                   text = "Scan completed. Found ${bleScanState.devices.size} device(s).",
               )
           }

            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn {
                items(count = bleScanState.devices.size) { index ->
                    Text(
                        text = "${bleScanState.devices[index].name} - ${bleScanState.devices[index].address}"
                    )
                }
            }

            if (bleScanState.progress == BleScanProgress.COMPLETED) {
                if (bleScanState.devices.isEmpty()) {
                    Text(
                        text = "No devices found. Try again.",
                    )
                }

                Button(
                    onClick = {
                        homeViewModel.startBleServices()
                    }
                ) {
                    Text(
                        text = "Scan Again"
                    )
                }
            }
        }
    }
}