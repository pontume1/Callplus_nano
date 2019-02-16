package com.luncher.bounjour.ringlerr;

/**
 * Created by santanu on 11/11/17.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luncher.bounjour.ringlerr.model.Blocks;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class MyCustomDialog extends Activity
{
    TextView tv_client;
    TextView sender;
    TextView sender_name;
    TextView talk_time_view;
    TextView block;
    TextView report;
    ImageView r_image;
    ImageView profile_image;
    LinearLayout msg_view;
    String phone_no;
    String message;
    String image;
    String type;
    String talk_time;
    String mName;
    Button dialog_ok;
    Button dialog_revert;
    private DatabaseReference mRootRef;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    public static Activity fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setFinishOnTouchOutside(false);
        super.onCreate(savedInstanceState);
        fa = this;
        try {


            phone_no = getIntent().getExtras().getString("phone_no");
            message = getIntent().getExtras().getString("message");
            image = getIntent().getExtras().getString("image");
            type = getIntent().getExtras().getString("type");
            talk_time = getIntent().getExtras().getString("talk_time");

            if(type.equals("flash")) {
                setContentView(R.layout.dialog_flash);
            }else {
                setContentView(R.layout.dialog);
            }

            this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            //this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            //this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            this.getWindow().setLayout( WindowManager.LayoutParams.MATCH_PARENT,
                                        WindowManager.LayoutParams.MATCH_PARENT);

            initializeContent();
            /*WindowManager.LayoutParams params = getWindow().getAttributes();
            params.x = -100;
            params.height = 70;
            params.width = 1000;
            params.y = -50;

            this.getWindow().setAttributes(params);*/

            msg_view.setVisibility(View.VISIBLE);
            if(message.equals("")){
                msg_view.setVisibility(View.GONE);
            }

            sender.setText(phone_no);
            tv_client.setText(message);
            talk_time_view.setText(talk_time);

            if(!image.equals("none")) {
                String imageStoragePath = Environment.getExternalStorageDirectory() + "/ringerrr/Images/"+image;

                if(type.equals("gif")) {
                    Glide.with(this).asGif()
                            .load(imageStoragePath)
                            .into(r_image);
                }else if(type.equals("libgif")){
                    //"file:///android_asset/"
                    String asseturl = Environment.getExternalStorageDirectory() + "/ringerrr/animation/"+image;
                    Glide.with(this).asGif()
                            .load(asseturl)
                            .into(r_image);
                }else if(type.equals("sticker")){
                    String iconsStoragePath = Environment.getExternalStorageDirectory() + "/ringerrr/stickers/"+image;
                    File istr = new  File(iconsStoragePath);
                    Bitmap bitmap = BitmapFactory.decodeFile(istr.getAbsolutePath());
                    r_image.setImageBitmap(bitmap);
                }else{
                    //File imageFile = new File(imageStoragePath);
                    Bitmap bitmap = BitmapFactory.decodeFile(imageStoragePath);
                    //bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
                    r_image.setImageBitmap(bitmap);
                }

            }

            Button dialog_block   = findViewById(R.id.dialog_block);
            dialog_block.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    blockNo(phone_no);
                }
            });

            r_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Code to show image in full screen:
                    try {
                        new PhotoFullPopupWindow(getApplicationContext(), R.layout.popup_photo_full, view, image, type, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });

            dialog_ok.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    MyCustomDialog.this.finish();
                }
            });

            dialog_revert.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final Intent intent1 = new Intent(MyCustomDialog.this, MyOutgoingCustomDialog.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent1.putExtra("phone_no", phone_no);
                    intent1.putExtra("sim", 1);
                    intent1.putExtra("name", mName);
                    //context.startActivity(intent1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(intent1);
                        }
                    }, 100);
                    finish();
                }
            });

        } catch (Exception e) {
            Log.d("Exception", e.toString());
            e.printStackTrace();
        }
    }

    private void blockNo(final String phone) {
        new AlertDialog.Builder(MyCustomDialog.this)
                .setTitle("Block")
                .setMessage("Are you sure you want to block "+phone+" ?")
                .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        Long tsLong = System.currentTimeMillis()/1000;

                        MyDbHelper myDbHelper;
                        myDbHelper = new MyDbHelper(MyCustomDialog.this, null, 1);
                        String bnumber = myDbHelper.checkBlockNumber(phone);
                        if(!bnumber.equals("null")){
                            Toast.makeText(MyCustomDialog.this, mName+" is already in your block list", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                        SessionManager session = new SessionManager(MyCustomDialog.this);
                        // get user data from session
                        HashMap<String, String> user = session.getUserDetails();
                        // phone
                        String mPhoneNo = user.get(SessionManager.KEY_PHONE);

                        String key = mRootRef.child("blocks").child(mPhoneNo).push().getKey();
                        Blocks blocks = new Blocks(mPhoneNo, phone, mName, tsLong.toString());
                        Map<String, Object> postValues = blocks.toMap();

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/blocks/" + mPhoneNo + "/" + key, postValues);

                        mRootRef.updateChildren(childUpdates);

                        DatabaseReference blockrootRef = mRootRef.child("block_count/"+ phone +"/count");
                        blockrootRef.addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Object count = dataSnapshot.getValue();
                                        String bl_count = "0";
                                        if(count!=null){
                                            bl_count = count.toString();
                                        }
                                        int block_count = getCount(Integer.parseInt(bl_count));
                                        int total_block = block_count+1;
                                        mRootRef.child("block_count").child(phone).child("count").setValue(total_block);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                        myDbHelper.addBlockNumber(phone);

                        Toast.makeText(MyCustomDialog.this, mName+" added to your block list", Toast.LENGTH_SHORT).show();

                    }
                })
                .create()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().post(new Runnable() {

            @Override
            public void run() {
                mRootRef = FirebaseDatabase.getInstance().getReference();
                String name = getContactName(getApplicationContext(), phone_no);
                if(name != null){
                    mName = name;
                    sender_name.setText(name);
                }else{
                    DatabaseReference rootRef = mRootRef.child("identity/"+ phone_no +"/name");
                    rootRef.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Object count = dataSnapshot.getValue();
                                String name = "";
                                if(count!=null){
                                    name = count.toString();
                                }
                                mName = name;
                                sender_name.setText(name);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                }

                DatabaseReference blockrootRef = mRootRef.child("block_count/"+ phone_no +"/count");
                blockrootRef.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Object count = dataSnapshot.getValue();
                                String bl_count = "0";
                                if(count!=null){
                                    bl_count = count.toString();
                                }
                                int block_count = getCount(Integer.parseInt(bl_count));
                                if(block_count>2){
                                    block.setText(block_count+" people has block this number");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                String filePath = Environment.getExternalStorageDirectory() + "/ringerrr/profile/"+phone_no+".jpeg";
                File file = new File(filePath);
                if (file.exists()){
                    RequestOptions requestOptions = RequestOptions.circleCropTransform()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true);
                    Glide.with(MyCustomDialog.this)
                            .load(filePath)
                            .apply(requestOptions)
                            .into(profile_image);
                }

                final StorageReference filepath = storage.getReferenceFromUrl("gs://dailer-f8664.appspot.com").child("profile_images").child(phone_no + ".jpg");
                download_image(filepath, phone_no);

                DatabaseReference spamrootRef = mRootRef.child("report/"+ phone_no +"/count");
                spamrootRef.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Object count = dataSnapshot.getValue();
                                String bl_count = "0";
                                if(count!=null){
                                    bl_count = count.toString();
                                }
                                int block_count = getCount(Integer.parseInt(bl_count));
                                if(block_count>2){
                                    report.setText(block_count+" people has report this number");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

        });
    }

    private void download_image(StorageReference filepath, final String phone_no) {

        final long ONE_MEGABYTE = 1024 * 1024;

        //download file as a byte array
        filepath.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                String file_path = storeProfileImage(bitmap, phone_no+".jpeg");

                RequestOptions requestOptions = RequestOptions.circleCropTransform()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true);
                Glide.with(MyCustomDialog.this)
                        .load(file_path)
                        .apply(requestOptions)
                        .into(profile_image);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                String mgs = exception.getMessage();
                String h = mgs;
            }
        });
    }

    private String storeProfileImage(Bitmap imageData, String filename) {
        //get path to external storage (SD card)
        String iconsStoragePath = Environment.getExternalStorageDirectory() + "/ringerrr/profile/";
        File sdIconStorageDir = new File(iconsStoragePath);
        String filePath = "";

        //create storage directories, if they don't exist
        sdIconStorageDir.mkdirs();

        try {
            filePath = sdIconStorageDir.toString() + "/" + filename;
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            //choose another format if PNG doesn't suit you
            imageData.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            bos.flush();
            bos.close();

        } catch (FileNotFoundException e) {
            return e.getMessage();
        } catch (IOException e) {
            return e.getMessage();
        }

        return filePath;
    }

    private int getCount(int count){
        return count;
    }

    private void initializeContent()
    {
        tv_client   = findViewById(R.id.tv_client);
        sender   = findViewById(R.id.sender);
        sender_name   = findViewById(R.id.name);
        talk_time_view   = findViewById(R.id.talk_time_view);
        block   = findViewById(R.id.block);
        report   = findViewById(R.id.report);
        dialog_ok   = findViewById(R.id.dialog_ok);
        dialog_revert   = findViewById(R.id.dialog_revert);
        r_image = findViewById(R.id.r_image);
        profile_image = findViewById(R.id.profile_image);
        msg_view = findViewById(R.id.msg_view);
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

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

}

