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
    private List<Alarm> mRawNonPersistedAlarms;
    private List<Alarm> mRawPersistedAlarms;

    /**
     * Holds processed alarms after removal of duplications
     * this lists of alarms will use for passing the information to the listening app
     */
    private List<Alarm> mNonPersistedAlarms;
    private List<Alarm> mPersistedAlarms;

    public ReceivedAlarmData(TimePoint timePoint, List<Alarm> nonPersistedAlarms, List<Alarm> persistedAlarms) {
        this.mTimePoint = timePoint;
        this.mRawNonPersistedAlarms = nonPersistedAlarms;
        this.mRawPersistedAlarms = persistedAlarms;
        this.mNonPersistedAlarms = nonPersistedAlarms == null ? null : AlarmTools.removeDuplicated(nonPersistedAlarms);
        this.mPersistedAlarms = persistedAlarms == null ? null : AlarmTools.removeDuplicated(persistedAlarms);
    }

    public TimePoint getTimePoint() {
        return mTimePoint;
    }

    public List<Alarm> getRawNonPersistedAlarms() {
        return mRawNonPersistedAlarms;
    }

    public List<Alarm> getRawPersistedAlarms() {
        return mRawPersistedAlarms;
    }

    public List<Alarm> getNonPersistedAlarms() {
        return mNonPersistedAlarms;
    }

    public List<Alarm> getPersistedAlarms() {
        return mPersistedAlarms;
    }

}
