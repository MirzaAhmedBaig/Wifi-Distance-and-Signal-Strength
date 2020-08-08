package com.example.signalstrength.dialogs

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.signalstrength.AppPreferences
import com.example.signalstrength.BuildConfig
import com.example.signalstrength.R
import com.example.signalstrength.listeners.AlertListener
import kotlinx.android.synthetic.main.menu_dialog.*

class MenuDialog : DialogFragment() {
    private val menuOption = arrayOf("Change unit", "Share app", "Rate app")

    private val options by lazy {
        arrayOf(option_one, option_two, option_three)
    }

    private var alertListener: AlertListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(STYLE_NO_TITLE, R.style.DialogStyle)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.menu_dialog, container, true)
    }


    private fun setupDialog() {
        options.forEachIndexed { index, textView ->
            textView.text = menuOption[index]
            textView.setOnClickListener {
                performOnClick(menuOption[index])
            }
        }

        listOf(meter_radio, meter_title).forEach {
            it.setOnClickListener {
                meter_radio.isChecked = true
                feet_radio.isChecked = false
            }
        }

        listOf(feet_radio, feet_title).forEach {
            it.setOnClickListener {
                feet_radio.isChecked = true
                meter_radio.isChecked = false
            }
        }

        done_button.setOnClickListener {
            AppPreferences(requireContext()).setUnit(getUnit())
            dismiss()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDialog()
    }

    private fun performOnClick(text: String) {
        when (text) {
            "Change unit" -> {
                changeUnit()
            }
            "Share app" -> {
                shareApp()
                dismiss()
            }
            "Rate app" -> {
                rateApp()
                dismiss()
            }
        }
    }


    private fun changeUnit() {
        menu_group.visibility = View.GONE
        unit_group.visibility = View.VISIBLE
        val unit = AppPreferences(requireContext()).getUnit()
        if (unit == "M") {
            meter_radio.isChecked = true
            feet_radio.isChecked = false
        } else {
            feet_radio.isChecked = true
            meter_radio.isChecked = false
        }
    }

    private fun getUnit(): String {
        return if (meter_radio.isChecked) "M" else "F"
    }

    private fun shareApp() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "I recommend ${getString(R.string.app_name)} app to find information related to wifi signals.\nhttps://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
            )
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun rateApp() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
            )
        )
    }

    fun setDismissListener(alertListener: AlertListener) {
        this.alertListener = alertListener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        alertListener?.onDismiss()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        manager.beginTransaction().add(this, tag).commitAllowingStateLoss()
    }
}