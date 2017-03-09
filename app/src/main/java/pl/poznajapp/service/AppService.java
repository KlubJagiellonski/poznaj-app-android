package pl.poznajapp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import pl.poznajapp.R;
import pl.poznajapp.pojo.Point;

/**
 * Created by Rafał Gawlik on 01.12.2016.
 */

public class AppService extends Service {

    public static final String TAG = "AppService";
    public static final String POINTS = "POINTS";

    public static final int notify = 300000;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;

    ArrayList<String> points;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        points = intent.getStringArrayListExtra(POINTS);

        if (mTimer != null)
            mTimer.cancel();
        else
            mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);


        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    private void showNotification(){
        Intent intent = new Intent(this, Point.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        Notification notification = new Notification.Builder(this)
                .setContentTitle("new notification")
                .setContentText("Subject")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //TODO check location
                    //TODO check near points
                    //TODO show notification
                    showNotification();



                }
            });
        }
    }
}
