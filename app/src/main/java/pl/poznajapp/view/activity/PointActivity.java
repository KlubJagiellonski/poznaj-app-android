package pl.poznajapp.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.poznajapp.R;

/**
 * Created by Rafał Gawlik on 29.11.2016.
 */

public class PointActivity extends AppCompatActivity {

    public static final String POINT_TITLE = "POINT_TITLE";
    public static final String POINT_DESCRIPTION = "POINT_DESCRIPTION";
    public static final String POINT_IMAGES = "POINT_IMAGES";

    @BindView(R.id.point_toolbar) Toolbar toolbar;
    @BindView(R.id.point_description) TextView pointDescription;


    String title;
    String description;
    List<String> images;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        title = getIntent().getExtras().getString(POINT_TITLE);
        description = getIntent().getExtras().getString(POINT_DESCRIPTION);
        images = getIntent().getExtras().getStringArrayList(POINT_IMAGES);

        toolbar.setTitle(title);
        pointDescription.setText(description);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
