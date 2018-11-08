package com.tomergoldst.timekeeper.tools;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.util.LongSparseArray;

import com.tomergoldst.timekeeper.core.Config;
import com.tomergoldst.timekeeper.core.WakeOnAlarmReceiver;
import com.tomergoldst.timekeeper.model.TimePoint;

import java.util.ArrayList;
import java.util.List;

public class TimePointsTools {

    public static List<TimePoint> removeDuplicated(@NonNull List<TimePoint> timePoints){
        LongSparseArray<TimePoint> timePointsSparseArray = new LongSparseArray<>();

        for (TimePoint timePoint : timePoints) {
            timePointsSparseArray.put(timePoint.getTime(), timePoint);
        }

        return asList(timePointsSparseArray);
    }

    private static <C> List<C> asList(LongSparseArray<C> sparseArray) {
        if (sparseArray == null) {
            return null;
        }

        List<C> arrayList = new ArrayList<C>(sparseArray.size());
        for (int i = 0, N = sparseArray.size(); i < N ; i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

    public static PendingIntent getPendingIntent(Context context, TimePoint timePoint){
        Intent intent = new Intent(context, WakeOnAlarmReceiver.class);
        intent.setAction(timePoint.getActionIdentifierSignature());
        intent.putExtra(TimePoint.PARAM_TIME_POINT_SIGNATURE, timePoint.getTime());
        return PendingIntent.getBroadcast(context, Config.REQUEST_CODE_WAKE_ON_ALARM, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getNoCreatePendingIntent(Context context, TimePoint timePoint){
        Intent intent = new Intent(context, WakeOnAlarmReceiver.class);
        intent.setAction(timePoint.getActionIdentifierSignature());
        intent.putExtra(TimePoint.PARAM_TIME_POINT_SIGNATURE, timePoint.getTime());
        return PendingIntent.getBroadcast(context, Config.REQUEST_CODE_WAKE_ON_ALARM, intent, PendingIntent.FLAG_NO_CREATE);
    }

}
