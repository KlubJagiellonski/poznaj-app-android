package pl.poznajapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rafał Gawlik on 06.09.17.
 */

public class Feature {

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    @SerializedName("properties")
    @Expose
    private Properties properties;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

}