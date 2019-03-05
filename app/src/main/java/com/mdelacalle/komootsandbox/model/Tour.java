package com.mdelacalle.komootsandbox.model;

import io.realm.RealmObject;

public class Tour extends RealmObject {
    String status;
    String type;
    String date;
    String name;
    Source source;
    String distance;
    String duration;
    String sport;
    String map_image;
    String map_image_preview;
    String id;

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

    public String getMap_image() {
        return map_image;
    }

    public void setMap_image(String map_image) {
        this.map_image = map_image;
    }

    public String getMap_image_preview() {
        return map_image_preview;
    }

    public void setMap_image_preview(String map_image_preview) {
        this.map_image_preview = map_image_preview;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
                ", map_image='" + map_image + '\'' +
                ", map_image_preview='" + map_image_preview + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
