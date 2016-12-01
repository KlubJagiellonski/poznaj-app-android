package pl.poznajapp.utils;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.poznajapp.model.Point;

/**
 * Created by rafal on 01.12.16.
 */

public class PhotoUtils {

    List<Drawable> getRandomImages(List<Point> points){
        List<Drawable> photos = new ArrayList<>();
        for(Point point : points)
            photos.addAll(point.getImages());
        Collections.shuffle(photos);
        List<Drawable> promoPhotos = new ArrayList<>();
        for(int i = 0 ; i < 5; i++){
            promoPhotos.add(photos.get(i));
        }
        return promoPhotos;
    }




}
