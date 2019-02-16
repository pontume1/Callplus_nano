package com.luncher.bounjour.ringlerr;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luncher.bounjour.ringlerr.activity.FullScreenHolder;
import com.luncher.bounjour.ringlerr.activity.FullScreenVideoHolder;
import com.luncher.bounjour.ringlerr.model.Message;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * Created by santanu on 25/12/17.
 */

public class Utility {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static  boolean sendMessage(Context mContext, String mPhoneNo, String phone_no, String image, String message, String talkTime, String type, String videoPath){

        if(!image.equals("none") || !message.isEmpty()){

            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();

            if(talkTime.equals("Talk Time")){
                talkTime = "";
            }

            long id;
            MyDbHelper myDbHelper = new MyDbHelper(mContext, null, 4);
            if(type.equals("vid") || type.equals("jpg")){
                id = myDbHelper.addMessages(message, mPhoneNo, videoPath, type, talkTime, tsLong, phone_no);
            }else{
                id = myDbHelper.addMessages(message, mPhoneNo, image, type, talkTime, tsLong, phone_no);
            }

            DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference mTo = mRootRef.child("message").child(phone_no);
            Message messages_to = new Message((int)id, mPhoneNo, phone_no, message, image, type, "false", ts, talkTime);
            mTo.setValue(messages_to);

            String key = mRootRef.child("chats").child(mPhoneNo).child(phone_no).push().getKey();
            Message chat = new Message((int)id, mPhoneNo, phone_no, message, image, type, "false", ts, talkTime);
            Map<String, Object> postValues = chat.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/chats/" + mPhoneNo + "/" + phone_no + "/" + key, postValues);

            if(!type.equals("snap")) {
                childUpdates.put("/chats/" + phone_no + "/" + mPhoneNo + "/" + key, postValues);
            }

            mRootRef.updateChildren(childUpdates);
        }

        return true;
    }
}
