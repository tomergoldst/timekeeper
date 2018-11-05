package com.tomergoldst.timekeeper.model;

import com.tomergoldst.timekeeper.tools.AlarmTools;

import java.util.List;

public class ReceivedAlarmData {

    /**
    *  The time point this data object refer to
    */
    private TimePoint mTimePoint;

    /**
    *  Holds raw alarms as received by reading directly from the repository
    *  this lists of alarms will use for continuing inner work
    */
    private List<Alarm> mRawAlarms;
    private List<Alarm> mRawPersistedAlarms;

    /**
     * Holds processed alarms after removal of duplications
     * this lists of alarms will use for passing the information to the listening app
     */
    private List<Alarm> mAlarms;
    private List<Alarm> mPersistedAlarms;

    public ReceivedAlarmData(TimePoint timePoint, List<Alarm> alarms, List<Alarm> persistedAlarms) {
        this.mTimePoint = timePoint;
        this.mRawAlarms = alarms;
        this.mRawPersistedAlarms = persistedAlarms;
        this.mAlarms = alarms == null ? null : AlarmTools.removeDuplicated(alarms);
        this.mPersistedAlarms = persistedAlarms == null ? null : AlarmTools.removeDuplicated(persistedAlarms);
    }

    public TimePoint getTimePoint() {
        return mTimePoint;
    }

    public List<Alarm> getRawAlarms() {
        return mRawAlarms;
    }

    public List<Alarm> getRawPersistedAlarms() {
        return mRawPersistedAlarms;
    }

    public List<Alarm> getAlarms() {
        return mAlarms;
    }

    public List<Alarm> getPersistedAlarms() {
        return mPersistedAlarms;
    }

}
