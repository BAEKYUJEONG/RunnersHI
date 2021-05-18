package com.A306.runnershi.Fragment.Home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.A306.runnershi.Model.Run
import com.A306.runnershi.R
import kotlinx.android.synthetic.main.item_timeline.view.*

class TimelineAdapter : RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    inner class TimelineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    val diffCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        return TimelineViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_timeline,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {

        holder.itemView.apply {
            nickname.text
        }
    }


}