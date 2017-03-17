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
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pl.poznajapp.R;
import pl.poznajapp.network.API;
import pl.poznajapp.pojo.Point;
import pl.poznajapp.pojo.Story;
import pl.poznajapp.utils.LocalisationUtils;
import pl.poznajapp.utils.Utils;
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
    static final long MIN_TIME = 1000;
    static final float MIN_DISTANCE = 30;

    API service;

    ArrayList<Point> points;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://poznaj-wroclaw.herokuapp.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(API.class);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return Service.START_STICKY;
        }
        locationManager.requestLocationUpdates(GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, (LocationListener) this);

        points = new ArrayList<>();
        getPoints(intent.getStringArrayListExtra(POINTS));

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    private void showNotification(Point p){
        Intent intent = new Intent(this, PointActivity.class);
        intent.putExtra(PointActivity.POINT_TITLE, p.getProperties().getTitle());
        intent.putExtra(PointActivity.POINT_DESCRIPTION, p.getProperties().getDescription());
        intent.putStringArrayListExtra(PointActivity.POINT_IMAGES, new ArrayList<String>(p.getProperties().getImages()));

        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        Notification notification = new Notification.Builder(this)
                .setContentTitle("Zdobyłeś punkt")
                .setContentText(p.getProperties().getTitle())
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
                    p.getGeometry().getCoordinates().get(0),
                    p.getGeometry().getCoordinates().get(1));

            LatLng latLngUser= new LatLng(
                    location.getLatitude(),
                    location.getLongitude());

            if(LocalisationUtils.distanceBeetwenPoints(latLngPoint, latLngUser) < 50)
                showNotification(p);
            points.remove(p);
            break;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void getPoints(final List<String> poinstList) {

        for(String p : poinstList){
            Call<Point> call = service.getPoint(Integer.parseInt(p));
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
