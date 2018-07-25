package com.luncher.santanu.dailer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

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
            context.startService(serviceIntent);
        }

        int status = NetworkUtil.getConnectivityStatusString(context);
        if (!"android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if(status==NetworkUtil.NETWORK_STATUS_NOT_CONNECTED){
                //new ForceExitPause(context).execute();
                Intent serviceIntent = new Intent(context, MessageService.class);
                context.stopService(serviceIntent);
                Toast.makeText(context, "Service stoped "+status, Toast.LENGTH_SHORT).show();

            }else{
                //new ResumeForceExitPause(context).execute();
                Intent serviceIntent = new Intent(context, MessageService.class);
                context.startService(serviceIntent);
                Toast.makeText(context, "Service Started "+status, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
