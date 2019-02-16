package com.luncher.bounjour.ringlerr.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by santanu on 13/3/18.
 */

@IgnoreExtraProperties
public class Notification {

    public String from;
    public String description;
    public String remkey;
    public Long time;
    public Integer type;
    public Integer seen;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Notification() {
    }

    public Notification(String from, String description, Long time, Integer seen, Integer type, String remkey) {
        this.from = from;
        this.description = description;
        this.seen = seen;
        this.type = type;
        this.time = time;
        this.remkey = remkey;
    }

    public String getFrom() {
        return from;
    }
    public String getDescription() {
        return description;
    }
    public Integer getSeen() {
        return seen;
    }
    public Integer getType() {
        return type;
    }
    public String getRemkey() {
        return remkey;
    }
    public Long getSTime() {
        return time;
    }

    public void setFrom(String from) {
        this.from = from;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setSTime(Long time) {
        this.time = time;
    }
    public void setType(Integer type) {
        this.type = type;
    }
    public void setSeen(Integer seen) {
        this.seen = seen;
    }
    public void setKey(String remkey) {
        this.remkey = remkey;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", from);
        result.put("description", description);
        result.put("type", type);
        result.put("seen", seen);
        result.put("time", time);

        return result;
    }
}
