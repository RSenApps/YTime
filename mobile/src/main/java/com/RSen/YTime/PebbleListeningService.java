package com.RSen.YTime;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PebbleListeningService extends Service {
    public PebbleListeningService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    final Queue<String> messageQueue = new LinkedList<String>();
    protected int queueSize;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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
                    AlarmsDataSource dataSource = new AlarmsDataSource(PebbleListeningService.this);
                    try {
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
                        AlarmHelper.setAlarms(PebbleListeningService.this, null);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
                //key is 0, value is 0
                //key is 2, arrival formatted same way as sent
                //key is 3, location, as sent

            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
}
