package com.tomergoldst.timekeeper.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Date;

public final class Alarm implements Parcelable {

    /** Tag used on log messages.*/
    private static final String TAG = Alarm.class.getSimpleName();

    private long id;

    private String uid;

    private long time;

    private boolean persist;

    private String payload;

    public Alarm(@NonNull String uid, long time){
        if (TextUtils.isEmpty(uid)){
            throw new RuntimeException("Alarm uid must not be null");
        }

        this.uid = uid;
        this.time = time;
        this.persist = false;
    }

    /**
     * Constructor for reading from database
     * @param id id
     * @param uid uid
     * @param time time
     * @param persist persist
     * @param payload payload
     */
    Alarm(long id, String uid, long time, boolean persist, String payload){
        this.id = id;
        this.uid = uid;
        this.time = time;
        this.persist = persist;
        this.payload = payload;
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getUid(){
        return uid;
    }

    public String getReadableDate(){
        return new Date(time).toString();
    }

    public long getTime(){
        return time;
    }

    public void setTime(long time){
        this.time = time;
    }

    public boolean isPersist() {
        return persist;
    }

    public void setPersist(boolean persist) {
        this.persist = persist;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", time=" + time +
                ", persist=" + persist +
                ", payload='" + payload + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(uid);
        dest.writeLong(time);
        dest.writeInt(persist ? 1 : 0);
        dest.writeString(payload);
    }

    public static final Parcelable.Creator<Alarm> CREATOR
            = new Parcelable.Creator<Alarm>() {
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    private Alarm(Parcel in) {
        id = in.readLong();
        uid = in.readString();
        time = in.readLong();
        persist = in.readInt() == 1;
        payload = in.readString();
    }

}
