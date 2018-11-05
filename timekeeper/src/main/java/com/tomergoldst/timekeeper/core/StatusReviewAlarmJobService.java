package com.tomergoldst.timekeeper.core;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.tomergoldst.timekeeper.tools.Logger;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Tomer on 28/10/2018.
 */

public final class StatusReviewAlarmJobService extends JobService {

    private static final String TAG = StatusReviewAlarmJobService.class.getSimpleName();

    @Override
    public boolean onStartJob(final JobParameters job) {
        Logger.d(TAG, "onStartJob");

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AlarmManager alarmManager = TimeKeeper.getInstance().getAlarmManger();
                if (!alarmManager.isStatusReviewAlarmSet()){
                    Logger.w(TAG, "status review alarm is not set, Rescheduling all system alarms");

                    // reschedule status review alarm
                    alarmManager.setStatusReviewAlarm();
                    // reschedule all alarms
                    TimeKeeper.getInstance().getAlarmManger().resetSystemAlarms();

                    // TODO: 28/10/2018 report on issue
                }

                jobFinished(job, false);

            }
        });

        return true; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // Answers the question: "Should this job be retried?"
    }

}
