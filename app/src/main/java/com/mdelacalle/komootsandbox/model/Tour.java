package com.mdelacalle.komootsandbox.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Tour extends RealmObject {
    String status;
    String type;
    String date;
    String name;
    Source source;
    String distance;
    String duration;
    String sport;
    User user;
    Image map_image;
    Image map_image_preview;
    @PrimaryKey
    String id;
    RealmList<Coordinate> coordinates;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Image getMap_image() {
        return map_image;
    }

    public void setMap_image(Image map_image) {
        this.map_image = map_image;
    }

    public Image getMap_image_preview() {
        return map_image_preview;
    }

    public void setMap_image_preview(Image map_image_preview) {
        this.map_image_preview = map_image_preview;
    }

    public RealmList<Coordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(RealmList<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "Tour{" +
                "status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", source=" + source +
                ", distance='" + distance + '\'' +
                ", duration='" + duration + '\'' +
                ", sport='" + sport + '\'' +
                ", map_image=" + map_image +
                ", map_image_preview=" + map_image_preview +
                ", id='" + id + '\'' +
                ", coordinates=" + coordinates +
                '}';
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
