package com.luncher.bounjour.ringlerr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.luncher.bounjour.ringlerr.model.Reminder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by santanu on 19/11/17.
 */

public class OnUpgradeReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.PACKAGE_REPLACED";
    @Override
    public void onReceive(Context context, Intent intent) {
        // BOOT_COMPLETED” start Service
        if (intent.getAction().equals(ACTION)) {
            SessionManager session = new SessionManager(context);
            // get user data from session
            HashMap<String, String> user = session.getUserDetails();
            // phone
            final String myPhone = user.get(SessionManager.KEY_PHONE);

            DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
            assert myPhone != null;
            Query messageRef = mRootRef.child("reminder").child(myPhone);
            final MyDbHelper myDbHelper = new MyDbHelper(context, null, 9);
            messageRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Reminder reminder = child.getValue(Reminder.class);
                            String reminderKey = child.getKey();
                            Long crLong = System.currentTimeMillis();
                            Long time_and_date = Long.valueOf(reminder.time);
                            Date date = new Date(time_and_date);
                            SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss", Locale.US);
                            String formattedDate = formatter.format(date);
                            int response;
                            switch (reminder.is_accepted) {
                                case "true":
                                    response = 1;
                                    break;
                                case "false":
                                    response = 2;
                                    break;
                                default:
                                    response = 0;
                                    break;
                            }

                            //myDbHelper.addReminder(reminder.message, time_and_date, reminder.shared_with, reminderKey, reminder.remindAgo, response, reminder.from);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
