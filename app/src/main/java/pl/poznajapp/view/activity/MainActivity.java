package pl.poznajapp.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.poznajapp.R;
import pl.poznajapp.database.Database;
import pl.poznajapp.network.API;
import pl.poznajapp.pojo.Story;
import pl.poznajapp.view.adapter.StoryAdapter;
import pl.poznajapp.view.listener.RecyclerItemClickListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static pl.poznajapp.network.API.API_URL;

/**
 * Created by Rafał Gawlik on 30.11.2016.
 */

public class MainActivity extends AppCompatActivity implements
        ResultCallback<LocationSettingsResult> {

    final String TAG = MainActivity.class.getSimpleName();

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected static final int PERMISSIONS_REQUEST_LOCATION = 0x2;

    @BindView(R.id.main_toolbar) Toolbar toolbar;
    @BindView(R.id.main_trip_list) RecyclerView tripList;
    @BindView(R.id.activity_main) LinearLayout main;
    @BindView(R.id.main_current_story_title) TextView storyTitle;
    @BindView(R.id.main_current_story) RelativeLayout currentStory;
    @BindView(R.id.main_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;

    int currectStoryId = -1;
    Story story;

    StoryAdapter mAdapter;
    ArrayList<Story> stories;
    API service;

    LocationManager mLocationManager;
    static final long MIN_TIME = 100;
    static final float MIN_DISTANCE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(API.class);
        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);

        } else {
            displayLocationSettingsRequest(getApplicationContext());
        }

    }

    void initUI() {
        initToolbar();
        initList();
//        initAnimations();
        initClickListsners();
    }

    void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        toolbar.inflateMenu(R.menu.main_manu);
    }

    @OnClick(R.id.main_current_story_clean)
    void deleteCurrentStory() {
        Database.setCurrectStory(getSharedPreferences(Database.PREFERENCES_NAME, Database.MODE), -1);
        currentStory.setVisibility(View.GONE);
        Snackbar.make(main, getResources().getString(R.string.story_delete_current_story), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_manu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // TODO
            // menu elements
        }
        return false;
    }

    void initList() {
        mAdapter = new StoryAdapter(getApplicationContext(), new ArrayList<Story>());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        tripList.setLayoutManager(mLayoutManager);
        tripList.setItemAnimator(new DefaultItemAnimator());
        tripList.setAdapter(mAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startLocationUpdates();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    void initClickListsners() {
        tripList.addOnItemTouchListener(new RecyclerItemClickListener(this, tripList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (stories == null) {
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), StoryActivity.class);
                intent.putExtra(StoryActivity.EXTRA_STORY, stories.get(position).getId());
                startActivity(intent);

            }

            @Override
            public void onItemLongClick(View view, int position) {
                //TODO implement
            }
        }));
    }

    void getStoriesList(Location location) {
        stories = new ArrayList<>();
        Call<List<Story>> call = service.listStories(location.getLatitude(), location.getLongitude());
        call.enqueue(new Callback<List<Story>>() {
            @Override
            public void onResponse(Call<List<Story>> call, Response<List<Story>> response) {
                List<Story> stories = response.body();
                MainActivity.this.stories = (ArrayList<Story>) stories;
                mAdapter.setItemList(stories);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Story>> call, Throwable t) {
                Log.d(TAG, "error");
            }
        });
    }

    @OnClick(R.id.main_current_story)
    void currentStoryClick() {
        Intent intent = new Intent(getApplicationContext(), StoryActivity.class);
        intent.putExtra(StoryActivity.EXTRA_STORY, currectStoryId);
        startActivity(intent);
    }

    private void loadCurrentStory() {
        currectStoryId = Database.getCurrectStory(getSharedPreferences(Database.PREFERENCES_NAME, Database.MODE));
        if (currectStoryId > 0) {
            currentStory.setVisibility(View.VISIBLE);
            Call<Story> call = service.getStory(currectStoryId);
            call.enqueue(new Callback<Story>() {
                @Override
                public void onResponse(Call<Story> call, Response<Story> response) {
                    story = response.body();
                    storyTitle.setText(story.getTitle());
                }

                @Override
                public void onFailure(Call<Story> call, Throwable t) {
                }
            });
        }
    }

    protected void startLocationUpdates() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        loadCurrentStory();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        getStoriesList(mLocation);
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    displayLocationSettingsRequest(getApplicationContext());
                } else {
                    Snackbar.make(main, R.string.location_permission_text, Snackbar.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
