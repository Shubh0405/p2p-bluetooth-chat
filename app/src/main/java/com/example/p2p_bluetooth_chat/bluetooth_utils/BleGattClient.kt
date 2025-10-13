package com.example.p2p_bluetooth_chat.bluetooth_utils

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothStatusCodes
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.p2p_bluetooth_chat.utils.BluetoothConstants

class BleGattClient(
    private val context: Context
) {

    private var bluetoothGatt: BluetoothGatt? = null
    private var targetCharacteristic: BluetoothGattCharacteristic? = null

    private val gattCallback = object : BluetoothGattCallback() {

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    // Connected to the GATT server
                    gatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    // Disconnected from the GATT server
                    bluetoothGatt?.close()
                    bluetoothGatt = null
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt.getService(BluetoothConstants.SERVICE_UUID.uuid)
                val characteristic = service?.getCharacteristic(BluetoothConstants.SERVICE_UUID.uuid)

                if (characteristic != null) {
                    targetCharacteristic = characteristic
                    Log.d("BleGattClient", "Chat characteristic found")
                } else {
                    Log.w("BleGattClient", "Chat characteristic not found")
                }
            } else {
                Log.e("BleGattClient", "Service discovery failed with status $status")
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BleGattClient", "Message sent successfully")
            } else {
                Log.e("BleGattClient", "Message send failed: $status")
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connect(device: BluetoothDevice) {
        disconnect() // ensure clean state
        Log.d("BleGattClient", "Connecting to ${device.address}")
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun sendMessage(text: String) {
        if (bluetoothGatt == null || targetCharacteristic == null) {
            Log.w("BleGattClient", "Cannot send message, not connected or characteristic not found")
            return
        }

        val data = text.toByteArray(Charsets.UTF_8)
        Log.d("BleGattClient", "Sending message: $text")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val result = bluetoothGatt?.writeCharacteristic(
                targetCharacteristic!!,
                data,
                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            )
            if (result == BluetoothStatusCodes.SUCCESS) {
                Log.d("BleGattClient", "Message sent successfully (API 33+)")
            } else {
                Log.e("BleGattClient", "Failed to send message, status=$result")
            }
        } else {
            targetCharacteristic?.value = data
            val success = bluetoothGatt?.writeCharacteristic(targetCharacteristic) ?: false
            if (success) {
                Log.d("BleGattClient", "Message sent successfully (legacy)")
            } else {
                Log.e("BleGattClient", "Message send failed (legacy)")
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        targetCharacteristic = null
    }

}