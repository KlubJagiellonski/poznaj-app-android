package pl.poznajapp.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.poznajapp.R;
import pl.poznajapp.network.API;
import pl.poznajapp.pojo.Story;
import pl.poznajapp.utils.Utils;
import pl.poznajapp.view.adapter.StoryAdapter;
import pl.poznajapp.view.listener.RecyclerItemClickListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rafał Gawlik on 30.11.2016.
 */


public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";

    @BindView(R.id.main_toolbar) android.support.v7.widget.Toolbar toolbar;
    @BindView(R.id.main_trip_list) android.support.v7.widget.RecyclerView tripList;
    @BindView(R.id.main_action_button) android.support.design.widget.FloatingActionButton actionButton;

    StoryAdapter mAdapter;
    ArrayList<Story> stories;
    API service;

    private static final int ANIM_DURATION_TOOLBAR = 200;
    private static final int ANIM_DURATION_FAB = 400;
    private static final int ANIM_DURATION_LIST = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://poznaj-wroclaw.herokuapp.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(API.class);

    }

    @Override
    public void onResume(){
        super.onResume();
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
        mAdapter = new StoryAdapter(new ArrayList<Story>());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        tripList.setLayoutManager(mLayoutManager);
        tripList.setItemAnimator(new DefaultItemAnimator());
        tripList.setAdapter(mAdapter);
        getStoriesList();
    }

    void inicClickListsners(){
        tripList.addOnItemTouchListener(new RecyclerItemClickListener(this, tripList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "Click on item");
                Intent intent =  new Intent(getApplicationContext(), StoryActivity.class);
                intent.putExtra(StoryActivity.EXTRA_STORY, stories.get(position).getId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                //TODO implement
            }
        }));
    }


    void getStoriesList(){
        stories = new ArrayList<>();

        Call<List<Story>> call = service.listStories();
        call.enqueue(new Callback<List<Story>>() {
            @Override
            public void onResponse(Call<List<Story>> call, Response<List<Story>> response) {
                Log.d("APIResult", "SIEMA");
                List<Story> stories = response.body();
                for(Story story : stories){
                    MainActivity.this.stories.add(story);
                    mAdapter.setItemList(MainActivity.this.stories);
                    mAdapter.notifyDataSetChanged();
                    Log.d("APIResult", "added");
                }
            }

            @Override
            public void onFailure(Call<List<Story>> call, Throwable t) {
                Log.d("APIResult", "SIEMA2");
            }
        });
    }


    @OnClick(R.id.main_action_button)
    void onClickActionButton(){
        //TODO implement
        Intent intent =  new Intent(getApplicationContext(), AllPointActivity.class);
        startActivity(intent);
    }

}
