package com.tomergoldst.timekeeper.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.tomergoldst.timekeeper.tools.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomer on 11/12/2015.
 */
public class TimePointDao {

    /** Tag used on log messages.*/
    private final String TAG = this.getClass().getSimpleName();

    private static final int ERROR = -1;

    private SQLiteDatabase database;

    public TimePointDao(SQLiteDatabase database) {
        this.database = database;
    }

    public TimePoint getTimePoint(long id) {
        TimePoint timePoint = null;
        Cursor cursor;

        // find timePoint query
        String selection = DatabaseContract.TimePointEntry._ID + " = ?";
        String selectionArgs[] = {String.valueOf(id)};

        // get timePoint
        cursor = database.query(DatabaseContract.TimePointEntry.TABLE_NAME,
                DatabaseContract.getAllTimePointColumns(), selection, selectionArgs, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            long _id = cursor.getLong(cursor.getColumnIndex(DatabaseContract.TimePointEntry._ID));
            long date = cursor.getLong(cursor.getColumnIndex(DatabaseContract.TimePointEntry.COLUMN_TIME_POINT_TIME));

            timePoint = new TimePoint(_id, date);
        }
        cursor.close();

        return timePoint;
    }

    public TimePoint getTimePointAt(long time) {
        TimePoint timePoint = null;
        Cursor cursor;

        // find timePoint query
        String selection = DatabaseContract.TimePointEntry.COLUMN_TIME_POINT_TIME + " = ?";
        String selectionArgs[] = {String.valueOf(time)};

        cursor = database.query(DatabaseContract.TimePointEntry.TABLE_NAME,
                DatabaseContract.getAllTimePointColumns(), selection, selectionArgs, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            long id = cursor.getLong(cursor.getColumnIndex(DatabaseContract.TimePointEntry._ID));
            long date = cursor.getLong(cursor.getColumnIndex(DatabaseContract.TimePointEntry.COLUMN_TIME_POINT_TIME));

            timePoint = new TimePoint(id, date);
        }
        cursor.close();

        return timePoint;
    }

    public List<TimePoint> getAll() {

        List<TimePoint> timePoints = new ArrayList<>();
        Cursor cursor;

        String orderBy = DatabaseContract.TimePointEntry.COLUMN_TIME_POINT_TIME + " ASC";

        cursor = database.query(DatabaseContract.TimePointEntry.TABLE_NAME,
                DatabaseContract.getAllTimePointColumns(), null, null, null, null, orderBy, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            long id = cursor.getLong(cursor.getColumnIndex(DatabaseContract.TimePointEntry._ID));
            long date = cursor.getLong(cursor.getColumnIndex(DatabaseContract.TimePointEntry.COLUMN_TIME_POINT_TIME));

            TimePoint timePoint = new TimePoint(id, date);
            timePoints.add(timePoint);

            cursor.moveToNext();
        }
        cursor.close();

        return timePoints;
    }

    long countTimePoints(){
        return DatabaseUtils.queryNumEntries(database,
                DatabaseContract.TimePointEntry.TABLE_NAME,
                null,
                null
                );

    }

    List<TimePoint> getTimePointsUpTo(long time) {

        List<TimePoint> timePoints = new ArrayList<>();
        Cursor cursor;

        String selection = DatabaseContract.TimePointEntry.COLUMN_TIME_POINT_TIME + "<= ?";
        String selectionArgs[] = {String.valueOf(time)};

        String orderBy = DatabaseContract.TimePointEntry.COLUMN_TIME_POINT_TIME + " ASC";

        cursor = database.query(DatabaseContract.TimePointEntry.TABLE_NAME,
                DatabaseContract.getAllTimePointColumns(), selection, selectionArgs, null, null, orderBy, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            long id = cursor.getLong(cursor.getColumnIndex(DatabaseContract.TimePointEntry._ID));
            long date = cursor.getLong(cursor.getColumnIndex(DatabaseContract.TimePointEntry.COLUMN_TIME_POINT_TIME));

            TimePoint timePoint = new TimePoint(id, date);
            timePoints.add(timePoint);

            cursor.moveToNext();
        }
        cursor.close();

        return timePoints;
    }

