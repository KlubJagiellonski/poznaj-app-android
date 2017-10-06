package pl.poznajapp.view;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import pl.poznajapp.API.APIService;
import pl.poznajapp.BuildConfig;
import pl.poznajapp.PoznajApp;
import pl.poznajapp.R;
import pl.poznajapp.adapter.StoryListAdapter;
import pl.poznajapp.helpers.FacebookPageUrl;
import pl.poznajapp.helpers.Utils;
import pl.poznajapp.listeners.RecyclerViewItemClickListener;
import pl.poznajapp.model.Story;
import pl.poznajapp.view.base.BaseAppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Rafał Gawlik on 13.08.17.
 */
public class MainActivity extends BaseAppCompatActivity {

    protected static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    public Location location = null;
    private FusedLocationProviderClient mFusedLocationClient;

    private APIService service;
    private List<Story> stories;

    private SwipeRefreshLayout swipeRefreshSRL;
    private RecyclerView storyListRV;
    private LinearLayout noContentLL;

    private StoryListAdapter adapter;

    private Boolean progressDialogShowed = false; //progress dialog show only first time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stories = new ArrayList<Story>();
        setupView();
        initListeners();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_about:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", Utils.INSTANCE.getURL_POZNAJAPP_ABOUT());
                intent.putExtra("title", getString(R.string.action_about));
                startActivity(intent);
                return true;
            case R.id.action_club:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", Utils.INSTANCE.getURL_POZNAJAPP_KJ());
                intent.putExtra("title", getString(R.string.action_club));
                startActivity(intent);
                return true;
            case R.id.action_team:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", Utils.INSTANCE.getURL_POZNAJAPP_TEAM());
                intent.putExtra("title", getString(R.string.action_team));
                startActivity(intent);
                return true;
            case R.id.action_partners:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", Utils.INSTANCE.getURL_POZNAJAPP_PARTNERS());
                intent.putExtra("title", getString(R.string.action_partners));
                startActivity(intent);
                return true;
            case R.id.action_bug:
                intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{Utils.INSTANCE.getPOZNAJAPP_MAIL()}); // do kogo
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.bug_mail_title)); // tytuł maila
                intent.putExtra(android.content.Intent.EXTRA_TEXT, "APP_VERSION_NAME: "+ BuildConfig.VERSION_NAME + " | ANDROID_VERSION: "+ Build.VERSION.RELEASE + " | DEVICE_MODEL: " +  android.os.Build.MODEL + " | "+ getString(R.string.bug_mail_text) ); // tresc maila
                startActivity(intent);
                return true;

            case R.id.action_rate:
                Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
                intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
                }
                return true;
            case R.id.action_fb:
                String facebookPageURL = new FacebookPageUrl().getFacebookPageURL(this, Utils.INSTANCE.getURL_POZNAJAPP_FB(), Utils.INSTANCE.getURL_POZNAJAPP_FB_PAGENAME());
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookPageURL));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupView() {
        swipeRefreshSRL = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_srl);
        storyListRV = (RecyclerView) findViewById(R.id.activity_main_story_list_rv);
        noContentLL = (LinearLayout) findViewById(R.id.activity_main_no_content_ll);

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

        swipeRefreshSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!checkPermissions()) {
                    requestPermissions();
                }
            }
        });

    }

    private void loadStories(Location location) {
        if (isInternetEnable()) {
            service = PoznajApp.retrofit.create(APIService.class);

            if (!progressDialogShowed) {
                showProgressDialog(null, "Pobieranie tras");
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

                    if(stories.size() == 0){
                        storyListRV.setVisibility(View.GONE);
                        noContentLL.setVisibility(View.VISIBLE);
                    } else {
                        storyListRV.setVisibility(View.VISIBLE);
                        noContentLL.setVisibility(View.GONE);
                    }

                    if (progressDialog.isShowing())
                        hideProgressDialog();

                    if(swipeRefreshSRL.isRefreshing())
                        swipeRefreshSRL.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<List<Story>> call, Throwable t) {
                    Timber.e(t);
                    if (progressDialog.isShowing())
                        hideProgressDialog();
                }
            });
        } else {
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    getString(R.string.no_internet),
                    Snackbar.LENGTH_INDEFINITE
            ).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.i("onResume");

        if (!checkPermissions()) {
            requestPermissions();
        }
    }

    protected boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
            requestPermissions();
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location currentLocation) {
                            if (currentLocation != null) {
                                location = currentLocation;
                                loadStories(location);
                            } else {
                                Timber.d("There is no last location.");
                            }
                        }
                    });
        }

        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Timber.d("onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Timber.d("User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermissions();
            } else {
                Snackbar.make(
                        findViewById(R.id.activity_main),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    protected void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            Timber.d("Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Timber.d("Requesting permission");

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}