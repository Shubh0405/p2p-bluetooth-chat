package com.example.p2p_bluetooth_chat.utils

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class BleDevice(
    val name: String?,
    val address: String,
    val rssi: Int
)

class BleScanner(context: Context) {

    private val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val adapter: BluetoothAdapter? = bluetoothManager?.adapter
    private val scanner = adapter?.bluetoothLeScanner


    private val _devices = MutableSharedFlow<List<BleDevice>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val devices = _devices.asSharedFlow()

    private val discoveredDevices = mutableMapOf<String, BleDevice>()

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun handleResult(result: ScanResult) {
        val device = result.device
        val record = result.scanRecord ?: return
        val uuids = record.serviceUuids ?: return

        // only include devices advertising our service UUID
        if (uuids.contains(BluetoothConstants.SERVICE_UUID)) {
            val bleDevice = BleDevice(
                name = device.name ?: "Unknown Device",
                address = device.address,
                rssi = result.rssi
            )
            discoveredDevices[device.address] = bleDevice
            CoroutineScope(Dispatchers.IO).launch {
                _devices.emit(discoveredDevices.values.toList())
            }
        }
    }

    private val scanCallback = ScanCallbackImpl(::handleResult)

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScanning() {
        if (scanner == null) {
            Log.w("BleScanner", "Bluetooth LE Scanner not available")
            return
        }

        val filter = ScanFilter.Builder()
            .setServiceUuid(BluetoothConstants.SERVICE_UUID)
            .build()

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanner.startScan(listOf(filter), settings, scanCallback)
        Log.d("BleScanner", "Started scanning")
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScanning() {
        try {
            scanner?.stopScan(scanCallback)
            Log.d("BleScanner", "Scan stopped")
        } catch (t: Throwable) {
            Log.e("BleScanner", "Error stopping scan: ${t.message}")
        }
    }

}

class ScanCallbackImpl(
    val handleResult: (ScanResult) -> Unit
) : ScanCallback() {
    override fun onScanResult(callbackType: Int, result: ScanResult) {
        handleResult(result)
    }

    override fun onBatchScanResults(results: MutableList<ScanResult>) {
        results.forEach { handleResult(it) }
    }

    override fun onScanFailed(errorCode: Int) {
        Log.e("BleScanner", "Scan failed: $errorCode")
    }
}