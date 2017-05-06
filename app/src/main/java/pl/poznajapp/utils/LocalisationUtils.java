package pl.poznajapp.utils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Rafał Gawlik on 01.12.2016.
 */

public class LocalisationUtils {

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
