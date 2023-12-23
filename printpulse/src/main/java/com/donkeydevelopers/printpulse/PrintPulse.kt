package com.donkeydevelopers.printpulse

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Parcelable
import android.util.DisplayMetrics
import android.util.Log
import androidx.core.app.ActivityCompat
import com.donkeydevelopers.printpulse.thermal.OnBluetoothPermissionsGranted
import com.donkeydevelopers.printpulse.thermal.async.AsyncBluetoothEscPosPrint
import com.donkeydevelopers.printpulse.thermal.async.AsyncEscPosPrint
import com.donkeydevelopers.printpulse.thermal.async.AsyncEscPosPrinter
import com.donkeydevelopers.printpulse.thermal.async.AsyncUsbEscPosPrint
import com.donkeydevelopers.printpulse.thermal.sdk.connection.DeviceConnection
import com.donkeydevelopers.printpulse.thermal.sdk.connection.bluetooth.BluetoothConnection
import com.donkeydevelopers.printpulse.thermal.sdk.connection.bluetooth.BluetoothPrintersConnections
import com.donkeydevelopers.printpulse.thermal.sdk.connection.usb.UsbConnection
import com.donkeydevelopers.printpulse.thermal.sdk.connection.usb.UsbPrintersConnections
import com.donkeydevelopers.printpulse.thermal.sdk.textparser.PrinterTextParserImg
import com.donkeydevelopers.printpulse.utils.BluetoothUtils
import java.text.SimpleDateFormat
import java.util.Date

class PrintPulse(var context: Context) {
    companion object {
        private const val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    }

    /*==============================================================================================
    ======================================BLUETOOTH PART============================================
    ==============================================================================================*/

    fun printBluetooth() {
        Log.d("TAG","printBluetooth")
        BluetoothUtils.checkBluetoothPermissions(context,object : OnBluetoothPermissionsGranted {
            override fun onPermissionsGranted() {
                Log.d("TAG","onPermissionsGranted")
                val selectedDevice = getBluetoothDevice()
                selectedDevice?.let {
                    AsyncBluetoothEscPosPrint(
                        context,
                        object : AsyncEscPosPrint.OnPrintFinished() {
                            override fun onError(asyncEscPosPrinter: AsyncEscPosPrinter?, codeException: Int) {
                                Log.e("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : An error occurred !")
                            }

                            override fun onSuccess(asyncEscPosPrinter: AsyncEscPosPrinter?) {
                                Log.i("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : Print is finished !")
                            }
                        }
                    ).execute(getAsyncEscPosPrinter(selectedDevice))
                }
            }

            override fun onRequestBluetoothPermission() {
                // Request for permission
            }

            override fun onRequestBluetoothAdminPermission() {
                // Request for permission
            }

            override fun onRequestBluetoothConnectPermission() {
                // Request for permission
            }

            override fun onRequestBluetoothScanPermission() {
                // Request for permission
            }

        })
    }

    fun getBluetoothDevice():BluetoothConnection? {
        val bluetoothDevicesList: Array<BluetoothConnection>? = BluetoothPrintersConnections().list
        Log.d("TAG","getBluetoothDevice ${bluetoothDevicesList?.size}")
        bluetoothDevicesList?.let {
            val items = arrayOfNulls<String>(bluetoothDevicesList.size + 1)
            items[0] = "Default printer"
            var i = 0
            bluetoothDevicesList.forEach {device->
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    // Auto select the Thermal Printer
                    Log.d("TAG","printer name = ${device.device.name}")
                    if (device.device.name.equals("BlueTooth Printer")) {
                        return device
                    }
                }
            }

        }
        return null
    }

    /*==============================================================================================
    ===========================================USB PART=============================================
    ==============================================================================================*/

    private val usbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager?
                    val usbDevice = intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbManager != null && usbDevice != null) {
                            AsyncUsbEscPosPrint(
                                context,
                                object : AsyncEscPosPrint.OnPrintFinished() {
                                    override fun onError(asyncEscPosPrinter: AsyncEscPosPrinter?, codeException: Int) {
                                        Log.e("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : An error occurred !")
                                    }

                                    override fun onSuccess(asyncEscPosPrinter: AsyncEscPosPrinter?) {
                                        Log.i("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : Print is finished !")
                                    }
                                }
                            ).execute(getAsyncEscPosPrinter(UsbConnection(usbManager, usbDevice)))
                        }
                    }
                }
            }
        }
    }

    fun printUsb() {
        val usbConnection: UsbConnection? = UsbPrintersConnections.selectFirstConnected(context)
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager?
        if (usbConnection == null || usbManager == null) {
            AlertDialog.Builder(context)
                .setTitle("USB Connection")
                .setMessage("No USB printer found.")
                .show()
            return
        }
        val permissionIntent = PendingIntent.getBroadcast(context, 0, Intent(ACTION_USB_PERMISSION),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
        )

        val filter = IntentFilter(ACTION_USB_PERMISSION)
        context.registerReceiver(this.usbReceiver, filter)
        usbManager.requestPermission(usbConnection.device, permissionIntent)
    }

    /*==============================================================================================
    ===================================ESC/POS PRINTER PART=========================================
    ==============================================================================================*/
    /**
     * Asynchronous printing
     */
    @SuppressLint("SimpleDateFormat")
    fun getAsyncEscPosPrinter(printerConnection: DeviceConnection?): AsyncEscPosPrinter? {
        val format = SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss")
        val printer = AsyncEscPosPrinter(printerConnection, 203, 48f, 32)
        return printer.addTextToPrint(
            """
            ${
                "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
                    printer,
                    context.resources?.getDrawableForDensity(R.drawable.ic_codesap, DisplayMetrics.DENSITY_MEDIUM,null)
                )
            }</img>
            [L]
            [C]<u><font size='big'>ORDER N°045</font></u>
            [L]
            [C]<u type='double'>${format.format(Date())}</u>
            [C]
            [C]================================
            [L]
            [L]<b>BEAUTIFUL SHIRT</b>[R]9.99€
            [L]  + Size : S
            [L]
            [L]<b>AWESOME HAT</b>[R]24.99€
            [L]  + Size : 57/58
            [L]
            [C]--------------------------------
            [R]TOTAL PRICE :[R]34.98€
            [R]TAX :[R]4.23€
            [L]
            [C]================================
            [L]
            [L]<u><font color='bg-black' size='tall'>Customer :</font></u>
            [L]Raymond DUPONT
            [L]5 rue des girafes
            [L]31547 PERPETES
            [L]Tel : +33801201456
            
            [C]<barcode type='ean13' height='10'>831254784551</barcode>
            [L]
            [C]<qrcode size='20'>https://dantsu.com/</qrcode>
            
            """.trimIndent()
        )
    }
}