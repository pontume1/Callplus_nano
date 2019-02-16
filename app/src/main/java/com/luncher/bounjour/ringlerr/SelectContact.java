package com.luncher.bounjour.ringlerr;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Query;
import com.luncher.bounjour.ringlerr.adapter.ContactSelectAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SelectContact extends Activity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public EditText search;
    List<Contact> contacts;
    String image;
    String type;

    public SelectContact() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_select_contact);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        search = (EditText) findViewById( R.id.search);
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Contacts.initialize(this);
        Query q = Contacts.getQuery();
        q.hasPhoneNumber();
        contacts = q.find();

        List<String> input = new ArrayList<>();
        List<String> phoneNo = new ArrayList<>();

        for (int i = 0; i < contacts.size(); i++) {
            if(contacts.get(i).getPhoneNumbers().size()>0) {
                input.add(contacts.get(i).getDisplayName().toString());
                //photo.add(contacts.get(i).getPhotoUri().toString());
                if (contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber() == null) {
                    phoneNo.add(contacts.get(i).getPhoneNumbers().get(0).getNumber());
                } else {
                    phoneNo.add(contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber().toString());
                }
            }
        }// define an adapter

        mAdapter = new ContactSelectAdapter(input, phoneNo, image, type);
        recyclerView.setAdapter(mAdapter);

        //addTextListener

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

                List<String> input = new ArrayList<>();
                List<String> phoneNo = new ArrayList<>();
                for (int i = 0; i < contacts.size(); i++) {
                    String name = contacts.get(i).getDisplayName().toString();
                    if(name.toLowerCase().contains(query)) {
                        input.add(name);
                        if (contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber() == null) {
                            phoneNo.add(contacts.get(i).getPhoneNumbers().get(0).getNumber());
                        } else {
                            phoneNo.add(contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber().toString());
                        }
                    }
                }// define an adapter

                mAdapter = new ContactSelectAdapter(input, phoneNo, image, type);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();  // data set changed
            }
        });
    }

}

