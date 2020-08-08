package com.example.signalstrength.datamodels

data class DeviceInfo(
    var name: String,
    var macAddress: String,
    var distance: String,
    var strength: Int,
    var rssi: Int
)