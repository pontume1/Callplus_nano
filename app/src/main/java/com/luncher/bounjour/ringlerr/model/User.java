package com.luncher.bounjour.ringlerr.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by santanu on 13/3/18.
 */

@IgnoreExtraProperties
public class User {

    public String name;
    public String phone;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
}
