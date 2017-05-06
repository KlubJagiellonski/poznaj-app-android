package pl.poznajapp.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.poznajapp.R;
import pl.poznajapp.database.Database;
import pl.poznajapp.network.API;
import pl.poznajapp.pojo.FeatureCollection;
import pl.poznajapp.pojo.Image;
import pl.poznajapp.pojo.Point;
import pl.poznajapp.pojo.Story;
import pl.poznajapp.service.AppService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.location.LocationManager.GPS_PROVIDER;

/**
 * Created by Rafał Gawlik on 29.11.2016.
 */

public class StoryActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private static final String TAG = StoryActivity.class.toString();

    @BindView(R.id.story_coordinatorlayout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.story_toolbar) Toolbar toolbar;
    @BindView(R.id.story_backdrop) ImageView storyBackdrop;
    @BindView(R.id.story_collapsing) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.story_description) TextView storyDescription;
    @BindView(R.id.story_duration) TextView storyDuration;
    @BindView(R.id.story_pictures) RecyclerView picturesRecyclerView;

    public static final String EXTRA_STORY = "EXTRA_STORY";

    MapFragment supportMapFragment;
    GoogleMap googleMap;

    List<MarkerOptions> markers;

    LocationManager locationManager;
    static final long MIN_TIME = 400;
    static final float MIN_DISTANCE = 1000;

    Story story;
    Integer story_id;
    List<Point> points;
    API service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        ButterKnife.bind(this);
        story_id = getIntent().getIntExtra(EXTRA_STORY, -1);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://poznaj-wroclaw.herokuapp.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(API.class);
        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    void initUI() {
        initToolbar();
        initMap();
    }

    @OnClick(R.id.story_fab)
    void onClickFab() {
        //Save current story
        Database.setCurrectStory(getSharedPreferences(Database.PREFERENCES_NAME, Database.MODE), story_id);

        Intent i = new Intent(getApplicationContext(), AppService.class);
        startService(i);

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Ustaliłeś swoją trasę", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "Trasa porzucona", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                });

        snackbar.show();
    }

    void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void initMap() {
        supportMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.story_map);
        supportMapFragment.getMapAsync(this);
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

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));

            if (!success) Log.e(TAG, "Style parsing failed.");
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        this.googleMap.setOnMarkerClickListener(
                new GoogleMap.OnMarkerClickListener() {
                    boolean doNotMoveCameraToCenterMarker = true;

                    public boolean onMarkerClick(Marker marker) {
                        return doNotMoveCameraToCenterMarker;
                    }
                });
        getStoryDetails();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        locationManager.requestLocationUpdates(GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, (LocationListener) this);

    }

    void getStoryDetails() {
        Call<Story> call = service.getStory(story_id);
        call.enqueue(new Callback<Story>() {
            @Override
            public void onResponse(Call<Story> call, Response<Story> response) {
                Log.d(TAG, response.body().getTitle());
                story = response.body();
                collapsingToolbarLayout.setTitle(story.getTitle());
                storyDuration.setText(story.getDuration());
                storyDescription.setText(story.getDescription());
                injectPointOnMap();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        return false;
    }

    void injectPointOnMap() {
        markers = new ArrayList<>();

        Call<FeatureCollection> call = service.getStoryPoints(story_id);
        call.enqueue(new Callback<FeatureCollection>() {
            @Override
            public void onResponse(Call<FeatureCollection> call, Response<FeatureCollection> response) {
                points = response.body().getFeatures();
                for (Point point : points) {
                    Double longitude = point.getGeometry().getCoordinates().get(0);
                    Double latitude = point.getGeometry().getCoordinates().get(1);
                    LatLng position = new LatLng(latitude, longitude);
                    markers.add(new MarkerOptions()
                            .position(position)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_36dp))
                            .title(point.getProperties().getTitle()));
                    googleMap.addMarker(markers.get(markers.size() - 1));

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (MarkerOptions marker : markers) {
                        builder.include(marker.getPosition());
                    }
                    int padding = 100;
                    LatLngBounds bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    googleMap.animateCamera(cu);
                    initImage();
                }
            }

            @Override
            public void onFailure(Call<FeatureCollection> call, Throwable t) {

            }
        });
    }

    void initImage() {

        Call<Image> call = service.getImage(points.get(0).getProperties().getImages().get(0));
        call.enqueue(new Callback<Image>() {
            @Override
            public void onResponse(Call<Image> call, Response<Image> response) {
                Picasso.with(getApplicationContext()).load(response.body().getImageFile()).into(storyBackdrop);
            }

            @Override
            public void onFailure(Call<Image> call, Throwable t) {
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        if (markers == null)
            return;
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_black_36dp)));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MarkerOptions marker : markers) {
            builder.include(marker.getPosition());
        }

        builder.include(new LatLng(location.getLatitude(), location.getLongitude()));


        int padding = 100;
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.animateCamera(cu);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }
}
