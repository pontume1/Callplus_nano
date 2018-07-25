package com.luncher.santanu.dailer.model;

import android.graphics.Bitmap;

import java.util.List;

public class MyResolvFrequent {

    List<String> name;
    List<String> number;
    List<Integer> image;
    List<Integer> count;
    List<Long> duration;

    public MyResolvFrequent(List<String> name, List<String> number, List<Integer> image, List<Integer> count, List<Long> duration){
        this.name = name;
        this.number = number;
        this.image = image;
        this.count = count;
        this.duration = duration;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public List<String> getNumber() {
        return number;
    }

    public void setNumber(List<String> number) {
        this.number = number;
    }

    public List<Integer> getImage() {
        return image;
    }

    public void setImage(List<Integer> image) {
        this.image = image;
    }

    public List<Integer> getCounts() {
        return count;
    }

    public void setCounts(List<Integer> count) {
        this.count = count;
    }
    public List<Long> getDuration() {
        return duration;
    }

    public void setDuration(List<Long> duration) {
        this.duration = duration;
    }
}
