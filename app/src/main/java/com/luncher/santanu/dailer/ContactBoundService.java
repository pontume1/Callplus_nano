package com.luncher.santanu.dailer;

import android.Manifest;
import android.app.IntentService;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Query;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luncher.santanu.dailer.model.MyContact;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class ContactBoundService extends IntentService {
    List<Contact> contacts;

    MyDbHelper myDbHelper;
    private DatabaseReference mRootRef;

    public ContactBoundService() {
        super("ContactBoundService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        updateContact();
    }

    public void updateContact() {

        mRootRef = FirebaseDatabase.getInstance().getReference();

        Contacts.initialize(this);
        Query q = Contacts.getQuery();
        q.hasPhoneNumber();
        contacts = q.find();

        myDbHelper = new MyDbHelper(getApplicationContext(), null, null, 1);

        for (int i = 0; i < contacts.size(); i++) {

            final String phone_no;
            String phone_num;
            if(contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber()==null){
                phone_num = contacts.get(i).getPhoneNumbers().get(0).getNumber();
            }else{
                phone_num = contacts.get(i).getPhoneNumbers().get(0).getNormalizedNumber().toString();
            }

            phone_no = phone_num.replaceAll("[*#]", "");

            DatabaseReference users = mRootRef.child("identity");
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(phone_no)) {
                        // run some code
                        myDbHelper.addRinglerrUser(phone_no);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }
}
