package com.luncher.santanu.dailer.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by santanu on 13/3/18.
 */

@IgnoreExtraProperties
public class Identity {

    public String uid;
    public String email;
    public String name;
    public String profile_pic;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Identity(String uid, String email, String name, String profile_pic) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.profile_pic = profile_pic;
    }

    public Identity(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("email", email);
        result.put("name", name);
        result.put("profile_pic", profile_pic);

        return result;
    }
}
