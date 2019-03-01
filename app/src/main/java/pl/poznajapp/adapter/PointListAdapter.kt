package pl.poznajapp.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import pl.poznajapp.R
import pl.poznajapp.adapter.PointListAdapter.ViewHolder
import pl.poznajapp.model.Feature

/**
 * Created by Rafał Gawlik on 06.09.17.
 */

class PointListAdapter(var pointList: List<Feature> = ArrayList(), private val listener: OnItemClickListener) : RecyclerView.Adapter<ViewHolder>() {

    interface OnItemClickListener {
        fun onDetailsClick(feature: Feature, position: Int)
        fun onMoveClick(feature: Feature, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_point_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv.text = pointList[position].properties.title

        holder.adapterPosition.apply {
            holder.tv.setOnClickListener { listener.onMoveClick(pointList[this], this) }
            holder.visit.setOnClickListener { listener.onDetailsClick(pointList[this], this) }
        }
    }

    override fun getItemCount(): Int {
        return pointList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv: TextView = itemView.findViewById(R.id.rowPointListTitle)
        var visit: TextView = itemView.findViewById(R.id.rowPointDetailsVisit)

    }
}
