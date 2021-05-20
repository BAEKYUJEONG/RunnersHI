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
        holder.itemView.apply {
            fNickname.text = timelineList[position].userName
            fTimestamp.text = timelineList[position].endTime.toString()
            fTime.text = timelineList[position].runningTime.toString()
            fTitle.text = timelineList[position].title
            fDistance.text = timelineList[position].distance.toString()
        }
    }

    override fun getItemCount(): Int = timelineList.size
}