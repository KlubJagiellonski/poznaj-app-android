package pl.poznajapp.utils;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import pl.poznajapp.R;
import pl.poznajapp.model.Point;

/**
 * Created by Rafał Gawlik on 01.12.2016.
 */

public class LocalisationUtils {

    void pointIsNear(Context context, Location userPosition, List<Point> points){
        int distance = context.getResources().getInteger(R.integer.standard_destination_acc);
        for(Point point : points){
            Location locPoint = new Location("");
            locPoint.setLatitude(point.getmLant());
            locPoint.setLongitude(point.getmLong());
            if(userPosition.distanceTo(locPoint) < distance) {
                //TODO send notification
            }
        }
    }

    public static float distanceBeetwenPoints(LatLng from, LatLng to) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(to.latitude-from.latitude);
        double dLng = Math.toRadians(to.longitude-from.longitude);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(from.latitude)) * Math.cos(Math.toRadians(to.latitude)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }



}
