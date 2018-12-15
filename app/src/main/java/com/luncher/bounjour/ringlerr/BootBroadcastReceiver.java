package com.luncher.bounjour.ringlerr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

/**
 * Created by santanu on 19/11/17.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        // BOOT_COMPLETED” start Service
        if (intent.getAction().equals(ACTION)) {
            //Service
            Intent serviceIntent = new Intent(context, MessageService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }

        int status = NetworkUtil.getConnectivityStatusString(context);
        if (!"android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if(status==NetworkUtil.NETWORK_STATUS_NOT_CONNECTED){
                //new ForceExitPause(context).execute();
                Intent serviceIntent = new Intent(context, MessageService.class);
                context.stopService(serviceIntent);
                Toast.makeText(context, "You are not connected to the internet", Toast.LENGTH_SHORT).show();

            }else{
                //new ResumeForceExitPause(context).execute();
                Intent serviceIntent = new Intent(context, MessageService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //mActivity.startForegroundService(cbIntent);
                    ContextCompat.startForegroundService(context, serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }
                //Toast.makeText(context, "Service Started "+status, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
