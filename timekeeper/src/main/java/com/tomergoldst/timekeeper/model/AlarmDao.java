package com.tomergoldst.timekeeper.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tomergoldst.timekeeper.tools.Logger;

import java.util.ArrayList;
import java.util.List;

class AlarmDao {

    /** Tag used on log messages.*/
    final String TAG = this.getClass().getSimpleName();

    private static final int ERROR = -1;

    private SQLiteDatabase database;

    public AlarmDao(SQLiteDatabase database) {
        this.database = database;
    }

    public Alarm get(long id) {
        Alarm alarm = null;
        Cursor cursor;

        String selection = DatabaseContract.AlarmEntry._ID + " = ?";
        String selectionArgs[] = {String.valueOf(id)};

        cursor = database.query(DatabaseContract.AlarmEntry.TABLE_NAME,
                DatabaseContract.getAllAlarmColumns(), selection, selectionArgs, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            long _id = cursor.getLong(cursor.getColumnIndex(DatabaseContract.AlarmEntry._ID));
            String uid = cursor.getString(cursor.getColumnIndex(DatabaseContract.AlarmEntry.COLUMN_ALARM_UID));
            long date = cursor.getLong(cursor.getColumnIndex(DatabaseContract.AlarmEntry.COLUMN_ALARM_TIME));
            int persist = cursor.getInt(cursor.getColumnIndex(DatabaseContract.AlarmEntry.COLUMN_ALARM_PERSIST));
            String tag = cursor.getString(cursor.getColumnIndex(DatabaseContract.AlarmEntry.COLUMN_ALARM_PAYLOAD));

            alarm = new Alarm(_id, uid, date, persist == 1, tag);
        }
        cursor.close();

        return alarm;
    }

    List<Alarm> getAlarmsAt(long time) {
        List<Alarm> alarmsList = new ArrayList<>();
        Cursor cursor;

        String selection = DatabaseContract.AlarmEntry.COLUMN_ALARM_TIME + " = ?";
        String selectionArgs[] = {String.valueOf(time)};

        cursor = database.query(DatabaseContract.AlarmEntry.TABLE_NAME,
                DatabaseContract.getAllAlarmColumns(), selection, selectionArgs, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            long id = cursor.getLong(cursor.getColumnIndex(DatabaseContract.AlarmEntry._ID));
            String uid = cursor.getString(cursor.getColumnIndex(DatabaseContract.AlarmEntry.COLUMN_ALARM_UID));
            long date = cursor.getLong(cursor.getColumnIndex(DatabaseContract.AlarmEntry.COLUMN_ALARM_TIME));
            int persist = cursor.getInt(cursor.getColumnIndex(DatabaseContract.AlarmEntry.COLUMN_ALARM_PERSIST));
            String tag = cursor.getString(cursor.getColumnIndex(DatabaseContract.AlarmEntry.COLUMN_ALARM_PAYLOAD));

            Alarm alarm = new Alarm(id, uid, date, persist == 1, tag);
            alarmsList.add(alarm);

            cursor.moveToNext();
        }
        cursor.close();

        return alarmsList;
    }

    List<Alarm> getAll() {
        List<Alarm> alarmsList = new ArrayList<>();
        Cursor cursor;

        String orderBy = DatabaseContract.AlarmEntry.COLUMN_ALARM_TIME + " ASC";

        cursor = database.query(DatabaseContract.AlarmEntry.TABLE_NAME,
                DatabaseContract.getAllAlarmColumns(), null, null, null, null, orderBy, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            long id = cursor.getLong(cursor.getColumnIndex(DatabaseContract.AlarmEntry._ID));
            String _uid = cursor.getString(cursor.getColumnIndex(DatabaseContract.AlarmEntry.COLUMN_ALARM_UID));
            long date = cursor.getLong(cursor.getColumnIndex(DatabaseContract.AlarmEntry.COLUMN_ALARM_TIME));
            int persist = cursor.getInt(cursor.getColumnIndex(DatabaseContract.AlarmEntry.COLUMN_ALARM_PERSIST));
            String tag = cursor.getString(cursor.getColumnIndex(DatabaseContract.AlarmEntry.COLUMN_ALARM_PAYLOAD));

            Alarm alarm = new Alarm(id, _uid, date, persist == 1, tag);
            alarmsList.add(alarm);

            cursor.moveToNext();
        }
        cursor.close();

        return alarmsList;
    }

