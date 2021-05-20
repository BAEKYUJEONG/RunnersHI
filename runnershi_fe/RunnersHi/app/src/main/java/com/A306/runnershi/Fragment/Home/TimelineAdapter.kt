package com.A306.runnershi.Fragment.Home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.A306.runnershi.Model.Detail
import com.A306.runnershi.R
import kotlinx.android.synthetic.main.item_timeline.view.*

class TimelineAdapter(private val timelineList:Array<Detail>, private val homeFragment: HomeFragment) : RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    var title = ""

    class TimelineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val fDate: TextView = itemView.findViewById(R.id.fDate)
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
        var runningTimeSec = timelineList[position].runningTime
        var formattedSec = ((runningTimeSec % 60) + 100).toString().substring(1)
        var runningTimeMin = runningTimeSec / 60
        var formattedMin = ((runningTimeMin % 60) + 100).toString().substring(1)
        var runningTimeHour = runningTimeMin / 60
        var formattedHour = ((runningTimeHour % 60) + 100).toString().substring(1)

        val formattedRunningTime = "$formattedHour:$formattedMin:$formattedSec"
//        val formattedTime = "${String.format("00", runningTimeHour)}:${String.format("00", runningTimeMin)}:${String.format("00", runningTimeSec)}"

        val dateTimeHour = timelineList[position].endTime.toString().substring(0, 2).toInt().toString()
        val dateTimeMin = timelineList[position].endTime.toString().substring(3, 5).toInt().toString()

        val formattedDateTime = "${dateTimeHour}시 ${dateTimeMin}분에"

        holder.itemView.apply {
            if(!title.equals(timelineList[position].runningDate)){
                fDate.text = timelineList[position].runningDate
            } else {
                fDate.isGone = true
            }

            fNickname.text = timelineList[position].userName
            fTimestamp.text = formattedDateTime
            fTime.text = formattedRunningTime
            fTitle.text = timelineList[position].title
            fDistance.text = timelineList[position].distance.toString()
        }

        title = timelineList[position].runningDate.toString()
    }

    override fun getItemCount(): Int = timelineList.size
}