package pl.poznajapp;

import android.app.Application;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pl.poznajapp.helpers.Config;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rafał Gawlik on 17.08.17.
 */

public class PoznajApp extends Application {

    public static Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Config.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(Config.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(this.getResources().getString(R.string.poznaj_app_api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();


    }

}
