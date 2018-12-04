package com.tomergoldst.timekeeper.tools;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tomergoldst.timekeeper.model.Alarm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AlarmTools {

    public static List<Alarm> removeDuplicated(@NonNull List<Alarm> alarms){
        List<Alarm> unDuplicatedAlarms = new ArrayList<>();
        HashMap<String, Alarm> alarmsHashMap = new HashMap<>();

        for (Alarm alarm : alarms) {
            if (!alarmsHashMap.containsKey(alarm.getUid())) {
                alarmsHashMap.put(alarm.getUid(), alarm);
                unDuplicatedAlarms.add(alarm);
            }
        }

        return unDuplicatedAlarms;
    }

    @Nullable
    public static List<Alarm> extractNonPersistedAlarms(@NonNull List<Alarm> alarms){
        List<Alarm> nonPersistedAlarms = new ArrayList<>();

        for (Alarm alarm : alarms) {
            if (!alarm.isPersist()) {
                nonPersistedAlarms.add(alarm);
            }
        }

        return nonPersistedAlarms;
    }

    @Nullable
    public static List<Alarm> extractPersistedAlarms(@NonNull List<Alarm> alarms){
        List<Alarm> persistedAlarms = new ArrayList<>();

        for (Alarm alarm : alarms) {
            if (alarm.isPersist()) {
                persistedAlarms.add(alarm);
            }
        }

        return persistedAlarms;
    }
}
