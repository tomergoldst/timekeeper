package com.tomergoldst.timekeeper.core;

import android.text.TextUtils;

import com.tomergoldst.timekeeper.alarmscheduler.MockSystemAlarmScheduler;
import com.tomergoldst.timekeeper.model.MockRepository;
import com.tomergoldst.timekeeper.model.RepositoryDataSource;
import com.tomergoldst.timekeeper.model.Alarm;
import com.tomergoldst.timekeeper.model.TimePoint;
import com.tomergoldst.timekeeper.tools.DateTools;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Calendar;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TextUtils.class})
public class AlarmManagerUnitTest {

    private RepositoryDataSource mRepository = new MockRepository();
    private SystemAlarmScheduler mSystemAlarmScheduler = new MockSystemAlarmScheduler();
    private AlarmManager mAlarmManager = new AlarmManager(
            mRepository,
            mSystemAlarmScheduler);

    @Before
    public void setUp(){
        mockStatic(TextUtils.class);
        mRepository.clear();
    }

    @Test
    public void whenAddingAlarmWhenTimePointNotExist_alarmAndTimePointAdded() {
        String alarmUid = "alarm1";
        Calendar alarmTimeCal = Calendar.getInstance();
        DateTools.setTimeToEndOfDay(alarmTimeCal);
        long alarmTime = alarmTimeCal.getTimeInMillis();

        // check no such alarm in repo
        List<Alarm> alarms = mRepository.getAlarmsByUid(alarmUid);
        assertEquals(0, alarms.size());

        // add alarm
        Alarm alarm = new Alarm(alarmUid, alarmTime);
        mAlarmManager.setAlarm(alarm);

        // check new timePoint created for this alarm
        TimePoint timePoint = mRepository.getTimePointAt(alarmTime);
        assertNotNull(timePoint);

        // check timePoint is set in the system alarms
        assertTrue(mSystemAlarmScheduler.isAlarmSet(timePoint));

        // check alarm added to repo
        List<Alarm> actualAlarms = mRepository.getAlarms(timePoint);
        assertEquals(1, actualAlarms.size());

        // check alarm uid is correct
        assertEquals(alarmUid, actualAlarms.get(0).getUid());
        // check alarm time is correct
        assertEquals(alarmTime, actualAlarms.get(0).getTime());

        // check number of time points set is exactly 1
        int timePointsNumber = mRepository.countTimePoints();
        assertEquals(1, timePointsNumber);
    }

    @Test
    public void whenCancelingAlarmWhenOnlyThisAlarmExistForThisTimePoint_alarmAndTimePointRemoved() {
        String alarmUid = "alarm1";
        Calendar alarmTimeCal = Calendar.getInstance();
        DateTools.setTimeToEndOfDay(alarmTimeCal);
        long alarmTime = alarmTimeCal.getTimeInMillis();

        // add alarm
        Alarm alarm = new Alarm(alarmUid, alarmTime);
        mAlarmManager.setAlarm(alarm);

        // obtain timePoint before canceling for checking if timePoint was removed from system alarms
        TimePoint timePointPreCancelAlarm = mRepository.getTimePointAt(alarmTime);
        assertNotNull(timePointPreCancelAlarm);

        // cancel alarm
        mAlarmManager.cancelAlarm(alarm);

        // check timePoint is not set in the system alarms
        assertFalse(mSystemAlarmScheduler.isAlarmSet(timePointPreCancelAlarm));

        // check timePoint removed from repo
        TimePoint timePoint = mRepository.getTimePointAt(alarmTime);
        assertNull(timePoint);

        // check alarm removed from repo
        List<Alarm> actualAlarms = mRepository.getAlarmsByUid(alarmUid);
        assertEquals(0, actualAlarms.size());

        // check number of time points set is exactly 0
        int timePointsNumber = mRepository.countTimePoints();
        assertEquals(0, timePointsNumber);
    }

    @Test
    public void whenAddingAlarmWhenTimePointAlreadyExist_onlyAlarmAdded() {
        String alarmUid1 = "alarm1";
        String alarmUid2 = "alarm2";
        Calendar alarmTimeCal = Calendar.getInstance();
        DateTools.setTimeToEndOfDay(alarmTimeCal);
        long alarmTime = alarmTimeCal.getTimeInMillis();

        // add alarm 1
        Alarm alarm1 = new Alarm(alarmUid1, alarmTime);
        mAlarmManager.setAlarm(alarm1);

        // add alarm2
        Alarm alarm2 = new Alarm(alarmUid2, alarmTime);
        mAlarmManager.setAlarm(alarm2);

        // check new timePoint created for this alarms
        TimePoint timePoint = mRepository.getTimePointAt(alarmTime);
        assertNotNull(timePoint);

        // check timePoint is still set in the system alarms
        assertTrue(mSystemAlarmScheduler.isAlarmSet(timePoint));

        // check 2 alarms added to repo
        List<Alarm> actualAlarms = mRepository.getAlarms(timePoint);
        assertEquals(2, actualAlarms.size());

        // check number of time points set is exactly 1 (2 alarms but on the same time => same timePoint)
        int timePointsNumber = mRepository.countTimePoints();
        assertEquals(1, timePointsNumber);
    }


