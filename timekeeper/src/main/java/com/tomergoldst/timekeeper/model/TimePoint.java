package com.tomergoldst.timekeeper.model;

import android.app.PendingIntent;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.tomergoldst.timekeeper.core.Config;
import com.tomergoldst.timekeeper.core.WakeOnAlarmReceiver;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "timepoint")
public final class TimePoint implements Parcelable {

    /** Tag used on log messages.*/
    private static final String TAG = TimePoint.class.getSimpleName();

    public static final String PARAM_TIME_POINT_SIGNATURE = "param_time_point_signature";

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "time")
    private long time;

    @Ignore
    public TimePoint(long time){
        this.time = time;
    }

    /**
     * Constructor for reading from database
     * @param id id
     * @param time time
     */
    public TimePoint(long id, long time){
        this.id = id;
        this.time = time;
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getTime(){
        return time;
    }

    public void setTime(long time){
        this.time = time;
    }

    public String getReadableDate(){
        return new Date(time).toString();
    }

    public PendingIntent getPendingIntent(Context context){
        Intent intent = new Intent(context, WakeOnAlarmReceiver.class);
        intent.setAction(getActionIdentifierSignature());
        intent.putExtra(PARAM_TIME_POINT_SIGNATURE, time);
        return PendingIntent.getBroadcast(context, Config.REQUEST_CODE_WAKE_ON_ALARM, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public PendingIntent getNoCreatePendingIntent(Context context){
        Intent intent = new Intent(context, WakeOnAlarmReceiver.class);
        intent.setAction(getActionIdentifierSignature());
        intent.putExtra(PARAM_TIME_POINT_SIGNATURE, time);
        return PendingIntent.getBroadcast(context, Config.REQUEST_CODE_WAKE_ON_ALARM, intent, PendingIntent.FLAG_NO_CREATE);
    }

    private Calendar getCalendar(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time));
        return cal;
    }

    private String getActionIdentifierSignature(){
        Calendar cal = getCalendar();
        return "timepoint-"
                + cal.get(Calendar.YEAR)
                + "-" + formatToTwoDigits(cal.get(Calendar.MONTH) + 1)
                + "-" + formatToTwoDigits(cal.get(Calendar.DAY_OF_MONTH))
                + "-" + formatToTwoDigits(cal.get(Calendar.HOUR_OF_DAY))
                + "-" + formatToTwoDigits(cal.get(Calendar.MINUTE))
                + "-" + formatToTwoDigits(cal.get(Calendar.SECOND));
    }

    private String formatToTwoDigits(int num){
        return String.format(Locale.US, "%02d", num);
    }

    @Override
    public String toString() {
        return "TimePoint{" +
                "id=" + id +
                ", time=" + time +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(time);
    }

    public static final Parcelable.Creator<TimePoint> CREATOR
            = new Parcelable.Creator<TimePoint>() {
        public TimePoint createFromParcel(Parcel in) {
            return new TimePoint(in);
        }

        public TimePoint[] newArray(int size) {
            return new TimePoint[size];
        }
    };

    private TimePoint(Parcel in) {
        id = in.readLong();
        time = in.readLong();
    }
}
