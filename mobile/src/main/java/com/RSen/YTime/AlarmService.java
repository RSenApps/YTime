package com.RSen.YTime;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

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
        if (intent != null) {
            id = intent.getLongExtra("id", -1);
            if (id != -1) {
                mLocationClient = new LocationClient(this, this, this);
                mLocationClient.connect();

            } else {
                stopSelf();
            }
        } else {
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
        AlarmHelper.updateAlarmTime(this, id, mLocationClient);
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
