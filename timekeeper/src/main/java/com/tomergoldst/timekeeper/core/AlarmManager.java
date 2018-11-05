 package com.tomergoldst.timekeeper.core;

import android.support.annotation.NonNull;

import com.tomergoldst.timekeeper.model.RepositoryDataSource;
import com.tomergoldst.timekeeper.model.Alarm;
import com.tomergoldst.timekeeper.model.ReceivedAlarmData;
import com.tomergoldst.timekeeper.model.TimePoint;
import com.tomergoldst.timekeeper.tools.Logger;

import java.util.Date;
import java.util.List;

 /**
  * Manage set and cancel alarm operations
  */
 final class AlarmManager {

     private static final String TAG = AlarmManager.class.getSimpleName();

     /**
      * Handle read / write operations to database
      */
     private RepositoryDataSource mRepository;

     /**
      * Handle scheduling alarms using the Android AlarmManger.java
      */
     private SystemAlarmScheduler mSystemAlarmScheduler;

     AlarmManager(RepositoryDataSource repository,
                  SystemAlarmScheduler systemAlarmScheduler) {
         mRepository = repository;
         mSystemAlarmScheduler = systemAlarmScheduler;
     }

     /**
      * Set status review alarm to monitor when unexpected behaviour occur
      */
     void setStatusReviewAlarm() {
         mSystemAlarmScheduler.setStatusReviewAlarm();
     }

     /**
      * check if status review alarm exist on system alarms
      * @return true is status review alarm exist on system alarms, false otherwise
      */
     boolean isStatusReviewAlarmSet(){
         return mSystemAlarmScheduler.isStatusReviewAlarmSet();
     }


     /**
      * set alarm to be triggered
      * @param alarm alarm
      * @return inserted alarm id
      */
     long setAlarm(@NonNull Alarm alarm) {
         TimePoint timePoint = new TimePoint(alarm.getTime());

         // warning trying to set a past due timePoint
         if (timePoint.getTime() < System.currentTimeMillis()) {
             Logger.w(TAG, "setting an overdue alarm");
         }

         long id;

         if (!isTimePointExist(timePoint)) {
             // save timePoint and alarm to database, and set timePoint if needed
             id = addAlarmWithNewTimePoint(timePoint, alarm);
         } else {
             // add alarm entry
             id = addAlarmToExistingTimePoint(alarm);
         }

         return id;
     }

     /**
      * Schedule system alarm
      * @param timePoint timePoint
      */
     private void setSystemAlarm(@NonNull TimePoint timePoint) {
         // set android system timePoint for the desired timePoint time
         mSystemAlarmScheduler.setAlarm(timePoint);
     }

     /**
      * Add alarm with non existing time points
      * @param timePoint timePoint
      * @param alarm alarm
      * @return inserted alarm id
      */
     private long addAlarmWithNewTimePoint(TimePoint timePoint, Alarm alarm) {
         long id;

         // If buffer not full
         if (mRepository.countTimePoints() < 1) {
             id = insertAlarmIntoNotFullBuffer(timePoint, alarm);
         } else {
             long lastTimePointSet = getLastTimePointSetTime();
             // if buffer full and need to replace last timePoint with new timePoint
             if (timePoint.getTime() < lastTimePointSet) {
                 id = insertAlarmIntoFullBuffer(timePoint, alarm, lastTimePointSet);
             } else {
                 // if buffer full and no need to replace last timePoint
                 id = storeNewAlarm(timePoint, alarm);
             }
         }

         return id;
     }

     /**
      * Insert new timePoint into full buffer logic
      * Replace oldest timePoint inside buffer with new timePoint
      *
      * @param timePoint - timePoint
      * @param alarm - alarm
      * @param lastTimePointSet - last timePoint time
      */
     private long insertAlarmIntoFullBuffer(TimePoint timePoint, Alarm alarm, long lastTimePointSet) {
         long id;

         TimePoint timePointToCancel = mRepository.getTimePointAt(lastTimePointSet);

         if (timePointToCancel != null) {
             // cancel system alarm for existing time point
             cancelSystemAlarm(timePointToCancel);
             // enter new alarm for new time point
             id = storeNewAlarm(timePoint, alarm);
             // set system alarm for newly entered alarm
             setSystemAlarm(timePoint);
         } else {
             throw new RuntimeException("timePoint to cancel is null");
         }

         return id;
     }

     /**
      * Insert timePoint into not full buffer.
      * simply add the timePoint, update info and set schedule system alarm for the time point
      *
      * @param timePoint - timePoint
      * @param alarm - alarm
      */
     private long insertAlarmIntoNotFullBuffer(TimePoint timePoint, Alarm alarm) {
         long id = storeNewAlarm(timePoint, alarm);
         setSystemAlarm(timePoint);
         return id;
     }

     /**
      * Store new alarm. store both the alarm and time point
      *
      * @param timePoint - timePoint
      * @param alarm - alarm
      */
     private long storeNewAlarm(TimePoint timePoint, Alarm alarm) {
         mRepository.insertTimePoint(timePoint);
         return mRepository.insertAlarm(alarm);
     }

     /**
      * Cancel scheduled alarm
      *
      * @param alarm - alarm
      */
     void cancelAlarm(Alarm alarm) {
         TimePoint timePoint = mRepository.getTimePointAt(alarm.getTime()); //new TimePoint(alarm.getTime());
         if (timePoint != null) {
             // delete alarm
             mRepository.deleteAlarm(alarm);

             // if no alarms exist for the timePoint then cancel the timePoint
             if (mRepository.getAlarms(timePoint).size() == 0) {
                 cancelTimePoint(timePoint);
                 // after deleting an alarm set next timePoint
                 setNextTimePoint();
             }

         } else {
             throw new RuntimeException("Trying to cancel an alarm for non existing time point");
         }
     }

     /**
      * Cancel scheduled alarms by uid
      *
      * @param uid uid
      */
     void cancelAlarm(String uid) {
         List<Alarm> alarms = mRepository.getAlarmsByUid(uid);

         if (alarms != null && !alarms.isEmpty()){
             for (Alarm alarm : alarms){
                 cancelAlarm(alarm);
             }
         } else {
             Logger.w(TAG, "Could not find matching alarms for uid = " + uid);
         }
     }

     /**
      * Cancel scheduled alarm by id
      *
      * @param id id
      */
     void cancelAlarm(long id) {
         Alarm alarm = mRepository.getAlarm(id);
         if (alarm != null){
             cancelAlarm(alarm);
         } else {
             Logger.w(TAG, "Could not find alarm with id = " + id);
         }
     }

     /**
      * Cancel system alarm for a timePoint
      *
      * @param timePoint - timePoint
      */
     private void cancelSystemAlarm(TimePoint timePoint) {
         // Cancel system timePoint
         mSystemAlarmScheduler.cancelAlarm(timePoint);
     }

     /**
      * Cancel timePoint
      *
      * @param timePoint - timePoint
      */
     private void cancelTimePoint(TimePoint timePoint) {
         // Cancel system timePoint if was set
         if (mSystemAlarmScheduler.isAlarmSet(timePoint)) {
             cancelSystemAlarm(timePoint);
         }

         // Delete timePoint from database
         deleteTimePointEntryFromDb(timePoint);
     }

     /**
      * Store alarm to database
      *
      * @param alarm - alarm
      */
     private long addAlarmToExistingTimePoint(Alarm alarm) {
         return mRepository.insertAlarm(alarm);
     }

     /**
      * Handle delete timePoint logic
      *
      * @param timePoint - timePoint
      */
     private void deleteTimePointEntryFromDb(TimePoint timePoint) {
         mRepository.deleteTimePoint(timePoint);
     }

     /**
      * Set system alarm for the next waiting time point (or time points)
      *
      */
     private void setNextTimePoint() {
         List<TimePoint> timePoints = mRepository.getTimePoints(1);
         if (timePoints != null && !timePoints.isEmpty()) {
             for (TimePoint timePoint : timePoints) {
                 setSystemAlarm(timePoint);
             }
         } else {
             Logger.i(TAG, "Got empty timePoints list from getNextTimePoints() - No new timePoints have been schedule");
         }
     }

     /**
      * Check if TimePoint entry exist in database
      * @param timePoint time point
      * @return true is exist on database, false otherwise
      */
     private boolean isTimePointExist(@NonNull TimePoint timePoint){
         return mRepository.getTimePointAt(timePoint.getTime()) != null;
     }

     /**
      * cancel all alarms and reset all data
      */
     void clear(){
         List<TimePoint> timePoints = mRepository.getAllTimePoints();

         for (TimePoint timePoint : timePoints){
             List<Alarm> alarms = mRepository.getAlarms(timePoint);
             for (Alarm alarm : alarms){
                 cancelAlarm(alarm);
             }
         }

         // remove all persisted alarms
         mRepository.deleteAllPersistedAlarms();

     }

     /**
      * Update the status of alarm after it was triggered
      * @param receivedAlarmData receivedAlarmData
      */
     private void onAlarmReceived(ReceivedAlarmData receivedAlarmData){
         // Delete all alarms received
         for (Alarm alarm : receivedAlarmData.getAlarms()){
             if (!alarm.isPersist()){
                 mRepository.deleteAlarm(alarm);
             }
         }

         // Delete current timePoint
         deleteTimePointEntryFromDb(receivedAlarmData.getTimePoint());

         // Set next timePoint - restore system stability
         setNextTimePoint();
     }

     /**
      * Process the received alarm and update status
      * @param alarmTime alarm time
      * @return ReceivedAlarmData
      */
     ReceivedAlarmData processReceivedAlarm(long alarmTime){
         // Get timePoint
         TimePoint timePoint = mRepository.getTimePointAt(alarmTime);

         // Check timePoint for that time exist
         if (timePoint == null){
             throw new RuntimeException("Could not found timePoint for time " + new Date(alarmTime).toString());
         }

         // Get current timePoint alarms
         List<Alarm> alarms = mRepository.getAlarms(timePoint);
         // Get persisted alarms - previous alarms
         List<Alarm> persistAlarms = mRepository.getPersistedAlarmsList(timePoint.getTime());
         // Create processed alarm data
         ReceivedAlarmData receivedAlarmData = new ReceivedAlarmData(timePoint, alarms, persistAlarms);

         // update alarms status
         onAlarmReceived(receivedAlarmData);

         return receivedAlarmData;
     }

     /**
      * Delete all past persisted alarms up to current time
      */
     void removePastPersistAlarms() {
         mRepository.deleteAllPreviousPersistedAlarms();
     }

     /**
      * Get all time points in the system
      * @return list of time points
      */
     List<TimePoint> getTimePoints() {
         return mRepository.getAllTimePoints();
     }

     /**
      * Get all alarms in the system
      * @return list of alarms
      */
     List<Alarm> getAlarms() {
         return mRepository.getAllAlarms();
     }

     /**
      * get next upcoming alarm for a specific alarm
      * @param alarmUid alarm uid
      * @return alarm time in millis
      */
     Alarm getNext(String alarmUid){
         return mRepository.getFirstUpcomingAlarm(alarmUid);
     }

     /**
      * get next upcoming alarm from all the alarms
      * @return alarm time in millis
      */
     List<Alarm> getNext(){
         TimePoint timePoint = mRepository.getFirstUpcomingTimePoint();
         return mRepository.getAlarms(timePoint);
     }

     /**
      * get next upcoming alarm time
      * @return alarm time in millis
      */
     long getNextAlarmTime(){
         TimePoint timePoint = mRepository.getFirstUpcomingTimePoint();
         if (timePoint == null){
             return -1;
         }
         return timePoint.getTime();
     }

     /**
      * Restore all alarms after device boot
      */
     void resetSystemAlarms(){
         // Get all timePoints from DB
         List<TimePoint> timePoints = mRepository.getAllTimePoints();

         if (timePoints != null) {
             long maxTime = getMaxTimeBetweenLastTimePointSetTimeAndCurrentTime();

             for (TimePoint timePoint : timePoints) {
                 if (timePoint.getTime() <= maxTime) {
                     Logger.d(TAG, "reset timePoint " + timePoint.getId() + " for " + timePoint.getReadableDate());
                     setSystemAlarm(timePoint);
                 } else{
                     break;
                 }
             }
             Logger.d(TAG, "All time points were reset");

         }
         Logger.d(TAG, "No time points exist to reset");
     }

     private long getMaxTimeBetweenLastTimePointSetTimeAndCurrentTime() {
         long currentTime = System.currentTimeMillis();
         long lastTimePointSet = getLastTimePointSetTime();
         return currentTime > lastTimePointSet ? currentTime : lastTimePointSet;
     }

     private long getLastTimePointSetTime(){
         List<TimePoint> timePoints = mRepository.getTimePoints(1);
         if (timePoints.isEmpty()) return 0;
         return timePoints.get(timePoints.size() - 1).getTime();
      }

 }
