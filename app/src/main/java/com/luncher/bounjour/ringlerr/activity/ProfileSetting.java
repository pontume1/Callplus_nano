package com.luncher.bounjour.ringlerr.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.luncher.bounjour.ringlerr.MainActivity;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.Utility;
import com.luncher.bounjour.ringlerr.model.Identity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
    private String mName;
    private String mEmail;
    SessionManager session;

    // Storage Firebase
    private StorageReference mImageStorage;

    private ProgressDialog mProgressDialog;
    public static final int MULTIPLE_PERMISSIONS = 11; // code you want.
    //Manifest.permission.RECORD_AUDIO,
    String[] permissions = new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
    };
    String[] permissionsList = {};

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
        mName = user.get(SessionManager.KEY_NAME);
        mEmail = user.get(SessionManager.KEY_EMAIL);

        picture = (ImageView) findViewById(R.id.profile_image);
        profile_name = (EditText) findViewById(R.id.profile_name);
        profile_email = (EditText) findViewById(R.id.profile_email);
        email_sign_in_button = (Button) findViewById(R.id.email_sign_in_button);

        profile_name.setText(mName);
        profile_email.setText(mEmail);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ringlerr needs the following permissions.");
        // Specify the message text for alert dialog
        String messageText = "1)Storage: To send and receive images while calling"+
                "\n2)Contacts: To sync and connect with Ringlerr users"+
                "\n3)Call: To make Ringlerr calls to your contacts";
        builder.setMessage(messageText)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        checkPermissions();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();


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

                if(name.equals("")){
                    Toast.makeText(getApplicationContext(), "Please enter your Name", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    return;
                }

                if(!validatename(name)){
                    Toast.makeText(getApplicationContext(), "Please enter a valid Name", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    return;
                }

                if(email.equals("")){
                    Toast.makeText(getApplicationContext(), "Please enter your Email", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    return;
                }

                if(!isEmailValid(email)){
                    Toast.makeText(getApplicationContext(), "Please enter a valid Email Id", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    return;
                }

                session.addName(name);
                session.addEmail(email);
                session.addFirstTime("0");

                mProgressDialog.dismiss();

                DatabaseReference getkey = mRootRef.child("identity").child(mPhoneNo).child("uid");
                getkey.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, String> user = session.getUserDetails();
                        String token = user.get(SessionManager.KEY_TOKEN);

                        String uid = dataSnapshot.getValue(String.class);
                        Identity identity = new Identity(uid, email, name, profile_pic, false, token);
                        Map<String, Object> postValues = identity.toMap();

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/identity/" + mPhoneNo , postValues);

                        mRootRef.updateChildren(childUpdates);

                        Intent mainintent = new Intent(ProfileSetting.this, IntroScreen.class);
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

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(ProfileSetting.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissions granted.
                    String filePath = Environment.getExternalStorageDirectory() + "/ringerrr/profile/my_profile.jpeg";
                    File fdelete = new File(filePath);
                    if (fdelete.exists()) {
                        fdelete.delete();
                    }
                } else {
                    String u_permissions = "";
                    for (String per : permissionsList) {
                        u_permissions += "\n" + per;
                    }
                    // permissions list of don't granted permission
                }
                return;
            }
        }
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean validatename(String txt) {

        String regx = "^[a-zA-Z\\s]*$";
        Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(txt);
        return matcher.find();

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


                final StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
                //final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Glide.with(ProfileSetting.this)
                                    .load(new File(resultUri.getPath())) // Uri of the picture
                                    .into(picture);
                            mProgressDialog.dismiss();
                            Toast.makeText(ProfileSetting.this, "Picture updated!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
