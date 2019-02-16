package com.luncher.bounjour.ringlerr.activity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import androidx.appcompat.app.AppCompatActivity;

public class CouponWeb extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webview = new WebView(this);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        setContentView(webview);
        String url = "http://www.hoopoun.com/admin/api_v2/mobileRedemption";
        String latitude = "28.6315";
        String longitude = "77.2167";
        String offer_id = "106";
        String name = "Santanu";
        String mobileNumber = "7827829419";
        String postData = null;
        try {
            postData = "latitude=" + URLEncoder.encode(latitude, "UTF-8") + "&longitude=" + URLEncoder.encode(longitude, "UTF-8")
                    + "&offer_id=" + URLEncoder.encode(offer_id, "UTF-8") + "&name=" + URLEncoder.encode(name, "UTF-8")
                    + "&mobileNumber=" + URLEncoder.encode(mobileNumber, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        webview.postUrl(url,postData.getBytes());
    }
}
