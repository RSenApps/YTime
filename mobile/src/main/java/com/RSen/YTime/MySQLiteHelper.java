package com.RSen.YTime;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ryan on 9/20/2014.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_ALARMS = "alarms";
    public static final String TABLE_LOCATIONS = "locations";
    public static final String COLUMN_ID = "_id";
    //0=location, 1=time
    public static final String COLUMN_IS_LOCATION = "location";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LNG = "lng";
    public static final String COLUMN_ARRIVE_HOURS = "arrivehours";
    public static final String COLUMN_ARRIVE_MINUTES = "arriveminutes";
    public static final String COLUMN_WAKEUP_HOURS = "wakeuphours";
    public static final String COLUMN_WAKEUP_MINUTES = "wakeupminutes";
    public static final String COLUMN_GET_READY = "getready";
    public static final String COLUMN_LOCATION_NAME = "location_name";
    public static final String COLUMN_IS_ENABLED = "enabled";

    public static final String COLUMN_NAME = "name";

    private static final String DATABASE_NAME = "alarms.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_ALARMS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_IS_LOCATION
            + " integer, " + COLUMN_LAT + " real, " + COLUMN_LNG + " real, "
            + COLUMN_ARRIVE_HOURS + " integer, " + COLUMN_ARRIVE_MINUTES + " integer, " + COLUMN_WAKEUP_HOURS + " integer, "
            + COLUMN_WAKEUP_MINUTES + " integer, " + COLUMN_GET_READY + " integer, " + COLUMN_LOCATION_NAME + " text, "
            + COLUMN_IS_ENABLED + " integer);";
    private static final String DATABASE_CREATE2 = "create table " + TABLE_LOCATIONS
            + "(" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_NAME + " text, " + COLUMN_LAT + " real, "
            + COLUMN_LNG + " real);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        database.execSQL(DATABASE_CREATE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);

        onCreate(db);
    }

}