    @Test
    public void whenCancelingAlarmWhenMoreThenOneAlarmExistForThisTimePoint_onlyAlarmRemoved() {
        String alarmUid1 = "alarm1";
        String alarmUid2 = "alarm2";
        Calendar alarmTimeCal = Calendar.getInstance();
        DateTools.setTimeToEndOfDay(alarmTimeCal);
        long alarmTime = alarmTimeCal.getTimeInMillis();

        // add alarm 1
        Alarm alarm1 = new Alarm(alarmUid1, alarmTime);
        mAlarmManager.setAlarm(alarm1);

        // add alarm2
        Alarm alarm2 = new Alarm(alarmUid2, alarmTime);
        mAlarmManager.setAlarm(alarm2);

        // cancel alarm 2
        mAlarmManager.cancelAlarm(alarm2);

        // check timePoint is still in repo (cancel alarm should have only remove the specific alarm at this case)
        TimePoint timePoint = mRepository.getTimePointAt(alarmTime);
        assertNotNull(timePoint);

        // check timePoint is still set in the system alarms
        assertTrue(mSystemAlarmScheduler.isAlarmSet(timePoint));

        // check 1 alarm left at repo
        List<Alarm> actualAlarms = mRepository.getAlarms(timePoint);
        assertEquals(1, actualAlarms.size());

        // check number of time points set is exactly 1 (1 alarm left)
        int timePointsNumber = mRepository.countTimePoints();
        assertEquals(1, timePointsNumber);
    }

    @Test
    public void whenClear_allAlarmsAndTimePointsRemoved() {
        String alarmUid1 = "alarm1";
        String alarmUid2 = "alarm2";
        Calendar alarmTimeCal = Calendar.getInstance();
        DateTools.setTimeToEndOfDay(alarmTimeCal);
        long alarmTime = alarmTimeCal.getTimeInMillis();

        // add alarm 1
        Alarm alarm1 = new Alarm(alarmUid1, alarmTime);
        mAlarmManager.setAlarm(alarm1);
        // add alarm2
        Alarm alarm2 = new Alarm(alarmUid2, alarmTime);
        mAlarmManager.setAlarm(alarm2);

        // obtain timePoint before canceling for checking if timePoint was removed from system alarms
        TimePoint timePointPreCancelAlarm = mRepository.getTimePointAt(alarmTime);
        assertNotNull(timePointPreCancelAlarm);

        // cancel alarm 2
        mAlarmManager.clear();

        // check timePoint is not set in the system alarms
        assertFalse(mSystemAlarmScheduler.isAlarmSet(timePointPreCancelAlarm));

        // check timePoint is still in repo (cancel alarm should have only remove the specific alarm at this case)
        TimePoint timePoint = mRepository.getTimePointAt(alarmTime);
        assertNull(timePoint);

        // check no alarms left at repo
        List<Alarm> actualAlarms1 = mRepository.getAlarmsByUid(alarmUid1);
        assertEquals(0, actualAlarms1.size());

        List<Alarm> actualAlarms2 = mRepository.getAlarmsByUid(alarmUid2);
        assertEquals(0, actualAlarms2.size());

        // check number of time points set is 0
        int timePointsNumber = mRepository.countTimePoints();
        assertEquals(0, timePointsNumber);
    }

    @Test
    public void whenDeviceRebooted_allSystemAlarmsRestored() {
        String alarmUid1 = "alarm1";
        Calendar alarmTimeCal = Calendar.getInstance();
        DateTools.setTimeToEndOfDay(alarmTimeCal);
        long alarmTime1 = alarmTimeCal.getTimeInMillis();

        // add alarm 1
        Alarm alarm1 = new Alarm(alarmUid1, alarmTime1);
        mAlarmManager.setAlarm(alarm1);

        // when devices is booted all system alarms are cleared
        // we mock this issue by clear the system alarms
        mSystemAlarmScheduler.clear();

        // check system alarm is not set
        TimePoint timePoint1 = mRepository.getTimePointAt(alarmTime1);
        assertFalse(mSystemAlarmScheduler.isAlarmSet(timePoint1));

        // restore all system alarms after boot
        mAlarmManager.resetSystemAlarms();

        assertTrue(mSystemAlarmScheduler.isAlarmSet(timePoint1));

    }

