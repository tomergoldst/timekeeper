package com.tomergoldst.timekeeper.tools;

import java.text.DateFormat;
import java.util.Calendar;

public class ReadableDateTools {

    public static String getReadableTimeText(Calendar calendar) {
        DateFormat df = DateFormat.getTimeInstance();
        return df.format(calendar.getTime());
    }

    public static String getReadableDateText(Calendar calendar) {
        DateFormat df = DateFormat.getDateInstance();
        return df.format(calendar.getTime());
    }}
