package com.example.signalstrength.activities

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.signalstrength.AppPreferences
import com.example.signalstrength.R
import com.example.signalstrength.adapter.DeviceAdapter
import com.example.signalstrength.datamodels.DeviceInfo
import com.example.signalstrength.dialogs.AlertDialog
import com.example.signalstrength.dialogs.MenuDialog
import com.example.signalstrength.extensions.calculateDistance
import com.example.signalstrength.extensions.convetToFeet
import com.example.signalstrength.extensions.getDistanceInMeter
import com.example.signalstrength.extensions.round
import com.example.signalstrength.listeners.AlertListener
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.tool_bar.*


class ListActivity : AppCompatActivity() {

    private val TAG = ListActivity::class.java.simpleName

    private val permsRequestCode = 200
    private val perms = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private val wifiManager by lazy {
        applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    private val deviceList = ArrayList<DeviceInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        showWarningDialog()
        setListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        scannerHandler.removeCallbacks(scannerRunnable)
        if (isRegistered)
            unregisterReceiver(wifiScanReceiver)
    }

    private fun setListeners() {
        permission_text.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(perms, permsRequestCode)
            }
        }

        more_button.setOnClickListener {
            showMenuDialog()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startWIFI()
            setAdapter()
        } else {
            permission_text.visibility = View.VISIBLE
            Toast.makeText(this, "Permission needed...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showWarningDialog() {
        AlertDialog().apply {
            setTitle("Warning")
            setMessage("Results will only update every 30 seconds due to android restrictions.")
            setDismissListener(object : AlertListener {
                override fun onDismiss() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isPermissionGranted()) {
                        showLocationDialog()
                    } else {
                        startWIFI()
                        setAdapter()
                    }
                }
            })
        }.show(supportFragmentManager, "dialog")
    }

    private fun showLocationDialog() {
        AlertDialog().apply {
            setTitle("Attention")
            setMessage("In android, We need location permission to search WIFI devices we will not user your location for any purpose.")
            setDismissListener(object : AlertListener {
                override fun onPositive() {
                    requestPermissions(perms, permsRequestCode)
                }
            })
        }.show(supportFragmentManager, "dialog")
    }

    private fun showMenuDialog() {
        MenuDialog().apply {
            setDismissListener(object : AlertListener {
                override fun onDismiss() {
                    refreshList()
                }
            })
        }.show(supportFragmentManager, "dialog")
    }

    private var isRegistered = false
    private fun startWIFI() {
        permission_text.visibility = View.GONE
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanReceiver, intentFilter)
        isRegistered = true
        try {
            wifiManager.startScan()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun scanSuccess() {
        Log.d(TAG, "scan devices ")
        val results = wifiManager.scanResults
        findDistance(results)
    }


    private fun findDistance(results: List<ScanResult>) {
        deviceList.clear()
        results.forEach {
            val rssi = it.level
            val range = WifiManager.calculateSignalLevel(rssi, 100)
            val deviceRssi = wifiManager.connectionInfo.rssi

            val distance = getDistanceInMeter(rssi.toDouble(), deviceRssi)
            val newDi = calculateDistance(rssi.toDouble(),wifiManager.connectionInfo.frequency.toDouble())
            val newDistance =
                if (AppPreferences(this).getUnit() == "M") "${round(distance, 2)}m" else "${round(
                    convetToFeet(
                        distance
                    ), 2
                )}ft"
            Log.d(TAG, "SSID : ${it.SSID}  RSSI : $rssi Range : $range Distance $newDistance $newDi")
            deviceList.add(
                DeviceInfo(
                    it.SSID,
                    it.BSSID,
                    newDistance,
                    range,
                    rssi
                )
            )
        }
        wifi_list?.adapter?.notifyDataSetChanged()
    }

    private fun refreshList() {
        if (deviceList.isEmpty())
            return
        val deviceRssi = wifiManager.connectionInfo.rssi
        deviceList.forEach {
            val distance = getDistanceInMeter(it.rssi.toDouble(), deviceRssi)
            val newDistance =
                if (AppPreferences(this).getUnit() == "M") "${round(distance, 2)}m" else "${round(
                    convetToFeet(
                        distance
                    ), 2
                )}ft"
            it.distance = newDistance
        }
        wifi_list?.adapter?.notifyDataSetChanged()
    }

    private fun setAdapter() {
        with(wifi_list) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@ListActivity)
            adapter = DeviceAdapter(deviceList)
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            scannerHandler.removeCallbacks(scannerRunnable)
            scannerHandler.postDelayed(scannerRunnable, 30000L)
            scanSuccess()
        }
    }

    private val scannerHandler = Handler()
    private val scannerRunnable = Runnable {
        try {
            wifiManager.startScan()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Toast.makeText(this, "Scanning...", Toast.LENGTH_LONG).show()
    }
}
