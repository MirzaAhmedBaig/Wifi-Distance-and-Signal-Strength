package com.example.signalstrength.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.signalstrength.R
import com.example.signalstrength.datamodels.DeviceInfo

class DeviceAdapter(private val list: ArrayList<DeviceInfo>) :
    RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.device_info_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = list[position].name
        holder.mac.text = list[position].macAddress
        holder.distance.text = list[position].distance
        holder.strength.text = "${list[position].strength}%"
        holder.strength.setTextColor(getColor(list[position].strength))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.device_name)
        val mac: TextView = itemView.findViewById(R.id.device_mac)
        val distance: TextView = itemView.findViewById(R.id.distance_text)
        val strength: TextView = itemView.findViewById(R.id.strength_text)
    }

    private fun getColor(percent: Int): Int {
        return when (percent) {
            in -1..50 -> Color.parseColor("#FF8272")
            in 51..75 -> Color.parseColor("#FFD242")
            else -> Color.parseColor("#4DE1B0")
        }
    }

}