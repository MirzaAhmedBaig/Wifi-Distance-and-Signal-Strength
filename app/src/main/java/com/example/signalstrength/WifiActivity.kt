package com.example.signalstrength

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.socialdistancing.R
import com.example.socialdistancing.adapter.DeviceAdapter
import com.example.socialdistancing.extensions.getDistance
import com.example.socialdistancing.models.DeviceInfo
import kotlinx.android.synthetic.main.activity_wifi.*


class WifiActivity : AppCompatActivity() {

    private val TAG = WifiActivity::class.java.simpleName

    private val wifiManager by lazy {
        applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    private val deviceList = ArrayList<DeviceInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi)
        startWIFI()
        setAdapter()
    }

    private fun startWIFI() {
        val wifiScanReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                countDownTimer?.cancel()
                startCountDown()
                scanSuccess()
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanReceiver, intentFilter)
        wifiManager.startScan()
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

            val distance = getDistance(
                rssi.toDouble(),
                deviceRssi
            ).toInt()
            Log.d(TAG, "SSID : ${it.SSID}  RSSI : $rssi Range : $range Distance $distance ")
/*
            deviceList.add(
                DeviceInfo(
                    it.SSID,
                    "", 0,
                    distance,
                    range,
                    rssi,
                    false
                )
            )
*/
        }
        device_list?.adapter?.notifyDataSetChanged()
    }

    private fun setAdapter() {
        with(device_list) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@WifiActivity)
            adapter =
                DeviceAdapter(deviceList)
        }
    }

    private var countDownTimer: CountDownTimer? = null
    private fun startCountDown() {
        countDownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                counter.text = "${millisUntilFinished / 1000} sec"
            }

            override fun onFinish() {
                Toast.makeText(this@WifiActivity, "Scanning...", Toast.LENGTH_LONG).show()
            }
        }.start()
    }
}
