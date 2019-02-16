package com.luncher.bounjour.ringlerr.activity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luncher.bounjour.ringlerr.R;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

public class FullScreenHolder extends AppCompatActivity {

    String phone_no;
    String name;
    String image;
    String type;
    String screen_type;
    String talk_time;
    ImageView photoView;
    ProgressBar loading;
    ImageButton thumbUpButton;
    ImageButton thumbDownButton;
    Button reject_button;
    TextView name_view;
    public static AppCompatActivity fsh;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setFinishOnTouchOutside(false);
        super.onCreate(savedInstanceState);
        fsh = this;

        screen_type = Objects.requireNonNull(getIntent().getExtras()).getString("screen_type");
        type = getIntent().getExtras().getString("type");
        assert screen_type != null;
        if(screen_type.equals("half") || screen_type.equals("sender_full")){
            setContentView(R.layout.popup_photo_half);
        }else{
            if(type.equals("snap")) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            }
            setContentView(R.layout.popup_photo_full);
        }

        name_view = findViewById(R.id.name);
        photoView = findViewById(R.id.image);
        loading = findViewById(R.id.loading);
        ImageButton closeButton = findViewById(R.id.ib_close);
        thumbUpButton = findViewById(R.id.thumbUpButton);
        thumbDownButton = findViewById(R.id.thumbDownButton);
        reject_button = findViewById(R.id.reject);

        phone_no = getIntent().getExtras().getString("phone_no");
        image = getIntent().getExtras().getString("image");
        talk_time = getIntent().getExtras().getString("talk_time");

        name = getContactName(this, phone_no);

        if(null != name_view) {
            if (name == null) {
                getName();
            } else {
                if(type.equals("snap")){
                    name_view.setText(name + " snap calling..");
                }else {
                    name_view.setText(name + " calling..");
                }
            }
        }

        assert talk_time != null;
        if(!talk_time.equals("")){
            reject_button.setVisibility(View.VISIBLE);
            reject_button.setText("Talk time "+talk_time);
        }

        this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                finish();
            }
        });

        thumbUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thumbUpButton.setImageResource(R.drawable.thumb_up_primary_24dp);
                thumbDownButton.setImageResource(R.drawable.thumb_down_white_24dp);
            }
        });

        thumbDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thumbDownButton.setImageResource(R.drawable.ic_thumb_down_primary_24dp);
                thumbUpButton.setImageResource(R.drawable.thumb_up_white_24dp);
            }
        });

        loading.setVisibility(View.GONE);
        label:
        switch (type) {
            case "jpg": {
                switch (screen_type) {
                    case "half":
                        Glide.with(this)
                                .load(image)
                                .into(photoView);
                        break;
                    case "sender_full":
                        byte[] imageByteArray = Base64.decode(image, Base64.DEFAULT);
                        Glide.with(this)
                                .load(imageByteArray)
                                .into(photoView);
                        break label;
                    default:
                        String imageStoragePath = Environment.getExternalStorageDirectory() + "/ringerrr/Images/" + image;
                        Glide.with(this)
                                .load(imageStoragePath)
                                .into(photoView);
                        break;
                }
            }
            break;
            case "gif":
                String imageStoragePath = Environment.getExternalStorageDirectory() + "/ringerrr/Images/" + image;
                Glide.with(this).asGif()
                        .load(Uri.parse(imageStoragePath))
                        .into(photoView);

                break;
            case "libgif": {

                String asseturl = Environment.getExternalStorageDirectory() + "/ringerrr/animation/" + image;
                Glide.with(this).asGif()
                        .load(asseturl)
                        .into(photoView);
                break;
            }
            case "sticker": {
                String asseturl = Environment.getExternalStorageDirectory() + "/ringerrr/stickers/" + image;
                Glide.with(this)
                        .load(asseturl)
                        .into(photoView);
                break;
            }
            default:
                byte[] imageByteArray = Base64.decode(image, Base64.DEFAULT);
                Glide.with(this)
                        .load(imageByteArray)
                        .into(photoView);
                break;
        }

        loading.setVisibility(View.GONE);
    }

    private void getName() {
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference rootRef = mRootRef.child("identity/"+ phone_no +"/name");
        rootRef.addListenerForSingleValueEvent(
            new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object count = dataSnapshot.getValue();
                    String name = "";
                    if(count!=null){
                        name = count.toString();
                    }
                    name_view.setText(name+" calling..");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    @Override
    public void onStop() {
        super.onStop();
        fsh = null;
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
}
