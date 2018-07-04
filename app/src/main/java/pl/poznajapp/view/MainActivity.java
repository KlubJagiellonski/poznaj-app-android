package pl.poznajapp.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.patloew.rxlocation.RxLocation;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import pl.poznajapp.API.APIService;
import pl.poznajapp.BuildConfig;
import pl.poznajapp.PoznajApp;
import pl.poznajapp.R;
import pl.poznajapp.adapter.StoryListAdapter;
import pl.poznajapp.listeners.RecyclerViewItemClickListener;
import pl.poznajapp.model.Story;
import pl.poznajapp.view.base.BaseAppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rafał Gawlik on 13.08.17.
 */

public class MainActivity extends BaseAppCompatActivity {

    protected static final int REQUEST_CHECK_SETTINGS = 1;
    protected static final int REQUEST_PERMISSIONS_REQUEST_CODE = 2;

    private GoogleApiClient googleApiClient;

    private RecyclerView storyListRV;
    private LinearLayout noContentLL;

    private StoryListAdapter adapter;
    private List<Story> stories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stories = new ArrayList<>();
        setupView();
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            checkLocationEnabled();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                startActivity(new Intent(this, InfoActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupView() {
        storyListRV = findViewById(R.id.activity_main_story_list_rv);
        noContentLL = findViewById(R.id.activity_main_no_content_ll);

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

    private void loadStories(@NonNull Location location) {
        if (isInternetEnable()) {
            APIService service = PoznajApp.retrofit.create(APIService.class);

            showProgressDialog(null, getString(R.string.download_stories));

            Call<List<Story>> storyListCall = service.listStories(location.getLatitude(), location.getLongitude());
            storyListCall.enqueue(new Callback<List<Story>>() {
                @Override
                public void onResponse(@NonNull Call<List<Story>> call, @NonNull Response<List<Story>> response) {
                    if(response.body() !=null)
                    stories.clear();
                    stories.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    showNoContent(stories.size() == 0);
                    hideProgressDialog();
                }

                @Override
                public void onFailure(@NonNull Call<List<Story>> call, @NonNull Throwable throwable) {
                    hideProgressDialog();
                }
            });
        } else {
            Snackbar.make(findViewById(R.id.activity_main), getString(R.string.no_internet),
                    Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.reload_content), v -> getLocation())
                    .show();
        }
    }

    private void showNoContent(boolean isListEmpty){
        storyListRV.setVisibility(isListEmpty ? View.GONE : View.VISIBLE);
        noContentLL.setVisibility(isListEmpty ? View.VISIBLE : View.GONE);
    }

    protected boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    protected void checkLocationEnabled() {
        //location settings
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        googleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(connectionResult -> {

                }).build();

        builder.setAlwaysShow(true);

        googleApiClient.connect();
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(locationSettingsResult -> {
            final Status status = locationSettingsResult.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    getLocation();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (@SuppressLint("NewApi") IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }

                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    //TODO show toast - location turn off
                    break;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationEnabled();
                } else {
                    Snackbar.make(
                            findViewById(R.id.activity_main),
                            R.string.permission_denied_explanation,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.settings, view -> {
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            })
                            .show();
                }
            }
        }
    }

    protected void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            hideProgressDialog();
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, view -> {
                        // Request permission
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_PERMISSIONS_REQUEST_CODE);
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        //TODO show toast - location turn off
                        break;
                }
                break;
        }
    }

    public void getLocation() {

        RxLocation rxLocation = new RxLocation(this);
        LocationRequest locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        rxLocation.location().updates(locationRequest).subscribe(new Observer<Location>() {
            @Override
            public void onSubscribe(Disposable d) {
                showProgressDialog(null, getString(R.string.get_location));
            }

            @Override
            public void onNext(Location location) {
                hideProgressDialog();
                loadStories(location);
            }

            @Override
            public void onError(Throwable e) {
                hideProgressDialog();
            }

            @Override
            public void onComplete() {
                hideProgressDialog();
            }
        });
    }
}
