package pl.poznajapp.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.poznajapp.API.APIService;
import pl.poznajapp.PoznajApp;
import pl.poznajapp.R;
import pl.poznajapp.model.Story;
import pl.poznajapp.view.base.BaseActivity;
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
public class StoryDetailsActivity extends BaseActivity {

    public static final String EXTRAS_STORY_ID = "EXTRAS_STORY_ID";

    @BindView(R.id.story_details_toolbar) Toolbar toolbar;
    @BindView(R.id.story_details_back_iv) ImageView backgroundImage;
    @BindView(R.id.story_duration_text_tv) TextView duration;
    @BindView(R.id.story_details_text_tv) TextView description;
    @BindView(R.id.story_details_fab) FloatingActionButton fab;

    private APIService service;

    public static Intent getConfigureIntent(Context context, Integer storyId){
        Intent intent =  new Intent(context, StoryDetailsActivity.class);
        intent.putExtra(EXTRAS_STORY_ID, storyId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_details);
        ButterKnife.bind(this);

        if(getIntent().getExtras() != null){
            loadStory(getIntent().getIntExtra(EXTRAS_STORY_ID, 0));
        } else {
            finish();
        }
    }

    private void loadStory(Integer id) {
        service = PoznajApp.retrofit.create(APIService.class);

        Call<Story> storyCall = service.getStory(id);
        storyCall.enqueue(new Callback<Story>() {
            @Override
            public void onResponse(Call<Story> call, Response<Story> response) {
                Timber.d(response.message());

                Story story = response.body();
                toolbar.setTitle(story.getTitle());
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

}
