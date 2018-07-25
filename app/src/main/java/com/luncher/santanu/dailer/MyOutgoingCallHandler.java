package com.luncher.santanu.dailer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by santanu on 13/11/17.
 */

public class MyOutgoingCallHandler extends BroadcastReceiver {

    public static boolean check_mgs = true;
    private final static String simSlotName[] = {
            "extra_asus_dial_use_dualsim",
            "com.android.phone.extra.slot",
            "slot",
            "simslot",
            "sim_slot",
            "subscription",
            "Subscription",
            "phone",
            "com.android.phone.DialingMode",
            "simSlot",
            "slot_id",
            "simId",
            "simnum",
            "phone_type",
            "slotId",
            "slotIdx"
    };


    @Override
    public void onReceive(final Context context, final Intent intent) {
        // Extract phone number reformatted by previous receivers
        String phoneNumber = getResultData();
        if (phoneNumber == null) {
            // No reformatted number, use the original
            phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            if(phoneNumber == null){
                phoneNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            }
        }
        Log.d("test","outgoing handeler");

        // My app will bring up the call, so cancel the broadcast
        setResultData(null);
        // Start my app to bring up the call
        final Intent intent1 = new Intent(context, MyOutgoingCustomDialog.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent1.putExtra("phone_no", phoneNumber);
        intent1.putExtra("sim", 1);
        //context.startActivity(intent1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                context.startActivity(intent1);
            }
        }, 1100);

        String msg = "New Phone Call Event. Outgoing Number pontu: "+phoneNumber;
        int duration = Toast.LENGTH_LONG;
        // Context pcontext;
        Toast toast;
        toast = Toast.makeText(context, msg, duration);
        toast.show();

    }
}
