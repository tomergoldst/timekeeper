package com.tomergoldst.timekeeper.core;

/**
 * Main configuration class
 */
public final class Config {

    private Config() {
    }

    // Channel
    static final String CHANNEL_ID = "com.tomergoldst.timekeeper.channel.alarms";

    // Pending intents request codes
    public static final int REQUEST_CODE_WAKE_ON_ALARM = 2000000010;
    static final int REQUEST_CODE_STATUS_ALARM = 2000000020;

    // Foreground service id
    static final int FOREGROUND_SERVICE_NOTIFICATION_ID = 2000000021;

    // Job services ids
    static final String JOB_SERVICE_REVIEW_ALARMS_STATUS = "com.tomergoldst.timekeeper.job.review_alarms_status";

    // Intent action id
    // Any app uses this sdk has to create a receiver with intent filter of this action
    static final String INTENT_ACTION_RECEIVE_ALARMS = "com.tomergoldst.timekeeper.intent_action.RECEIVE_ALARMS";

}
