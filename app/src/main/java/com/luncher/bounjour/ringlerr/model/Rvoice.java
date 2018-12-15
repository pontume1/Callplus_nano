package com.luncher.bounjour.ringlerr.model;

import java.util.Map;

public class Rvoice {

    public static final int TEXT_TYPE=0;
    public static final int LIST_TYPE=1;
    public static final int IMAGE_TYPE=2;
    public static final int TEXT_TYPE_USER=3;
    public static final int TEXT_TYPE_SUGG=4;

    public int type;
    public Map<String, String> data;
    public String text;

    public Rvoice(int type, String text, Map<String, String> data)
    {
        this.type=type;
        this.data=data;
        this.text=text;
    }
}
