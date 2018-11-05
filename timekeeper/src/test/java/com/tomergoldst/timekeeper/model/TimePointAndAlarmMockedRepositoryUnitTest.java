package com.tomergoldst.timekeeper.model;

import android.text.TextUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TextUtils.class})
public class TimePointAndAlarmMockedRepositoryUnitTest {

    private RepositoryDataSource mRepository = new MockRepository();

    @Before
    public void setUp(){
        mockStatic(TextUtils.class);
        mRepository.clear();
    }

    @Test
    public void whenStoreTimePoint_timePointIsStored() {
        TimePoint timePoint = new TimePoint(System.currentTimeMillis());
        mRepository.insertTimePoint(timePoint);
        TimePoint resultTimePoint = mRepository.getTimePointAt(timePoint.getTime());
        assertNotNull(resultTimePoint);
    }

    @Test
    public void whenGettingTimePointById_getCorrectTimePoint() {
        TimePoint timePoint = new TimePoint(System.currentTimeMillis());
        mRepository.insertTimePoint(timePoint);
        TimePoint resultTimePoint = mRepository.getTimePointById(timePoint.getId());
        assertNotNull(resultTimePoint);
    }

    @Test
    public void whenDeleteTimePoint_timePointIsRemoved() {
        TimePoint timePoint = new TimePoint(System.currentTimeMillis());
        mRepository.insertTimePoint(timePoint);
        mRepository.deleteTimePoint(timePoint);
        TimePoint resultTimePoint = mRepository.getTimePointAt(timePoint.getTime());
        assertNull(resultTimePoint);
    }

    @Test
    public void whenGettingTimePoints_getAllTimePointsSorted() {
        long now = System.currentTimeMillis();
        long nowPlus1Min = now + (60 * 1000);
        long nowPlus5Min = now + (5 * 60 * 1000);
        mRepository.insertTimePoint(new TimePoint(now));
        mRepository.insertTimePoint(new TimePoint(nowPlus1Min));
        mRepository.insertTimePoint(new TimePoint(nowPlus5Min));
        List<TimePoint> resultTimePoints = mRepository.getAllTimePoints();
        assertEquals(3, resultTimePoints.size());

        for (int i = 0, N = resultTimePoints.size(); i < N - 1 ; i++){
            assertTrue(resultTimePoints.get(i).getTime() < resultTimePoints.get(i+1).getTime());
        }
    }

    @Test
    public void whenStoreAlarm_alarmIsStored() {
        TimePoint timePoint = new TimePoint(System.currentTimeMillis());
        String alarmUid = "reminder";
        Alarm alarm = new Alarm(alarmUid, timePoint.getTime());
        mRepository.insertAlarm(alarm);
        List<Alarm> alarms = mRepository.getAlarms(timePoint);
        assertEquals(1, alarms.size());
    }

    @Test
    public void whenDeleteAlarm_alarmIsRemoved() {
        TimePoint timePoint = new TimePoint(System.currentTimeMillis());
        String alarmUid = "reminder";
        Alarm alarm = new Alarm(alarmUid, timePoint.getTime());
        mRepository.insertAlarm(alarm);
        mRepository.deleteAlarm(alarm);
        List<Alarm> alarms = mRepository.getAlarms(timePoint);
        assertEquals(0, alarms.size());
    }

    @Test
    public void whenMultipleAlarmsExistForTimePoint_getAlarmsContainsAllAlarmsForTimePoint() {
        TimePoint timePoint = new TimePoint(System.currentTimeMillis());
        Alarm alarm1 = new Alarm("reminder1", timePoint.getTime());
        Alarm alarm2 = new Alarm("reminder2", timePoint.getTime());
        mRepository.insertAlarm(alarm1);
        mRepository.insertAlarm(alarm2);
        List<Alarm> alarms = mRepository.getAlarms(timePoint);
        assertEquals(2, alarms.size());
        assertEquals("reminder1", alarms.get(0).getUid());
        assertEquals("reminder2", alarms.get(1).getUid());
    }

    @Test
    public void whenGettingFirstUpcomingAlarm_getActualFirstUpcomingAlarm() {
        long now = System.currentTimeMillis();
        Alarm alarm1 = new Alarm("reminder1", now);
        Alarm alarm2 = new Alarm("reminder1", now + (60 * 1000));
        Alarm alarm3 = new Alarm("reminder1", now + (2 *60 * 1000));
        Alarm alarm4 = new Alarm("reminder1", now - (60 * 1000));
        mRepository.insertAlarm(alarm1);
        mRepository.insertAlarm(alarm2);
        mRepository.insertAlarm(alarm3);
        mRepository.insertAlarm(alarm4);
        Alarm alarm = mRepository.getFirstUpcomingAlarm("reminder1");
        assertNotNull(alarm);
        assertEquals(now - (60 * 1000), alarm.getTime());
    }

}