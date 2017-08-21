package pl.poznajapp.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.util.Log;

import java.util.List;

import pl.poznajapp.model.Story;
import timber.log.Timber;

/**
 * Created by Rafał Gawlik on 17.08.17.
 */

public class MainViewModel extends BaseObservable implements ViewModel {

    @Bindable
    public ObservableArrayList<StoryViewModel> stories;

    public MainViewModel() {
        this.stories = new ObservableArrayList<>();
        stories.add(new StoryViewModel(new Story()));
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

    public void loadStories(List<Story> stories) {
        for ( Story s : stories ){
            Log.d("SIUEMKA", "SIEMMA1");
            addStory(s);
        }
    }

    public void addStory(Story story) {
        Timber.d("SIEMKA 1");
        this.stories.add(new StoryViewModel(story));
    }

}