    @Test
    public void whenSettingEarlierTimePointIntoFullBuffer_lastTimePointRemovedNewTimePointEnterIntoBuffer() {
        Calendar alarmTimeCal = Calendar.getInstance();
        DateTools.setTimeToStartOfDay(alarmTimeCal);
        int timeStepMin = 10;
        int timePointsBuffer = 1;
        long originalLastTimePoint = 0;

        // create alarms to fill time points buffer
        for (int i = 0 ; i < timePointsBuffer ; i++){
            DateTools.addMinutes(alarmTimeCal, timeStepMin);
            Alarm alarm = new Alarm("alarm" + i, alarmTimeCal.getTimeInMillis());
            mAlarmManager.setAlarm(alarm);

            if (i == timePointsBuffer - 1){
                originalLastTimePoint = alarmTimeCal.getTimeInMillis();
            }
        }

        // pull all timePoints
        List<TimePoint> timePoints = mRepository.getAllTimePoints();
        assertNotNull(timePoints);
        assertEquals(timePointsBuffer, timePoints.size());

        // check last time point is set
        assertTrue(mSystemAlarmScheduler.isAlarmSet(timePoints.get(timePoints.size() - 1)));

        // create alarm for time less then last time point
        DateTools.subtractMinutes(alarmTimeCal, timeStepMin - 1);
        Alarm alarm = new Alarm("alarm" + timePointsBuffer, alarmTimeCal.getTimeInMillis());
        mAlarmManager.setAlarm(alarm);

        // check last time point is not set
        assertFalse(mSystemAlarmScheduler.isAlarmSet(timePoints.get(timePoints.size() - 1)));

        // pull again all timePoints
        timePoints = mRepository.getAllTimePoints();
        assertNotNull(timePoints);
        assertEquals(timePointsBuffer + 1, timePoints.size());

        // check system alarm status
        for (int i = 0 ; i < timePointsBuffer + 1 ; i++){
            if (i < timePointsBuffer) {
                // check all previous timePoints and new alarm alarm is set
                assertTrue(mSystemAlarmScheduler.isAlarmSet(timePoints.get(i)));
            } else {
                // check last time point outside of buffer thus system alarm not set
                assertFalse(mSystemAlarmScheduler.isAlarmSet(timePoints.get(i)));
                // check current last time point is the previous original last time point
                assertEquals(originalLastTimePoint, timePoints.get(i).getTime());
            }
        }
    }

    @Test
    public void whenCancelAlarmByUid_allAlarmsWithThisUidRemoved() {
        String alarmUid = "alarm";
        Calendar alarmTimeCal = Calendar.getInstance();
        DateTools.setTimeToEndOfDay(alarmTimeCal);

        int timeStepMin = 10;

        DateTools.addMinutes(alarmTimeCal, timeStepMin);
        mAlarmManager.setAlarm(new Alarm(alarmUid, alarmTimeCal.getTimeInMillis()));
        DateTools.addMinutes(alarmTimeCal, timeStepMin);
        mAlarmManager.setAlarm(new Alarm(alarmUid, alarmTimeCal.getTimeInMillis()));
        DateTools.addMinutes(alarmTimeCal, timeStepMin);
        mAlarmManager.setAlarm(new Alarm(alarmUid, alarmTimeCal.getTimeInMillis()));

        List<Alarm> alarms;

        // check no alarms left at repo
        alarms = mRepository.getAlarmsByUid(alarmUid);
        assertEquals(3, alarms.size());

        // cancel alarm by uid
        mAlarmManager.cancelAlarm(alarmUid);

        alarms = mRepository.getAlarmsByUid(alarmUid);
        assertEquals(0, alarms.size());


    }

    @Test
    public void whenGettingNextAlarmByUid_gettingActualNextAlarmFromAllAlarmsWithThisUid() {
        String alarmUid = "alarm";
        Calendar alarmTimeCal = Calendar.getInstance();
        DateTools.setTimeToEndOfDay(alarmTimeCal);
        long expectedTime = alarmTimeCal.getTimeInMillis();

        int timeStepMin = 10;

        DateTools.addMinutes(alarmTimeCal, timeStepMin);
        mAlarmManager.setAlarm(new Alarm(alarmUid, alarmTimeCal.getTimeInMillis()));
        DateTools.addMinutes(alarmTimeCal, timeStepMin);
        mAlarmManager.setAlarm(new Alarm(alarmUid, alarmTimeCal.getTimeInMillis()));
        DateTools.subtractMinutes(alarmTimeCal, 2 * timeStepMin);
        mAlarmManager.setAlarm(new Alarm(alarmUid, alarmTimeCal.getTimeInMillis()));

        Alarm alarm = mAlarmManager.getNext(alarmUid);

        assertEquals(expectedTime, alarm.getTime());
    }

