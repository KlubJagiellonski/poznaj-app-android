package pl.poznajapp.database.model;

import io.realm.RealmObject;

/**
 * Created by Rafał Gawlik on 23.08.17.
 */

public class PointObject extends RealmObject {

    private Integer id;
    private String title;
    private Double latitude;
    private Double longitude;
    private Boolean visited;

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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getVisited() {
        return visited;
    }

    public void setVisited(Boolean visited) {
        this.visited = visited;
    }
}
