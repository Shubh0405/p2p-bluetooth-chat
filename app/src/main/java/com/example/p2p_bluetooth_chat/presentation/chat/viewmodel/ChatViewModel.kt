package com.example.p2p_bluetooth_chat.presentation.chat.viewmodel

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p2p_bluetooth_chat.bluetooth_utils.BleGattClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val application: Application
): ViewModel() {

    private val client = BleGattClient(application)

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun sendMessage(device: BluetoothDevice, message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            client.connect(device)
            delay(1000) // wait for discovery
            client.sendMessage(message)
            delay(500)
            client.disconnect()
        }
    }

}