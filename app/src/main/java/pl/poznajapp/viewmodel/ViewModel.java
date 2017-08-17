package pl.poznajapp.viewmodel;

/**
 * Created by Rafał Gawlik on 17.08.17.
 */

public interface ViewModel {

    void onCreate();
    void onPause();
    void onResume();
    void onDestroy();

}
