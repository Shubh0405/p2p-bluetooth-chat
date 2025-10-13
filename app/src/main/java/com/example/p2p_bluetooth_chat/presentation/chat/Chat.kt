package com.example.p2p_bluetooth_chat.presentation.chat

import android.Manifest
import android.annotation.SuppressLint
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.p2p_bluetooth_chat.bluetooth_utils.BleDevice
import com.example.p2p_bluetooth_chat.presentation.chat.viewmodel.ChatViewModel

@SuppressLint("MissingPermission")
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    bleDevice: BleDevice
) {

    val chatViewModel: ChatViewModel = hiltViewModel()

    var message by remember { mutableStateOf("") }

    Column {
        Text(
            text = "Hello Chat Screen! Connected to ${bleDevice.name}",
            modifier = modifier
        )
        Spacer(modifier = Modifier.height(5.dp))
        OutlinedTextField(
            value = message,
            onValueChange = {
                message = it
            },
        )
        Spacer(modifier = Modifier.height(5.dp))
        Button(
            onClick = { chatViewModel.sendMessage(bleDevice.bluetoothDevice, message) }
        ) {
            Text(
                text = "Send Message"
            )
        }
    }
}