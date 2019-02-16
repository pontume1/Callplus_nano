package com.luncher.bounjour.ringlerr.activity;

/**
 * Created by santanu on 11/11/17.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SelectContact;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

//import com.luncher.bounjour.ringlerr.SessionManager;
//import java.util.HashMap;


public class SosSettings extends AppCompatActivity {

    String phone_no;
    String c_name;
    String scheduler_phone_no;
    String scheduler_c_name;

    String message;
    EditText sendMgs;
    Button dialog_save;
    ImageView dialog_contact;
    TextView scheduler_person_text;
    ImageView secondary_dialog_contact;
    TextView secondary_scheduler_person_text;

//    EditText fec_sel;

    AlarmManager alarmManager;
    static final int PICK_CONTACT_REQUEST = 1;
    static final int PICK_CONTACT_REQUEST_SECONDARY = 5;
    private int PERMISSION_REQUEST_SEND_SMS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d("test","outgoing custom dialog a");

//        SessionManager session = new SessionManager(this);
//        // get user data from session
//        HashMap<String, String> user = session.getUserDetails();
//        String mPhoneNo = user.get(SessionManager.KEY_PHONE);
//        String mName = user.get(SessionManager.KEY_NAME);

        setContentView(R.layout.sos_settings);


        //ImageView profile_image = findViewById(R.id.profile_image);
        scheduler_person_text = findViewById(R.id.scheduler_person_text);
        dialog_contact   = findViewById(R.id.scheduler_person_image);
        secondary_dialog_contact = findViewById(R.id.scheduler_second_person_image);
        secondary_scheduler_person_text  = findViewById(R.id.scheduler_second_person_text);
        dialog_save   = findViewById(R.id.save);
        sendMgs = findViewById(R.id.scheduler_message);

        MyDbHelper myDbHelper = new MyDbHelper(SosSettings.this, null, 2);
        ArrayList settings = myDbHelper.getSosSetting();
        if (!settings.isEmpty()) {
            String number = settings.get(1).toString();
            String message = settings.get(3).toString();

            sendMgs.setText(message);
            phone_no = number;
            c_name = getContactName(this, number);

            // Do something with the phone number...
            if(c_name == null){
                c_name = number;
                scheduler_person_text.setText(number);
            }else{
                scheduler_person_text.setText(c_name);
            }
            String filePath = Environment.getExternalStorageDirectory() + "/ringerrr/profile/"+number+".jpeg";
            File file = new File(filePath);
            if (file.exists()){
                RequestOptions requestOptions = RequestOptions.circleCropTransform()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true);
                Glide.with(SosSettings.this)
                        .load(filePath)
                        .apply(requestOptions)
                        .into(dialog_contact);
            }else{
                String fl = c_name.substring(0, 1);
                if (fl.equals("+")) {
                    fl = c_name.substring(1, 2);
                }
                ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                // generate random color
                int color1 = generator.getRandomColor();
                TextDrawable drawable2 = TextDrawable.builder()
                        .buildRound(fl, color1);
                dialog_contact.setImageDrawable(drawable2);
            }

            scheduler_person_text.setText(c_name);

            if(null != settings.get(2)) {
                String secondary_number = settings.get(2).toString();

                scheduler_c_name = getContactName(this, secondary_number);
                scheduler_phone_no = secondary_number;
                // Do something with the phone number...
                if(scheduler_c_name == null){
                    scheduler_c_name = secondary_number;
                    secondary_scheduler_person_text.setText(secondary_number);
                }else{
                    secondary_scheduler_person_text.setText(scheduler_c_name);
                }

                String filePath2 = Environment.getExternalStorageDirectory() + "/ringerrr/profile/"+secondary_number+".jpeg";
                File file2 = new File(filePath2);
                if (file2.exists()){
                    RequestOptions requestOptions = RequestOptions.circleCropTransform()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true);
                    Glide.with(SosSettings.this)
                            .load(filePath2)
                            .apply(requestOptions)
                            .into(secondary_dialog_contact);
                }else{
                    String fl = scheduler_c_name.substring(0, 1);
                    if (fl.equals("+")) {
                        fl = scheduler_c_name.substring(1, 2);
                    }
                    ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                    // generate random color
                    int color1 = generator.getRandomColor();
                    TextDrawable drawable2 = TextDrawable.builder()
                            .buildRound(fl, color1);
                    secondary_dialog_contact.setImageDrawable(drawable2);
                }

                secondary_scheduler_person_text.setText(scheduler_c_name);
            }
        }else{
            sendMgs.setText("I need help urgently as I am in trouble. Track me from here");
        }

        dialog_contact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                choosePhoneNo(PICK_CONTACT_REQUEST);
            }

        });

        secondary_dialog_contact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                choosePhoneNo(PICK_CONTACT_REQUEST_SECONDARY);
            }

        });

        scheduler_person_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoneNo(PICK_CONTACT_REQUEST);
            }

        });

        secondary_scheduler_person_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoneNo(PICK_CONTACT_REQUEST_SECONDARY);
            }

        });

//        closeButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//
//        });

//        scheduler_prompt_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                // update your model (or other business logic) based on isChecked
//                if(isChecked){
//                    ago_sel.setVisibility(View.VISIBLE);
//                }else{
//                    ago_sel.setVisibility(View.GONE);
//                    remindAgo = 0;
//                }
//            }
//        });

        dialog_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_settings();
            }
        });
    }

    private void save_settings(){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {

                String[] permissions = {Manifest.permission.SEND_SMS};
                requestPermissions(permissions, PERMISSION_REQUEST_SEND_SMS);
            }else{
                saveIt();
            }
        }else {
            saveIt();
        }
    }

    private void saveIt(){
        message = sendMgs.getText().toString();

        if (message.equals("")) {
            Toast.makeText(SosSettings.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone_no == null) {
            Toast.makeText(SosSettings.this, "Please Select a Contact", Toast.LENGTH_SHORT).show();
            return;
        }

        phone_no = phone_no.replace(" ", "");

        MyDbHelper myDbHelper = new MyDbHelper(SosSettings.this, null, 1);
        myDbHelper.addSosSetting(phone_no, scheduler_phone_no, message);

        Toast.makeText(SosSettings.this, "Saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void choosePhoneNo(int requestCode) {
        Intent intent = new Intent(this, SelectSosContact.class);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check which request it is that we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                phone_no = data.getExtras().getString("POS_PHONE");
                phone_no = "+91"+getLastnCharacters(phone_no.replace(" ", ""), 10);

                MyDbHelper myDbHelper = new MyDbHelper(this, null, 1);
                Boolean is_ringlerr = myDbHelper.checkRinglerrUser(phone_no);

                if(!is_ringlerr){
                    // Context pcontext;
                    Toast.makeText(this, "Please Select a Ringlerr User", Toast.LENGTH_LONG).show();
                    return;
                }

                c_name = getContactName(this, phone_no);

                // Do something with the phone number...
                if(c_name == null){
                    c_name = phone_no;
                    scheduler_person_text.setText(phone_no);
                }else{
                    scheduler_person_text.setText(c_name);
                }

                String filePath = Environment.getExternalStorageDirectory() + "/ringerrr/profile/"+phone_no+".jpeg";
                File file = new File(filePath);
                if (file.exists()){
                    RequestOptions requestOptions = RequestOptions.circleCropTransform()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true);
                    Glide.with(SosSettings.this)
                            .load(filePath)
                            .apply(requestOptions)
                            .into(dialog_contact);
                }else{
                    String fl = c_name.substring(0, 1);
                    if (fl.equals("+")) {
                        fl = c_name.substring(1, 2);
                    }
                    ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                    // generate random color
                    int color1 = generator.getRandomColor();
                    TextDrawable drawable2 = TextDrawable.builder()
                            .buildRound(fl, color1);
                    dialog_contact.setImageDrawable(drawable2);
                }
            }
        }

        if(requestCode == PERMISSION_REQUEST_SEND_SMS && resultCode == Activity.RESULT_OK ){
            save_settings();
        }

        if (requestCode == PICK_CONTACT_REQUEST_SECONDARY) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                scheduler_phone_no = data.getExtras().getString("POS_PHONE");;
                scheduler_phone_no = "+91"+getLastnCharacters(scheduler_phone_no.replace(" ", ""), 10);

                MyDbHelper myDbHelper = new MyDbHelper(this, null, 1);
                Boolean is_ringlerr = myDbHelper.checkRinglerrUser(scheduler_phone_no);

                if(!is_ringlerr){
                    // Context pcontext;
                    Toast.makeText(this, "Please Select a Ringlerr User", Toast.LENGTH_LONG).show();
                    return;
                }

                scheduler_c_name = getContactName(this, scheduler_phone_no);

                // Do something with the phone number...
                if(scheduler_c_name == null){
                    scheduler_c_name = scheduler_phone_no;
                    secondary_scheduler_person_text.setText(scheduler_phone_no);
                }else{
                    secondary_scheduler_person_text.setText(scheduler_c_name);
                }

                String filePath = Environment.getExternalStorageDirectory() + "/ringerrr/profile/"+scheduler_phone_no+".jpeg";
                File file = new File(filePath);
                if (file.exists()){
                    RequestOptions requestOptions = RequestOptions.circleCropTransform()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true);
                    Glide.with(SosSettings.this)
                            .load(filePath)
                            .apply(requestOptions)
                            .into(secondary_dialog_contact);
                }else{
                    String fl = scheduler_c_name.substring(0, 1);
                    if (fl.equals("+")) {
                        fl = scheduler_c_name.substring(1, 2);
                    }
                    ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                    // generate random color
                    int color1 = generator.getRandomColor();
                    TextDrawable drawable2 = TextDrawable.builder()
                            .buildRound(fl, color1);
                    secondary_dialog_contact.setImageDrawable(drawable2);
                }

            }
        }

        int PICK_IMAGE_REQUEST = 2;
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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