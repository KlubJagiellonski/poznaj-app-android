package pl.poznajapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Rafał Gawlik on 06.09.17.
 */

public class Properties {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("point_images")
    @Expose
    private List<Object> pointImages = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Object> getPointImages() {
        return pointImages;
    }

    public void setPointImages(List<Object> pointImages) {
        this.pointImages = pointImages;
    }

}
