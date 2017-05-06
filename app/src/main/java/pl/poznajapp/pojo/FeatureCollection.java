package pl.poznajapp.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rafal on 06.05.17.
 */

public class FeatureCollection {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("features")
    @Expose
    private List<Point> features = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Point> getFeatures() {
        return features;
    }

    public void setFeatures(List<Point> features) {
        this.features = features;
    }

}
