package com.luncher.bounjour.ringlerr.services;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.model.MyFrequent;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class FrequentService extends IntentService {
    // Session Manager Class
    SessionManager session;
    ArrayList<MyFrequent> all_lists = new ArrayList<MyFrequent>();
    public final static String CALLBACK_INFO = "cashback_info";

    public FrequentService() {
        super("FrequentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        updateFrequentContact();
        sendCallbackInfoToClient("Done");
    }

    public void updateFrequentContact() {

        // Session class instance
        session = new SessionManager(getApplicationContext());

        List<String> input_f = new ArrayList<>();
        List<String> phone_no_f = new ArrayList<>();
        List<Bitmap> user_image_f = new ArrayList<>();
        List<Integer> count_f = new ArrayList<Integer>();
        List<Long> duration_f = new ArrayList<Long>();

        for(int l = 0; l < all_lists.size(); l++) {

            String f_name = all_lists.get(l).getName();
            String f_phone = all_lists.get(l).getNumber();
            Long f_duration = all_lists.get(l).getDuration();
            Bitmap f_user_image = null;
            if(null != all_lists.get(l).getImage()) {
                f_user_image = openPhoto(all_lists.get(l).getImage());
            }
            Integer f_counts = all_lists.get(l).getCounts();
            if(f_counts == null){
                f_counts = 0;
            }

            input_f.add(f_name);
            phone_no_f.add(f_phone);
            user_image_f.add(f_user_image);
            count_f.add(f_counts);
            duration_f.add(f_duration);

            if(l >= 9){
                break;
            }

        }

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

    private void sendCallbackInfoToClient(String msg){
        Intent intent = new Intent();
        intent.setAction(CALLBACK_INFO);
        intent.putExtra("cashback",msg);
        sendBroadcast(intent);
    }
}
