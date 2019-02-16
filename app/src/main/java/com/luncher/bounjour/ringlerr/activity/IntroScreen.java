package com.luncher.bounjour.ringlerr.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.luncher.bounjour.ringlerr.MainActivity;
import com.luncher.bounjour.ringlerr.MyDbHelper;
import com.luncher.bounjour.ringlerr.R;
import com.luncher.bounjour.ringlerr.SessionManager;
import com.luncher.bounjour.ringlerr.model.Blocks;
import com.luncher.bounjour.ringlerr.model.Reminder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class IntroScreen extends AppCompatActivity {

    PreferenceManager preferenceManager;
    LinearLayout Layout_bars;
    TextView[] bottomBars;
    int[] screens;
    Button Skip, Next;
    ViewPager vp;
    MyViewPagerAdapter myvpAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen);

        screens = new int[]{
                R.layout.intro_screen1,
                R.layout.intro_screen2,
                R.layout.intro_screen3,
                R.layout.intro_screen4,
                R.layout.intro_screen5,
                R.layout.intro_screen6,
                R.layout.intro_screen7
        };

        vp = findViewById(R.id.view_pager);
        Layout_bars = findViewById(R.id.layoutBars);
        Skip = findViewById(R.id.skip);
        Next = findViewById(R.id.next);
        myvpAdapter = new MyViewPagerAdapter();
        vp.setAdapter(myvpAdapter);
        vp.addOnPageChangeListener(viewPagerPageChangeListener);

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               next(v);
            }

        });

        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(v);
            }

        });

        ColoredBars(0);

        SessionManager session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // phone
        String mPhoneNo = user.get(SessionManager.KEY_PHONE);

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        Query blockrootRef = mRootRef.child("blocks").child(mPhoneNo);

        blockrootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Blocks block = child.getValue(Blocks.class);
                        String block_number = block.getBlock_no();
                        MyDbHelper myDbHelper = new MyDbHelper(IntroScreen.this, null, 1);
                        String bnumber = myDbHelper.checkBlockNumber(block_number);
                        if(bnumber.equals("null")){
                            myDbHelper.addBlockNumber(block_number);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        assert mPhoneNo != null;
        Query messageRef = mRootRef.child("reminder").child(mPhoneNo);
        final MyDbHelper myDbHelper = new MyDbHelper(IntroScreen.this, null, 9);
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Reminder reminder = child.getValue(Reminder.class);
                        String reminderKey = child.getKey();
                        Long crLong = System.currentTimeMillis();
                        Long time_and_date = crLong;
                        if(null != reminder.time) {
                            time_and_date = Long.valueOf(reminder.time);
                        }
                        Date date = new Date(time_and_date);
                        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss", Locale.US);
                        String formattedDate = formatter.format(date);
                        int response;
                        switch (reminder.is_accepted) {
                            case "true":
                                response = 1;
                                break;
                            case "false":
                                response = 2;
                                break;
                            default:
                                response = 0;
                                break;
                        }

                        myDbHelper.addReminder(reminder.message, time_and_date, reminder.shared_with, reminderKey, reminder.remindAgo, response, reminder.from);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void next(View v) {
        int i = getItem(+1);
        if (i < screens.length) {
            vp.setCurrentItem(i);
        } else {
            launchMain();
        }
    }

    public void skip(View view) {
        launchMain();
    }

    private void ColoredBars(int thisScreen) {
        int[] colorsInactive = getResources().getIntArray(R.array.dot_on_page_not_active);
        int[] colorsActive = getResources().getIntArray(R.array.dot_on_page_active);
        bottomBars = new TextView[screens.length];

        Layout_bars.removeAllViews();
        for (int i = 0; i < bottomBars.length; i++) {
            bottomBars[i] = new TextView(this);
            bottomBars[i].setTextSize(100);
            bottomBars[i].setText(Html.fromHtml("¯"));
            Layout_bars.addView(bottomBars[i]);
            bottomBars[i].setTextColor(colorsInactive[thisScreen]);
        }
        if (bottomBars.length > 0)
            bottomBars[thisScreen].setTextColor(colorsActive[thisScreen]);
    }

    private int getItem(int i) {
        return vp.getCurrentItem() + i;
    }

    private void launchMain() {
        Intent mainintent = new Intent(IntroScreen.this, MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            ColoredBars(position);
            if (position == screens.length - 1) {
                Next.setText("start");
                Skip.setVisibility(View.GONE);
            } else {
                Next.setText(getString(R.string.next));
                Skip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater inflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(screens[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return screens.length;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View v = (View) object;
            container.removeView(v);
        }

        @Override
        public boolean isViewFromObject(View v, Object object) {
            return v == object;
        }
    }
}