package com.A306.runnershi.Fragment.Ranking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.A306.runnershi.Model.Ranking
import com.A306.runnershi.R
import timber.log.Timber

class RankingAdapter(private val rankingList: Array<Ranking>, private val rankingFragment: RankingFragment): RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {

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
        holder.rankingData.text = String.format("%.2f", rankingList[position].userData) + "K"
        holder.rankingName.setOnClickListener {
            Timber.e(rankingList[position].userId)
            rankingFragment.itemClick(rankingList[position].userId.toString())
        }
    }

    override fun getItemCount(): Int = rankingList.size
}