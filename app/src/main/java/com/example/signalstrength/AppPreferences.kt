package com.example.signalstrength

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    private var _sharedPrefs: SharedPreferences =
        context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
    private var _prefsEditor: SharedPreferences.Editor

    private val UNIT = "${context.packageName}.givenUnit"

    init {
        this._prefsEditor = _sharedPrefs.edit()
        this._prefsEditor.apply()
    }

    fun setUnit(unit: String) {
        _prefsEditor.putString(UNIT, unit)
        _prefsEditor.commit()
    }

    fun getUnit(): String {
        return _sharedPrefs.getString(UNIT, "M") ?: "M"
    }
}