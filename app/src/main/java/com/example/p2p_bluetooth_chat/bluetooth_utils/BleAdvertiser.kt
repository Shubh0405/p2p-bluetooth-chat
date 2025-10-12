package com.example.p2p_bluetooth_chat.bluetooth_utils

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.p2p_bluetooth_chat.utils.BluetoothConstants

class BleAdvertiser(
    context: Context
) {

    private val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val adapter: BluetoothAdapter? = bluetoothManager?.adapter
    private val advertiser: BluetoothLeAdvertiser? = adapter?.bluetoothLeAdvertiser

    fun startAdvertising() {
        if (advertiser == null) {
            Log.w("BleAdvertiser", "Device does not support BLE Advertising")
            return
        }

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(false) // Not required since we're not accepting GATT connections
            .build()

        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .addServiceUuid(BluetoothConstants.SERVICE_UUID)
            .build()

        advertiser.startAdvertising(settings, data, AdvertiseCallbackImpl)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    fun stopAdvertising() {
        advertiser?.stopAdvertising(AdvertiseCallbackImpl)
        Log.d("BleAdvertiser", "Advertising stopped")
    }
}

object AdvertiseCallbackImpl : AdvertiseCallback() {
    override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
        Log.d("BleAdvertiser", "Advertising started successfully")
    }

    override fun onStartFailure(errorCode: Int) {
        Log.e("BleAdvertiser", "Advertising failed: $errorCode")
    }
}