    @Test
    public void whenGettingNextAlarmTime_gettingActualFirstUpComingTimePointOfAllAlarms() {
        Calendar alarmTimeCal = Calendar.getInstance();
        DateTools.setTimeToEndOfDay(alarmTimeCal);
        long expectedTime = alarmTimeCal.getTimeInMillis();

        int timeStepMin = 10;

        DateTools.addMinutes(alarmTimeCal, timeStepMin);
        mAlarmManager.setAlarm(new Alarm("alarm1", alarmTimeCal.getTimeInMillis()));
        DateTools.addMinutes(alarmTimeCal, timeStepMin);
        mAlarmManager.setAlarm(new Alarm("alarm2", alarmTimeCal.getTimeInMillis()));
        DateTools.subtractMinutes(alarmTimeCal, 2 * timeStepMin);
        mAlarmManager.setAlarm(new Alarm("alarm3", alarmTimeCal.getTimeInMillis()));

        long actualTime = mAlarmManager.getNextAlarmTime();

        assertEquals(expectedTime, actualTime);
    }

    @Test
    public void whenProcessAlarmReceived_alarmAndTimePointRemoved() {
        String alarmUid = "alarm";
        Calendar alarmTimeCal = Calendar.getInstance();
        DateTools.setTimeToEndOfDay(alarmTimeCal);
        long alarmTime = alarmTimeCal.getTimeInMillis();

        // add alarm
        Alarm alarm = new Alarm(alarmUid, alarmTime);
        mAlarmManager.setAlarm(alarm);

        // check new timePoint created for this alarm
        TimePoint timePoint = mRepository.getTimePointAt(alarmTime);
        assertNotNull(timePoint);

        // check alarm added to repo
        List<Alarm> actualAlarms = mRepository.getAlarms(timePoint);
        assertEquals(1, actualAlarms.size());

        // call to processReceivedAlarm(alarmTime) which is called when alarm triggered
        // and the WakeOnAlarm service start and process the alarm
        mAlarmManager.processReceivedAlarm(alarmTime);

        // check timePoint created for this alarm removed
        assertNull(mRepository.getTimePointAt(alarmTime));
        // check alarm removed
        assertEquals(0, mRepository.getAlarms(timePoint).size());

    }

    @Test
    public void whenProcessPersistedAlarmReceived_alarmDoesNotRemovedAfterAlarmReceived() {
        String alarmUid = "alarm";
        Calendar alarmTimeCal = Calendar.getInstance();
        DateTools.setTimeToStartOfDay(alarmTimeCal);
        long alarmTime = alarmTimeCal.getTimeInMillis();

        // add alarm
        Alarm alarm = new Alarm(alarmUid, alarmTime);
        alarm.setPersist(true);
        mAlarmManager.setAlarm(alarm);

        // check new timePoint created for this alarm
        TimePoint timePoint = mRepository.getTimePointAt(alarmTime);
        assertNotNull(timePoint);

        // check alarm added to repo
        List<Alarm> actualAlarms = mRepository.getAlarms(timePoint);
        assertEquals(1, actualAlarms.size());

        // call to processReceivedAlarm(alarmTime) which is called when alarm triggered
        // and the WakeOnAlarm service start and process the alarm
        mAlarmManager.processReceivedAlarm(alarmTime);

        // check timePoint created for this alarm removed
        assertNull(mRepository.getTimePointAt(alarmTime));
        // check alarm not removed
        assertEquals(1, mRepository.getAlarms(timePoint).size());

    }

    @Test
    public void whenCallRemovePastPersistAlarms_allPastPersistedAlarmRemoved() {
        String alarmUid = "alarm";
        Calendar alarmTimeCal = Calendar.getInstance();
        DateTools.setTimeToStartOfDay(alarmTimeCal);
        long alarmTime = alarmTimeCal.getTimeInMillis();

        // add alarm
        Alarm alarm = new Alarm(alarmUid, alarmTime);
        alarm.setPersist(true);

        mAlarmManager.setAlarm(alarm);

        // check new timePoint created for this alarm
        TimePoint timePoint = mRepository.getTimePointAt(alarmTime);
        assertNotNull(timePoint);

        // check alarm added to repo
        List<Alarm> actualAlarms = mRepository.getAlarms(timePoint);
        assertEquals(1, actualAlarms.size());

        // call to processReceivedAlarm(alarmTime) which is called when alarm triggered
        // and the WakeOnAlarm service start and process the alarm
        mAlarmManager.processReceivedAlarm(alarmTime);

        // check timePoint created for this alarm removed
        assertNull(mRepository.getTimePointAt(alarmTime));
        // check alarm not removed
        assertEquals(1, mRepository.getAlarms(timePoint).size());

        mAlarmManager.removePastPersistAlarms();

        assertEquals(0, mRepository.getAlarms(timePoint).size());

    }


}