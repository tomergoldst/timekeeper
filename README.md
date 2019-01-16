# TimeKeeper
Alarms scheduling for Android.

Set and receive exact alarms with ease, TimeKeeper handle all the work for you.

Support `Android 8.0` (API level 26) back to `Android 4.0` (API level 14)

In order to support exact alarms on `Android 9.0` your app has to be in the foreground at
any time, this can be achieved by using a foreground service running at all time.
Otherwise your app will be subject to [App Standby Buckets](https://developer.android.com/about/versions/pie/power#buckets)
and alarms may be deferred by few minutes or more according to [Power management restrictions](https://developer.android.com/topic/performance/power/power-details)

## Instructions

Add dependencies to your app build.gradle (This library uses Firebase Job Dispatcher)
```groovy
dependencies {
    // TimeKeeper
    implementation 'com.tomergoldst.android:timekeeper:1.0.2'

    // Firebase Job Dispatcher
    implementation 'com.firebase:firebase-jobdispatcher:0.8.5'
}
```

Add the following permissions to your app manifest if you don't already have it
```xml
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

Create a BroadcastReceiver (Alarms will be delivered to this receiver)
```java
public class MyAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /*
          Extract alarms from intent
         */
        List<Alarm> alarms = intent.getParcelableArrayListExtra("alarms");

    }
}
```

Add receiver to your manifest. It is important to define the intent-filter with the appropriate action name
```xml
<receiver
    android:name=".MyAlarmReceiver"
    android:exported="false">
    <intent-filter>
        <action android:name="com.tomergoldst.timekeeper.intent_action.RECEIVE_ALARMS" />
    </intent-filter>
</receiver>
```

Init TimeKeeper at your application class onCreate()
```java
TimeKeeper.initialize(this);
```

Now we can start using the sdk and create an alarm:
Create an Alarm object and pass in a UID and a time to be triggered in millis and then call setAlarm
```java
Alarm alarm = new Alarm("my-alarm-id", 1541433600000);
TimeKeeper.setAlarm(alarm);
```

To cancel an alarm:
```java
TimeKeeper.cancelAlarm("my-alarm-id");
```

To see list of all existing alarms:
```java
List<Alarm> alarms = TimeKeeper.getAlarms();
```

To remove all alarms:
```java
TimeKeeper.clear();
```

To get next alarm time:
```java
long time = TimeKeeper.getNextAlarmTime();
```

### License
```
Copyright 2018 Tomer Goldstein

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```


