package com.luncher.bounjour.ringlerr.activity;

/**
 * Created by santanu on 11/11/17.
 */

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.R;

import androidx.appcompat.app.AppCompatActivity;

public class AddGroup extends AppCompatActivity {

    String phone_no;
    String scheduler_phone_no;

    String message;
    EditText sendMgs;
    Button dialog_save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sos_settings);


//        //ImageView profile_image = findViewById(R.id.profile_image);
//        scheduler_person_text = findViewById(R.id.scheduler_person_text);
//        dialog_contact   = findViewById(R.id.scheduler_person_image);
//        secondary_dialog_contact = findViewById(R.id.scheduler_second_person_image);
//        secondary_scheduler_person_text  = findViewById(R.id.scheduler_second_person_text);
//        dialog_save   = findViewById(R.id.save);
//        sendMgs = findViewById(R.id.scheduler_message);
//
//        MyDbHelper myDbHelper = new MyDbHelper(AddGroup.this, null, 2);
//        ArrayList settings = myDbHelper.getSosSetting();

//        closeButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//
//        });



        dialog_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_settings();
            }
        });
    }

    private void save_settings(){


    }

    private void saveIt(){
        message = sendMgs.getText().toString();

        if (message.equals("")) {
            Toast.makeText(AddGroup.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone_no == null) {
            Toast.makeText(AddGroup.this, "Please Select a Contact", Toast.LENGTH_SHORT).show();
            return;
        }

        phone_no = phone_no.replace(" ", "");

        MyDbHelper myDbHelper = new MyDbHelper(AddGroup.this, null, 1);
        myDbHelper.addSosSetting(phone_no, scheduler_phone_no, message);

        Toast.makeText(AddGroup.this, "Saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void choosePhoneNo(int requestCode) {
        Intent intent = new Intent(this, SelectSosContact.class);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(!cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    public String getLastnCharacters(String inputString, int subStringLength){
        int length = inputString.length();
        if(length <= subStringLength){
            return inputString;
        }
        int startIndex = length-subStringLength;
        return inputString.substring(startIndex);
    }
}