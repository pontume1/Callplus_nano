package com.luncher.bounjour.ringlerr.activity;

import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.database.FirebaseDatabase;
import com.luncher.bounjour.ringlerr.MainActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class OnboardingWithCenterAnimationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
        finish();
    }
}


