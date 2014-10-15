package com.RSen.YTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Ryan on 9/20/2014.
 */
public class BingMapsAPI {
    private static final String MAPS_API_BASE = "http://dev.virtualearth.net/REST/V1";
    private static final String TYPE_ROUTES = "/Routes";
    private static final String MODE_DRIVING = "/Driving";

    private static final String API_KEY = "AmXC0roDXBSoAn6AUz9ScsUWbYrvoqCvjerGZ-Q4O1KxFfea9AHCi3cZ8Prl5aIM";

    public enum TRANSIT_MODE {driving, transit, walking}

    public static int getTimeToLocation(double currentlat, double currentLng, double lat, double lng, int arrivalHours, int arrivalMinutes, TRANSIT_MODE mode, boolean useTraffic) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(MAPS_API_BASE + TYPE_ROUTES + MODE_DRIVING);
            sb.append("?o=json");
            sb.append("&key=" + API_KEY);
            sb.append("&wp.0=" + currentlat + "," + currentLng);
            sb.append("&wp.1=" + lat + "," + lng);
            if (useTraffic) {
                sb.append("&optmz=timeWithTraffic");
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, arrivalHours);
            calendar.set(Calendar.MINUTE, arrivalMinutes);
            calendar.set(Calendar.SECOND, 0);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            sb.append("&dt=" + sdf.format(calendar.getTime()));
            sb.append("&tt=Arrival");
            sb.append("&rpo=none");
            if (mode.equals(TRANSIT_MODE.walking)) {
                sb.append("travelMode=Walking");
            } else if (mode.equals(TRANSIT_MODE.transit)) {
                sb.append("travelMode=Transit");
            }
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONObject resourceObj = jsonObj.getJSONArray("resourceSets").getJSONObject(0).getJSONArray("resources").getJSONObject(0);
            int duration;
            if (useTraffic) {
                duration = resourceObj.getInt("travelDurationTraffic");
            } else {
                duration = resourceObj.getInt("travelDuration");
            }
            String durationUnit = resourceObj.getString("durationUnit");
            float divider = 1;
            if (durationUnit.equals("Second")) {
                divider = 60;
            } else if (durationUnit.equals("Hour")) {
                divider = 1 / 60;
            } else if (durationUnit.equals("Day")) {
                divider = 1 / 60 / 24;
            }
            return (int) Math.round((double) duration / divider);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
