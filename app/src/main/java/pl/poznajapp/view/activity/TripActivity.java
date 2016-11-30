package pl.poznajapp.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.poznajapp.R;
import pl.poznajapp.model.Picture;
import pl.poznajapp.model.Trip;
import pl.poznajapp.view.adapter.PictureAdapter;
import pl.poznajapp.view.adapter.TripAdapter;

/**
 * Created by rafal on 29.11.2016.
 */

public class TripActivity extends AppCompatActivity {

    @BindView(R.id.trip_toolbar) Toolbar toolbar;
    @BindView(R.id.trip_pictures) RecyclerView picturesRecyclerView;

    PictureAdapter mAdapter;
    List pictures;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Rzeszów");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        initList();
    }

    void initList(){
        //TODO implement
        mockList();
        mAdapter = new PictureAdapter(pictures);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        picturesRecyclerView.setLayoutManager(mLayoutManager);
        picturesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        picturesRecyclerView.setAdapter(mAdapter);
    }

    void mockList(){
        pictures = new ArrayList<Picture>();
        Picture pic = new Picture();
        pic.setUrl("http://google.pl");
        pictures.add(pic);

        pic = new Picture();
        pic.setUrl("http://google.pl");
        pictures.add(pic);

        pic = new Picture();
        pic.setUrl("http://google.pl");
        pictures.add(pic);

        pic = new Picture();
        pic.setUrl("http://google.pl");
        pictures.add(pic);

        pic = new Picture();
        pic.setUrl("http://google.pl");
        pictures.add(pic);

    }

}
