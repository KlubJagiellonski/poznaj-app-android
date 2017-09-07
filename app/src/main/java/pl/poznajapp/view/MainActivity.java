package pl.poznajapp.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
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

import pl.poznajapp.API.APIService;
import pl.poznajapp.BuildConfig;
import pl.poznajapp.PoznajApp;
import pl.poznajapp.R;
import pl.poznajapp.adapter.StoryListAdapter;
import pl.poznajapp.helpers.Utils;
import pl.poznajapp.listeners.RecyclerViewItemClickListener;
import pl.poznajapp.model.Story;
import pl.poznajapp.service.LocationService;
import pl.poznajapp.view.base.BaseAppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Rafał Gawlik on 13.08.17.
 */

public class MainActivity extends BaseAppCompatActivity {

    private LocationReceiver locationReceiver;

    private APIService service;
    private List<Story> stories;

    private StoryListAdapter adapter;
    private RecyclerView storyListRV;

    private Boolean progressDialogShowed = false; //progress dialog show only first time

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationReceiver = new LocationReceiver();
        setContentView(R.layout.activity_main);

        stories = new ArrayList<Story>();
        setupView();
        initListeners();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            checkLocationEnabled();
        }
    }

    private void setupView() {
        storyListRV = (RecyclerView) findViewById(R.id.activity_main_story_list_rv);
        adapter = new StoryListAdapter(getApplicationContext(), stories);
        storyListRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        storyListRV.setAdapter(adapter);
        storyListRV.setItemAnimator(new DefaultItemAnimator());
    }


    private void initListeners() {
        storyListRV.addOnItemTouchListener(new RecyclerViewItemClickListener(this,
                storyListRV, new RecyclerViewItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(StoryDetailsActivity.getConfigureIntent(getApplicationContext(), stories.get(position).getId()));
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }

    private void loadStories(Location location) {
        service = PoznajApp.retrofit.create(APIService.class);

        if (!progressDialogShowed) {
            showProgressDialog("Trasy", "Pobieraniem tras");
            progressDialogShowed = true;
        }
        Call<List<Story>> storyListCall = service.listStories(location.getLatitude(), location.getLongitude());
        storyListCall.enqueue(new Callback<List<Story>>() {
            @Override
            public void onResponse(Call<List<Story>> call, Response<List<Story>> response) {
                Timber.d(response.message());
                stories.clear();
                stories.addAll(response.body());
                adapter.notifyDataSetChanged();
                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<List<Story>> call, Throwable t) {
                Timber.e(t);
                hideProgressDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.i("onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver,
                new IntentFilter(LocationService.ACTION_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Timber.i("onStop");
        if (bound) {
            unbindService(serviceConnection);
            bound = false;
        }
        super.onStop();
    }

    private class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationService.EXTRA_LOCATION);
            if (location != null) {
                Timber.d( Utils.INSTANCE.getLocationText(location));
                loadStories(location);
            }
        }
    }

}