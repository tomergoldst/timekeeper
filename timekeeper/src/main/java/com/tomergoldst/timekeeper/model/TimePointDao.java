package com.tomergoldst.timekeeper.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tomergoldst.timekeeper.tools.Logger;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomer on 11/12/2015.
 */
@Dao
interface TimePointDao {

    @Query("SELECT * FROM timepoint WHERE id = :id")
    TimePoint getTimePoint(long id);

    @Query("SELECT * FROM timepoint WHERE time = :time")
    TimePoint getTimePointAt(long time);

    @Query("SELECT * FROM timepoint ORDER BY time ASC")
    List<TimePoint> getAll();

    @Query("SELECT count(*) FROM timepoint")
    int countTimePoints();

    @Query("SELECT * FROM timepoint WHERE time <= :time ORDER BY time ASC")
    List<TimePoint> getTimePointsUpTo(long time);

    @Query("SELECT * FROM timepoint ORDER BY time ASC LIMIT :limit")
    List<TimePoint> getTimePoints(int limit);

    @Query("SELECT * FROM timepoint  WHERE time > :time ORDER BY time ASC LIMIT :limit")
    List<TimePoint> getNextTimePoints(long time, int limit);

    @Insert
    long insert(TimePoint timePoint);

    @Update
    void update(TimePoint timePoint);

    @Delete
    void delete(TimePoint timePoint);

    @Query("DELETE FROM timepoint")
    void deleteAll();

}
