package com.tomergoldst.timekeeper.tools;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Logger {

    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int NONE = Integer.MAX_VALUE;

    private static final int MAX_LOG_LENGTH = 4000;
    private static final int MAX_TAG_LENGTH = 23;

    private static List<LoggerExecutor> sLoggerExecutors =
            Collections.synchronizedList(new ArrayList<LoggerExecutor>());

    public interface LoggerExecutor {
        boolean isLoggable(@NonNull String tag, int priority);
        void log(int priority, @Nullable String tag, @NonNull String message, @Nullable Throwable t);
    }

    public static void addExecutor(LoggerExecutor loggerExecutor){
        sLoggerExecutors.add(loggerExecutor);
    }

    public static void removeExecutor(LoggerExecutor loggerExecutor){
        sLoggerExecutors.remove(loggerExecutor);
    }

    public static void v(String tag, String message) {
        for (LoggerExecutor loggerExecutor : sLoggerExecutors){
            if (loggerExecutor.isLoggable(tag, VERBOSE)){
                loggerExecutor.log(VERBOSE, tag, message, null);
            }
        }
    }

    public static void v(String tag, String message, Throwable throwable) {
        for (LoggerExecutor loggerExecutor : sLoggerExecutors){
            if (loggerExecutor.isLoggable(tag, VERBOSE)){
                loggerExecutor.log(VERBOSE, tag, message, throwable);
            }
        }
    }

    public static void d(String tag, String message) {
        for (LoggerExecutor loggerExecutor : sLoggerExecutors){
            if (loggerExecutor.isLoggable(tag, DEBUG)){
                loggerExecutor.log(DEBUG, tag, message, null);
            }
        }
    }

    public static void d(String tag, String message, Throwable throwable) {
        for (LoggerExecutor loggerExecutor : sLoggerExecutors){
            if (loggerExecutor.isLoggable(tag, DEBUG)){
                loggerExecutor.log(DEBUG, tag, message, throwable);
            }
        }
    }

    public static void i(String tag, String message) {
        for (LoggerExecutor loggerExecutor : sLoggerExecutors){
            if (loggerExecutor.isLoggable(tag, INFO)){
                loggerExecutor.log(INFO, tag, message, null);
            }
        }
    }

    public static void i(String tag, String message, Throwable throwable) {
        for (LoggerExecutor loggerExecutor : sLoggerExecutors){
            if (loggerExecutor.isLoggable(tag, INFO)){
                loggerExecutor.log(INFO, tag, message, throwable);
            }
        }
    }

    public static void w(String tag, String message) {
        for (LoggerExecutor loggerExecutor : sLoggerExecutors){
            if (loggerExecutor.isLoggable(tag, WARN)){
                loggerExecutor.log(WARN, tag, message, null);
            }
        }
    }

    public static void w(String tag, String message, Throwable throwable) {
        for (LoggerExecutor loggerExecutor : sLoggerExecutors){
            if (loggerExecutor.isLoggable(tag, WARN)){
                loggerExecutor.log(WARN, tag, message, throwable);
            }
        }
    }

    public static void e(String tag, String message) {
        for (LoggerExecutor loggerExecutor : sLoggerExecutors){
            if (loggerExecutor.isLoggable(tag, ERROR)){
                loggerExecutor.log(ERROR, tag, message, null);
            }
        }
    }

    public static void e(String tag, String message, Throwable throwable) {
        for (LoggerExecutor loggerExecutor : sLoggerExecutors){
            if (loggerExecutor.isLoggable(tag, ERROR)){
                loggerExecutor.log(ERROR, tag, message, throwable);
            }
        }
    }

    public static void wtf(String tag, String message) {
        for (LoggerExecutor loggerExecutor : sLoggerExecutors){
            if (loggerExecutor.isLoggable(tag, ERROR)){
                loggerExecutor.log(VERBOSE, tag, message, null);
            }
        }
    }

    public static void wtf(String tag, String message, Throwable throwable) {
        for (LoggerExecutor loggerExecutor : sLoggerExecutors){
            if (loggerExecutor.isLoggable(tag, ERROR)){
                loggerExecutor.log(ERROR, tag, message, throwable);
            }
        }
    }

    public static class DebugLoggerExecutor implements LoggerExecutor {

        @Override
        public boolean isLoggable(@NonNull String tag, int priority) {
            return priority >= VERBOSE;
        }

        @Override
        public void log(int priority, @Nullable String tag, @NonNull String message, @Nullable Throwable t) {
            if (message.length() < MAX_LOG_LENGTH) {
                if (priority == Log.ASSERT) {
                    Log.wtf(tag, message);
                } else {
                    Log.println(priority, tag, message);
                }
                return;
            }

            // Split by line, then ensure each line can fit into Log's maximum length.
            for (int i = 0, length = message.length(); i < length; i++) {
                int newline = message.indexOf('\n', i);
                newline = newline != -1 ? newline : length;
                do {
                    int end = Math.min(newline, i + MAX_LOG_LENGTH);
                    String part = message.substring(i, end);
                    if (priority == Log.ASSERT) {
                        Log.wtf(tag, part);
                    } else {
                        Log.println(priority, tag, part);
                    }
                    i = end;
                } while (i < newline);
            }
        }
    }
}











