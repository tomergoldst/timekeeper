package com.tomergoldst.timekeeper.model;

import android.content.Context;

import com.tomergoldst.timekeeper.tools.Logger;

import java.util.List;

/**
 * Repository
 * The repository is the pipeline to all read/write database operations
 */
public final class Repository implements RepositoryDataSource {

    private static final String TAG = Repository.class.getSimpleName();

    private DatabaseAccessPoint db;
    private AlarmDao mAlarmDao;
    private TimePointDao mTimePointDao;

    public Repository(Context context){
        db = DatabaseAccessPoint.getDatabase(context.getApplicationContext());
        mAlarmDao = db.alarmDao();
        mTimePointDao = db.timePointDao();
    }

    /**
     * Store timePoint to database
     * @param timePoint timePoint
     */
    @Override
    public synchronized long insertTimePoint(TimePoint timePoint) {
        long id = mTimePointDao.insert(timePoint);
        timePoint.setId(id);
        Logger.d(TAG, "timePoint id = " + timePoint.getId());
        return id;
    }

    /**
     * Store alarm to database
     * @param alarm alarm
     */
    @Override
    public synchronized long insertAlarm(Alarm alarm) {
        long id = mAlarmDao.insert(alarm);
        alarm.setId(id);
        Logger.d(TAG, "alarm id = " + alarm.getId());
        return id;
    }

    /**
     * Delete timePoint from database. deleting timePoint will delete all it's alarms
     * @param timePoint time point
     */
    @Override
    public synchronized void deleteTimePoint(TimePoint timePoint) {
        mTimePointDao.delete(timePoint);
    }


    /**
     * Delete alarm from database
     * @param alarm alarm
     */
    @Override
    public synchronized void deleteAlarm(Alarm alarm) {
        mAlarmDao.delete(alarm);
    }

    @Override
    public Alarm getAlarm(long id) {
        return mAlarmDao.get(id);
    }

    /**
     * Get all alarms of an timePoint from database
     * @param timePoint timePoint
     * @return list of alarms
     */
    @Override
    public synchronized List<Alarm> getAlarms(TimePoint timePoint) {
        List<Alarm> alarms = mAlarmDao.getAlarmsAt(timePoint.getTime());
        if (alarms != null) {
            for (Alarm a : alarms) {
                Logger.d(TAG, "Found alarm = " + a.getUid() + " at database");
            }
        }
        return alarms;
    }

    @Override
    public List<Alarm> getAllAlarms() {
        return mAlarmDao.getAll();
    }

    @Override
    public synchronized List<Alarm> getAlarmsByUid(String uid) {
        List<Alarm> alarms = mAlarmDao.getAlarmsByUid(uid);
        if (alarms != null) {
            for (Alarm a : alarms) {
                Logger.d(TAG, "Found alarm = " + a.getUid() + " at database");
            }
        }
        return alarms;
    }

    @Override
    public synchronized Alarm getFirstUpcomingAlarm(String uid) {
        List<Alarm> alarms = mAlarmDao.getAlarmsByUid(uid, 1);
        if (alarms != null) {
            for (Alarm a : alarms) {
                Logger.d(TAG, "Found alarm = " + a.getUid() + " at database");
            }

            if (alarms.size() > 0) {
                return alarms.get(0);
            }
        }
        return null;
    }

    @Override
    public TimePoint getFirstUpcomingTimePoint() {
        List<TimePoint> timePoints = mTimePointDao.getTimePoints(1);
        if (timePoints == null || timePoints.isEmpty()){
            return null;
        }
        return timePoints.get(0);
    }

    @Override
    public synchronized List<Alarm> getPersistedAlarmsList(long time) {
        List<Alarm> alarms = mAlarmDao.getPersisted(time);
        if (alarms != null) {
            for (Alarm a : alarms) {
                Logger.d(TAG, "Found persist alarm = " + a.getUid() + "at database");
            }
        }
        return alarms;
    }

    /**
     * Return alarm from database by time or null if not exist
     * @param time alarm time
     * @return alarm
     */
    @Override
    public synchronized TimePoint getTimePointAt(long time) {
        return mTimePointDao.getTimePointAt(time);
    }

    /**
     * Return alarm from database by time or null if not exist
     * @param id id
     * @return timePoint
     */
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
        return mTimePointDao.countTimePoints();
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
    public void deleteAllPersistedAlarms() {
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
    public void clear() {
        mTimePointDao.deleteAll();
        mAlarmDao.deleteAll();
    }


}
