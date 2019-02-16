package com.luncher.bounjour.ringlerr.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by santanu on 13/3/18.
 */

@IgnoreExtraProperties
public class Coupons {

    public String name;
    public String store;
    public String description;
    public String address;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Coupons() {
    }

    public Coupons(String name, String store, String description, String address) {
        this.name = name;
        this.store = store;
        this.description = description;
        this.address = address;
    }

    public String getStore() {
        return store;
    }
    public String getDescription() {
        return description;
    }
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }

}
