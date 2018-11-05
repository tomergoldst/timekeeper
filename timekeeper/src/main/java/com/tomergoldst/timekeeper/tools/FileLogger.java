package com.tomergoldst.timekeeper.tools;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tomergoldst.timekeeper.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileLogger extends Logger.DebugLoggerExecutor {

    private static final String TAG = FileLogger.class.getSimpleName();

    private static final String DIRECTORY = "AndroidAppLogs";
    private static final String FILE_NAME_TIMESTAMP = "dd-MM-yyyy";
    private static final String LOG_TIMESTAMP = "E MMM dd yyyy 'at' HH:mm:ss:SSS";

    private final String mPackageName;
    private final String mExternalDirectory;

    public FileLogger(Context context) {
        mPackageName = context.getApplicationContext().getPackageName();
        mExternalDirectory = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
    }

    @Override
    public void log(int priority,
                       @Nullable String tag,
                       @NonNull String message,
                       @Nullable Throwable t) {

        try {

            File direct = new File(getWriteDir());

            if (!direct.exists()) {
                direct.mkdirs();
            }

            String fileNameTimeStamp = new SimpleDateFormat(FILE_NAME_TIMESTAMP, Locale.getDefault()).format(new Date());
            String logTimeStamp = new SimpleDateFormat(LOG_TIMESTAMP, Locale.getDefault()).format(new Date());

            String fileName = fileNameTimeStamp + ".html";

            File file = new File(getWriteDir() + File.separator + fileName);

            file.createNewFile();

            if (file.exists()) {

                OutputStream fileOutputStream = new FileOutputStream(file, true);

                fileOutputStream.write(("<p style=\"background:lightgray;\">" +
                        "<strong style=\"background:lightblue;\">&nbsp&nbsp" + logTimeStamp + " :&nbsp&nbsp</strong>" +
                        "&nbsp&nbsp" + message + "&nbsp&nbsp" +
                        "<strong style=\"background:yellow;\">&nbsp&nbsp" + tag + " &nbsp</strong>" +
                        "<strong style=\"background:orange;\">&nbsp&nbsp" + getPriorityReadableText(priority) + " &nbsp</strong>" +
                        "</p>").getBytes());
                fileOutputStream.close();

            }

        } catch (Exception e) {
            Log.e(TAG, "Error while logging into file : " + e);
        }

    }

    private String getWriteDir(){
        return String.format("%s/%s/%s/%s", mExternalDirectory, DIRECTORY, mPackageName, BuildConfig.VERSION_NAME);
    }

    private String getPriorityReadableText(int priority){
        switch (priority){
            case 2: return "VERBOSE";
            case 3: return "DEBUG";
            case 4: return "INFO";
            case 5: return "WARN";
            case 6: return "ERROR";
            case 7: return "ASSERT";
            default: return String.valueOf(priority);
        }

    }


}