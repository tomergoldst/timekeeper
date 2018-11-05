package com.tomergoldst.timekeeper.model;

import java.util.List;

public interface RepositoryDataSource {

    /*
        TimePoint data source access methods
    */
    TimePoint getTimePointById(long id);
    TimePoint getTimePointAt(long time);
    TimePoint getFirstUpcomingTimePoint();
    List<TimePoint> getTimePoints(int limit);
    List<TimePoint> getTimePointsUpTo(long time);
    List<TimePoint> getAllTimePoints();
    int countTimePoints();
    long insertTimePoint(TimePoint timePoint);
    void updateTimePoint(TimePoint timePoint);
    void deleteTimePoint(TimePoint timePoint);

    /*
        Alarm data source access methods
    */
    Alarm getAlarm(long id);
    List<Alarm> getAlarmsByUid(String uid);
    List<Alarm> getAlarms(TimePoint timePoint);
    List<Alarm> getAllAlarms();
    Alarm getFirstUpcomingAlarm(String uid);
    List<Alarm> getPersistedAlarmsList(long time);
    long insertAlarm(Alarm alarm);
    void updateAlarm(Alarm alarm);
    void deleteAllPreviousPersistedAlarms();
    void deleteAllPersistedAlarms();
    void deleteAlarm(Alarm alarm);

    /*
        Joint data source access methods
    */
    void clear();

}
