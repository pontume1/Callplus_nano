package com.luncher.santanu.dailer.model;

import android.graphics.Bitmap;

public class MyFrequent {

    String name;
    String number;
    Integer image;
    Integer count;
    Long  duration;

    public MyFrequent(String name, String number, Integer image, Integer count, Long duration){
        this.name = name;
        this.number = number;
        this.image = image;
        this.count = count;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public Integer getCounts() {
        return count;
    }

    public void setCounts(Integer count) {
        this.count = count;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }
}
