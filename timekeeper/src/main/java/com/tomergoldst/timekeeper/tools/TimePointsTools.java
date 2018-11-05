package com.tomergoldst.timekeeper.tools;

import android.support.annotation.NonNull;
import android.support.v4.util.LongSparseArray;

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

}
