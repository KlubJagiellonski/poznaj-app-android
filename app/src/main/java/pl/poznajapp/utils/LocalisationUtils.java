package pl.poznajapp.utils;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import pl.poznajapp.R;
import pl.poznajapp.model.Point;

/**
 * Created by rafal on 01.12.2016.
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
}
