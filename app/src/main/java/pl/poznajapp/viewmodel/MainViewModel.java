package pl.poznajapp.viewmodel;

import android.databinding.ObservableField;

/**
 * Created by Rafał Gawlik on 17.08.17.
 */

public class MainViewModel implements ViewModel {

    public final ObservableField<String> text = new ObservableField<>();


    @Override
    public void onCreate() {
        text.set("Test text");
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
