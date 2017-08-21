package pl.poznajapp.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.util.Log;

import pl.poznajapp.model.Story;
import timber.log.Timber;

/**
 * Created by Rafał Gawlik on 18.08.17.
 */

public class StoryViewModel extends BaseObservable implements ViewModel {

    private Story model;
    public final ObservableField<String> winner = new ObservableField<>();

    public StoryViewModel(Story model) {
        this.model = model;
        this.winner.set("SIEMMMAAAAAA");
    }

    public Story getModel() {
        return model;
    }

    public String getTitle(){
        Log.d("SIUEMKA", "SIEMMA1 title");
        return "SIEMAA";
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
}
