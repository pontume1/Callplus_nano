package com.luncher.santanu.dailer.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by santanu on 13/3/18.
 */

@IgnoreExtraProperties
public class Reminder {

    public String from;
    public String to;
    public String message;
    public Long time;
    public Integer remindAgo;
    public Boolean is_deleted;
    public String is_accepted;
    public Boolean is_seen;
    public String datetime;
    public Boolean taken;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Reminder() {
    }

    public Reminder(String from, String to, String message, Long time, Integer remindAgo, Boolean is_deleted, String is_accepted, Boolean is_seen, String datetime, Boolean taken) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.time = time;
        this.remindAgo = remindAgo;
        this.is_deleted = is_deleted;
        this.is_accepted = is_accepted;
        this.is_seen = is_seen;
        this.datetime = datetime;
        this.taken = taken;
    }

    public String getMessage() {
        return message;
    }
    public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }
    public String getIs_accepted() {
        return is_accepted;
    }
    public Long getTime() {
        return time;
    }
    public String getReminderTime() {
        return datetime;
    }
    public Boolean getTaken() {
        return taken;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("from", from);
        result.put("to", to);
        result.put("message", message);
        result.put("time", time);
        result.put("remindAgo", remindAgo);
        result.put("is_deleted", is_deleted);
        result.put("is_accepted", is_accepted);
        result.put("is_seen", is_seen);
        result.put("datetime", datetime);
        result.put("taken", taken);

        return result;
    }
}
