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
    private int arriveHours = 0;
    private int arriveMinutes = 0;
    private int wakeupHours = 0;

    public int getArriveHours() {
        return arriveHours;
    }

    public void setArriveHours(int arriveHours) {
        this.arriveHours = arriveHours;
    }

    public int getArriveMinutes() {
        return arriveMinutes;
    }

    public void setArriveMinutes(int arriveMinutes) {
        this.arriveMinutes = arriveMinutes;
    }

    public int getWakeupHours() {
        return wakeupHours;
    }

    public void setWakeupHours(int wakeupHours) {
        this.wakeupHours = wakeupHours;
    }

    public int getWakeupMinutes() {
        return wakeupMinutes;
    }

    public void setWakeupMinutes(int wakeupMinutes) {
        this.wakeupMinutes = wakeupMinutes;
    }

    private int wakeupMinutes = 0;
    private int getReady = 0;
    private boolean enabled = true;
    public Alarm(long id, double lat, double lng, int arriveHours, int arriveMinutes, int getready)
    {
        locationBased = true;
        this.lat = lat;
        this.lng = lng;
        this.arriveHours = arriveHours;
        this.arriveMinutes = arriveMinutes;
    }
    public Alarm(long id, int hours, int minutes)
    {
        locationBased = false;
        this.wakeupHours = hours;
        this.wakeupMinutes = minutes;
    }
    public Alarm(long id, int isLocation, double lat, double lng, int arriveHours, int arriveMinutes, int wakeupHours, int wakeupMinutes, int getready)
    {
        locationBased = (isLocation != 0);
        this.lat = lat;
        this.lng = lng;
        this.arriveHours = arriveHours;
        this.arriveMinutes = arriveMinutes;
        this.wakeupHours = wakeupHours;
        this.wakeupMinutes = wakeupMinutes;

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


    public int getGetReady()
    {
        return getReady;
    }
    public void setGetReady (int getReady)
    {
        this.getReady = getReady;
    }
    public boolean isEnabled () {return enabled;}
    public void setEnabled (boolean enabled) {this.enabled = enabled;}



}
