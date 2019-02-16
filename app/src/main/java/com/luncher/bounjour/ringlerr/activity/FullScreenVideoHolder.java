package com.luncher.bounjour.ringlerr.activity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luncher.bounjour.ringlerr.R;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class FullScreenVideoHolder extends AppCompatActivity {

    String image;
    String type;
    String screen_type;
    String talk_time;
    ProgressBar loading;
    String phone_no;
    TextView name_view;
    Boolean mute = true;
    public static AppCompatActivity fsh;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setFinishOnTouchOutside(false);
        super.onCreate(savedInstanceState);
        fsh = this;

        screen_type = Objects.requireNonNull(getIntent().getExtras()).getString("screen_type");
        assert screen_type != null;
        setContentView(R.layout.popup_video_full);

        name_view = findViewById(R.id.name);
        loading = findViewById(R.id.loading);
        ImageButton closeButton = findViewById(R.id.ib_close);
        final VideoView videoView = findViewById(R.id.videoView);
        ConstraintLayout transparent_view = findViewById(R.id.transparent_view);
        final ImageView mute_icon = findViewById(R.id.mute_icon);

        phone_no = getIntent().getExtras().getString("phone_no");
        image = getIntent().getExtras().getString("image");
        type = getIntent().getExtras().getString("type");
        talk_time = getIntent().getExtras().getString("talk_time");
        screen_type = getIntent().getExtras().getString("screen_type");

        String name = getContactName(this, phone_no);

        if(null != name_view && screen_type.equals("full")) {
            if (name == null) {
                getName();
            } else {
                name_view.setText(name+" calling..");
            }
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

        loading.setVisibility(View.GONE);
        switch (type) {
            case "vid": {
                String path;
                if(screen_type.equals("half") || screen_type.equals("half_full")){
                    path = image;
                }else{
                    path = Environment.getExternalStorageDirectory() + "/ringerrr/videos/receive/"+image;
                }

//                MediaController mediaController = new MediaController(this);
//                mediaController.setAnchorView(videoView);
//                videoView.setMediaController(mediaController);
                final MediaPlayer[] cmp = new MediaPlayer[1];
                videoView.setVideoPath(path);
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        cmp[0] = mp;
                        mp.setVolume(0f, 0f);
                        mp.setLooping(true);
                        videoView.start();
                    }
                });

                if(!screen_type.equals("half")){
                    transparent_view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Dismiss the popup window
                            if (mute) {
                                //mute_icon.setVisibility(View.GONE);
                                mute_icon.setImageResource(R.drawable.ic_volume_up_black_30dp);
                                cmp[0].setVolume(1f, 1f);
                            } else {
                                //mute_icon.setVisibility(View.VISIBLE);
                                mute_icon.setImageResource(R.drawable.ic_volume_off_red_30dp);
                                cmp[0].setVolume(0f, 0f);
                            }
                            mute = !mute;
                        }
                    });
                }else{
                    mute_icon.setVisibility(View.GONE);
                    //mute_icon.setImageResource(R.drawable.ic_volume_up_black_30dp);
                }

            }
                break;
            default:
                break;
        }

        loading.setVisibility(View.GONE);
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

    @Override
    public void onStop() {
        super.onStop();
        fsh = null;
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
}
