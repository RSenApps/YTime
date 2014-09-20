package com.RSen.YTime;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class CreateAlarmActivity extends Activity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener{
    LocationClient mLocationClient;
    GoogleMap map;
    int hours = 12;
    int minutes = 0;
    AlarmsDataSource dataSource;
    double lat = 0;
    double lng = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alarm);
        if (GoogleMapsAPI.servicesConnected(this)) {
            dataSource = new AlarmsDataSource(this);

            MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
            map = mapFragment.getMap();
            map.setTrafficEnabled(true);
            map.setMyLocationEnabled(true);

            mLocationClient = new LocationClient(this, this, this);
            final EditText getReadyInput = (EditText) findViewById(R.id.get_ready_input);
            final EditText timeInput = (EditText) findViewById(R.id.time_input);
            timeInput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimePickerDialog dialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(RadialPickerLayout radialPickerLayout, int i, int i2) {
                            hours = i;
                            minutes = i2;
                            Calendar date = Calendar.getInstance();
                            date.set(Calendar.HOUR_OF_DAY, i);
                            date.set(Calendar.MINUTE, i2);
                            DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                            timeInput.setText(dateFormat.format(date.getTime()));
                        }
                    }, hours, minutes, false);
                    dialog.setThemeDark(true);
                    dialog.show(getFragmentManager(), "timepicker");
                }
            });
            findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*
                    final Handler handler = new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message message) {
                            Toast.makeText(CreateAlarmActivity.this, "Estimated time to location: " + message.obj.toString(), Toast.LENGTH_LONG).show();

                            return true;
                        }
                    });
                    */

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            dataSource.createLocationAlarm(lat, lng, hours, minutes, Integer.parseInt(getReadyInput.getText().toString()));
                            AlarmHelper.setAlarms(CreateAlarmActivity.this, mLocationClient);
                        }
                    });
                    thread.start();


                }
            });
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Connect the client.
        mLocationClient.connect();
    }
    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        dataSource.close();
        super.onStop();
    }
    @Override
    public void onConnected(Bundle dataBundle) {
        Location location = mLocationClient.getLastLocation();
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));

        // Zoom in the Google Map
        map.animateCamera(CameraUpdateFactory.zoomTo(12));
        final AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.location_input);
        final PlacesAutoCompleteAdapter adapter = new PlacesAutoCompleteAdapter(this, mLocationClient, android.R.layout.simple_list_item_1);
        autoCompView.setAdapter(adapter);
        autoCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final Handler handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message message) {
                        map.clear();
                        // Zoom in the Google Map
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom((LatLng) message.obj, 15));
                        MarkerOptions options = new MarkerOptions();
                        options.position((LatLng) message.obj);
                        options.title(adapter.getItem(i));
                        map.addMarker(options);
                        return true;
                    }
                });
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = handler.obtainMessage();
                        LatLng latLng  = GoogleMapsAPI.getLocationForPlace(adapter.getPlaceID(i));
                        lat = latLng.latitude;
                        lng = latLng.longitude;
                        message.obj = latLng;
                        handler.sendMessage(message);
                    }
                });
                thread.start();

            }
        });


    }
    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
    }
    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        9000);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Toast.makeText(this, connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
        }
    }

}
