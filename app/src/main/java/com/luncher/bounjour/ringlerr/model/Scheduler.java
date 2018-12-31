package com.luncher.bounjour.ringlerr.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by santanu on 13/3/18.
 */

@IgnoreExtraProperties
public class Scheduler {

    public String name;
    public String phone;
    public String message;
    public Long time;
    public String is_done;
    public String is_manual;
    public String spinner_type;
    public String ago;
    public Integer sid;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Scheduler() {
    }

    public Scheduler(Integer sid, String name, String phone, String message, Long time, String is_done, String is_manual, String spinner_type, String ago) {
        this.name = name;
        this.phone = phone;
        this.message = message;
        this.time = time;
        this.is_done = is_done;
        this.is_manual = is_manual;
        this.spinner_type = spinner_type;
        this.sid = sid;
        this.ago = ago;
    }

    public String getPhone() {
        return phone;
    }
    public String getMessage() {
        return message;
    }
    public String getName() {
        return name;
    }
    public Long getSTime() {
        return time;
    }
    public String getIsDone() {
        return is_done;
    }
    public String getIsManual() {
        return is_manual;
    }
    public Integer getSid() {
        return sid;
    }
    public String getSpinnerType(){ return spinner_type; }
    public String getAgo(){ return ago; }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setSTime(Long time) {
        this.time = time;
    }
    public void setIsDone(String is_done) {
        this.is_done = is_done;
    }
    public void setIsManual(String is_manual) {
        this.is_manual = is_manual;
    }
    public void setSid(Integer sid) {
        this.sid = sid;
    }
    public void setSpinnerType(String spinner_type) {
        this.spinner_type = spinner_type;
    }
    public void setAgo(String ago) {
        this.ago = ago;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("phone", phone);
        result.put("message", message);
        result.put("time", time);
        result.put("is_done", is_done);
        result.put("is_manual", is_manual);

        return result;
    }
}
