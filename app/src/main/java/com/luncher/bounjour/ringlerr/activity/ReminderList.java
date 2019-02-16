package com.luncher.bounjour.ringlerr.activity;

import android.app.Activity;
//import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luncher.bounjour.ringlerr.MainActivity;
import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.adapter.ReminderAdapter;
import com.luncher.bounjour.ringlerr.model.Reminder;
import com.luncher.bounjour.ringlerr.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
//import java.util.List;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.Manifest.permission.READ_CONTACTS;

public class ReminderList extends Activity {

    private RecyclerView reminder_list;
    private List<Reminder> reminderList = new ArrayList<>();
    private List<Reminder> searchlist = new ArrayList<>();
    private DatabaseReference mRootRef;
    private String mPhoneNo;
    private String filterHeader = "Today";
    private String search_text = "";
    private TextView no_data;
    private ReminderAdapter mAdapter;

    // Session Manager Class
    SessionManager session;
    private static final int REQUEST_READ_CONTACTS = 444;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.reminder_list);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        EditText contact_search = findViewById(R.id.contact_search);
        no_data = findViewById(R.id.no_data);
        Button pending_button = findViewById(R.id.pending_button);

        no_data.setVisibility(View.GONE);
        contact_search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                search_text = query.toString().toLowerCase();
                reminderList.clear();
                if (searchlist.size() > 0) {
                    //loop through contents
                    for (int i = 0; i < searchlist.size(); i++) {
                        if (searchlist.get(i).getFrom().toLowerCase().contains(search_text) || searchlist.get(i).getMessage().toLowerCase().contains(search_text)) {
                            reminderList.add(searchlist.get(i));
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        //get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.filter);
        //create a list of items for the spinner.
        String[] items = new String[]{"Today", "Upcoming", "Past"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_selected, items);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                filterHeader = (String) parent.getItemAtPosition(position);
                fetchReminder(filterHeader);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        pending_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View itemView) {
                final Intent intent1 = new Intent(ReminderList.this, PendingReminderList.class);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent1);
                    }
                }, 100);
            }
        });

        mRootRef = FirebaseDatabase.getInstance().getReference();
        // Session class instance
        session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // phone
        mPhoneNo = user.get(SessionManager.KEY_PHONE);
        mAdapter = new ReminderAdapter(this, reminderList);
        reminder_list = findViewById(R.id.reminder_list);
        reminder_list.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        layoutManager.setStackFromEnd(false);
        layoutManager.setReverseLayout(false);
        reminder_list.setLayoutManager(layoutManager);
        reminder_list.setAdapter(mAdapter);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mayRequestContacts()) {
            fetchReminder(filterHeader);
            //new LongOperation(this).execute("");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if(null != getIntent().getStringExtra("activity")){
            Intent mainintent = new Intent(this, MainActivity.class);
            startActivity(mainintent);
        }else {
            super.onBackPressed();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //new LongOperation(this).execute("");
                fetchReminder("Today");
            }
        }
    }

    public void fetchReminder(String filterHeader){

        MyDbHelper myDbHelper = new MyDbHelper(ReminderList.this, null, 9);
        List<Reminder> sec = myDbHelper.getAllReminders();
        reminderList.clear();
        searchlist.clear();

        if (sec.size() > 0) {
            //loop through contents
            for (int i = 0; i < sec.size(); i++) {

                Long timestamp = Long.valueOf(sec.get(i).getTime());
                Calendar now = Calendar.getInstance();
                Calendar postTime = Calendar.getInstance();
                postTime.setTimeInMillis(timestamp);

                if(filterHeader.equals("Today")) {
                    if (now.get(Calendar.DATE) == postTime.get(Calendar.DATE) && now.get(Calendar.MONTH) == postTime.get(Calendar.MONTH)) {
                        //add data to list used in adapter
                        if (null != sec.get(i).getFrom()) {
                            reminderList.add(sec.get(i));
                        }
                    }
                }
                if(filterHeader.equals("Past")) {
                    if (now.get(Calendar.DATE) == postTime.get(Calendar.DATE) && now.get(Calendar.MONTH) == postTime.get(Calendar.MONTH)) {

                    }else if (System.currentTimeMillis() - timestamp > 0) {
                        if (null != sec.get(i).getFrom()) {
                            reminderList.add(sec.get(i));
                        }
                    }
                }
                if(filterHeader.equals("Upcoming")) {
                    if (now.get(Calendar.DATE) == postTime.get(Calendar.DATE) && now.get(Calendar.MONTH) == postTime.get(Calendar.MONTH)) {

                    }else if (System.currentTimeMillis() - timestamp > 0) {

                    }else{
                        if (null != sec.get(i).getFrom()) {
                            reminderList.add(sec.get(i));
                        }
                    }
                }
            }
            //notify data change
            mAdapter.notifyDataSetChanged();
            searchlist = new ArrayList<>(reminderList);
        }else{
            no_data.setVisibility(View.VISIBLE);
        }

    //  boolean alarmUp = (PendingIntent.getActivity(ReminderList.this, 0,
    //  new Intent("com.luncher.bounjour.ringlerr.activity"),
    //  PendingIntent.FLAG_NO_CREATE) != null);
    //
    //  if (alarmUp)
    //  {
    //      Toast.makeText(ReminderList.this, "active", Toast.LENGTH_SHORT).show();
    //  }else{
    //      Toast.makeText(ReminderList.this, "inactive", Toast.LENGTH_SHORT).show();
    //  }

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

        if(!cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        /** progress dialog to show user that the backup is processing. */
        private ProgressDialog mProgressDialog;
        /** application context. */
        private ReminderList activity;

        public LongOperation(ReminderList activity) {
            this.activity = activity;
            mProgressDialog = new ProgressDialog(activity);
        }

        @Override
        protected String doInBackground(String... params) {
            fetchReminder(filterHeader);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                }
            }, 3000);
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setTitle("Fetching Reminders...");
            mProgressDialog.setMessage("");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
