package pl.poznajapp.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.poznajapp.R;
import pl.poznajapp.network.API;
import pl.poznajapp.pojo.Image;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static pl.poznajapp.network.API.API_URL;

/**
 * Created by Rafał Gawlik on 29.11.2016.
 */

public class PointActivity extends AppCompatActivity {

    private final String TAG = PointActivity.class.getSimpleName();

    @BindView(R.id.point_toolbar) Toolbar toolbar;
    @BindView(R.id.point_backdrop) ImageView pointBackdrop;
    @BindView(R.id.point_collapsing) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.point_description) TextView pointDescription;

    public static final String POINT_TITLE = "POINT_TITLE";
    public static final String POINT_DESCRIPTION = "POINT_DESCRIPTION";
    public static final String POINT_IMAGES = "POINT_IMAGES";

    API service;

    String title;
    String description;
    List<Integer> images;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);
        ButterKnife.bind(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(API.class);
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initToolbarImage();
    }

    void initToolbarImage() {
        Call<Image> call = service.getImage(images.get(0));
        call.enqueue(new Callback<Image>() {
            @Override
            public void onResponse(Call<Image> call, Response<Image> response) {
                Picasso.with(getApplicationContext()).load(response.body().getImageFile()).into(pointBackdrop);
            }

            @Override
            public void onFailure(Call<Image> call, Throwable t) {
            }
        });
    }

    void initUI() {
        initToolbar();
        title = getIntent().getExtras().getString(POINT_TITLE);
        description = getIntent().getExtras().getString(POINT_DESCRIPTION);
        images = getIntent().getExtras().getIntegerArrayList(POINT_IMAGES);

        collapsingToolbarLayout.setTitle(title);
        pointDescription.setText(description);
    }

    void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    @Override
    protected void onPause() {
        super.onPause();
    }
}
