package com.luncher.santanu.dailer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class MyContact  implements Parcelable {

    String name;
    String number;
    String type;
    Long time;
    Integer _id;
    Integer contact_id;
    String ago;

    public MyContact(String name, String number, String type, Long time, Integer _id, String ago, Integer contact_id){
        this.name = name;
        this.number = number;
        this.type = type;
        this.time = time;
        this._id = _id;
        this.ago = ago;
        this.contact_id = contact_id;
    }

    protected MyContact(Parcel in) {
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
        if (in.readByte() == 0) {
            contact_id = null;
        } else {
            contact_id = in.readInt();
        }
        ago = in.readString();
    }

    public static final Creator<MyContact> CREATOR = new Creator<MyContact>() {
        @Override
        public MyContact createFromParcel(Parcel in) {
            return new MyContact(in);
        }

        @Override
        public MyContact[] newArray(int size) {
            return new MyContact[size];
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

    public String getAgo() {
        return ago;
    }

    public Integer getContact_id() {
        return contact_id;
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
        if (contact_id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(contact_id);
        }
        parcel.writeString(ago);
    }
}
