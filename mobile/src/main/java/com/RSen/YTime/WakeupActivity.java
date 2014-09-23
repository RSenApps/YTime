package com.RSen.YTime;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;



public class WakeupActivity extends Activity {

    MediaPlayer mMediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_wakeup);
        PebbleKit.startAppOnPebble(getApplicationContext(), AlarmHelper.PEBBLE_APP_UUID);
        PebbleDictionary data = new PebbleDictionary();
        data.addUint8(0, (byte) 2);
        PebbleKit.sendDataToPebble(this, AlarmHelper.PEBBLE_APP_UUID, data);



        try {
            Uri alert =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(this, alert);
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch(Exception e) {
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }
}
