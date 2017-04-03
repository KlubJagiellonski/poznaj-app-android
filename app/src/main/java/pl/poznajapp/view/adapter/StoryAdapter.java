package pl.poznajapp.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import pl.poznajapp.R;
import pl.poznajapp.network.API;
import pl.poznajapp.pojo.Image;
import pl.poznajapp.pojo.Point;
import pl.poznajapp.pojo.Story;
import pl.poznajapp.view.activity.MainActivity;
import pl.poznajapp.view.widget.PanoramicImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rafał Gawlik on 29.11.2016.
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.MyTripViewHolder> {

    List<Story> trips;
    Context context;
    API service;

    public StoryAdapter(Context context, List trips) {
        this.context = context;

        this.trips = trips;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://poznaj-wroclaw.herokuapp.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(API.class);

    }

    @Override
    public StoryAdapter.MyTripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_card_view, parent, false);

        return new MyTripViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StoryAdapter.MyTripViewHolder holder, int position) {
        final Story trip = trips.get(position);
        holder.vTitle.setText(trip.getTitle());

        Call<Point> call = service.getPoint(trip.getPoints().get(0));
        call.enqueue(new Callback<Point>() {
            @Override
            public void onResponse(Call<Point> call, Response<Point> response) {
                Point point = response.body();
                Call<Image> call_image = service.getImage(point.getProperties().getImages().get(0));
                call_image.enqueue(new Callback<Image>() {
                    @Override
                    public void onResponse(Call<Image> call, Response<Image> response) {
                        Image image = response.body();
                        Picasso.with(context).load(image.getImageFile()).into(holder.panoramicImageView);
                    }

                    @Override
                    public void onFailure(Call<Image> call, Throwable t) {
                        Log.d("APIResult", t.toString());
                    }
                });

            }

            @Override
            public void onFailure(Call<Point> call, Throwable t) {
                Log.d("APIResult",  t.getMessage());
            }
        });

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
            panoramicImageView = (PanoramicImageView) itemView.findViewById(R.id.panoramicImageView);
        }
    }
}
