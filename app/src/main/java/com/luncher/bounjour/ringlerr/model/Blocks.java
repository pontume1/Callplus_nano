package com.luncher.bounjour.ringlerr.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by santanu on 13/3/18.
 */

@IgnoreExtraProperties
public class Blocks {

    public String from;
    public String block_no;
    public String name;
    public String datetime;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Blocks() {
    }

    public Blocks(String from, String block_no, String name, String datetime) {
        this.from = from;
        this.block_no = block_no;
        this.name = name;
        this.datetime = datetime;
    }

    public String getFrom() {
        return from;
    }
    public String getBlock_no() {
        return block_no;
    }
    public String getName() {
        return name;
    }
    public String getReminderTime() {
        return datetime;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("from", from);
        result.put("block_no", block_no);
        result.put("name", name);
        result.put("datetime", datetime);

        return result;
    }
}
