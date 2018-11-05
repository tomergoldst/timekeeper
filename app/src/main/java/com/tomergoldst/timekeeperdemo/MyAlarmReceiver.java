package com.tomergoldst.timekeeperdemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.tomergoldst.timekeeper.model.Alarm;

import java.util.List;

public class MyAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /*
          Extract alarms from intent
         */
        List<Alarm> alarms = intent.getParcelableArrayListExtra("alarms");

        /*
         * When Using persisted alarms it is your responsibility to call removePastPersistAlarms()
         * when persisted alarms been used so no need to keep deliver it again with any new alarm
         */
        //TimeKeeper.getInstance().removePastPersistAlarms();

        /*
         * Example of action to be taken when an alarm received.
         * Everything that happen on this method occurs under wakelock.
         */
        notifyUser(context, alarms);

    }

    private void notifyUser(Context context, List<Alarm> actions){

        StringBuilder actionsStr = new StringBuilder();
        for (int i = 0, N = actions.size() ; i < N ; i++){
            actionsStr.append(actions.get(i).getUid());
            if (i < N - 1) actionsStr.append(", ");
        }

        String channelId = MyApplication.CHANNEL_ID;

        Intent notifyIntent = new Intent(context, MainActivity.class);
        // Set the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Create the PendingIntent
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                context, 1, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_stat_access_alarm)
                .setContentTitle("Alarm Notification")
                .setContentText(context.getString(R.string.uid) + ": " + actionsStr.toString())
                .setContentIntent(notifyPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, mBuilder.build());
        }
    }
}
