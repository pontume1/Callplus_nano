package com.luncher.bounjour.ringlerr.model;

public class Library {
    private String name;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Library() {
    }

    public Library(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
