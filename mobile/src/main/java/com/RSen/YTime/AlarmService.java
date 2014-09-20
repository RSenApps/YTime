package com.RSen.YTime;

import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.UUID;

/**
 * Created by Ryan on 9/20/2014.
 */
public class AlarmService extends Service implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {
    LocationClient mLocationClient;
    long id;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!= null) {
            id = intent.getLongExtra("id", -1);
            if (id != -1)
            {
                mLocationClient = new LocationClient(this, this, this);
                mLocationClient.connect();

            }
            else {
                stopSelf();
            }
        }
        else {
            stopSelf();
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mLocationClient.disconnect();
        super.onDestroy();
    }
    @Override
    public void onConnected(Bundle dataBundle) {
        AlarmHelper.updateAlarm(this, id, mLocationClient);
        AlarmHelper.setAlarms(this, mLocationClient);
        stopSelf();

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

    }
}
