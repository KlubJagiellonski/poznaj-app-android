package pl.poznajapp.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import pl.poznajapp.R;

/**
 * Created by Rafał Gawlik on 29.11.2016.
 */

public class PointActivity extends AppCompatActivity {

    public static final String POINT_TITLE = "POINT_TITLE";
    public static final String POINT_DESCRIPTION = "POINT_DESCRIPTION";
    public static final String POINT_IMAGES = "POINT_IMAGES";

    String title;
    String description;
    List<String> images;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);
    }

    @Override
    protected void onResume() {
        super.onResume();
        title = getIntent().getStringExtra(POINT_TITLE);
        description = getIntent().getStringExtra(POINT_DESCRIPTION);
        images = getIntent().getStringArrayListExtra(POINT_IMAGES);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
