package pl.poznajapp.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import pl.poznajapp.R;
import pl.poznajapp.database.Database;
import pl.poznajapp.network.API;
import pl.poznajapp.pojo.Point;
import pl.poznajapp.pojo.Story;
import pl.poznajapp.utils.LocalisationUtils;
import pl.poznajapp.view.activity.PointActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.location.LocationManager.GPS_PROVIDER;

/**
 * Created by Rafał Gawlik on 01.12.2016.
 */

public class AppService extends Service implements LocationListener {

    public static final String TAG = AppService.class.getSimpleName();
    public static final String POINTS = "POINTS";

    LocationManager locationManager;
    static final long MIN_TIME = 100;
    static final float MIN_DISTANCE = 0;

    static final int USER_LOCATION_RADIUS = 100;

    API service;

    int currectStoryId = -1;
    Story story;
    List<Point> points = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://poznaj-wroclaw.herokuapp.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(API.class);

        if(!checkPermission())
            return Service.START_STICKY;
        else
            locationManager.requestLocationUpdates(GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

        loadCurrentStory();
        return Service.START_STICKY;
    }

    boolean checkPermission(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            // TODO: Consider calling
            return true;
        else
            return false;
    }

    private void loadCurrentStory() {
        currectStoryId = Database.getCurrectStory(getSharedPreferences(Database.PREFERENCES_NAME, Database.MODE));
        Call<Story> call = service.getStory(currectStoryId);
        call.enqueue(new Callback<Story>() {
            @Override
            public void onResponse(Call<Story> call, Response<Story> response) {
                story = response.body();
                getPoints(story.getPoints());
            }

            @Override
            public void onFailure(Call<Story> call, Throwable t) {}
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification(String title, String description, List<Integer> images){
        Intent intent = new Intent(this, PointActivity.class);
        intent.putExtra(PointActivity.POINT_TITLE, title);
        intent.putExtra(PointActivity.POINT_DESCRIPTION, description);
        intent.putIntegerArrayListExtra(PointActivity.POINT_IMAGES, (ArrayList<Integer>) images);

        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        Notification notification = new Notification.Builder(this)
                .setContentTitle("Zdobyłeś punkt")
                .setContentText(title)
                .setSmallIcon(R.drawable.ic_directions_walk_white_24dp)
                .setContentIntent(pIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

    @Override
    public void onLocationChanged(Location location) {

        for(Point p : points){
            LatLng latLngPoint = new LatLng(
                    p.getGeometry().getCoordinates().get(1),
                    p.getGeometry().getCoordinates().get(0));

            LatLng latLngUser= new LatLng(
                    location.getLatitude(),
                    location.getLongitude());

            if(LocalisationUtils.distanceBeetwenPoints(latLngPoint, latLngUser) < USER_LOCATION_RADIUS){
                showNotification(p.getProperties().getTitle(), p.getProperties().getDescription(), p.getProperties().getImages());
                points.remove(p);
                if(points.size()==0){
                    showEndNotification();
                    onDestroy();
                }
                break;
            }
        }
    }

    private void showEndNotification() {
        Notification notification = new Notification.Builder(this)
                .setContentTitle("Zakończyłeś trasę")
                .setSmallIcon(R.drawable.ic_directions_walk_white_24dp)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}

    void getPoints(List<Integer> poinstList) {
        for(Integer p : poinstList){
            Call<Point> call = service.getPoint(p);
            call.enqueue(new Callback<Point>() {

                @Override
                public void onResponse(Call<Point> call, Response<Point> response) {
                    Point p =  new Point();
                    p.setType(response.body().getType());
                    p.setGeometry(response.body().getGeometry());
                    p.setProperties(response.body().getProperties());
                    points.add(p);
                }

                @Override
                public void onFailure(Call<Point> call, Throwable t) {

                }
            });
        }
    }
}
