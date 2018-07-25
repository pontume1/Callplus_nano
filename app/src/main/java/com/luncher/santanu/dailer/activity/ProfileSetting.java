package com.luncher.santanu.dailer.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.luncher.santanu.dailer.LoginActivity;
import com.luncher.santanu.dailer.MainActivity;
import com.luncher.santanu.dailer.R;
import com.luncher.santanu.dailer.SessionManager;
import com.luncher.santanu.dailer.model.Identity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import bolts.Task;

public class ProfileSetting extends AppCompatActivity {

    public String name;
    public String email;
    public String profile_pic;
    public ImageView picture;

    private EditText profile_name;
    private EditText profile_email;
    private Button email_sign_in_button;

    private DatabaseReference mRootRef;
    private String mPhoneNo;
    SessionManager session;

    // Storage Firebase
    private StorageReference mImageStorage;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_setting);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mImageStorage = FirebaseStorage.getInstance().getReference();
        // Session class instance
        session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // phone
        mPhoneNo = user.get(SessionManager.KEY_PHONE);

        picture = (ImageView) findViewById(R.id.profile_image);
        profile_name = (EditText) findViewById(R.id.profile_name);
        profile_email = (EditText) findViewById(R.id.profile_email);
        email_sign_in_button = (Button) findViewById(R.id.email_sign_in_button);

        email_sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgressDialog = new ProgressDialog(ProfileSetting.this);
                mProgressDialog.setTitle("Creating Profile...");
                mProgressDialog.setMessage("");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                name = profile_name.getText().toString();
                email = profile_email.getText().toString();

                DatabaseReference getkey = mRootRef.child("identity").child(mPhoneNo).child("uid");
                getkey.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String uid = dataSnapshot.getValue(String.class);
                        Identity identity = new Identity(uid, email, name, profile_pic);
                        Map<String, Object> postValues = identity.toMap();

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/identity/" + mPhoneNo , postValues);

                        mRootRef.updateChildren(childUpdates);

                        mProgressDialog.dismiss();

                        Intent mainintent = new Intent(ProfileSetting.this, MainActivity.class);
                        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainintent);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .setMinCropWindowSize(500, 500)
                        .start(ProfileSetting.this);

            }
        });

    }

    public String getLastnCharacters(String inputString, int subStringLength){
        int length = inputString.length();
        if(length <= subStringLength){
            return inputString;
        }
        int startIndex = length-subStringLength;
        return inputString.substring(startIndex);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(ProfileSetting.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                final Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());

                String current_user_id = mPhoneNo;


//                Bitmap thumb_bitmap = new Compressor(this)
//                        .setMaxWidth(200)
//                        .setMaxHeight(200)
//                        .setQuality(75)
//                        .compressToBitmap(thumb_filePath);

//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                final byte[] thumb_byte = baos.toByteArray();


                StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
                //final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Glide.with(ProfileSetting.this)
                                    .load(new File(resultUri.getPath())) // Uri of the picture
                                    .into(picture);
                            mProgressDialog.dismiss();
                        }
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
