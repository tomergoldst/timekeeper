package com.tomergoldst.timekeeper.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import com.tomergoldst.timekeeper.model.TimePoint;
import com.tomergoldst.timekeeper.tools.Logger;
import com.tomergoldst.timekeeper.tools.TimePointsTools;

import java.util.Date;

public final class SystemAlarmSchedulerImpl implements SystemAlarmScheduler {

    private static final String TAG = SystemAlarmSchedulerImpl.class.getSimpleName();

    private static Context mContext;

    SystemAlarmSchedulerImpl(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public void setAlarm(@NonNull TimePoint timePoint) {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timePoint.getTime(), TimePointsTools.getPendingIntent(mContext, timePoint));
            Logger.d(TAG, "Set system alarm to " + timePoint.getReadableDate());
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timePoint.getTime(),  TimePointsTools.getPendingIntent(mContext, timePoint));
            Logger.d(TAG, "Set system alarm to " + timePoint.getReadableDate());
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timePoint.getTime(),  TimePointsTools.getPendingIntent(mContext, timePoint));
            Logger.d(TAG, "Set system alarm to " + timePoint.getReadableDate());
        }

    }

    @Override
    public void cancelAlarm(@NonNull TimePoint timePoint) {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.cancel(TimePointsTools.getPendingIntent(mContext, timePoint));
    }

    @Override
    public boolean isAlarmSet(@NonNull TimePoint timePoint) {
        return TimePointsTools.getNoCreatePendingIntent(mContext, timePoint) != null;
    }

    @Override
    public void clear() {
        // todo remove all alarms
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void setStatusReviewAlarm() {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;

        long time = System.currentTimeMillis();
        time += 10 * DateUtils.YEAR_IN_MILLIS;

        alarmManager.set(AlarmManager.RTC_WAKEUP, time, getStatusReviewAlarmPi(true));
        Logger.i(TAG, "set status review alarm to " + new Date(time).toString());
    }

    @Override
    public boolean isStatusReviewAlarmSet() {
        return getStatusReviewAlarmPi(false) != null;
    }

    private PendingIntent getStatusReviewAlarmPi(boolean flagCreate){
        Intent intent = new Intent(mContext, StatusReviewAlarmReceiver.class);
        return PendingIntent.getBroadcast(mContext,
                Config.REQUEST_CODE_STATUS_ALARM,
                intent,
                flagCreate ? PendingIntent.FLAG_UPDATE_CURRENT : PendingIntent.FLAG_NO_CREATE);
    }
}
