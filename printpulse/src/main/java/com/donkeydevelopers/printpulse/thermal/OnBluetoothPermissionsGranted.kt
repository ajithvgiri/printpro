package com.donkeydevelopers.printpulse.thermal

interface OnBluetoothPermissionsGranted {
    fun onPermissionsGranted()
    fun onRequestBluetoothPermission()
    fun onRequestBluetoothAdminPermission()
    fun onRequestBluetoothConnectPermission()
    fun onRequestBluetoothScanPermission()
}