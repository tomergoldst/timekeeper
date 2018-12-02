package com.tomergoldst.timekeeper.model;

import android.provider.BaseColumns;

public class DatabaseContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DatabaseContract() {}

    /* Inner class that defines the table contents */
    static abstract class TimePointEntry implements BaseColumns {
        static final String TABLE_NAME = "time_point";
        static final String COLUMN_TIME_POINT_TIME = "time";
    }

    static abstract class AlarmEntry implements BaseColumns {
        static final String TABLE_NAME = "alarm";
        static final String COLUMN_ALARM_UID = "uid";
        static final String COLUMN_ALARM_TIME = "time";
        static final String COLUMN_ALARM_PERSIST = "persist";
        static final String COLUMN_ALARM_PAYLOAD = "payload";
    }

    static String[] getAllTimePointColumns() {
        return new String[]{
                TimePointEntry._ID,
                TimePointEntry.COLUMN_TIME_POINT_TIME,
        };
    }

    static String[] getAllAlarmColumns() {
        return new String[]{
                AlarmEntry._ID,
                AlarmEntry.COLUMN_ALARM_UID,
                AlarmEntry.COLUMN_ALARM_TIME,
                AlarmEntry.COLUMN_ALARM_PERSIST,
                AlarmEntry.COLUMN_ALARM_PAYLOAD
        };
    }

}

