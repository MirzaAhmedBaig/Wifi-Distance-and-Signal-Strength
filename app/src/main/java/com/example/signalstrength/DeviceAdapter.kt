package com.example.signalstrength

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialdistancing.R
import com.example.socialdistancing.models.DeviceInfo

class DeviceAdapter(private val list: ArrayList<DeviceInfo>) :
    RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.device_info_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = list[position].macAddress
        /*holder.distance.text = "${list[position].distance}m"
        if (list[position].strength != -1) {
            holder.strength.text = "${list[position].strength}%"
            holder.strength.visibility = View.VISIBLE
        } else {
            holder.strength.visibility = View.GONE
        }*/
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.index_text)
        val distance: TextView = itemView.findViewById(R.id.data_text)
        val strength: TextView = itemView.findViewById(R.id.strength_text)
    }

}