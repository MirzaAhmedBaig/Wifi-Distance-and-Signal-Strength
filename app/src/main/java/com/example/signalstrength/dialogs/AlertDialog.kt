package com.example.signalstrength.dialogs

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.signalstrength.R
import com.example.signalstrength.listeners.AlertListener
import kotlinx.android.synthetic.main.alert_dialog.*

class AlertDialog : DialogFragment() {

    private var dialogMessage = ""
    private var spanMessage: SpannableStringBuilder? = null
    private var buttonText = "Okay"
    private var titleText = ""

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
        return inflater.inflate(R.layout.alert_dialog, container, true)
    }


    private fun setupDialog() {
        if (titleText.isBlank()) {
            no_title_group.visibility = View.GONE
        } else {
            title_text.text = titleText
            no_title_group.visibility = View.VISIBLE
        }

        if (buttonText.isNotBlank()) {
            done_button.text = buttonText
        }

        if (spanMessage != null) {
            discription_text.text = spanMessage
        } else {
            discription_text.text = dialogMessage
        }

        done_button.setOnClickListener {
            alertListener?.onPositive()
            dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDialog()
    }

    fun setTitle(title: String) {
        titleText = title
    }

    fun setMessage(message: SpannableStringBuilder?) {
        spanMessage = message
    }

    fun setMessage(msg: String) {
        dialogMessage = msg
    }

    fun setOkayButtonText(text: String) {
        buttonText = text
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