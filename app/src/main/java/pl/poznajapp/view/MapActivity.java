package pl.poznajapp.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import pl.poznajapp.API.APIService;
import pl.poznajapp.PoznajApp;
import pl.poznajapp.R;
import pl.poznajapp.adapter.PointListAdapter;
import pl.poznajapp.model.Feature;
import pl.poznajapp.model.Point;
import pl.poznajapp.view.base.BaseAppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Rafał Gawlik on 02.09.17.
 */

public class MapActivity extends BaseAppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRAS_STORY_ID = "EXTRAS_STORY_ID";
    public static final String EXTRAS_STORY_TITLE = "EXTRAS_STORY_TITLE";

    private PointListAdapter adapter;
    private APIService service;

    private GoogleMap googleMap;
    private List<Feature> features;

    private int id;
    private String title;

    public static Intent getConfigureIntent(Context context, Integer storyId, String storyTitle) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra(EXTRAS_STORY_ID, storyId);
        intent.putExtra(EXTRAS_STORY_TITLE, storyTitle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (getIntent().getExtras() != null) {
            id = getIntent().getIntExtra(EXTRAS_STORY_ID, -1);
            title = getIntent().getStringExtra(EXTRAS_STORY_TITLE);
            setupView();

            features = new ArrayList<>();
            service = PoznajApp.retrofit.create(APIService.class);
        } else {
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.i("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        Timber.i("onStop");
        super.onStop();
    }

    private void setupView() {
        RecyclerView pointsList = (RecyclerView) findViewById(R.id.activity_map_point_list_rv);
        adapter = new PointListAdapter(new ArrayList<Feature>(), new PointListAdapter.OnItemClickListener() {
            @Override
            public void onDetailsClick(Feature feature, int position) {

                if (feature.getProperties().getPointImages().size() > 0)
                    startActivity(PointDetailsActivity.getConfigureIntent(
                            getApplicationContext(),
                            feature.getProperties().getPointImages().get(0).getImageFile(),
                            feature.getProperties().getTitle(),
                            feature.getGeometry().getCoordinates().get(1),
                            feature.getGeometry().getCoordinates().get(0),
                            feature.getProperties().getDescription())
                    );
            }

            @Override
            public void onMoveClick(Feature feature, int position) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(
                                feature.getGeometry().getCoordinates().get(1),
                                feature.getGeometry().getCoordinates().get(0)), 16.5f));
            }
        });

        pointsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        pointsList.setAdapter(adapter);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_map_map);
        supportMapFragment.getMapAsync(this);

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        styleMap(googleMap);

        if (isInternetEnable()){
            Call<List<Point>> pointListCall = service.getStoryPoints(id);
            pointListCall.enqueue(new Callback<List<Point>>() {
                @Override
                public void onResponse(Call<List<Point>> call, Response<List<Point>> response) {
                    Timber.d(response.message());
                    features = response.body().get(1).getFeatures();

                    adapter.setPointList(response.body().get(1).getFeatures());
                    adapter.notifyDataSetChanged();

                    for (Feature feature : response.body().get(1).getFeatures())
                        addMarker(feature);

                    zoomToPoint(new LatLng(features.get(0).getGeometry().getCoordinates().get(1),
                            features.get(0).getGeometry().getCoordinates().get(0)));

                }

                @Override
                public void onFailure(Call<List<Point>> call, Throwable t) {
                    Timber.e(t);
                }
            });
        } else {
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    getString(R.string.no_internet),
                    Snackbar.LENGTH_INDEFINITE)
                    .show();
        }
    }

    private void zoomToPoint(LatLng latLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
    }

    private void styleMap(GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
            if (!success) {
                Timber.e("Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Timber.e(e);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void addMarker(Feature feature) {
        LatLng latLng = new LatLng(feature.getGeometry().getCoordinates().get(1), feature.getGeometry().getCoordinates().get(0));
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black))
        );
    }
}
