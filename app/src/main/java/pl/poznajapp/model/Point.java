package pl.poznajapp.model;

import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * Created by Rafał Gawlik on 01.12.2016.
 */

public class Point {

    long mLant;
    long mLong;
    String mTitle;
    String mDescription;
    List<Drawable> images;

    public long getmLant() {
        return mLant;
    }

    public void setmLant(long mLant) {
        this.mLant = mLant;
    }

    public long getmLong() {
        return mLong;
    }

    public void setmLong(long mLong) {
        this.mLong = mLong;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public List<Drawable> getImages() {
        return images;
    }

    public void setImages(List<Drawable> images) {
        this.images = images;
    }
}
