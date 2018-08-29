package pl.poznajapp.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import pl.poznajapp.R
import pl.poznajapp.adapter.PointListAdapter.ViewHolder
import pl.poznajapp.model.Feature

/**
 * Created by Rafał Gawlik on 06.09.17.
 */

class PointListAdapter(private var pointList: List<Feature>?, private val listener: OnItemClickListener) : RecyclerView.Adapter<ViewHolder>() {

    interface OnItemClickListener {
        fun onDetailsClick(feature: Feature, position: Int)
        fun onMoveClick(feature: Feature, position: Int)
    }

    fun setPointList(pointList: List<Feature>) {
        this.pointList = pointList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_point_list, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = holder.adapterPosition
        holder.tv.text = pointList!![position].properties.title
        holder.tv.setOnClickListener { listener.onMoveClick(pointList!![pos], pos) }
        holder.iv.setOnClickListener { listener.onDetailsClick(pointList!![pos], pos) }
    }

    override fun getItemCount(): Int {
        return pointList!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv: TextView = itemView.findViewById(R.id.row_point_list_tv)
        var iv: ImageView = itemView.findViewById(R.id.row_point_details_iv)

    }
}
