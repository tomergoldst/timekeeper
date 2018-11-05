package com.tomergoldst.timekeeper.tools;

import android.support.annotation.NonNull;

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
}
