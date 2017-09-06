package pl.poznajapp.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;

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

    private RecyclerView pointsList;
    private SupportMapFragment supportMapFragment;

    private PointListAdapter adapter;
    private APIService service;

    public static Intent getConfigureIntent(Context context, Integer storyId) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra(EXTRAS_STORY_ID, storyId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setupView();
        service = PoznajApp.retrofit.create(APIService.class);
    }

    private void setupView() {
        pointsList = (RecyclerView) findViewById(R.id.activity_map_point_list_rv);

        adapter = new PointListAdapter(getApplicationContext(), new ArrayList<Feature>());
        pointsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        pointsList.setAdapter(adapter);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_map_map);
        supportMapFragment.getMapAsync(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        styleMap(googleMap);
        Call<List<Point>> pointListCall = service.getStoryPoints(6);
        pointListCall.enqueue(new Callback<List<Point>>() {
            @Override
            public void onResponse(Call<List<Point>> call, Response<List<Point>> response) {
                Timber.d(response.message());
                adapter.setPointList(response.body().get(1).getFeatures());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Point>> call, Throwable t) {
                Timber.e(t);
            }
        });
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
}
