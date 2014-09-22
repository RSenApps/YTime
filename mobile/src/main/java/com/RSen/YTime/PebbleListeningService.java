package com.RSen.YTime;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PebbleListeningService extends Service implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {
    public PebbleListeningService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    final Queue<String> messageQueue = new LinkedList<String>();
    protected int queueSize;
    LocationClient mLocationClient;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLocationClient = new LocationClient(this, this, this);
        mLocationClient.connect();
        PebbleKit.registerReceivedAckHandler(getApplicationContext(), new PebbleKit.PebbleAckReceiver(AlarmHelper.PEBBLE_APP_UUID) {
            @Override
            public void receiveAck(Context context, int transactionId) {
                Log.i("pebble", "Received ack for transaction " + transactionId);
                if (messageQueue.size() > 0) {
                    PebbleDictionary returnData = new PebbleDictionary();
                    returnData.addInt32(0, (byte) 1);

                    returnData.addString(1, messageQueue.poll());

                    returnData.addInt32(2, (byte) queueSize);
                    PebbleKit.sendDataToPebbleWithTransactionId(context, AlarmHelper.PEBBLE_APP_UUID, returnData, 1);
                }
            }
        });

        PebbleKit.registerReceivedNackHandler(getApplicationContext(), new PebbleKit.PebbleNackReceiver(AlarmHelper.PEBBLE_APP_UUID) {
            @Override
            public void receiveNack(Context context, int transactionId) {
                Log.i("pebble", "Received nack for transaction " + transactionId);
            }
        });
        PebbleKit.registerReceivedDataHandler(this, new PebbleKit.PebbleDataReceiver(AlarmHelper.PEBBLE_APP_UUID) {
            @Override
            public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {
                //if request locations
                PebbleKit.sendAckToPebble(getApplicationContext(), transactionId);
                if (data.getInteger(0) == 1)
                {
                    AlarmsDataSource dataSource = new AlarmsDataSource(PebbleListeningService.this);
                    try {
                        dataSource.open();
                        List<String> locationsList = dataSource.getLocationNames();
                        for (String location : locationsList)
                        {
                            messageQueue.add(location);
                        }
                        queueSize = locationsList.size();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dataSource.close();
                    PebbleDictionary returnData = new PebbleDictionary();
                    returnData.addInt32(0, (byte) 1);
                    if(queueSize > 0) {
                        returnData.addString(1, messageQueue.poll());
                    }
                    returnData.addInt32(2, (byte) queueSize);
                    PebbleKit.sendDataToPebbleWithTransactionId(context, AlarmHelper.PEBBLE_APP_UUID, returnData, 1);
                }
                else if (data.getInteger(0) == 0)
                {

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    AlarmsDataSource dataSource = new AlarmsDataSource(PebbleListeningService.this);
                                    dataSource.open();
                                    LatLng location = dataSource.getLocationByName(data.getString(3));
                                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                    Date arrival = null;
                                    try {
                                        arrival = sdf.parse(data.getString(2));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                dataSource.createLocationAlarm(location.latitude, location.longitude, arrival.getHours(), arrival.getMinutes(), 0, data.getString(3));
                                AlarmHelper.setAlarms(PebbleListeningService.this, mLocationClient);

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }});
                         thread.start();


                }
                //key is 0, value is 0
                //key is 2, arrival formatted same way as sent
                //key is 3, location, as sent

            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
    }
    @Override
    public void onConnected(Bundle dataBundle) {
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

        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Toast.makeText(this, connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
        }
    }
}
