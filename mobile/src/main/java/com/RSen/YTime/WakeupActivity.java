package com.RSen.YTime;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;



public class WakeupActivity extends Activity {
    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup);
        PebbleKit.startAppOnPebble(getApplicationContext(), AlarmHelper.PEBBLE_APP_UUID);
        PebbleDictionary data = new PebbleDictionary();
        data.addUint8(0, (byte) 2);
        PebbleKit.sendDataToPebble(this, AlarmHelper.PEBBLE_APP_UUID, data);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();

    }



}
