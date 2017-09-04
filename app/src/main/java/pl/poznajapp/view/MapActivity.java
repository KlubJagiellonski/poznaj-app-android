package pl.poznajapp.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import pl.poznajapp.R;
import pl.poznajapp.view.base.BaseAppCompatActivity;

/**
 * Created by Rafał Gawlik on 02.09.17.
 */

public class MapActivity extends BaseAppCompatActivity {

    public static final String EXTRAS_STORY_ID = "EXTRAS_STORY_ID";

    public static Intent getConfigureIntent(Context context, Integer storyId) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra(EXTRAS_STORY_ID, storyId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

    }


}
