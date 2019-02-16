package com.luncher.bounjour.ringlerr.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by santanu on 13/3/18.
 */

@IgnoreExtraProperties
public class Message {

    public int id;
    public String from;
    public String to;
    public String message;
    public String image;
    public String type;
    public String seen;
    public String datetime;
    public String talk_time;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Message() {
    }

    public Message(int id, String from, String to, String message, String image, String type, String seen, String datetime, String talk_time) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.message = message;
        this.image = image;
        this.type = type;
        this.seen = seen;
        this.datetime = datetime;
        this.talk_time = talk_time;
    }

    public int getId() {
        return id;
    }
    public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }
    public String getMessage() {
        return message;
    }
    public String getImage() {
        return image;
    }
    public String getType() {
        return type;
    }
    public String getSeen() {
        return seen;
    }
    public String getDateAndTime() {
        return datetime;
    }
    public String getTalk_time() {
        return talk_time;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public void setTo(String to) { this.to = to; }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setSeen(String seen) {
        this.seen = seen;
    }
    public void setDateAndTime(String datetime) {
        this.datetime = datetime;
    }
    public void setTalk_time(String talk_time) {
        this.talk_time =  talk_time;
    }

    //7042083338
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("from", from);
        result.put("to", to);
        result.put("message", message);
        result.put("image", image);
        result.put("type", type);
        result.put("seen", seen);
        result.put("datetime", datetime);
        result.put("talk_time", talk_time);

        return result;
    }
}
