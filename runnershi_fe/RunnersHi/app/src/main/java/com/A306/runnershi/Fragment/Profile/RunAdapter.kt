package com.A306.runnershi.Fragment.Profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.A306.runnershi.Fragment.SingleRun.RunResultFragment.Companion.TITLE
import com.A306.runnershi.Model.Run
import com.A306.runnershi.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_run.view.*
import kotlinx.android.synthetic.main.item_run.view.fDistance
import kotlinx.android.synthetic.main.item_run.view.fTime
import kotlinx.android.synthetic.main.item_timeline.view.*
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter(var link: ProfileFragment.runAdapterToList) : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    inner class RunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    val diffCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list : List<Run>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        return RunViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_run,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]

        holder.itemView.apply {
            Glide.with(this).load(run.img).into(ivRunImage)

            val calender = Calendar.getInstance().apply {
                timeInMillis = run.timestamp
            }

            val dateFormat = SimpleDateFormat("yyyy. MM. dd.", Locale.getDefault())
            tvDate.text = dateFormat.format(calender.time)

            val title = run.title
            tvTitle.text = title

            val pace = run.pace
//            val avgSpeed = "${run.avgSpeed}km/h"
            tvAvgSpeed.text = pace

//            String.format("%.2f", distanceData)+"K"

            val distance = "${run.distance}K"
            fDistance.text = distance

            fTime.text = run.time
        }

        holder.itemView.setOnClickListener {
            link.getRunningDetailId(run)
        }
    }
}