package com.tomergoldst.timekeeperdemo;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.facebook.stetho.Stetho;
import com.tomergoldst.timekeeper.core.TimeKeeper;

public class MyApplication extends Application {

    public static final String CHANNEL_ID = "alarm_notifications_01";

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable us to debug the application
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(getApplicationContext());
        }

        // Create notification channel on android O and above so we can receive notifications
        createNotificationChannel();

        // init TimeKeeper SDK
        TimeKeeper.initialize(this);

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

}
