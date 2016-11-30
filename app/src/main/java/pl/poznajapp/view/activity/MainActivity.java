package pl.poznajapp.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.poznajapp.R;
import pl.poznajapp.model.Trip;
import pl.poznajapp.utils.Utils;
import pl.poznajapp.view.adapter.TripAdapter;
import pl.poznajapp.view.listener.RecyclerItemClickListener;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_toolbar) android.support.v7.widget.Toolbar toolbar;
    @BindView(R.id.main_trip_list) android.support.v7.widget.RecyclerView tripList;
    @BindView(R.id.main_action_button) android.support.design.widget.FloatingActionButton actionButton;

    TripAdapter mAdapter;
    List trips;

    private static final int ANIM_DURATION_TOOLBAR = 200;
    private static final int ANIM_DURATION_FAB = 400;
    private static final int ANIM_DURATION_LIST = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initToolbar();
        initList();
        initAnimations();
        inicClickListsners();
    }

    void initToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lorem ipsum");
    }

    void initAnimations() {
        int actionbarSize = Utils.dpToPx(56);
        toolbar.setTranslationY(-actionbarSize);
        toolbar.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setInterpolator(new DecelerateInterpolator(1.f))
                .setStartDelay(300);

        actionButton.setTranslationY(2 * 200);
        actionButton.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(100)
                .setDuration(ANIM_DURATION_FAB)
                .start();


        tripList.setTranslationY(2000);
        tripList.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(1.f))
                .setStartDelay(500)
                .setDuration(ANIM_DURATION_LIST)
                .start();

    }

    void initList(){
        //TODO implement
        mockList();
        mAdapter = new TripAdapter(trips);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        tripList.setLayoutManager(mLayoutManager);
        tripList.setItemAnimator(new DefaultItemAnimator());
        tripList.setAdapter(mAdapter);
    }

    void inicClickListsners(){
        tripList.addOnItemTouchListener(new RecyclerItemClickListener(this, tripList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent =  new Intent(getApplicationContext(), TripActivity.class);
                //TODO intent etxtras
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                //TODO implement
            }
        }));
    }


    //TODO to remove
    void mockList(){
        trips = new ArrayList<Trip>();
        Trip trip = new Trip();
        trip.setTitle("Rzeszów");
        trips.add(trip);

        trip = new Trip();
        trip.setTitle("Rzeszów");
        trips.add(trip);

        trip = new Trip();
        trip.setTitle("Rzeszów");
        trips.add(trip);

        trip = new Trip();
        trip.setTitle("Rzeszów");
        trips.add(trip);

        trip = new Trip();
        trip.setTitle("Rzeszów");
        trips.add(trip);

        trip = new Trip();
        trip.setTitle("Rzeszów");
        trips.add(trip);

    }


    @OnClick(R.id.main_action_button)
    void onClickActionButton(){
        //TODO implement
        Intent intent =  new Intent(getApplicationContext(), AllPointActivity.class);
        startActivity(intent);
    }

}
