package com.luncher.santanu.dailer.services;

import android.Manifest;
import android.app.IntentService;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.luncher.santanu.dailer.SessionManager;
import com.luncher.santanu.dailer.TimeShow;
import com.luncher.santanu.dailer.model.MyContact;
import com.luncher.santanu.dailer.model.MyFrequent;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class RecentService extends IntentService {
    // Session Manager Class
    SessionManager session;
    ArrayList<MyFrequent> all_lists = new ArrayList<MyFrequent>();
    public final static String CASHBACK_INFO = "cashback_info";

    ArrayList<List> list = new ArrayList<List>();

    String[] projection = new String[] {
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls._ID
    };

    String sortOrder = String.format("%s limit 50 ", android.provider.CallLog.Calls.DATE + " DESC");

    public RecentService() {
        super("RecentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        ArrayList list = updateFrequentContact();
        sendCashbackInfoToClient(list);
    }

    public ArrayList updateFrequentContact() {

        List<String> input = new ArrayList<>();
        List<String> phone_no = new ArrayList<>();
        List<String> types = new ArrayList<>();
        List<String> ago = new ArrayList<>();
        List<Bitmap> user_image = new ArrayList<>();
        List<Integer> contact_id = new ArrayList<>();
        List<Integer> c_id = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {

            Calendar calender = Calendar.getInstance();
            calender.set(Calendar.DATE, -4);
            String toDate = String.valueOf(calender.getTimeInMillis());

            String[] whereValue = {toDate};

            Cursor cursor = getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, android.provider.CallLog.Calls.DATE + " > ?", whereValue, sortOrder);
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                String number = cursor.getString(1);
                String type = cursor.getString(2); //1 = incoming, 2 = outgoing, 3 missed, new = new
                Long time = cursor.getLong(3); // epoch time - https://developer.android.com/reference/java/text/DateFormat.html#parse(java.lang.String
                Integer _id = cursor.getInt(4);

                if(name == null){
                    name = getContactNameFromNumber(number);
                    if(name == null) {
                        input.add(number);
                    }else{
                        input.add(name);
                    }
                }else{
                    input.add(name);
                }

                phone_no.add(number);
                types.add(type);
                c_id.add(_id);

                TimeShow time_ago = new TimeShow();
                String tAgo = time_ago.DateDifference(time);
                ago.add(tAgo);

                int contactId = getContactIDFromNumber(number);
                contact_id.add(contactId);

                Bitmap profile_pic;
                if(contactId > 0){
                    //profile_pic = openPhoto(contactId);
                    profile_pic = null;
                }else{
                    profile_pic = null;
                }

                user_image.add(profile_pic);

            }
            cursor.close();

            list.add(input);
            list.add(phone_no);
            list.add(types);
            list.add(ago);
            list.add(user_image);
            list.add(contact_id);
            list.add(c_id);
        }

        return (ArrayList) list;
    }

    public Bitmap openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = getApplication().getContentResolver().query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return BitmapFactory.decodeStream(new ByteArrayInputStream(data));
                }
            }
        } finally {
            cursor.close();
        }
        return null;

    }

    private void sendCashbackInfoToClient(ArrayList list){
        Intent intent = new Intent();
        intent.setAction(CASHBACK_INFO);
        intent.putStringArrayListExtra("input", (ArrayList) list.get(0));
        intent.putStringArrayListExtra("phone_no", (ArrayList) list.get(1));
        intent.putStringArrayListExtra("types", (ArrayList) list.get(2));
        intent.putStringArrayListExtra("ago", (ArrayList) list.get(3));
        intent.putParcelableArrayListExtra("user_image", (ArrayList) list.get(4));
        intent.putStringArrayListExtra("contact_id", (ArrayList) list.get(5));
        intent.putStringArrayListExtra("c_id", (ArrayList) list.get(6));
        sendBroadcast(intent);
    }

    public int getContactIDFromNumber(String contactNumber)
    {
        int phoneContactID = new Random().nextInt();
        if(contactNumber == null || contactNumber.equals("")){
            phoneContactID = -1;
        }else {
            contactNumber = Uri.encode(contactNumber);
            Cursor contactLookupCursor = getApplicationContext().getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, contactNumber), new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
            while (contactLookupCursor.moveToNext()) {
                phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            contactLookupCursor.close();
        }

        return phoneContactID;
    }

    public String getContactNameFromNumber(String contactNumber)
    {
        String phoneContactID = null;
        if(contactNumber == null || contactNumber.equals("")){
            phoneContactID = null;
        }else {
            contactNumber = Uri.encode(contactNumber);
            Cursor contactLookupCursor = getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, contactNumber), new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
            while (contactLookupCursor.moveToNext()) {
                phoneContactID = contactLookupCursor.getString(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }
            contactLookupCursor.close();
        }

        return phoneContactID;
    }
}
