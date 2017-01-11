package pl.poznajapp.view.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.poznajapp.R;
import pl.poznajapp.model.Picture;
import pl.poznajapp.view.adapter.PictureAdapter;

/**
 * Created by rafal on 29.11.2016.
 */

public class TripActivity extends AppCompatActivity implements OnMapReadyCallback {

    @BindView(R.id.trip_toolbar) Toolbar toolbar;
    @BindView(R.id.trip_pictures) RecyclerView picturesRecyclerView;

    @BindString(R.string.trip_toolbar_title) String toolbarTitle;

    MapFragment supportMapFragment;
    PictureAdapter mAdapter;
    List pictures;
    List<MarkerOptions> markers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        ButterKnife.bind(this);
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
        getSupportActionBar().setTitle(toolbarTitle);
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

        LatLng PERTH = new LatLng(-31.952854, 115.857342);
        LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
        LatLng BRISBANE = new LatLng(-27.47093, 153.0235);


        markers = new ArrayList<>();
        markers.add(new MarkerOptions()
                .position(PERTH)
                .title("Perth"));
        markers.add(new MarkerOptions()
                .position(SYDNEY)
                .title("Sydnay"));
        markers.add(new MarkerOptions()
                .position(BRISBANE)
                .title("Brisbane"));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);


        PolylineOptions line= new PolylineOptions();
        line.width(5).color(Color.BLACK);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MarkerOptions marker : markers) {
            googleMap.addMarker(marker);
            builder.include(marker.getPosition());
            line.add(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        googleMap.addPolyline(line);
        //TODO implement
//        int padding = 50; // offset from edges of the map in pixels
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//        googleMap.moveCamera(cu);

    }
}
