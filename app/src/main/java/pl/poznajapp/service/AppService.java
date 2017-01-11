package pl.poznajapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Rafał Gawlik on 01.12.2016.
 */

public class AppService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

}
