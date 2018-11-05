package com.tomergoldst.timekeeper.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {TimePoint.class, Alarm.class}, version = 1)
abstract class DatabaseAccessPoint extends RoomDatabase {

    private static final String DATABASE_NAME = "com.tomergoldst.timekeeper.db";

    private static volatile DatabaseAccessPoint sInstance;

    abstract TimePointDao timePointDao();
    abstract AlarmDao alarmDao();

    static DatabaseAccessPoint getDatabase(final Context context) {
        if (sInstance == null) {
            synchronized (DatabaseAccessPoint.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            DatabaseAccessPoint.class, DATABASE_NAME)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return sInstance;
    }

}
