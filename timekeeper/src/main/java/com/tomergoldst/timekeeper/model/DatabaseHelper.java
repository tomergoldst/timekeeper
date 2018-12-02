package com.tomergoldst.timekeeper.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "com.tomergoldst.timekeeper.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String UNIQUE = " UNIQUE";

    private static final String SQL_CREATE_TIME_POINT_ENTRIES =
            "CREATE TABLE " + DatabaseContract.TimePointEntry.TABLE_NAME + " (" +
                    DatabaseContract.TimePointEntry._ID + " INTEGER PRIMARY KEY," +
                    DatabaseContract.TimePointEntry.COLUMN_TIME_POINT_TIME + INTEGER_TYPE + UNIQUE +
            " )";

    private static final String SQL_CREATE_ALARM_ENTRIES =
            "CREATE TABLE " + DatabaseContract.AlarmEntry.TABLE_NAME + " (" +
                    DatabaseContract.AlarmEntry._ID + " INTEGER PRIMARY KEY," +
                    DatabaseContract.AlarmEntry.COLUMN_ALARM_UID + TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.AlarmEntry.COLUMN_ALARM_TIME + INTEGER_TYPE + COMMA_SEP +
                    DatabaseContract.AlarmEntry.COLUMN_ALARM_PERSIST + INTEGER_TYPE + COMMA_SEP +
                    DatabaseContract.AlarmEntry.COLUMN_ALARM_PAYLOAD + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_TIME_POINT_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseContract.TimePointEntry.TABLE_NAME;

    private static final String SQL_DELETE_ALARM_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseContract.AlarmEntry.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TIME_POINT_ENTRIES);
        db.execSQL(SQL_CREATE_ALARM_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
