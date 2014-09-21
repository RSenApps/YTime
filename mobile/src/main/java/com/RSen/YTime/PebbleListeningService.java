package com.RSen.YTime;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

public class PebbleListeningService extends Service {
    public PebbleListeningService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PebbleKit.registerReceivedDataHandler(this, new PebbleKit.PebbleDataReceiver(AlarmHelper.PEBBLE_APP_UUID) {
            @Override
            public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {
                //if request locations
                PebbleKit.sendAckToPebble(getApplicationContext(), transactionId);
                if (data.getInteger(0) == 1)
                {
                    AlarmsDataSource dataSource = new AlarmsDataSource(PebbleListeningService.this);
                    String locationsString = "";
                    try {
                        dataSource.open();
                        List<String> locationsList = dataSource.getLocationNames();
                        for (String location : locationsList)
                        {
                            locationsString += location + ",";
                        }
                        locationsString = locationsString.substring(locationsString.length()-1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dataSource.close();
                    PebbleDictionary returnData = new PebbleDictionary();
                    data.addString(0, locationsString);
                    PebbleKit.sendDataToPebble(context, AlarmHelper.PEBBLE_APP_UUID, returnData);
                }

            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
}
