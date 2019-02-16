package com.luncher.bounjour.ringlerr.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Query;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.RecyclerViewClickListener;
import com.luncher.bounjour.ringlerr.adapter.ContactSelectReminderAdapter;
import com.luncher.bounjour.ringlerr.model.Tag;
import com.plumillonforge.android.chipview.Chip;
import com.plumillonforge.android.chipview.ChipView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.luncher.bounjour.ringlerr.activity.ReminderDialogGroup.getContactName;

public class SelectReminderGroupContact extends Activity implements RecyclerViewClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public EditText search;
    List<Contact> contacts;
    List<String> sel_contacts = new ArrayList<>();
    String message;
    Long time;
    Integer timeAgo;
    Button dialog_ok;
    List<String> phone_no = new ArrayList<>();
    List<Chip> chipList = new ArrayList<>();
    ChipView chipDefault;

    public SelectReminderGroupContact() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_select_contact);

        final RecyclerViewClickListener itemListener = this;

        phone_no = getIntent().getStringArrayListExtra("phone_nos");
        message = getIntent().getExtras().getString("message");
        time = getIntent().getExtras().getLong("time");
        timeAgo = getIntent().getExtras().getInt("timeAgo");

        dialog_ok = findViewById(R.id.dialog_ok);
        recyclerView = findViewById(R.id.my_recycler_view);
        search = findViewById( R.id.search);
        chipDefault = findViewById(R.id.chipview);
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

        mAdapter = new ContactSelectReminderAdapter(input, phoneNo, message, time, timeAgo, selected, this);
        recyclerView.setAdapter(mAdapter);

        for (int i=0; i<phone_no.size(); i++) {
            String c_no = phone_no.get(i);
            String c_na = getContactName(this, c_no);

            sel_contacts.add(c_no);
            chipList.add(new Tag(c_na));
            chipDefault.setChipList(chipList);
        }

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

        dialog_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(sel_contacts.size() == 0) {
                    finish();
                }else{
                    final Intent intent1 = new Intent(SelectReminderGroupContact.this, ReminderDialogGroup.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent1.putStringArrayListExtra("phone_nos", (ArrayList<String>) sel_contacts);
                    intent1.putExtra("sim", 1);
                    intent1.putExtra("message", message);
                    intent1.putExtra("timestamp", time);
                    intent1.putExtra("timeAgo", timeAgo);
                    startActivity(intent1);
                    finish();
                }
            }

        });
    }

    @Override
    public void recyclerViewListClicked(View v, String phone, String name, Boolean is_selected) {

        if(is_selected) {
            sel_contacts.add(phone);
            chipList.add(new Tag(name));
            chipDefault.setChipList(chipList);
        }else{
            int pos = sel_contacts.indexOf(phone);
            sel_contacts.remove(phone);
            chipDefault.remove(chipList.get(pos));
        }
    }
}

