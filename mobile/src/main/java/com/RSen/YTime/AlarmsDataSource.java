package com.RSen.YTime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan on 9/20/2014.
 */
public class AlarmsDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_IS_LOCATION, MySQLiteHelper.COLUMN_LAT, MySQLiteHelper.COLUMN_LNG,
            MySQLiteHelper.COLUMN_HOURS, MySQLiteHelper.COLUMN_MINUTES, MySQLiteHelper.COLUMN_GET_READY};

    public AlarmsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Alarm createLocationAlarm(double lat, double lng, int hours, int minutes, int getReady) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_IS_LOCATION, 1);
        values.put(MySQLiteHelper.COLUMN_LAT, lat);
        values.put(MySQLiteHelper.COLUMN_LNG, lng);
        values.put(MySQLiteHelper.COLUMN_HOURS, hours);
        values.put(MySQLiteHelper.COLUMN_MINUTES, minutes);
        values.put(MySQLiteHelper.COLUMN_GET_READY, getReady);
        long insertId = database.insert(MySQLiteHelper.TABLE_ALARMS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ALARMS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Alarm newAlarm = cursorToAlarm(cursor);
        cursor.close();
        return newAlarm;
    }

    public void deleteComment(Alarm alarm) {
        long id = alarm.getId();
        database.delete(MySQLiteHelper.TABLE_ALARMS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Alarm> getAllComments() {
        List<Alarm> comments = new ArrayList<Alarm>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ALARMS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Alarm alarm = cursorToAlarm(cursor);
            comments.add(alarm);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }

    private Alarm cursorToAlarm(Cursor cursor) {
        Alarm alarm = new Alarm(cursor.getLong(0), cursor.getInt(1), cursor.getDouble(2),
                cursor.getDouble(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6));
        return alarm;
    }
}
