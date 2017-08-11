package pl.poznajapp.di;

import javax.inject.Singleton;

import dagger.Component;
import pl.poznajapp.PoznajApp;
import pl.poznajapp.view.MainActivity;

/**
 * Created by Rafał Gawlik on 11.08.17.
 */

@Singleton
@Component
public interface PoznajAppComponent {


    final class Initializer {

        public static PoznajAppComponent init(PoznajApp app) {
            return DaggerPoznajAppComponent.builder()
                    .build();
        }

    }
    void inject(MainActivity mainActivity);

}
