package com.tomergoldst.timekeeper.tools;

import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class DateTools {

    public static int getDaysBetweenRoundUp(long t1, long t2) {
        long msDiff = t2 - t1;
        float daysFraction = ((float)msDiff / DateUtils.DAY_IN_MILLIS);
        return (int) Math.ceil(daysFraction);
    }

    public static int getDaysBetweenRoundUp(Calendar c1, Calendar c2) {
        return getDaysBetweenRoundUp(c1.getTimeInMillis(), c2.getTimeInMillis());
    }

    public static int getDaysBetween(long t1, long t2) {
        long msDiff = t2 - t1;
        return (int) TimeUnit.MILLISECONDS.toDays(msDiff);
    }

    public static int getDaysBetween(Calendar c1, Calendar c2) {
        return getDaysBetween(c1.getTimeInMillis(), c2.getTimeInMillis());
    }

    public static Calendar setTimeToStartOfDay(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Calendar setTimeToEndOfDay(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar;
    }

    public static void setTimeTo(Calendar calendar, int hourOfDay, int minute){
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static void setDateTo(Calendar calendar, int year, int month, int dayOfMonth){
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    }

    public static void addMinutes(Calendar calendar, int minute){
        calendar.add(Calendar.MINUTE, minute);
    }

    public static void subtractMinutes(Calendar calendar, int minute){
        calendar.add(Calendar.MINUTE, -minute);
    }

    public static boolean isSameDay(Calendar c1, Calendar c2){
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    public static void addDays(Calendar calendar, int days){
        calendar.add(Calendar.DAY_OF_MONTH, days);
    }

    public static void subtractDays(Calendar calendar, int days){
        calendar.add(Calendar.DAY_OF_MONTH, -days);
    }

    public static Calendar newCalendarInstance(long time){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal;
    }
}
