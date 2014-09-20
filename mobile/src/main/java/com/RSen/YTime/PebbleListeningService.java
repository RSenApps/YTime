package com.RSen.YTime;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

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
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
}
