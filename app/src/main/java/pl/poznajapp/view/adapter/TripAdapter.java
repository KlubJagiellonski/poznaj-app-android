package pl.poznajapp.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.poznajapp.R;
import pl.poznajapp.model.Trip;

/**
 * Created by rafal on 29.11.2016.
 */

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.MyTripViewHolder> {

    List<Trip> trips;

    public TripAdapter(List trips) {
        this.trips = trips;
    }

    @Override
    public TripAdapter.MyTripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_card_view, parent, false);

        return new MyTripViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TripAdapter.MyTripViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.vTitle.setText(trip.getTitle());

    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class MyTripViewHolder extends RecyclerView.ViewHolder {

        TextView vTitle;

        public MyTripViewHolder(View itemView) {
            super(itemView);
            vTitle = (TextView) itemView.findViewById(R.id.main_card_view_title);
        }
    }
}
