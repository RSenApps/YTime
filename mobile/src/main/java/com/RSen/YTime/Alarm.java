package com.RSen.YTime;

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
    private String placeName = "";

    public String getRingtoneURI() {
        return ringtoneURI;
    }

    public void setRingtoneURI(String ringtoneURI) {
        this.ringtoneURI = ringtoneURI;
    }

    public String getRingtoneName() {
        return ringtoneName;
    }

    public void setRingtoneName(String ringtoneName) {
        this.ringtoneName = ringtoneName;
    }

    private String ringtoneURI = "";
    private String ringtoneName = "";


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

    public Alarm(long id, double lat, double lng, int arriveHours, int arriveMinutes, int getready, String placeName) {
        locationBased = true;
        this.lat = lat;
        this.lng = lng;
        this.id = id;
        this.arriveHours = arriveHours;
        this.arriveMinutes = arriveMinutes;
        this.placeName = placeName;
        this.getReady = getready;
    }

    public Alarm(long id, int hours, int minutes) {
        locationBased = false;
        this.wakeupHours = hours;
        this.id = id;
        this.wakeupMinutes = minutes;
    }

    public Alarm(long id, int isLocation, double lat, double lng, int arriveHours, int arriveMinutes, int wakeupHours, int wakeupMinutes, int getready, String placeName, int enabled, String ringtoneURI, String ringtoneName) {
        locationBased = (isLocation != 0);
        this.enabled = (enabled != 0);

        this.lat = lat;
        this.lng = lng;
        this.arriveHours = arriveHours;
        this.arriveMinutes = arriveMinutes;
        this.wakeupHours = wakeupHours;
        this.wakeupMinutes = wakeupMinutes;
        this.placeName = placeName;
        this.id = id;
        this.getReady = getready;
        this.ringtoneURI = ringtoneURI;
        this.ringtoneName = ringtoneName;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
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


    public int getGetReady() {
        return getReady;
    }

    public void setGetReady(int getReady) {
        this.getReady = getReady;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}
