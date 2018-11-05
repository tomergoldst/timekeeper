package com.tomergoldst.timekeeper.core;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;

import com.tomergoldst.timekeeper.tools.Logger;

public class OnBootReceiver extends BroadcastReceiver {

    private static final String TAG = OnBootReceiver.class.getSimpleName();

    public OnBootReceiver() {
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "onReceive");

        Intent service = new Intent(context, WakeOnAlarmIntentService.class);
        service.setAction(WakeOnAlarmIntentService.ACTION_SET_ALARMS_AFTER_BOOT);
        service.putExtras(intent);
        // ContextCompat will call startForeground for android O and above
        // and will call startService for lower then android O
        ContextCompat.startForegroundService(context, service);

    }
}
