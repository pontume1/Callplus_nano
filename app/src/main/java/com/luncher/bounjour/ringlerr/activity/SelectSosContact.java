package com.luncher.bounjour.ringlerr.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Query;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.RecyclerViewClickListener;
import com.luncher.bounjour.ringlerr.adapter.ContactSelectReminderAdapter;
import com.luncher.bounjour.ringlerr.adapter.ContactSelectSosAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SelectSosContact extends Activity implements RecyclerViewClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public EditText search;
    List<Contact> contacts;
    List<String> sel_contacts = new ArrayList<>();
    String message;
    Long time;
    Integer timeAgo;

    public SelectSosContact() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sos_contact);

        recyclerView = findViewById(R.id.my_recycler_view);
        search = findViewById( R.id.search);
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();

        final RecyclerViewClickListener itemListener = SelectSosContact.this;
        Contacts.initialize(SelectSosContact.this);
        Query q = Contacts.getQuery();
        q.hasPhoneNumber();
        contacts = q.find();

        List<String> input = new ArrayList<>();
        List<String> phoneNo = new ArrayList<>();
        List<Boolean> selected = new ArrayList<>();
        for (int i = 0; i < contacts.size(); i++) {
            if(contacts.get(i).getPhoneNumbers().size()>0) {
                input.add(contacts.get(i).getDisplayName());
                selected.add(false);
                //photo.add(contacts.get(i).getPhotoUri().toString());
                if (contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber() == null) {
                    phoneNo.add(contacts.get(i).getPhoneNumbers().get(0).getNumber());
                } else {
                    phoneNo.add(contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber());
                }
            }
        }// define an adapter

        mAdapter = new ContactSelectSosAdapter(input, phoneNo, message, time, timeAgo, selected, SelectSosContact.this);
        recyclerView.setAdapter(mAdapter);

        //addTextListener
        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

                List<String> input = new ArrayList<>();
                List<String> phoneNo = new ArrayList<>();
                List<Boolean> selected = new ArrayList<>();

                for (int i = 0; i < contacts.size(); i++) {
                    String name = contacts.get(i).getDisplayName();
                    if(name.toLowerCase().contains(query)) {
                        input.add(name);
                        String phNo = null;
                        if (contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber() == null) {
                            phNo = contacts.get(i).getPhoneNumbers().get(0).getNumber();
                        } else {
                            phNo = contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber();
                        }

                        if(sel_contacts.contains(phNo)){
                            selected.add(true);
                        }else{
                            selected.add(false);
                        }
                        phoneNo.add(phNo);
                    }
                }// define an adapter

                mAdapter = new ContactSelectReminderAdapter(input, phoneNo, message, time, timeAgo, selected, itemListener);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();  // data set changed
            }
        });

    }

    @Override
    public void recyclerViewListClicked(View v, String phone, String name, Boolean is_selected) {

        if(is_selected) {
            Intent data = new Intent();
            data.putExtra("POS_PHONE", phone);
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }
}

