package com.RSen.YTime;

import java.util.Calendar;

/**
 * Created by Ryan on 9/20/2014.
 */
public class Alarm {
    private long id;
    private boolean locationBased = true;
    private double lat = 0;
    private double lng = 0;
    private int hours = 0;
    private int minutes = 0;
    private int getReady = 0;
    public Alarm(long id, double lat, double lng, int hours, int minutes, int getready)
    {
        locationBased = true;
        this.lat = lat;
        this.lng = lng;
        this.hours = hours;
        this.minutes = minutes;
    }
    public Alarm(long id, int hours, int minutes)
    {
        locationBased = false;
        this.hours = hours;
        this.minutes = minutes;
    }
    public Alarm(long id, int isLocation, double lat, double lng, int hours, int minutes, int getready)
    {
        locationBased = (isLocation != 0);
        this.lat = lat;
        this.lng = lng;
        this.hours = hours;
        this.minutes = minutes;

    }
    public long getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }
    public boolean isLocationBased() {
        return locationBased;
    }

    public void setLocationBased(boolean locationBased) {
        this.locationBased = locationBased;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
    public int getGetReady()
    {
        return getReady;
    }
    public void setGetReady (int getReady)
    {
        this.getReady = getReady;
    }





}
