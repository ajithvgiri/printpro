package com.donkeydevelopers.printpulse.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.donkeydevelopers.printpulse.thermal.OnBluetoothPermissionsGranted

object BluetoothUtils {

    fun checkBluetoothPermissions(context: Context, onBluetoothPermissionsGranted: OnBluetoothPermissionsGranted) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            onBluetoothPermissionsGranted.onRequestBluetoothPermission()
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            onBluetoothPermissionsGranted.onRequestBluetoothAdminPermission()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            onBluetoothPermissionsGranted.onRequestBluetoothConnectPermission()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            onBluetoothPermissionsGranted.onRequestBluetoothScanPermission()
        } else {
            onBluetoothPermissionsGranted.onPermissionsGranted()
        }
    }
}