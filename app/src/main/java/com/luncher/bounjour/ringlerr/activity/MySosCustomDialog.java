package com.luncher.bounjour.ringlerr.activity;

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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.MyOutgoingCustomDialog;
import com.luncher.bounjour.ringlerr.PhotoFullPopupWindow;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.model.Blocks;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

public class MySosCustomDialog extends Activity implements OnMapReadyCallback
{
    TextView tv_client;
    TextView sender;
    TextView sender_name;
    TextView talk_time_view;
    TextView block;
    TextView report;
    TextView full_view;
    ImageView profile_image;
    LinearLayout msg_view;
    String phone_no;
    String mName;
    String message;
    String type;
    String talk_time;
    String result;
    Button dialog_ok;
    Button dialog_revert;
    private DatabaseReference mRootRef;
    public static Activity sfa;
    private MapView mapView;
    private GoogleMap mMap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sfa = this;
        setContentView(R.layout.sos_dialog);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        try {

//            requestWindowFeature(Window.FEATURE_NO_TITLE);
//            this.setFinishOnTouchOutside(false);

            this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            this.getWindow().setLayout( WindowManager.LayoutParams.MATCH_PARENT,
                                        WindowManager.LayoutParams.MATCH_PARENT);

            initializeContent();
            /*WindowManager.LayoutParams params = getWindow().getAttributes();
            params.x = -100;
            params.height = 70;
            params.width = 1000;
            params.y = -50;

            this.getWindow().setAttributes(params);*/

            phone_no = getIntent().getExtras().getString("phone_no");
            message = getIntent().getExtras().getString("message");
            type = getIntent().getExtras().getString("type");
            talk_time = getIntent().getExtras().getString("talk_time");


            msg_view.setVisibility(View.VISIBLE);
            if(message.equals("")){
                msg_view.setVisibility(View.GONE);
            }

            sender.setText(phone_no);
            tv_client.setText(message);
            talk_time_view.setText(talk_time);

        } catch (Exception e) {
            Log.d("Exception", e.toString());
            e.printStackTrace();
        }

        full_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Code to show image in full screen:
                Intent fullmainintent = new Intent(MySosCustomDialog.this, MapsActivity.class);
                fullmainintent.putExtra("phone_no", phone_no);
                startActivity(fullmainintent);
            }
        });

        dialog_ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        dialog_revert.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final Intent intent1 = new Intent(MySosCustomDialog.this, MyOutgoingCustomDialog.class);
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

        Button dialog_block   = findViewById(R.id.dialog_block);
        dialog_block.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                blockNo(phone_no);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        String name = getContactName(getApplicationContext(), phone_no);
        if(name != null) {
            mName = name;
            sender_name.setText(name);
        }else{
            mName = phone_no;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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

    private void initializeContent()
    {
        tv_client   = findViewById(R.id.tv_client);
        sender   = findViewById(R.id.sender);
        sender_name   = findViewById(R.id.name);
        talk_time_view   = findViewById(R.id.talk_time_view);
        block   = findViewById(R.id.block);
        report   = findViewById(R.id.report);
        dialog_ok   = findViewById(R.id.dialog_ok);
        profile_image = findViewById(R.id.profile_image);
        msg_view = findViewById(R.id.msg_view);
        full_view = findViewById(R.id.full_view);
        dialog_revert   = findViewById(R.id.dialog_revert);
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

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mRootRef = FirebaseDatabase.getInstance().getReference();
        Query rootRef = mRootRef.child("location/"+ phone_no);
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object count = dataSnapshot.getValue();
                if(count!=null){
                    try {
                        mMap.clear();
                        JSONObject json = new JSONObject(count.toString());
                        Double latitude = json.getDouble("latitude");
                        Double longitude = json.getDouble("longitude");
                        LatLng sydney = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(sydney).title(""));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16.0f));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private int getCount(int count){
        return count;
    }

    private void blockNo(final String phone) {
        new AlertDialog.Builder(MySosCustomDialog.this)
                .setTitle("Block")
                .setMessage("Are you sure you want to block "+phone+" ?")
                .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        Long tsLong = System.currentTimeMillis()/1000;

                        MyDbHelper myDbHelper;
                        myDbHelper = new MyDbHelper(MySosCustomDialog.this, null, 1);
                        String bnumber = myDbHelper.checkBlockNumber(phone);
                        if(!bnumber.equals("null")){
                            Toast.makeText(MySosCustomDialog.this, mName+" is already in your block list", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                        SessionManager session = new SessionManager(MySosCustomDialog.this);
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

                        Toast.makeText(MySosCustomDialog.this, mName+" added to your block list", Toast.LENGTH_SHORT).show();

                    }
                })
                .create()
                .show();
    }
}

