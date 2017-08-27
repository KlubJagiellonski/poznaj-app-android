package pl.poznajapp.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import pl.poznajapp.API.APIService;
import pl.poznajapp.PoznajApp;
import pl.poznajapp.R;
import pl.poznajapp.model.Story;
import pl.poznajapp.view.base.BaseAppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Rafał Gawlik on 22.08.17.
 */

// TODO
// redesign
public class StoryDetailsActivity extends BaseAppCompatActivity {

    public static final String EXTRAS_STORY_ID = "EXTRAS_STORY_ID";

    ImageView backgroundImage;
    TextView duration;
    TextView description;
    FloatingActionButton fab;

    private APIService service;

    public static Intent getConfigureIntent(Context context, Integer storyId) {
        Intent intent = new Intent(context, StoryDetailsActivity.class);
        intent.putExtra(EXTRAS_STORY_ID, storyId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_details);

        if (getIntent().getExtras() != null) {
            setupView();
            loadStory(getIntent().getIntExtra(EXTRAS_STORY_ID, -1));
        } else {
            finish();
        }
    }

    private void setupView() {
        backgroundImage = (ImageView) findViewById(R.id.story_details_back_iv);
        duration = (TextView) findViewById(R.id.story_duration_text_tv);
        description = (TextView) findViewById(R.id.story_details_text_tv);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void loadStory(Integer id) {
        if (id.equals(-1))
            finish();

        service = PoznajApp.retrofit.create(APIService.class);

        Call<Story> storyCall = service.getStory(id);
        storyCall.enqueue(new Callback<Story>() {
            @Override
            public void onResponse(Call<Story> call, Response<Story> response) {
                Timber.d(response.message());

                Story story = response.body();
                getSupportActionBar().setTitle(story.getTitle());
                duration.setText(story.getDuration());
                description.setText(story.getDescription());

                //TODO
                Picasso.with(getApplicationContext()).load("http://i.imgur.com/DvpvklR.png").into(backgroundImage);

            }

            @Override
            public void onFailure(Call<Story> call, Throwable t) {
                Timber.e(t);
            }
        });

    }

    public void onMapClick(View view) {

    }

    public void onStartClick(View view) {
        //TODO load points into DB
        //bind service
    }
}
