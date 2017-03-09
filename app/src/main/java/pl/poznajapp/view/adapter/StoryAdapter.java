package pl.poznajapp.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pl.poznajapp.R;
import pl.poznajapp.pojo.Story;
import pl.poznajapp.view.widget.PanoramicImageView;

/**
 * Created by Rafał Gawlik on 29.11.2016.
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.MyTripViewHolder> {

    List<Story> trips;

    public StoryAdapter(List trips) {
        this.trips = trips;
    }

    @Override
    public StoryAdapter.MyTripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_card_view, parent, false);

        return new MyTripViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StoryAdapter.MyTripViewHolder holder, int position) {
        Story trip = trips.get(position);
        holder.vTitle.setText(trip.getTitle());
    }


    public void setItemList(List trips){
        this.trips = trips;
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class MyTripViewHolder extends RecyclerView.ViewHolder {

        TextView vTitle;
        PanoramicImageView panoramicImageView;

        public MyTripViewHolder(View itemView) {
            super(itemView);
            vTitle = (TextView) itemView.findViewById(R.id.main_card_view_title);
        }
    }
}
