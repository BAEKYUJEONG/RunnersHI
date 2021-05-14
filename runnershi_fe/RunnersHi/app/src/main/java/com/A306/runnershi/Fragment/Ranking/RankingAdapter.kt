package com.A306.runnershi.Fragment.Ranking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.A306.runnershi.Model.Ranking
import com.A306.runnershi.R

class RankingAdapter(private val rankingList: Array<Ranking>): RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {

    class RankingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val rankingNumber:TextView = itemView.findViewById(R.id.rankingNumber)
        val rankingName:TextView = itemView.findViewById(R.id.rankingName)
        val rankingData:TextView = itemView.findViewById(R.id.rankingData)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ranking_item, parent, false)

        return RankingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        holder.rankingNumber.text = (position+1).toString()
        holder.rankingName.text = rankingList[position].userName
        holder.rankingData.text = rankingList[position].userData.toString()
    }

    override fun getItemCount(): Int = rankingList.size
}