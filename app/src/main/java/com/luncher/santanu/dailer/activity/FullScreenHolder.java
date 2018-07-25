package com.luncher.santanu.dailer.activity;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.luncher.santanu.dailer.PhotoFullPopupWindow;
import com.luncher.santanu.dailer.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FullScreenHolder extends AppCompatActivity {

    String image;
    String type;
    String screen_type;
    ImageView r_image;
    ImageView photoView;
    ProgressBar loading;
    ImageButton thumbUpButton;
    ImageButton thumbDownButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        screen_type = getIntent().getExtras().getString("screen_type");
        if(screen_type.equals("half")){
            setContentView(R.layout.popup_photo_half);
        }else{
            setContentView(R.layout.popup_photo_full);
        }

        r_image = (ImageView) findViewById(R.id.r_image);
        photoView = (ImageView) findViewById(R.id.image);
        loading = (ProgressBar) findViewById(R.id.loading);
        ImageButton closeButton = (ImageButton) findViewById(R.id.ib_close);
        thumbUpButton = (ImageButton) findViewById(R.id.thumbUpButton);
        thumbDownButton = (ImageButton) findViewById(R.id.thumbDownButton);

        image = getIntent().getExtras().getString("image");
        type = getIntent().getExtras().getString("type");

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

        String imageStoragePath = Environment.getExternalStorageDirectory() + "/ringerrr/Images/"+image;

        loading.setVisibility(View.GONE);
        if(type.equals("gif")) {
            Glide.with(this).asGif()
                    .load(Uri.parse(imageStoragePath))
                    .into(photoView);

        }else if (type.equals("libgif")) {

            String asseturl = "file:///android_asset/" + image;
            Glide.with(this).asGif()
                    .load(Uri.parse(asseturl))
                    .into(photoView);
        } else if (type.equals("sticker")) {
            String asseturl = "file:///android_asset/" + image;
            Glide.with(this)
                    .load(Uri.parse(asseturl))
                    .into(photoView);
        } else {
            //File imageFile = new File(imageStoragePath);
            Bitmap bitmapz = BitmapFactory.decodeFile(imageStoragePath);
            //bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
            photoView.setImageBitmap(bitmapz);
        }

        loading.setVisibility(View.GONE);
    }
}
