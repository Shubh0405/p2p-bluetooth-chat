package com.example.p2p_bluetooth_chat.presentation.home.viewmodels

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p2p_bluetooth_chat.utils.BleDevice
import com.example.p2p_bluetooth_chat.utils.BleScanner
import com.example.p2p_bluetooth_chat.utils.enums.BleScanProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {

    private var _hasPermission = MutableStateFlow(false)
    val hasPermission = _hasPermission

    private var bluetoothManager: BluetoothManager? =
        application.getSystemService(BluetoothManager::class.java)

    private val scanner = BleScanner(application)

    private val _bleScanState = MutableStateFlow<BleScanState>(
        BleScanState(
            devices = emptyList(),
            progress = BleScanProgress.NOT_STARTED
        )
    )
    val bleScanState: StateFlow<BleScanState> = _bleScanState

    private var autoStopJob: Job? = null
    private var deviceScannerJob: Job? = null

    fun hasBluetoothPermission() {
        val bluetoothAdapter = bluetoothManager?.adapter

        if (bluetoothAdapter == null) {
            _hasPermission.value = false
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            _hasPermission.value =
                application.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                        application.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                        application.checkSelfPermission(Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED
        } else {
            _hasPermission.value =
                application.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScanning() {

        _bleScanState.value = _bleScanState.value.copy(
            progress = BleScanProgress.IN_PROGRESS
        )

        autoStopJob?.cancel()
        deviceScannerJob?.cancel()

        deviceScannerJob = viewModelScope.launch {
            scanner.devices.collect {
                _bleScanState.value = _bleScanState.value.copy(
                    devices = it
                )
            }
        }

        scanner.startScanning()

        autoStopJob = viewModelScope.launch {
            delay(60_000)
            stopScanning()
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScanning() {
        autoStopJob?.cancel()
        deviceScannerJob?.cancel()
        scanner.stopScanning()

        _bleScanState.value = _bleScanState.value.copy(
            progress = BleScanProgress.COMPLETED
        )
    }

    fun setBluetoothPermission(value: Boolean) {
        _hasPermission.value = value
    }

}

data class BleScanState(
    val devices: List<BleDevice>,
    val progress: BleScanProgress
)