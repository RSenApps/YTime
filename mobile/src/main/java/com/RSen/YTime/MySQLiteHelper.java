package com.RSen.YTime;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ryan on 9/20/2014.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_ALARMS = "alarms";
    public static final String COLUMN_ID = "_id";
    //0=location, 1=time
    public static final String COLUMN_IS_LOCATION = "location";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LNG = "lng";
    public static final String COLUMN_HOURS = "hours";
    public static final String COLUMN_MINUTES = "minutes";

    public static final String COLUMN_GET_READY = "getready";

    private static final String DATABASE_NAME = "alarms.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_ALARMS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_IS_LOCATION
            + " integer, " + COLUMN_LAT + " real, " + COLUMN_LNG + " real, "
            + COLUMN_HOURS + " integer, " + COLUMN_MINUTES + " integer, " + COLUMN_GET_READY + " integer);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
        onCreate(db);
    }

}