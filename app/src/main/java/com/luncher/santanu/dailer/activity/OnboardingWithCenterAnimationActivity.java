package com.luncher.santanu.dailer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.luncher.santanu.dailer.MainActivity;
import com.luncher.santanu.dailer.R;

import java.lang.ref.WeakReference;

public class OnboardingWithCenterAnimationActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
        finish();
    }
}


