package com.example.p2p_bluetooth_chat.presentation.home.viewmodels

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(): ViewModel() {

    private var _hasPermission = MutableStateFlow<Boolean>(false)
    val hasPermission = _hasPermission

    private var bluetoothManager: BluetoothManager? = null

    fun hasBluetoothPermission(context: Context) {
        bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager?.adapter

        if(bluetoothAdapter == null) {
            _hasPermission.value = false
        }

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            _hasPermission.value = context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED
        } else {
            _hasPermission.value = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun setBluetoothPermission(value: Boolean) {
        _hasPermission.value = value
    }

}