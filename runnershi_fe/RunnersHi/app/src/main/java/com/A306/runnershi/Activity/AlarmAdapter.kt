package com.A306.runnershi.Activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.A306.runnershi.Model.Alarm
import com.A306.runnershi.R

class AlarmAdapter(private val alarmList:Array<Alarm>, private val alarmFragment: AlarmActivity.AlarmFragment): RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val alarm_nick: TextView = itemView.findViewById(R.id.alarm_nick)
        val alarm_content: TextView = itemView.findViewById(R.id.alarm_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmAdapter.AlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itme_alarm, parent, false)

        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmAdapter.AlarmViewHolder, position: Int) {
        holder.alarm_nick.text = alarmList[position].userName.toString()
        holder.alarm_content.text = alarmList[position].content.toString()
    }

    override fun getItemCount(): Int = alarmList.size

}