    List<TimePoint> getTimePoints(int limitValue) {

        List<TimePoint> timePoints = new ArrayList<>();
        Cursor cursor;

        if (limitValue <= 0){
            return null;
        }

        String orderBy = DatabaseContract.TimePointEntry.COLUMN_TIME_POINT_TIME + " ASC";
        String limit = String.valueOf(limitValue);

        cursor = database.query(DatabaseContract.TimePointEntry.TABLE_NAME,
                DatabaseContract.getAllTimePointColumns(), null, null, null, null, orderBy, limit);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            long id = cursor.getLong(cursor.getColumnIndex(DatabaseContract.TimePointEntry._ID));
            long date = cursor.getLong(cursor.getColumnIndex(DatabaseContract.TimePointEntry.COLUMN_TIME_POINT_TIME));

            TimePoint timePoint = new TimePoint(id, date);
            timePoints.add(timePoint);

            cursor.moveToNext();
        }
        cursor.close();

        return timePoints;
    }

    List<TimePoint> getNextTimePoints(long time, int limitValue) {
        List<TimePoint> timePoints = new ArrayList<>();
        Cursor cursor;

        String selection = DatabaseContract.TimePointEntry.COLUMN_TIME_POINT_TIME + " > ?";
        String selectionArgs[] = {String.valueOf(time)};
        String orderBy = DatabaseContract.TimePointEntry.COLUMN_TIME_POINT_TIME + " ASC";
        String limit = String.valueOf(limitValue);

        cursor = database.query(DatabaseContract.TimePointEntry.TABLE_NAME,
                DatabaseContract.getAllTimePointColumns(), selection, selectionArgs, null, null, orderBy, limit);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            long id = cursor.getLong(cursor.getColumnIndex(DatabaseContract.TimePointEntry._ID));
            long date = cursor.getLong(cursor.getColumnIndex(DatabaseContract.TimePointEntry.COLUMN_TIME_POINT_TIME));

            TimePoint timePoint = new TimePoint(id, date);
            timePoints.add(timePoint);

            cursor.moveToNext();
        }
        cursor.close();

        return timePoints;
    }

    // Store timePoint, return database inserted id on success

    public long insert(TimePoint timePoint) {

        // Set information to store
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.TimePointEntry.COLUMN_TIME_POINT_TIME, timePoint.getTime());

        // Store in database
        long insertId = database.insert(DatabaseContract.TimePointEntry.TABLE_NAME, null, values);

        if (insertId == ERROR) {
            Logger.d(TAG,"Store timePoint failed");
            return ERROR;
        }

        Logger.d(TAG, "Store timePoint succeed");
        return insertId;

    }
    // Update timePoint data

    boolean update(TimePoint timePoint) {

        // Set information to store
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.TimePointEntry._ID, timePoint.getId());

        // update info
        String whereClause = DatabaseContract.TimePointEntry._ID +" = ?";
        String whereArgs[] = {String.valueOf(timePoint.getId())};
        long rows = database.update(DatabaseContract.TimePointEntry.TABLE_NAME,
                values, whereClause, whereArgs);

        if (rows == 0) {
            Logger.d(TAG,"update timePoint failed");
            return false;
        }

        Logger.d(TAG, "update timePoint " + timePoint.getId() + " succeed");
        return true;

    }

    public boolean delete(TimePoint timePoint) {
        // Delete timePoint query
        String selection = DatabaseContract.TimePointEntry._ID + " = ?";
        String selectionArgs[] = {String.valueOf(timePoint.getId())};

        long rows = database.delete(DatabaseContract.TimePointEntry.TABLE_NAME,
                selection, selectionArgs);

        // If no rows where deleted
        if (rows == 0){
            Logger.d(TAG,"timePoint " + timePoint.getId() + " for " + timePoint.getReadableDate() + " doesn't exist");
            return false;
        }

        Logger.d(TAG, "timePoint " + timePoint.getId() + " for " + timePoint.getReadableDate() + " deleted");
        return true;
    }

    long deleteAll() {
        long rows = database.delete(DatabaseContract.TimePointEntry.TABLE_NAME,
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
