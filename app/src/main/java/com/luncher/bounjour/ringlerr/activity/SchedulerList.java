package com.luncher.bounjour.ringlerr.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.adapter.SchedulerAdapter;
import com.luncher.bounjour.ringlerr.model.Scheduler;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SchedulerList extends Activity {

    private List<Scheduler> scheduleList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SchedulerAdapter mAdapter;
    private EditText contact_search;
    private ImageView add_scheduler;
    private TextView no_data;

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.scheduler_list);

        contact_search = findViewById(R.id.contact_search);
        add_scheduler = findViewById(R.id.add_scheduler);
        no_data = findViewById(R.id.no_data);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        contact_search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                //search_text = query.toString().toLowerCase();
            }
        });

        add_scheduler.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SchedulerList.this, SchedulerDialog.class);
                startActivity(intent);
            }
        });


        //initialise recyclerview
        recyclerView = findViewById(R.id.scheduler_recycle_view);
        //initialise adapter class
        mAdapter = new SchedulerAdapter(this, scheduleList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //set adapter to recyclerview
        recyclerView.setAdapter(mAdapter);
        //call method to fetch data from db and add to recyclerview
        prepareData();

    }

    private void prepareData() {
        MyDbHelper myDbHelper = new MyDbHelper(SchedulerList.this, null, 1);
        List<Scheduler> sec = myDbHelper.getAllScheduler();
        scheduleList.clear();

        if (sec.size() > 0) {
            //loop through contents
            for (int i = 0; i < sec.size(); i++) {
                //add data to list used in adapter
                scheduleList.add(sec.get(i));
                //notify data change
                mAdapter.notifyDataSetChanged();
            }
        }else{
            no_data.setVisibility(View.VISIBLE);
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

    @Override
    protected void onResume(){
        super.onResume();
        prepareData();
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
