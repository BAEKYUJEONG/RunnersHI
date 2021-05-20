package com.A306.runnershi.Fragment.Home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.A306.runnershi.Model.Detail
import com.A306.runnershi.R
import kotlinx.android.synthetic.main.item_timeline.view.*

class TimelineAdapter(private val timelineList:Array<Detail>, private val homeFragment: HomeFragment) : RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    class TimelineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val fNickname:TextView = itemView.findViewById(R.id.fNickname)
        val fTimestamp:TextView = itemView.findViewById(R.id.fTimestamp)
        val fTime:TextView = itemView.findViewById(R.id.fTime)
        val fTitle:TextView = itemView.findViewById(R.id.fTitle)
        val fDistance:TextView = itemView.findViewById(R.id.fDistance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        return TimelineViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_timeline,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        var runningTimeSec = ((timelineList[position].runningTime % 60) + 100).toString().substring(1)
        var runningTimeMin = (((runningTimeSec.toInt() / 60) % 60) + 100).toString().substring(1)
        var runningTimeHour = (((runningTimeMin.toInt() / 60) % 60) + 100).toString().substring(1)

        val formattedRunningTime = "$runningTimeHour:$runningTimeMin:$runningTimeSec"
//        val formattedTime = "${String.format("00", runningTimeHour)}:${String.format("00", runningTimeMin)}:${String.format("00", runningTimeSec)}"

        val dateTimeHour = timelineList[position].endTime.toString().substring(0, 2).toInt().toString()
        val dateTimeMin = timelineList[position].endTime.toString().substring(3, 5).toInt().toString()

        val formattedDateTime = "${dateTimeHour}시 ${dateTimeMin}분에"

        holder.itemView.apply {
            fNickname.text = timelineList[position].userName
            fTimestamp.text = formattedDateTime
            fTime.text = formattedRunningTime
            fTitle.text = timelineList[position].title
            fDistance.text = timelineList[position].distance.toString()
        }
    }

    override fun getItemCount(): Int = timelineList.size
}