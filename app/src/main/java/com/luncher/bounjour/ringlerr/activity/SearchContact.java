package com.luncher.bounjour.ringlerr.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Query;
import com.luncher.bounjour.ringlerr.MyAdapter;
import com.luncher.bounjour.ringlerr.R;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchContact extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public EditText search;
    public List<Contact> contacts;
    private Animation animationUp, animationDown;

    public SearchContact() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_contact);

        recyclerView = findViewById(R.id.search_recycler_view);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        search = findViewById( R.id.contact_search);
        search.requestFocus();


        Contacts.initialize(this);
        Query q = Contacts.getQuery();
        q.hasPhoneNumber();
        contacts = q.find();

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

//                Query q = Contacts.getQuery();
//                q.hasPhoneNumber();
//                q.whereContains(Contact.Field.DisplayName, query);
//                List<Contact> contacts = q.find();
//
                List<String> input = new ArrayList<>();
                List<String> phoneNo = new ArrayList<>();
                List<Bitmap> image = new ArrayList<>();
                List<Integer> counts = new ArrayList<>();
                List<Integer> contact_id = new ArrayList<>();
                List<Long> c_id = new ArrayList<>();

                for (int i = 0; i < contacts.size(); i++) {
                    if(contacts.get(i).getPhoneNumbers().size()>0) {
                        String name = contacts.get(i).getDisplayName();
                        String num = contacts.get(i).getPhoneNumbers().get(0).getNumber();
                        if (name.toLowerCase().contains(query) || num.toLowerCase().contains(query)) {
                            input.add(name);
                            image.add(null);
                            counts.add(0);
                            if (contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber() == null) {
                                phoneNo.add(contacts.get(i).getPhoneNumbers().get(0).getNumber());
                            } else {
                                phoneNo.add(contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber());
                            }
                        }

                        c_id.add(contacts.get(i).getId());
                        contact_id.add(i);
                    }
                }// define an adapter
                animationUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                animationDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                mAdapter = new MyAdapter(input, phoneNo, contact_id, c_id, animationUp, animationDown);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();  // data set changed
            }
        });
    }
}
