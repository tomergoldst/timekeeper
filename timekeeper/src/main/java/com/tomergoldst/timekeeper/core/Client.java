package com.tomergoldst.timekeeper.core;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.tomergoldst.timekeeper.BuildConfig;
import com.tomergoldst.timekeeper.R;
import com.tomergoldst.timekeeper.model.Repository;
import com.tomergoldst.timekeeper.model.RepositoryDataSource;
import com.tomergoldst.timekeeper.model.Alarm;
import com.tomergoldst.timekeeper.tools.DebugLogger;
import com.tomergoldst.timekeeper.tools.FileLogger;
import com.tomergoldst.timekeeper.tools.Logger;

import java.util.List;

/**
 * Client
 */
public final class Client {

    private static final String TAG = Client.class.getSimpleName();

    private AlarmManager mAlarmManager;

    /**
     * Initialize TimeKeeper library
     * @param context context
     */
    void initialize(final Context context){
        Context appContext = context.getApplicationContext();

        if (BuildConfig.DEBUG) {
            initDeveloperMode(appContext);
        }

        RepositoryDataSource repository = new Repository(appContext);
        SystemAlarmScheduler systemAlarmScheduler = new SystemAlarmSchedulerImpl(appContext);

        mAlarmManager = new AlarmManager(
                repository,
                systemAlarmScheduler);

        createNotificationChannel(appContext);

        // Set status review alarm to monitor for system alarm issues
        mAlarmManager.setStatusReviewAlarm();
        // run periodic status review job service
        startReviewAlarmsStatusJobService(appContext);

        // On every call to initialize reset system alarms is status review alarm is not set
        if (!mAlarmManager.isStatusReviewAlarmSet()) {
            mAlarmManager.resetSystemAlarms();
        }

    }

    private void initDeveloperMode(Context ctx){
        // Register debug logger to show logs on logcat
        Logger.addExecutor(new DebugLogger());
        // Register file logging to record all logs to file
        Logger.addExecutor(new FileLogger(ctx));
    }

    private void createNotificationChannel(Context ctx) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ctx.getString(R.string.time_keeper_channel_name);
            String description = ctx.getString(R.string.time_keeper_channel_desc);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(Config.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(null, null);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Job service that runs twice a day inorder to check the system alarms status
     * If an issue discover the job service will reschedule all alarms (same as after device reboot)
     * @param context context
     */
    private void startReviewAlarmsStatusJobService(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job myJob = dispatcher.newJobBuilder()
                .setService(StatusReviewAlarmJobService.class)
                .setTag(Config.JOB_SERVICE_REVIEW_ALARMS_STATUS)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow((60 * 60 * 5),60 * 60 * 6))
                .setLifetime(Lifetime.FOREVER)
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();

        dispatcher.mustSchedule(myJob);
    }

    /**
     * set alarm to be triggered
     * @param alarm alarm
     * @return inserted alarm id
     */
    public long setAlarm(Alarm alarm){
        return mAlarmManager.setAlarm(alarm);
    }

    /**
     * cancel alarm by id
     * @param id alarm id
     */
    public void cancelAlarm(long id) {
        mAlarmManager.cancelAlarm(id);
    }

    /**
     * cancel alarm
     * @param uid alarm uid
     */
    public void cancelAlarm(String uid) {
        mAlarmManager.cancelAlarm(uid);
    }

    /**
     * Get closest alarms to current time of request
     * @param alarmUid alarm uid
     * @return most closest alarm to current time of request
     */
    public Alarm getNextAlarm(String alarmUid){
        return mAlarmManager.getNext(alarmUid);
    }

    /**
     * Get closest alarms to current time of request
     * Each time point might be bound to more then one alarm and hence we return a list of the alarms
     * @return most closest alarms to current time of request
     */
    public List<Alarm> getNextAlarms(){
        return mAlarmManager.getNext();
    }

    /**
     * Get closest alarm time to current time of request
     * @return most closest alarm time to current time of request in millis
     */
    public long getNextAlarmTime(){
        return mAlarmManager.getNextAlarmTime();
    }

    /**
     * Get all alarms
     * @return alarms
     */
    public List<Alarm> getAlarms(){
        return mAlarmManager.getAlarms();
    }

    /**
     * Delete all past persisted alarms up to current time
     */
    public void removePastPersistAlarms(){
        mAlarmManager.removePastPersistAlarms();
    }

    /**
     * cancel all alarms
     */
    public void clear(){
        mAlarmManager.clear();
    }

    AlarmManager getAlarmManger(){
        return mAlarmManager;
    }
}
