package com.luncher.santanu.dailer.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ListView;

import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Query;
import com.luncher.santanu.dailer.MyAdapter;
import com.luncher.santanu.dailer.R;
import com.luncher.santanu.dailer.SelectContact;
import com.luncher.santanu.dailer.activity.SearchContact;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public EditText search;
    List<Contact> contacts;
    private Animation animationUp, animationDown;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        Contacts.initialize(getActivity());
        Query q = Contacts.getQuery();
        q.hasPhoneNumber();
        contacts = q.find();

        List<String> input = new ArrayList<>();
        List<String> phoneNo = new ArrayList<>();
        List<String> photo = new ArrayList<>();
        List<Integer> contact_id = new ArrayList<>();
        List<Long> c_id = new ArrayList<>();
        for (int i = 0; i < contacts.size(); i++) {
            input.add(contacts.get(i).getDisplayName().toString());
            //photo.add(contacts.get(i).getPhotoUri().toString());
            if(contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber()==null){
                phoneNo.add(contacts.get(i).getPhoneNumbers().get(0).getNumber());
            }else{
                phoneNo.add(contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber().toString());
            }

            c_id.add(contacts.get(i).getId());
            contact_id.add(i);
        }// define an adapter

        animationUp = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_up);
        animationDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
        mAdapter = new MyAdapter(input, phoneNo, contact_id, c_id, animationUp, animationDown);
        recyclerView.setAdapter(mAdapter);

    }

}
