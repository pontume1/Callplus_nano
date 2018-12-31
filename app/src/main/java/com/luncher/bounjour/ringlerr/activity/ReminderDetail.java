package com.luncher.bounjour.ringlerr.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.adapter.ListAdapter;
import com.luncher.bounjour.ringlerr.model.ReminderDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ReminderDetail extends Activity {

    private Query mReminderDatabase;
    private DatabaseReference mRootRef;
    private String mPhoneNo;
    public TextView messageView;
    public TextView date_timeView;
    public ListView list;
    public ListAdapter adapter;

    SessionManager session;
    public ArrayList<ReminderDetails> remDetails = new ArrayList<ReminderDetails>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.reminder_detail);

        // Session class instance
        session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // phone
        mPhoneNo = user.get(SessionManager.KEY_PHONE);

        messageView = (TextView) findViewById(R.id.message_rem_list);
        date_timeView = (TextView) findViewById(R.id.date_time);
        list = (ListView) findViewById(R.id.list);

        String message = getIntent().getExtras().getString("message");
        String time = getIntent().getExtras().getString("formattedDate");
        String share_with = getIntent().getExtras().getString("shared_with");
        Long timestamp = getIntent().getLongExtra("timestamp", 0);

        adapter = new ListAdapter(this, timestamp);
        list.setAdapter(adapter);

        try {
            JSONObject sharelist = new JSONObject(share_with);
            JSONArray keys = sharelist.names ();

            for (int i = 0; i < keys.length (); ++i) {

                String ph_key = keys.getString(i); // Here's your key
                String co_key = sharelist.getString (ph_key); // Here's your value
                String ph_name;

                if(mPhoneNo.equals(ph_key)) {
                    ph_name = "You";
                }else{
                    ph_name = getContactName(this, ph_key);
                }

                ReminderDetails reminderDetails = new ReminderDetails();
                reminderDetails.name = ph_name;
                reminderDetails.code = co_key;

                remDetails.add(reminderDetails);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();

        messageView.setText(message);
        date_timeView.setText(time);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mReminderDatabase = FirebaseDatabase.getInstance().getReference().child("reminder").child(mPhoneNo).orderByChild("time");
        mReminderDatabase.keepSynced(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onAddEventClicked(String message, String share_text, Long timestamp) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");

        long startTime = timestamp;
        long endTime = timestamp + 60;

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

        intent.putExtra(CalendarContract.Events.TITLE, share_text);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, message);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "");
        intent.putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY");

        startActivity(intent);
    }



    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return phoneNumber;
        }
        String contactName = phoneNumber;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }
}
