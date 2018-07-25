package com.luncher.santanu.dailer;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class PhotoFullPopupWindow extends PopupWindow {

    View view;
    Context mContext;
    ImageView photoView;
    ProgressBar loading;
    ViewGroup parent;
    private static PhotoFullPopupWindow instance = null;



    public PhotoFullPopupWindow(Context ctx, int layout, View v, String image, String type, Bitmap bitmap) throws IOException {
        super(((LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate( R.layout.popup_photo_full, null), ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        if (Build.VERSION.SDK_INT >= 21) {
            setElevation(5.0f);
        }
        this.mContext = ctx;
        this.view = getContentView();
        ImageButton closeButton = (ImageButton) this.view.findViewById(R.id.ib_close);
        setOutsideTouchable(true);

        setFocusable(true);
        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                dismiss();
            }
        });
        //---------Begin customising this popup--------------------

        photoView = (ImageView) view.findViewById(R.id.image);
        loading = (ProgressBar) view.findViewById(R.id.loading);
       // photoView.setMaximumScale(6);
        parent = (ViewGroup) photoView.getParent();
        // ImageUtils.setZoomable(imageView);
        //----------------------------
        if (bitmap == null) {
            loading.setVisibility(View.GONE);
            if(!image.equals("none")) {
                String imageStoragePath = Environment.getExternalStorageDirectory() + "/ringerrr/Images/" + image;

                if (type.equals("gif")) {

                    Glide.with(mContext).asGif()
                            .load(Uri.parse(imageStoragePath))
                            .into(photoView);

                } else if (type.equals("libgif")) {

                    String asseturl = "file:///android_asset/" + image;
                    Glide.with(mContext).asGif()
                            .load(Uri.parse(asseturl))
                            .into(photoView);
                } else if (type.equals("sticker")) {
                    String asseturl = "file:///android_asset/" + image;
                    Glide.with(mContext)
                            .load(Uri.parse(asseturl))
                            .into(photoView);
                } else {
                    //File imageFile = new File(imageStoragePath);
                    Bitmap bitmapz = BitmapFactory.decodeFile(imageStoragePath);
                    //bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
                    photoView.setImageBitmap(bitmapz);
                }
            }

            showAtLocation(v, Gravity.CENTER, 0, 0);
        }

    }

}
