package com.tomergoldst.timekeeper.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MockRepository implements RepositoryDataSource {

    private List<TimePoint> mTimePoints = new ArrayList<>();
    private long mTimePointIdCounter = 0;

    private List<Alarm> mAlarms = new ArrayList<>();
    private long mAlarmIdCounter = 0;

    @Override
    public synchronized long insertTimePoint(TimePoint timePoint) {
        mTimePointIdCounter++;
        timePoint.setId(mTimePointIdCounter);
        mTimePoints.add(timePoint);
        return mTimePointIdCounter;
    }

    @Override
    public synchronized long insertAlarm(Alarm alarm) {
        mAlarmIdCounter++;
        alarm.setId(mAlarmIdCounter);
        mAlarms.add(alarm);
        return mAlarmIdCounter;
    }

    @Override
    public synchronized void deleteTimePoint(TimePoint timePoint) {
        int index = -1;
        for (int i = 0, N = mTimePoints.size(); i < N; i++) {
            if (mTimePoints.get(i).getTime() == timePoint.getTime()) {
                index = i;
                break;
            }
        }

        if (index > -1) {
            mTimePoints.remove(index);
        }
    }

    @Override
    public synchronized void deleteAlarm(Alarm alarm) {
        int index = -1;
        for (int i = 0, N = mAlarms.size(); i < N; i++) {
            if (mAlarms.get(i).getTime() == alarm.getTime() &&
                    mAlarms.get(i).getUid().equals(alarm.getUid())) {
                index = i;
                break;
            }
        }

        if (index > -1) {
            mAlarms.remove(index);
        }
    }

    @Override
    public Alarm getAlarm(long id) {
        for (Alarm a : mAlarms) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }

    @Override
    public synchronized List<Alarm> getAlarms(TimePoint timePoint) {
        List<Alarm> alarms = new ArrayList<>();
        for (Alarm a : mAlarms) {
            if (a.getTime() == timePoint.getTime()) {
                alarms.add(a);
            }
        }

        return alarms;
    }

    @Override
    public List<Alarm> getAllAlarms() {
        sortAlarms(mAlarms);
        return mAlarms;
    }

    @Override
    public synchronized List<Alarm> getAlarmsByUid(String uid) {
        List<Alarm> alarms = new ArrayList<>();
        for (Alarm a : mAlarms) {
            if (a.getUid().equals(uid)) {
                alarms.add(a);
            }
        }

        return alarms;
    }

    @Override
    public synchronized Alarm getFirstUpcomingAlarm(String uid) {
        List<Alarm> alarmsList = new ArrayList<>(mAlarms);
        sortAlarms(alarmsList);
        for (Alarm a : alarmsList) {
            if (a.getUid().equals(uid)) {
                return a;
            }
        }
        return null;
    }

    @Override
    public TimePoint getFirstUpcomingTimePoint() {
        List<TimePoint> timePointList = new ArrayList<>(mTimePoints);
        sortTimePoints(timePointList);
        if (timePointList.isEmpty()) {
            return null;
        }
        return timePointList.get(0);
    }

    @Override
    public synchronized List<Alarm> getPersistedAlarmsList(long time) {
        return null;
    }

    @Override
    public synchronized TimePoint getTimePointAt(long time) {
        for (TimePoint a : mTimePoints) {
            if (a.getTime() == time) {
                return a;
            }
        }
        return null;
    }

    @Override
    public synchronized TimePoint getTimePointById(long id) {
        for (TimePoint a : mTimePoints) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }

    @Override
    public synchronized List<TimePoint> getAllTimePoints() {
        List<TimePoint> timePointList = new ArrayList<>(mTimePoints);
        sortTimePoints(timePointList);
        return timePointList;
    }

    @Override
    public int countTimePoints() {
        return mTimePoints.size();
    }

    @Override
    public synchronized List<TimePoint> getTimePoints(int limit) {
        List<TimePoint> timePointList = new ArrayList<>(mTimePoints);
        sortTimePoints(timePointList);

        int size = Math.min(timePointList.size(), limit);

        List<TimePoint> timePoints = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            timePoints.add(timePointList.get(i));
        }

        return timePoints;
    }

    @Override
    public synchronized void deleteAllPreviousPersistedAlarms() {
        List<Integer> indices = new ArrayList<>();

        long currentTime = System.currentTimeMillis();
        sortAlarms(mAlarms);

        for (int i = 0, N = mAlarms.size(); i < N ; i++) {
            if (mAlarms.get(i).isPersist() && mAlarms.get(i).getTime() <= currentTime){
                indices.add(i);

            }
        }

        Collections.sort(indices, Collections.reverseOrder());
        for (int i : indices) {
            mAlarms.remove(i);
        }
    }

    @Override
    public void deleteAllPersistedAlarms() {
        List<Integer> indices = new ArrayList<>();

        for (int i = 0, N = mAlarms.size(); i < N ; i++) {
            if (mAlarms.get(i).isPersist()){
                indices.add(i);
            }
        }

        Collections.sort(indices, Collections.reverseOrder());
        for (int i : indices) {
            mAlarms.remove(i);
        }
    }

    @Override
    public synchronized List<TimePoint> getTimePointsUpTo(long time) {
        List<TimePoint> timePointList = new ArrayList<>(mTimePoints);
        sortTimePoints(timePointList);

        List<TimePoint> timePoints = new ArrayList<>();

        for (TimePoint a : mTimePoints) {
            if (a.getTime() <= time) {
                timePoints.add(a);
            }
        }

        return timePoints;
    }

    @Override
    public synchronized void updateTimePoint(TimePoint timePoint) {
        int index = -1;
        for (int i = 0, N = mTimePoints.size(); i < N; i++) {
            if (mTimePoints.get(i).getId() == timePoint.getId()) {
                index = i;
                break;
            }
        }

        if (index > -1) {
            mTimePoints.set(index, timePoint);
        }
    }

    @Override
    public synchronized void updateAlarm(Alarm alarm) {
        int index = -1;
        for (int i = 0, N = mAlarms.size(); i < N; i++) {
            if (mAlarms.get(i).getId() == alarm.getId()) {
                index = i;
                break;
            }
        }

        if (index > -1) {
            mAlarms.set(index, alarm);

        }
    }

    @Override
    public void clear() {
        mTimePoints.clear();
        mTimePointIdCounter = 0;

        mAlarms.clear();
        mAlarmIdCounter = 0;

    }

    private void sortTimePoints(List<TimePoint> timePoints) {
        if (timePoints == null){
            return;
        }

        Collections.sort(timePoints, new Comparator<TimePoint>() {
            @Override
            public int compare(TimePoint o1, TimePoint o2) {
                return Long.compare(o1.getTime(), o2.getTime());
            }
        });
    }

    private void sortAlarms(List<Alarm> alarms) {
        if (alarms == null){
            return;
        }

        Collections.sort(alarms, new Comparator<Alarm>() {
            @Override
            public int compare(Alarm o1, Alarm o2) {
                return Long.compare(o1.getTime(), o2.getTime());
            }
        });
    }

}
