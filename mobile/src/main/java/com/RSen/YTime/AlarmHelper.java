package com.RSen.YTime;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.google.android.gms.location.LocationClient;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ryan on 9/20/2014.
 */
public class AlarmHelper extends BroadcastReceiver {

    public final static UUID PEBBLE_APP_UUID = UUID.fromString("bc20efe8-f0a9-4d86-912c-a4293b3df8da");

    @Override
    public void onReceive(Context context, Intent intent) {
        if (PebbleKit.isWatchConnected(context)) {
            context.startService(new Intent(context, PebbleListeningService.class));
        }
        setAlarms(context, null);
    }

    public static void updateAlarm(Context context, Alarm alarm) {
        AlarmsDataSource dataSource = new AlarmsDataSource(context);
        try {
            dataSource.open();
            dataSource.updateAlarm(alarm);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dataSource.close();
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("update"));
    }

    public static void updateAlarmTime(Context context, long id, LocationClient locationClient) {
        AlarmsDataSource dataSource = new AlarmsDataSource(context);
        try {
            dataSource.open();
            Alarm alarm = dataSource.getAlarmById(id);


            Calendar calendar = Calendar.getInstance();
            Location location = locationClient.getLastLocation();
            int duration = BingMapsAPI.getTimeToLocation(location.getLatitude(), location.getLongitude(), alarm.getLat(), alarm.getLng(), alarm.getArriveHours(), alarm.getWakeupMinutes(), BingMapsAPI.TRANSIT_MODE.driving, true);

            calendar.set(Calendar.HOUR_OF_DAY, alarm.getArriveHours());
            calendar.set(Calendar.MINUTE, alarm.getArriveMinutes());
            calendar.add(Calendar.MINUTE, -1 * (duration));
            calendar.add(Calendar.MINUTE, -1 * alarm.getGetReady());
            if (calendar.before(Calendar.getInstance())) {
                if (Calendar.getInstance().getTime().getTime() - calendar.getTime().getTime() < 30) {
                    //ring now
                    calendar = Calendar.getInstance();
                    calendar.add(Calendar.SECOND, 3);
                }
                //don't add another day as we are just updating the hour and day
            }
            alarm.setWakeupHours(calendar.get(Calendar.HOUR_OF_DAY));
            alarm.setWakeupMinutes(calendar.get(Calendar.MINUTE));
            dataSource.updateAlarm(alarm);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dataSource.close();
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("update"));
    }

    private static void setAlarm(Context context, Calendar calendar, PendingIntent pIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);

    }

    public static void setAlarms(Context context, LocationClient locationClient) {
        cancelAlarms(context);

        AlarmsDataSource dataSource = new AlarmsDataSource(context);
        try {
            dataSource.open();
            List<Alarm> alarms = dataSource.getAllAlarms();
            PendingIntent nextWakeupIntent = null;
            Calendar nextWakeup = null;
            Calendar nextAlarmTime = null;
            Alarm nextAlarm = null;
            for (Alarm alarm : alarms) {
                if (alarm.isEnabled()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, alarm.getWakeupHours());
                    calendar.set(Calendar.MINUTE, alarm.getWakeupMinutes());
                    calendar.set(Calendar.SECOND, 0);
                    if (locationClient != null && alarm.getWakeupHours() + alarm.getWakeupMinutes() == 0) {
                        Location location = locationClient.getLastLocation();
                        int duration = BingMapsAPI.getTimeToLocation(location.getLatitude(), location.getLongitude(), alarm.getLat(), alarm.getLng(), alarm.getArriveHours(), alarm.getWakeupMinutes(), BingMapsAPI.TRANSIT_MODE.driving, true);

                        calendar.set(Calendar.HOUR_OF_DAY, alarm.getArriveHours());
                        calendar.set(Calendar.MINUTE, alarm.getArriveMinutes());
                        calendar.add(Calendar.MINUTE, -1 * (duration));
                        calendar.add(Calendar.MINUTE, -1 * alarm.getGetReady());
                        alarm.setWakeupHours(calendar.get(Calendar.HOUR_OF_DAY));
                        alarm.setWakeupMinutes(calendar.get(Calendar.MINUTE));
                        dataSource.updateAlarm(alarm);
                    }
                    if (calendar.before(Calendar.getInstance())) {
                        calendar.add(Calendar.DATE, 1);
                    }
                    long minutesBeforeAlarm = (calendar.getTime().getTime() - Calendar.getInstance().getTime().getTime()) / 1000 / 60;
                    if ((nextAlarmTime == null || nextAlarmTime.after(calendar))) {
                        nextAlarmTime = calendar;
                        nextAlarm = alarm;
                    }
                    if (minutesBeforeAlarm > 70) {
                        calendar.add(Calendar.HOUR_OF_DAY, -1);
                        if (nextWakeup == null || nextWakeup.after(calendar)) {
                            nextWakeup = calendar;
                            nextWakeupIntent = createPendingIntent(context, alarm);
                        }
                    } else if (minutesBeforeAlarm > 40) {
                        calendar.add(Calendar.MINUTE, -30);
                        if (nextWakeup == null || nextWakeup.after(calendar)) {
                            nextWakeup = calendar;
                            nextWakeupIntent = createPendingIntent(context, alarm);
                        }
                    } else if (minutesBeforeAlarm > 15) {
                        calendar.add(Calendar.MINUTE, -10);
                        if (nextWakeup == null || nextWakeup.after(calendar)) {
                            nextWakeup = calendar;
                            nextWakeupIntent = createPendingIntent(context, alarm);
                        }
                    } else {
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        PebbleKit.startAppOnPebble(context, PEBBLE_APP_UUID);
                        Log.d("wakeup", "Alarm rings at: " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
                        PendingIntent intent = PendingIntent.getActivity(context, 1928, new Intent(context, WakeupActivity.class).putExtra("alarmid", alarm.getId()), PendingIntent.FLAG_ONE_SHOT);
                        if (Build.VERSION.SDK_INT > 18) {
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intent);
                        } else {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intent);

                        }
                    }
                }
            }
            if (nextWakeup != null && nextWakeupIntent != null) {
                if (nextWakeup.before(Calendar.getInstance())) {
                    nextWakeup.add(Calendar.DATE, 1);
                }
                Log.d("wakeup", "Next wakeup set to: " + nextWakeup.get(Calendar.HOUR_OF_DAY) + ":" + nextWakeup.get(Calendar.MINUTE));
                setAlarm(context, nextWakeup, nextWakeupIntent);
            }
            if (nextAlarmTime != null) {
                PebbleDictionary data = new PebbleDictionary();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, nextAlarm.getArriveHours());
                calendar.set(Calendar.MINUTE, nextAlarm.getArriveMinutes());
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                data.addInt32(0, (byte) 0);
                data.addString(1, sdf.format(nextAlarmTime.getTime()));
                data.addString(2, sdf.format(calendar.getTime()));
                data.addString(3, nextAlarm.getPlaceName());
                PebbleKit.sendDataToPebble(context, PEBBLE_APP_UUID, data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dataSource.close();
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("update"));
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
