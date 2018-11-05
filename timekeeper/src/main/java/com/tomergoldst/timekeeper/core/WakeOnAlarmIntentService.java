package com.tomergoldst.timekeeper.core;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.tomergoldst.timekeeper.model.Alarm;
import com.tomergoldst.timekeeper.model.ReceivedAlarmData;
import com.tomergoldst.timekeeper.model.TimePoint;
import com.tomergoldst.timekeeper.tools.Logger;

import java.util.ArrayList;
import java.util.List;

public final class WakeOnAlarmIntentService extends IntentService {

    private static final String TAG = WakeOnAlarmIntentService.class.getSimpleName();

    public static final String ACTION_SET_ALARMS_AFTER_BOOT =
            "com.tomergoldst.timekeeper.core.action.set_alarms";
    public static final String ACTION_SET_ALARMS_AFTER_MY_PACKAGE_REPLACED =
            "com.tomergoldst.timekeeper.core.action.my_package_replaced";

    public WakeOnAlarmIntentService() {
        super("WakeOnAlarmIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, Config.CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setContentTitle("Foreground Service running")
                    .setPriority(NotificationManager.IMPORTANCE_LOW)
                    .build();

            startForeground(Config.FOREGROUND_SERVICE_NOTIFICATION_ID, notification);
        }
    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        }
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            Logger.i(TAG, "start service action = " + action);

            assert action != null;
            switch(action){
                case ACTION_SET_ALARMS_AFTER_BOOT:
                    handleActionSetAlarmsAfterBoot();
                    break;
                case ACTION_SET_ALARMS_AFTER_MY_PACKAGE_REPLACED:
                    handleActionSetAlarmsAfterBoot();
                    break;
                default:
                    handleReceivedAlarm(intent);
                    break;
            }
        }
    }

    private void handleReceivedAlarm(Intent intent) {
        // Get timePoint time from intent extras
        long alarmTime = intent.getLongExtra(TimePoint.PARAM_TIME_POINT_SIGNATURE, 0);

        // handle response operation for received timePoint, Including regenerate recurring alarms
        // RecurringAlarmManger will call the AlarmManger.onAlarmReceived(TimePoint timePoint)
        ReceivedAlarmData receivedAlarmData = TimeKeeper.getInstance().getAlarmManger().processReceivedAlarm(alarmTime);

        // Call receiver for client to interact
        callReceiver(receivedAlarmData.getAlarms(), receivedAlarmData.getPersistedAlarms());
    }

    private void callReceiver(List<Alarm> alarms, List<Alarm> persistAlarms) {
        if (persistAlarms != null && !persistAlarms.isEmpty()) {
            alarms.addAll(persistAlarms);
        }

        Intent intent = new Intent();
        intent.setPackage(getApplicationContext().getPackageName());
        intent.setAction(Config.INTENT_ACTION_RECEIVE_ALARMS);
        intent.putExtra("alarms", (ArrayList)alarms);
        sendBroadcast(intent);
    }

    private void handleActionSetAlarmsAfterBoot() {
        TimeKeeper.getInstance().getAlarmManger().resetSystemAlarms();
    }

}
