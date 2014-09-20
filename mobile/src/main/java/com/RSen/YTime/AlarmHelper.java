package com.RSen.YTime;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.google.android.gms.location.LocationClient;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ryan on 9/20/2014.
 */
public class AlarmHelper extends BroadcastReceiver {

    public final static UUID PEBBLE_APP_UUID = UUID.fromString("bc20efe8-f0a9-4d86-912c-a4293b3df8da");

    @Override
    public void onReceive(Context context, Intent intent) {
        setAlarms(context, null);
    }

    public static void updateAlarm (Context context, long id, LocationClient locationClient)
    {
        AlarmsDataSource dataSource = new AlarmsDataSource(context);
        try {
            dataSource.open();
            Alarm alarm = dataSource.getAlarmById(id);

            Location location = locationClient.getLastLocation();
            int duration = BingMapsAPI.getTimeToLocation(location.getLatitude(), location.getLongitude(), alarm.getLat(), alarm.getLng(), alarm.getArriveHours(), alarm.getArriveMinutes(), BingMapsAPI.TRANSIT_MODE.driving, true);
            Toast.makeText(context, "New duration:" + duration, Toast.LENGTH_LONG).show();
            int arriveMinutes = alarm.getArriveHours() * 60 + alarm.getArriveMinutes();
            arriveMinutes -= duration;
            alarm.setWakeupHours(arriveMinutes / 60);
            alarm.setWakeupMinutes(arriveMinutes % 60);
            dataSource.updateAlarm(alarm);
        } catch (SQLException e) {
            e.printStackTrace();
        }
       dataSource.close();
    }
    private static void setAlarm(Context context, Calendar calendar, PendingIntent pIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
         alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);

    }
    public static void setAlarms(Context context, LocationClient locationClient ) {
        cancelAlarms(context);

        AlarmsDataSource dataSource = new AlarmsDataSource(context);
        try {
            dataSource.open();
            List<Alarm> alarms = dataSource.getAllAlarms();
            PendingIntent nextWakeupIntent=null;
            Calendar nextWakeup = null;
            Calendar nextAlarmTime = null;
            for (Alarm alarm : alarms) {
                if (alarm.isEnabled()) {



                    Calendar calendar = Calendar.getInstance();

                    calendar.set(Calendar.HOUR_OF_DAY, alarm.getWakeupHours());
                    calendar.set(Calendar.MINUTE, alarm.getWakeupMinutes());
                    calendar.set(Calendar.SECOND, 0);
                    if (locationClient != null && alarm.getWakeupHours() + alarm.getWakeupMinutes() == 0)
                    {
                        Location location = locationClient.getLastLocation();
                        int duration = BingMapsAPI.getTimeToLocation(location.getLatitude(), location.getLongitude(), alarm.getLat(), alarm.getLng(), alarm.getArriveHours(), alarm.getWakeupMinutes(), BingMapsAPI.TRANSIT_MODE.driving, true);

                        calendar.set(Calendar.HOUR_OF_DAY, alarm.getArriveHours());
                        calendar.set(Calendar.MINUTE, alarm.getArriveMinutes());
                        calendar.add(Calendar.MINUTE, -1 * duration);
                        alarm.setWakeupHours(calendar.get(Calendar.HOUR_OF_DAY));
                        alarm.setWakeupMinutes(calendar.get(Calendar.MINUTE));
                        dataSource.updateAlarm(alarm);
                    }

                    if (calendar.before(Calendar.getInstance()))
                    {
                        calendar.add(Calendar.DATE, 1);
                    }




                    long minutesBeforeAlarm = calendar.getTime().getTime() - Calendar.getInstance().getTime().getTime()/1000/60;
                    if (nextAlarmTime == null || nextAlarmTime.after(calendar))
                    {
                        nextAlarmTime = calendar;
                    }
                    if (minutesBeforeAlarm > 70)
                    {
                        calendar.add(Calendar.HOUR_OF_DAY, -1);
                        if (nextWakeup == null || nextWakeup.after(calendar))
                        {
                            nextWakeup = calendar;
                            nextWakeupIntent = createPendingIntent(context, alarm);
                        }
                    }
                    else if (minutesBeforeAlarm > 40)
                    {
                        calendar.add(Calendar.MINUTE, -30);
                        if (nextWakeup == null || nextWakeup.after(calendar))
                        {
                            nextWakeup = calendar;
                            nextWakeupIntent = createPendingIntent(context, alarm);
                        }
                    }
                    else if (minutesBeforeAlarm > 15)
                    {
                        calendar.add(Calendar.MINUTE, -10);
                        if (nextWakeup == null || nextWakeup.after(calendar))
                        {
                            nextWakeup = calendar;
                            nextWakeupIntent = createPendingIntent(context, alarm);
                        }
                    }
                    else {
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        if (Build.VERSION.SDK_INT > 18) {
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getActivity(context, 1928, new Intent(context, WakeupActivity.class), PendingIntent.FLAG_ONE_SHOT));
                        }
                        else {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getActivity(context, 1928, new Intent(context, WakeupActivity.class), PendingIntent.FLAG_ONE_SHOT));

                        }
                    }
                }
            }
            if (nextWakeup != null && nextWakeupIntent != null) {
                Log.d("wakeup", "Next wakeup set to: " + nextWakeup.get(Calendar.HOUR_OF_DAY) + ":" + nextWakeup.get(Calendar.MINUTE));
                setAlarm(context, nextWakeup, nextWakeupIntent);
            }
            if (nextAlarmTime != null)
            {
                PebbleDictionary data = new PebbleDictionary();
                data.addUint8(0, (byte) 1);
                data.addUint8(1, (byte) nextAlarmTime.get(Calendar.HOUR_OF_DAY));
                data.addUint8(2, (byte) nextAlarmTime.get(Calendar.MINUTE));
                PebbleKit.sendDataToPebble(context, PEBBLE_APP_UUID, data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dataSource.close();
    }
    private static PendingIntent createPendingIntent(Context context, Alarm alarm) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.putExtra("id", alarm.getId());
        return PendingIntent.getService(context, 2492, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void cancelAlarms(Context context) {
        AlarmsDataSource dataSource = new AlarmsDataSource(context);
        try {
            dataSource.open();
            List<Alarm> alarms = dataSource.getAllAlarms();

            if (alarms != null) {
                for (Alarm alarm : alarms) {
                    if (alarm.isEnabled()) {
                        PendingIntent pIntent = createPendingIntent(context, alarm);

                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        alarmManager.cancel(pIntent);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dataSource.close();

    }

}
