package com.tomergoldst.timekeeper.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
interface AlarmDao {

    @Query("SELECT * FROM alarm WHERE id = :id")
    Alarm get(long id);

    @Query("SELECT * FROM alarm WHERE time = :time")
    List<Alarm> getAlarmsAt(long time);

    @Query("SELECT * FROM alarm ORDER BY time ASC")
    List<Alarm> getAll();

    @Query("SELECT * FROM alarm WHERE uid LIKE :uid ORDER BY time ASC LIMIT :limit")
    List<Alarm> getAlarmsByUid(String uid, int limit);

    @Query("SELECT * FROM alarm WHERE uid LIKE :uid ORDER BY time ASC")
    List<Alarm> getAlarmsByUid(String uid);

    @Query("SELECT * FROM alarm WHERE time <= :time AND persist = 1 ORDER BY time ASC")
    List<Alarm> getPersisted(long time);

    @Insert
    long insert(Alarm alarm);

    @Update
    void update(Alarm alarm);

    @Delete
    void delete(Alarm alarm);

    @Query("DELETE FROM alarm WHERE time <= :time AND persist = 1")
    void deletePersisted(long time);

    @Query("DELETE FROM alarm WHERE persist = 1")
    void deletePersisted();

    @Query("DELETE FROM alarm")
    void deleteAll();

}