    List<Alarm> getAlarmsByUid(String uid, int limitValue) {
        List<Alarm> alarmsList = new ArrayList<>();
        Cursor cursor;

        // get alarms query
        String selection = DatabaseContract.AlarmEntry.COLUMN_ALARM_UID + " = ?";
        String selectionArgs[] = {String.valueOf(uid)};

        String orderBy = DatabaseContract.AlarmEntry.COLUMN_ALARM_TIME + " ASC";

        String limit = null;
        if (limitValue > 0) {
            limit = String.valueOf(limitValue);
        }

        cursor = database.query(DatabaseContract.AlarmEntry.TABLE_NAME,
                DatabaseContract.getAllAlarmColumns(), selection, selectionArgs, null, null, orderBy, limit);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            long id = cursor.getLong(cursor.getColumnIndex(DatabaseContract.AlarmEntry._ID));
            String _uid = cursor.getString(cursor.getColumnIndex(DatabaseContract.AlarmEntry.COLUMN_ALARM_UID));
            long date = cursor.getLong(cursor.getColumnIndex(DatabaseContract.AlarmEntry.COLUMN_ALARM_TIME));
            int persist = cursor.getInt(cursor.getColumnIndex(DatabaseContract.AlarmEntry.COLUMN_ALARM_PERSIST));
            String tag = cursor.getString(cursor.getColumnIndex(DatabaseContract.AlarmEntry.COLUMN_ALARM_PAYLOAD));

            Alarm alarm = new Alarm(id, _uid, date, persist == 1, tag);
            alarmsList.add(alarm);

            cursor.moveToNext();
        }
        cursor.close();

