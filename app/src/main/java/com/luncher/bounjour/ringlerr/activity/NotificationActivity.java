package com.luncher.bounjour.ringlerr.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.adapter.NotificationAdapter;
import com.luncher.bounjour.ringlerr.model.Notification;

import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationActivity extends AppCompatActivity {

    private List<Notification> notificationList = new ArrayList<>();
    private NotificationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_layout);

        //initialise recyclerview
        RecyclerView recyclerView = findViewById(R.id.notification_list);
        TextView search_text = findViewById(R.id.search_text);
        //initialise adapter class
        mAdapter = new NotificationAdapter(this, notificationList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //set adapter to recyclerview
        recyclerView.setAdapter(mAdapter);
        //call method to fetch data from db and add to recyclerview
        prepareData();

        search_text.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {
                updateSearchData(query.toString());
            }
        });
    }

    private void prepareData() {
        MyDbHelper myDbHelper = new MyDbHelper(NotificationActivity.this, null, 3);
        List<Notification> sec = myDbHelper.getAllNotification();
        notificationList.clear();

        if (sec.size() > 0) {
            //loop through contents
            for (int i = 0; i < sec.size(); i++) {
                //add data to list used in adapter
                notificationList.add(sec.get(i));
                //notify data change
                mAdapter.notifyDataSetChanged();
            }
        }else{

        }
    }

    private void updateSearchData(String search_text) {
        MyDbHelper myDbHelper = new MyDbHelper(NotificationActivity.this, null, 3);
        List<Notification> sec = myDbHelper.getSearchNotification(search_text);
        notificationList.clear();

        if (sec.size() > 0) {
            //loop through contents
            for (int i = 0; i < sec.size(); i++) {
                //add data to list used in adapter
                notificationList.add(sec.get(i));
                //notify data change
                mAdapter.notifyDataSetChanged();
            }
        }else{

        }
    }
}
