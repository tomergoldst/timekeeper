package com.tomergoldst.timekeeper.alarmscheduler;

import com.tomergoldst.timekeeper.core.SystemAlarmScheduler;
import com.tomergoldst.timekeeper.model.TimePoint;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MockedSystemAlarmSchedulerUnitTest {

    private SystemAlarmScheduler mSystemAlarmScheduler = new MockSystemAlarmScheduler();

    @Before
    public void setUp(){
        mSystemAlarmScheduler.clear();
    }

    @Test
    public void whenSettingTimePoint_timePointIsSet() {
        TimePoint timePoint = new TimePoint(System.currentTimeMillis());
        mSystemAlarmScheduler.setAlarm(timePoint);
        assertTrue(mSystemAlarmScheduler.isAlarmSet(timePoint));
    }

    @Test
    public void whenCancelingTimePoint_timePointNotSet() {
        TimePoint timePoint = new TimePoint(System.currentTimeMillis());
        mSystemAlarmScheduler.setAlarm(timePoint);
        mSystemAlarmScheduler.cancelAlarm(timePoint);
        assertFalse(mSystemAlarmScheduler.isAlarmSet(timePoint));
    }

    @Test
    public void whenSettingTimePointOverExistingTimePoint_timePointUpdated() {
        TimePoint timePoint = new TimePoint(System.currentTimeMillis());
        mSystemAlarmScheduler.setAlarm(timePoint);
        mSystemAlarmScheduler.setAlarm(timePoint);
        assertTrue(mSystemAlarmScheduler.isAlarmSet(timePoint));
    }

    @Test
    public void whenCancelingNotExistingTimePoint_timePointIsNotSet() {
        TimePoint timePoint = new TimePoint(System.currentTimeMillis());
        mSystemAlarmScheduler.cancelAlarm(timePoint);
        assertFalse(mSystemAlarmScheduler.isAlarmSet(timePoint));
    }
}