package pl.poznajapp.view.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.poznajapp.R;
import pl.poznajapp.model.Picture;
import pl.poznajapp.network.API;
import pl.poznajapp.pojo.Point;
import pl.poznajapp.pojo.Story;
import pl.poznajapp.view.adapter.PictureAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rafal on 29.11.2016.
 */

public class StoryActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_STORY = "EXTRA_STORY";
    private static final String TAG = StoryActivity.class.toString();

    @BindView(R.id.trip_toolbar) Toolbar toolbar;
    @BindView(R.id.story_description) TextView storyDescription;
    @BindView(R.id.story_duration) TextView storyDuration;
    @BindView(R.id.trip_pictures) RecyclerView picturesRecyclerView;

    MapFragment supportMapFragment;
    GoogleMap googleMap;
    PictureAdapter mAdapter;
    List pictures;
    List<MarkerOptions> markers;


    Story story;

    Integer id;
    API service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        ButterKnife.bind(this);
        id = getIntent().getIntExtra(EXTRA_STORY, -1);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://poznaj-wroclaw.herokuapp.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(API.class);

    }

    @Override
    public void onResume(){
        super.onResume();
        mockData();
        initToolbar();
        initList();
        initMap();
    }

    void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void initList(){
        mAdapter = new PictureAdapter(pictures);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        picturesRecyclerView.setLayoutManager(mLayoutManager);
        picturesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        picturesRecyclerView.setAdapter(mAdapter);
    }

    void initMap(){
        supportMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.trip_map);
        supportMapFragment.getMapAsync(this);
    }

    void mockData(){
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        this.googleMap.getUiSettings().setAllGesturesEnabled(false);
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.getUiSettings().setRotateGesturesEnabled(false);
        this.googleMap.getUiSettings().setCompassEnabled(false);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        this.googleMap.getUiSettings().setTiltGesturesEnabled(false);
        this.googleMap.getUiSettings().setZoomControlsEnabled(false);
        this.googleMap.getUiSettings().setZoomGesturesEnabled(false);

        this.googleMap.setOnMarkerClickListener(
                new GoogleMap.OnMarkerClickListener() {
                    boolean doNotMoveCameraToCenterMarker = true;
                    public boolean onMarkerClick(Marker marker) {
                        return doNotMoveCameraToCenterMarker;
                    }
                });
        getStoryDetails();

    }

    public void getStoryDetails() {

        Call<Story> call = service.getStory(id);
        call.enqueue(new Callback<Story>() {
            @Override
            public void onResponse(Call<Story> call, Response<Story> response) {
                Log.d(TAG, response.body().getTitle());
                story = response.body();
                getSupportActionBar().setTitle(story.getTitle());
                storyDuration.setText(story.getDuration());
                storyDescription.setText(story.getDescription());

                injectPointOnMap(story.getPoints());

            }

            @Override
            public void onFailure(Call<Story> call, Throwable t) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void injectPointOnMap(List<String> points) {
        markers = new ArrayList<>();
        for(String point : points){
            Log.d(TAG, "Points: " + point + " : " + Integer.toString(getIntFromString(point)));

            Call<Point> call = service.getPoint(getIntFromString(point));
            call.enqueue(new Callback<Point>() {
                @Override
                public void onResponse(Call<Point> call, Response<Point> response) {

                    Point point = response.body();
                    Log.d(TAG, point.getGeometry().getCoordinates().toString());

                    Double longitude = point.getGeometry().getCoordinates().get(0);
                    Double latitude = point.getGeometry().getCoordinates().get(1);
                    LatLng position = new LatLng(latitude, longitude);

                    markers.add(new MarkerOptions()
                            .position(position)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_24dp))
                            .title(point.getProperties().getTitle()));
                    googleMap.addMarker(markers.get(markers.size()-1));


                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (MarkerOptions marker : markers) {
                        builder.include(marker.getPosition());
                    }
                    int padding = 100;
                    LatLngBounds bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    googleMap.animateCamera(cu);
                }

                @Override
                public void onFailure(Call<Point> call, Throwable t) {

                }
            });

        }
    }


    private int getIntFromString(String s){
        Scanner in = new Scanner(s).useDelimiter("[^0-9]+");
        return in.nextInt();
    }
}
