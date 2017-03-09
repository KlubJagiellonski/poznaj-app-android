package pl.poznajapp.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.poznajapp.R;

/**
 * Created by Rafał Gawlik on 30.11.2016.
 */

public class AllPointActivity  extends AppCompatActivity {

    @BindView(R.id.allpoint_toolbar) android.support.v7.widget.Toolbar toolbar;
    @BindString(R.string.apppoint__title) String toolbarTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_point);
        ButterKnife.bind(this);

        initToolbar();
    }

    void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(toolbarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}