package com.example.signalstrength.extensions

import kotlin.math.pow
import kotlin.math.roundToInt

fun getDistanceInMeter(rssi: Double, txPower: Int): Double {
    if (rssi == 0.0)
        return -1.0
    val ratio = rssi * 1.0 / txPower
    return if (ratio < 1.0)
        ratio.pow(10.0)
    else
        0.89976 * ratio.pow(7.7095) + 0.111
}

fun convetToFeet(distance: Double): Double {
    return distance / 0.3048
}

fun round(input: Double, places: Int): Double {
    var value = input
    require(places >= 0)
    val factor = 10.0.pow(places.toDouble()).toLong()
    value *= factor
    val tmp = value.roundToInt()
    return tmp.toDouble() / factor
}