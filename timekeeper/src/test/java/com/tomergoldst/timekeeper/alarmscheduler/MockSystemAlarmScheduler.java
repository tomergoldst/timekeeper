package com.tomergoldst.timekeeper.alarmscheduler;

import android.support.annotation.NonNull;

import com.tomergoldst.timekeeper.core.SystemAlarmScheduler;
import com.tomergoldst.timekeeper.model.TimePoint;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class MockSystemAlarmScheduler implements SystemAlarmScheduler {

    private HashMap<Long, TimePoint> alarms = new LinkedHashMap<>();
    private boolean isStatusReviewAlarmSet = false;

    @Override
    public void setAlarm(@NonNull TimePoint timePoint) {
        alarms.put(timePoint.getTime(), timePoint);
    }

    @Override
    public void cancelAlarm(@NonNull TimePoint timePoint) {
        alarms.remove(timePoint.getTime());
    }

    @Override
    public boolean isAlarmSet(@NonNull TimePoint timePoint) {
        return alarms.get(timePoint.getTime()) != null;
    }

    @Override
    public void clear() {
        alarms.clear();
    }

    @Override
    public void setStatusReviewAlarm() {
        isStatusReviewAlarmSet = true;
    }

    @Override
    public boolean isStatusReviewAlarmSet() {
        return isStatusReviewAlarmSet;
    }
}
