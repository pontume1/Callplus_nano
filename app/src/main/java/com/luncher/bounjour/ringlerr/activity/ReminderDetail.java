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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
    private Integer backpress;

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

        messageView = findViewById(R.id.message_rem_list);
        date_timeView = findViewById(R.id.date_time);
        list = findViewById(R.id.list);

        String message = getIntent().getExtras().getString("message");
        String time = getIntent().getExtras().getString("formattedDate");
        String share_with = getIntent().getExtras().getString("shared_with");
        Long timestamp = getIntent().getLongExtra("timestamp", 0);
        String reminderKey = getIntent().getExtras().getString("reminderkey");
        backpress = getIntent().getIntExtra("backpress", 0);

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

        if(!reminderKey.equals("null")) {
            mRootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference myValue = mRootRef.child("reminder").child(mPhoneNo).child(reminderKey).child("shared_with");
            myValue.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String shared_with_me = dataSnapshot.getValue(String.class);
                    remDetails.clear();

                    if (null != shared_with_me){
                        try {
                            JSONObject sharelist = new JSONObject(shared_with_me);
                            JSONArray keys = sharelist.names();

                            for (int i = 0; i < keys.length(); ++i) {

                                String ph_key = keys.getString(i); // Here's your key
                                String co_key = sharelist.getString(ph_key); // Here's your value
                                String ph_name;

                                if (mPhoneNo.equals(ph_key)) {
                                    ph_name = "You";
                                } else {
                                    ph_name = getContactName(ReminderDetail.this, ph_key);
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
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        if(backpress == 1) {
            Intent mainintent = new Intent(this, ReminderList.class);
            mainintent.putExtra("activity","goHome");
            startActivity(mainintent);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
