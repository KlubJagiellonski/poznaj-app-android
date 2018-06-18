package pl.poznajapp.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import pl.poznajapp.API.APIService;
import pl.poznajapp.PoznajApp;
import pl.poznajapp.R;
import pl.poznajapp.view.base.BaseAppCompatActivity;
import timber.log.Timber;

/**
 * Created by Rafał Gawlik on 08.09.17.
 */

public class PointDetailsActivity extends BaseAppCompatActivity {

    public static final String EXTRAS_POINT_IMAGE = "EXTRAS_POINT_IMAGE";
    public static final String EXTRAS_POINT_TITLE = "EXTRAS_POINT_TITLE";
    public static final String EXTRAS_POINT_DESCRIPTION = "EXTRAS_POINT_DESCRIPTION";
    public static final String EXTRAS_POINT_LATLITUDE = "EXTRAS_POINT_LATLITUDE";
    public static final String EXTRAS_POINT_LONGITUDE = "EXTRAS_POINT_LONGITUDE";

    private double latitude, longitude;


    public static Intent getConfigureIntent(Context context, String image, String title, Double latitude, Double longitude, String details) {
        Intent intent = new Intent(context, PointDetailsActivity.class);
        intent.putExtra(EXTRAS_POINT_IMAGE, image);
        intent.putExtra(EXTRAS_POINT_TITLE, title);
        intent.putExtra(EXTRAS_POINT_LATLITUDE, latitude);
        intent.putExtra(EXTRAS_POINT_LONGITUDE,longitude);
        intent.putExtra(EXTRAS_POINT_DESCRIPTION, details);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_details);

        if (getIntent().getExtras() != null) {
            String image = getIntent().getStringExtra(EXTRAS_POINT_IMAGE);
            String title = getIntent().getStringExtra(EXTRAS_POINT_TITLE);
            String description = getIntent().getStringExtra(EXTRAS_POINT_DESCRIPTION);
            latitude = getIntent().getDoubleExtra(EXTRAS_POINT_LATLITUDE,0);
            longitude =  getIntent().getDoubleExtra(EXTRAS_POINT_LONGITUDE,0);

            setupView(image, title, description);
        } else {
            finish();
        }

    }

    private void setupView(String image, String title, String description) {
        ImageView pointIv = findViewById(R.id.point_details_back_iv);
        TextView pointTitleTv = findViewById(R.id.point_title_text_tv);
        TextView pointDetailsTv = findViewById(R.id.point_details_text_tv);

        pointTitleTv.setText(title);
        pointDetailsTv.setText(description);

        Picasso.with(getApplicationContext()).load(image).into(pointIv);

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    public void onShowGoogleMapsClick(View view) {
        String uri = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

}
