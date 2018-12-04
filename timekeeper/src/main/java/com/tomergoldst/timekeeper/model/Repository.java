package com.tomergoldst.timekeeper.model;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Repository
 * The repository is the pipeline to all read/write database operations
 */
public final class Repository implements RepositoryDataSource {

    private static final String TAG = Repository.class.getSimpleName();

    // Database fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private AlarmDao mAlarmDao;
    private TimePointDao mTimePointDao;

    public Repository(Context context){
        dbHelper = new DatabaseHelper(context);
        open();
        mAlarmDao = new AlarmDao(database);
        mTimePointDao = new TimePointDao(database);

    }

    // Open connection to database
    private void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    // Close connection to database
    void close() {
        dbHelper.close();
    }

    @Override
    public synchronized long insertTimePoint(TimePoint timePoint) {
        long id = mTimePointDao.insert(timePoint);
        timePoint.setId(id);
        return id;
    }

    @Override
    public synchronized long insertAlarm(Alarm alarm) {
        long id = mAlarmDao.insert(alarm);
        alarm.setId(id);
        return id;
    }

    @Override
    public synchronized void deleteTimePoint(TimePoint timePoint) {
        mTimePointDao.delete(timePoint);
    }


    @Override
    public synchronized void deleteAlarm(Alarm alarm) {
        mAlarmDao.delete(alarm);
    }

    @Override
    public synchronized Alarm getAlarm(long id) {
        return mAlarmDao.get(id);
    }

    @Override
    public synchronized List<Alarm> getAlarms(TimePoint timePoint) {
        return mAlarmDao.getAlarmsAt(timePoint.getTime());
    }

    @Override
    public synchronized List<Alarm> getAllAlarms() {
        return mAlarmDao.getAll();
    }

    @Override
    public synchronized List<Alarm> getAlarmsByUid(String uid) {
        return mAlarmDao.getAlarmsByUid(uid);
    }

    @Override
    public synchronized Alarm getFirstUpcomingAlarm(String uid) {
        List<Alarm> alarms = mAlarmDao.getAlarmsByUid(uid, 1);
        if (alarms != null && alarms.size() > 0) {
            return alarms.get(0);
        }
        return null;
    }

    @Override
    public synchronized TimePoint getFirstUpcomingTimePoint() {
        List<TimePoint> timePoints = mTimePointDao.getTimePoints(1);
        if (timePoints == null || timePoints.isEmpty()){
            return null;
        }
        return timePoints.get(0);
    }

    @Override
    public synchronized List<Alarm> getPersistedAlarmsList(long time) {
        return mAlarmDao.getPersisted(time);
    }

    @Override
    public synchronized TimePoint getTimePointAt(long time) {
        return mTimePointDao.getTimePointAt(time);
    }

    @Override
    public synchronized TimePoint getTimePointById(long id) {
        return mTimePointDao.getTimePoint(id);
    }

    @Override
    public synchronized List<TimePoint> getAllTimePoints() {
        return mTimePointDao.getAll();
    }

    @Override
    public int countTimePoints() {
        return (int) mTimePointDao.countTimePoints();
    }

    @Override
    public synchronized List<TimePoint> getTimePoints(int limit) {
        return mTimePointDao.getTimePoints(limit);
    }

    @Override
    public synchronized void deleteAllPreviousPersistedAlarms(){
        mAlarmDao.deletePersisted(System.currentTimeMillis());
    }

    @Override
    public synchronized void deleteAllPersistedAlarms() {
        mAlarmDao.deletePersisted();
    }

    @Override
    public synchronized List<TimePoint> getTimePointsUpTo(long time) {
        return mTimePointDao.getTimePointsUpTo(time);
    }

    @Override
    public synchronized void updateTimePoint(TimePoint timePoint){
        mTimePointDao.update(timePoint);
    }

    @Override
    public synchronized void updateAlarm(Alarm alarm){
        mAlarmDao.update(alarm);
    }

    @Override
    public synchronized void clear() {
        mTimePointDao.deleteAll();
        mAlarmDao.deleteAll();
    }


}
