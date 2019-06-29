package com.example.farmersmarket.Models;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.Map;

public class User implements ClusterItem {
    String name;    // the user's first name
    String imgUri;  // the user's img Uri (uploaded to firestore)
    String mission; // consumer or producer or company
    double lat;
    double lon;
    String title;
    public User(){
        this.name = "";
        this.mission = "";
    }

    public User(String name, String mission){
        this.name = name;
        this.mission = mission;
    }

    // setters
    public void setName(String name) {
        this.name = name;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public void setTitle(String title){
        this.title = title;
    }
    public void setLat(double lat){
        this.lat = lat;
    }
    public void setLon(double lon){
        this.lon = lon;
    }

    // getters
    public String getName() {
        return name;
    }

    public String getImgUri() {
        return imgUri;
    }

    public String getMission() {
        return mission;
    }

    public double getLat(){
        return lat;
    }
    public double getLon(){
        return lon;
    }

    @Override
    public LatLng getPosition() {
        LatLng latLng = new LatLng(lat,lon);
        return latLng;
    }

    @Override
    public String getTitle() {
        return title;

    }

    @Override
    public String getSnippet() {
        return null;
    }
}
