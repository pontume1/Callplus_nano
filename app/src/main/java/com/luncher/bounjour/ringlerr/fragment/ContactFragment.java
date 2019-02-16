package com.luncher.bounjour.ringlerr.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Query;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.luncher.bounjour.ringlerr.MyAdapter;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.activity.AddGroup;
import com.luncher.bounjour.ringlerr.activity.CouponWeb;
import com.luncher.bounjour.ringlerr.activity.CouponsActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        FloatingActionButton fab = v.findViewById(R.id.fab_bar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getActivity(), AddGroup.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //intent.putExtra("phone_no", "null");
                startActivity(intent);
            }
        });

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
            if(contacts.get(i).getPhoneNumbers().size()>0) {
                List<String> tempPhoneNo = new ArrayList<>();
                for (int j = 0; j < contacts.get(i).getPhoneNumbers().size(); j++) {
                    String TphoneNo;
                    if (contacts.get(i).getPhoneNumbers().get(j).getNormalizedNumber() == null) {
                        TphoneNo = contacts.get(i).getPhoneNumbers().get(j).getNumber();
                    } else {
                        TphoneNo = contacts.get(i).getPhoneNumbers().get(j).getNormalizedNumber().toString();
                    }

                    TphoneNo = TphoneNo.replace(" ","");

                    if(!tempPhoneNo.contains(TphoneNo)) {

                        tempPhoneNo.add(TphoneNo);
                        input.add(contacts.get(i).getDisplayName().toString());
                        //photo.add(contacts.get(i).getPhotoUri().toString());
                        phoneNo.add(TphoneNo);
                        c_id.add(contacts.get(i).getId());
                        contact_id.add(i);
                    }
                }
            }
        }// define an adapter

        animationUp = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_up);
        animationDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
        mAdapter = new MyAdapter(input, phoneNo, contact_id, c_id, animationUp, animationDown);
        recyclerView.setAdapter(mAdapter);

    }

}
