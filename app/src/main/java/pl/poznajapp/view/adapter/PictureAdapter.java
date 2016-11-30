package pl.poznajapp.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pl.poznajapp.R;
import pl.poznajapp.model.Picture;
import pl.poznajapp.model.Trip;

/**
 * Created by rafal on 30.11.2016.
 */

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.MyPictureViewHolder> {

    List<Picture> pictures;

    public PictureAdapter(List pictures) {
        this.pictures = pictures;
    }

    @Override
    public PictureAdapter.MyPictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_picture, parent, false);
        return new PictureAdapter.MyPictureViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PictureAdapter.MyPictureViewHolder holder, int position) {
        Picture trip = pictures.get(position);
        //TODO implement
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    public class MyPictureViewHolder extends RecyclerView.ViewHolder {

        ImageView vImageView;

        public MyPictureViewHolder(View itemView) {
            super(itemView);
            vImageView = (ImageView) itemView.findViewById(R.id.trip_picture_iv);
        }
    }
}