        return alarmsList;
    }

    List<Alarm> getAlarmsByUid(String uid) {
        return getAlarmsByUid(uid, -1);
    }

    List<Alarm> getPersisted(long time) {
        List<Alarm> alarmsList = new ArrayList<>();
        Cursor cursor;

        // get alarms query
        String selection = DatabaseContract.AlarmEntry.COLUMN_ALARM_TIME + " <= ?" +
                " AND " + DatabaseContract.AlarmEntry.COLUMN_ALARM_PERSIST + " = ?";
        String selectionArgs[] = {String.valueOf(time), String.valueOf(1)};

        cursor = database.query(DatabaseContract.AlarmEntry.TABLE_NAME,
                DatabaseContract.getAllAlarmColumns(), selection, selectionArgs, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            long id = cursor.getLong(cursor.getColumnIndex(DatabaseContract.AlarmEntry._ID));
            String uid = cursor.getString(cursor.getColumnIndex(DatabaseContract.AlarmEntry.COLUMN_ALARM_UID));
            long date = cursor.getLong(cursor.getColumnIndex(DatabaseContract.AlarmEntry.COLUMN_ALARM_TIME));
            int persist = cursor.getInt(cursor.getColumnIndex(DatabaseContract.AlarmEntry.COLUMN_ALARM_PERSIST));
            String tag = cursor.getString(cursor.getColumnIndex(DatabaseContract.AlarmEntry.COLUMN_ALARM_PAYLOAD));

            Alarm alarm = new Alarm(id, uid, date, persist == 1, tag);
            alarmsList.add(alarm);

            cursor.moveToNext();
        }
        cursor.close();

        return alarmsList;
    }

    // Store alarm, return database inserted id on success
    long insert(Alarm alarm) {
        // Set information to store
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.AlarmEntry.COLUMN_ALARM_TIME, alarm.getTime());
        values.put(DatabaseContract.AlarmEntry.COLUMN_ALARM_UID, alarm.getUid());
        values.put(DatabaseContract.AlarmEntry.COLUMN_ALARM_PERSIST, alarm.isPersist());
        values.put(DatabaseContract.AlarmEntry.COLUMN_ALARM_PAYLOAD, alarm.getPayload());

        // Store in database
        long insertId = database.insert(DatabaseContract.AlarmEntry.TABLE_NAME, null, values);

        if (insertId == ERROR) {
            Logger.d(TAG,"Store alarm failed");
            return ERROR;
        }

        Logger.d(TAG, "Store alarm succeed");
        return insertId;

    }

    boolean update(Alarm alarm) {
        // Set information to store
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.AlarmEntry.COLUMN_ALARM_TIME, alarm.getTime());
        values.put(DatabaseContract.AlarmEntry.COLUMN_ALARM_UID, alarm.getUid());
        values.put(DatabaseContract.AlarmEntry.COLUMN_ALARM_PERSIST, alarm.isPersist());
        values.put(DatabaseContract.AlarmEntry.COLUMN_ALARM_PAYLOAD, alarm.getPayload());

        // update info
        String whereClause = DatabaseContract.AlarmEntry._ID +" = ?";
        String whereArgs[] = {String.valueOf(alarm.getId())};
        long rows = database.update(DatabaseContract.AlarmEntry.TABLE_NAME,
                values, whereClause, whereArgs);

        if (rows == 0) {
            Logger.d(TAG,"update alarm failed");
            return false;
        }

        Logger.d(TAG, "update alarm " + alarm.getId() + " succeed");
        return true;

    }

    boolean delete(Alarm alarm) {
        // Delete alarm query
        String selection = DatabaseContract.AlarmEntry._ID + " = ?";
        String selectionArgs[] = {String.valueOf(alarm.getId())};

        long rows = database.delete(DatabaseContract.AlarmEntry.TABLE_NAME,
                selection, selectionArgs);

        // If no rows where deleted
        if (rows == 0){
            Logger.d(TAG,"alarm " + alarm + " for " + alarm.getReadableDate() + " doesn't exist");
            return false;
        }

        Logger.d(TAG, "alarm " + alarm + " for " + alarm.getReadableDate() + " deleted");
        return true;
    }

    // Delete all persisted alarms up to provided time
    long deletePersisted(long time) {
        // Delete alarm query
        String selection = DatabaseContract.AlarmEntry.COLUMN_ALARM_TIME + " <= ?"
                + " AND " + DatabaseContract.AlarmEntry.COLUMN_ALARM_PERSIST + " == ?";
        String selectionArgs[] = {String.valueOf(time), String.valueOf(1)};

        long rows = database.delete(DatabaseContract.AlarmEntry.TABLE_NAME,
                selection, selectionArgs);

        // If no rows where deleted
        if (rows == 0){
            Logger.d(TAG,"No persisted alarms for all previous alarms then " + time);
            return rows;
        }

        Logger.d(TAG, "persisted alarms for all previous alarms then " + time + " deleted");
        return rows;
    }

    long deletePersisted() {
        // Delete alarm query
        String selection = DatabaseContract.AlarmEntry.COLUMN_ALARM_PERSIST + " == ?";
        String selectionArgs[] = {String.valueOf(1)};

        long rows = database.delete(DatabaseContract.AlarmEntry.TABLE_NAME,
                selection, selectionArgs);

        // If no rows where deleted
        if (rows == 0){
            Logger.d(TAG,"No persisted alarms");
            return rows;
        }

        Logger.d(TAG, "all persisted alarms were deleted");
        return rows;
    }

    long deleteAll() {
        long rows = database.delete(DatabaseContract.AlarmEntry.TABLE_NAME,
                null, null);

        // If no rows where deleted
        if (rows == 0){
            Logger.d(TAG,"No entries found");
            return rows;
        }

        Logger.d(TAG, "all entries deleted");
        return rows;
    }


}
