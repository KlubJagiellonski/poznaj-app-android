package pl.poznajapp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.poznajapp.R;
import pl.poznajapp.database.Database;
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

import static pl.poznajapp.network.API.API_URL;

/**
 * Created by Rafał Gawlik on 30.11.2016.
 */

public class MainActivity extends AppCompatActivity {

    final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.main_toolbar) Toolbar toolbar;
    @BindView(R.id.main_trip_list) RecyclerView tripList;
    @BindView(R.id.activity_main) LinearLayout main;
    @BindView(R.id.main_current_story_title) TextView storyTitle;
    @BindView(R.id.main_current_story) RelativeLayout currentStory;
    @BindView(R.id.main_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;

    int currectStoryId = -1;
    Story story;

    StoryAdapter mAdapter;
    ArrayList<Story> stories;
    API service;

    private static final int ANIM_DURATION_TOOLBAR = 200;
    private static final int ANIM_DURATION_LIST = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(API.class);
        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCurrentStory();
        getStoriesList();
    }

    void initUI() {
        initToolbar();
        initList();
        initAnimations();
        initClickListsners();
    }

    void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        toolbar.inflateMenu(R.menu.main_manu);
    }

    @OnClick(R.id.main_current_story_clean)
    void deleteCurrentStory(){
        Database.setCurrectStory(getSharedPreferences(Database.PREFERENCES_NAME, Database.MODE), -1);
        currentStory.setVisibility(View.GONE);
        Snackbar.make(main, getResources().getString(R.string.story_delete_current_story), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_manu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // TODO
            // menu elements
        }
        return false;
    }

    void initAnimations() {
        int actionbarSize = Utils.dpToPx(56);
        toolbar.setTranslationY(-actionbarSize);
        toolbar.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setInterpolator(new DecelerateInterpolator(1.f))
                .setStartDelay(300);

        tripList.setTranslationY(2000);
        tripList.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(1.f))
                .setStartDelay(500)
                .setDuration(ANIM_DURATION_LIST)
                .start();

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    void initList() {
        mAdapter = new StoryAdapter(getApplicationContext(), new ArrayList<Story>());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        tripList.setLayoutManager(mLayoutManager);
        tripList.setItemAnimator(new DefaultItemAnimator());
        tripList.setAdapter(mAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getStoriesList();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    void initClickListsners() {
        tripList.addOnItemTouchListener(new RecyclerItemClickListener(this, tripList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (stories == null) {
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), StoryActivity.class);
                intent.putExtra(StoryActivity.EXTRA_STORY, stories.get(position).getId());
                startActivity(intent);

            }

            @Override
            public void onItemLongClick(View view, int position) {
                //TODO implement
            }
        }));
    }

    void getStoriesList() {
        stories = new ArrayList<>();

        Call<List<Story>> call = service.listStories();
        call.enqueue(new Callback<List<Story>>() {
            @Override
            public void onResponse(Call<List<Story>> call, Response<List<Story>> response) {
                List<Story> stories = response.body();
                for (Story story : stories) {
                    MainActivity.this.stories.add(story);
                    mAdapter.setItemList(MainActivity.this.stories);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Story>> call, Throwable t) {
                Log.d(TAG, "error");
            }
        });
    }



    @OnClick(R.id.main_current_story)
    void currentStoryClick(){
        Intent intent = new Intent(getApplicationContext(), StoryActivity.class);
        intent.putExtra(StoryActivity.EXTRA_STORY, currectStoryId);
        startActivity(intent);
    }

    private void loadCurrentStory() {
        currectStoryId = Database.getCurrectStory(getSharedPreferences(Database.PREFERENCES_NAME, Database.MODE));
        if (currectStoryId > 0) {
            currentStory.setVisibility(View.VISIBLE);
            Call<Story> call = service.getStory(currectStoryId);
            call.enqueue(new Callback<Story>() {
                @Override
                public void onResponse(Call<Story> call, Response<Story> response) {
                    story = response.body();
                    storyTitle.setText(story.getTitle());
                }

                @Override
                public void onFailure(Call<Story> call, Throwable t) {
                }
            });
        }
    }
}
