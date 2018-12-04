package com.tomergoldst.timekeeper.core;

import android.content.Context;

import com.tomergoldst.timekeeper.model.Alarm;

import java.util.List;

public final class TimeKeeper {

    private static Client sClient = null;

    /**
     * Gets the default instance.
     *
     * @return default instance
     */
    static Client getInstance() {
        if (sClient == null){
            sClient = new Client();
        }
        return sClient;
    }

    /**
     * Initialize the SDK with the Android app context
     * Initializing is required before calling other methods.
     *
     * @param context context
     */
    public static void initialize(Context context) {
        getInstance().initialize(context);
    }

    /**
     * set alarm to be triggered
     * @param alarm alarm
     * @return inserted alarm id
     */
    public static long setAlarm(Alarm alarm){
        return getInstance().setAlarm(alarm);
    }

    /**
     * cancel alarm by id
     * @param id alarm id
     */
    public static void cancelAlarm(long id) {
        getInstance().cancelAlarm(id);
    }

    /**
     * cancel alarm by uid
     * @param uid alarm uid
     */
    public static void cancelAlarm(String uid) {
        getInstance().cancelAlarm(uid);
    }

    /**
     * Get closest alarms to current time of request
     * @param alarmUid alarm uid
     * @return most closest alarm to current time of request
     */
    public static Alarm getNextAlarm(String alarmUid){
        return getInstance().getNextAlarm(alarmUid);
    }

    /**
     * Get closest alarms to current time of request
     * Each time point might be bound to more then one alarm and hence we return a list of the alarms
     * @return most closest alarms to current time of request
     */
    public static List<Alarm> getNextAlarms(){
        return getInstance().getNextAlarms();
    }

    /**
     * Get closest alarm time to current time of request
     * @return most closest alarm time to current time of request in millis
     */
    public static long getNextAlarmTime(){
        return getInstance().getNextAlarmTime();
    }

    /**
     * Get all alarms
     * @return alarms
     */
    public static List<Alarm> getAlarms(){
        return getInstance().getAlarms();
    }

    /**
     * Delete all past persisted alarms up to current time
     */
    public static void removePastPersistAlarms(){
        getInstance().removePastPersistAlarms();
    }

    /**
     * cancel all alarms
     */
    public static void clear(){
        getInstance().clear();
    }

}
