package com.tomergoldst.timekeeper.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tomergoldst.timekeeper.tools.Logger;

public class StatusReviewAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = StatusReviewAlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "OnReceive");
    }
}
