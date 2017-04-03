package pl.poznajapp.pojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rafał Gawlik on 11.01.17.
 */
public class Story {

    private Integer id;
    private String title;
    private String description;
    private String duration;
    private Integer firstPoint;
    private List<Integer> points = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Integer> getPoints() {
        return points;
    }

    public void setPoints(List<Integer> points) {
        this.points = points;
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

    public Integer getFirst_Point() {
        return firstPoint;
    }

    public void setFirst_Point(Integer firstPoint) {
        this.firstPoint = firstPoint;
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

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}