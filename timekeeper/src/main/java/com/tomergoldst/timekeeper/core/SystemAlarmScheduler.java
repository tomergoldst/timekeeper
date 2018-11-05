package com.tomergoldst.timekeeper.core;

import android.support.annotation.NonNull;

import com.tomergoldst.timekeeper.model.TimePoint;

public interface SystemAlarmScheduler {
    void setAlarm(@NonNull TimePoint timePoint);
    void cancelAlarm(@NonNull TimePoint timePoint);
    boolean isAlarmSet(@NonNull TimePoint timePoint);
    void clear();
    void setStatusReviewAlarm();
    boolean isStatusReviewAlarmSet();
}
