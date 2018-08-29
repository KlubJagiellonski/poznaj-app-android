package pl.poznajapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.squareup.picasso.Picasso

import pl.poznajapp.R
import pl.poznajapp.model.Story

/**
 * Created by Rafał Gawlik on 21.08.17.
 */

class StoryListAdapter(private val context: Context, private val storyList: List<Story>) : RecyclerView.Adapter<StoryListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_story_list, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv.text = storyList[position].title
        Picasso.with(context).load(storyList[position].storyImages[0].imageFile).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return storyList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv: TextView = itemView.findViewById<View>(R.id.row_story_list_tv) as TextView
        var imageView: ImageView = itemView.findViewById<View>(R.id.row_story_list_image_iv) as ImageView

    }
}
