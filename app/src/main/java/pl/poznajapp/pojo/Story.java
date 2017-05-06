package pl.poznajapp.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Rafał Gawlik on 11.01.17.
 */
public class Story {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("first_point")
    @Expose
    private Integer firstPoint;
    @SerializedName("points")
    @Expose
    private List<Integer> points = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getFirstPoint() {
        return firstPoint;
    }

    public void setFirstPoint(Integer firstPoint) {
        this.firstPoint = firstPoint;
    }

    public List<Integer> getPoints() {
        return points;
    }

    public void setPoints(List<Integer> points) {
        this.points = points;
    }

}