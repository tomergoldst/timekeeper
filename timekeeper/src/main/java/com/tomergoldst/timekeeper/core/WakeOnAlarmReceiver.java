package com.tomergoldst.timekeeper.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;

import com.tomergoldst.timekeeper.tools.Logger;

public class WakeOnAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = WakeOnAlarmReceiver.class.getSimpleName();

    public WakeOnAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "OnReceive action = " + intent.getAction());

        Intent service = new Intent(context, WakeOnAlarmIntentService.class);
        service.setAction(intent.getAction());
        service.putExtras(intent);

        // ContextCompat will call startForeground for android O and above
        // and will call startService for lower then android O
        ContextCompat.startForegroundService(context, service);
    }
}
