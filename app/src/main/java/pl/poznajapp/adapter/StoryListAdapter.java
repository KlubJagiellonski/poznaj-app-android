package pl.poznajapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import pl.poznajapp.R;
import pl.poznajapp.model.Story;

/**
 * Created by Rafał Gawlik on 21.08.17.
 */

public class StoryListAdapter extends RecyclerView.Adapter<StoryListAdapter.ViewHolder> {

    private List<Story> storyList;
    private Context context;

    public StoryListAdapter(Context context, List<Story> storyList){
        this.storyList = storyList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_story_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv.setText(storyList.get(position).getTitle());
        Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.row_story_list_tv);
            imageView = (ImageView) itemView.findViewById(R.id.row_story_list_image_iv);
        }
    }
}
