package com.luncher.bounjour.ringlerr.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.luncher.bounjour.ringlerr.services.MyFirebaseMessagingService;

public class MyRecent implements Parcelable {

    String name;
    String number;
    String type;
    Long time;
    Integer _id;

    public MyRecent(){

    }
    public MyRecent(String name, String number, String type, Long time, Integer _id){
        this.name = name;
        this.number = number;
        this.type = type;
        this.time = time;
        this._id = _id;
    }

    protected MyRecent(Parcel in) {
        name = in.readString();
        number = in.readString();
        type = in.readString();
        if (in.readByte() == 0) {
            time = null;
        } else {
            time = in.readLong();
        }
        if (in.readByte() == 0) {
            _id = null;
        } else {
            _id = in.readInt();
        }
    }

    public static final Creator<MyRecent> CREATOR = new Creator<MyRecent>() {
        @Override
        public MyRecent createFromParcel(Parcel in) {
            return new MyRecent(in);
        }

        @Override
        public MyRecent[] newArray(int size) {
            return new MyRecent[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public Long getTime() {
        return time;
    }

    public Integer get_id() {
        return _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(number);
        parcel.writeString(type);
        if (time == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(time);
        }
        if (_id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(_id);
        }
    }
}
