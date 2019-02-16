package com.luncher.bounjour.ringlerr;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.luncher.bounjour.ringlerr.activity.AboutActivity;
import com.luncher.bounjour.ringlerr.activity.BlockList;
import com.luncher.bounjour.ringlerr.activity.CouponsActivity;
import com.luncher.bounjour.ringlerr.activity.DialpadActivity;
import com.luncher.bounjour.ringlerr.activity.IntroScreen;
import com.luncher.bounjour.ringlerr.activity.MySettings;
import com.luncher.bounjour.ringlerr.activity.NotificationActivity;
import com.luncher.bounjour.ringlerr.activity.RateusDialog;
import com.luncher.bounjour.ringlerr.activity.ReminderSelfDialog;
import com.luncher.bounjour.ringlerr.activity.ReminderList;
import com.luncher.bounjour.ringlerr.activity.SchedulerDialog;
import com.luncher.bounjour.ringlerr.activity.SchedulerList;
import com.luncher.bounjour.ringlerr.activity.SearchContact;
import com.luncher.bounjour.ringlerr.activity.SosActivity;
import com.luncher.bounjour.ringlerr.activity.SosSettings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_CONTACTS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;

    public final static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    private static final int REQUEST_READ_CONTACTS = 444;
    private static final int REQUEST_READ_CALL_LOG = 452;
    private ImageButton sos_button;
    private ImageButton schedularButton;
    private ImageButton btnSelecReminder;
    private ImageButton btnsosSettingsButton;
    private ImageButton btnSelecKeyboard;
    private int PICK_ICON_REQUEST = 2, PICK_THEME_REQUEST = 3, PICK_STICKER_REQUEST = 5;

    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.

    private Boolean run_once = true;
    public EditText search;
    private TextView menu_name;
    private ImageView navImageView;
    private Boolean is_hide = true;
    private Boolean is_menu_hide = true;

    public String TAG = "DROIDSPEECH_LOG";

   //Manifest.permission.RECORD_AUDIO,
    //Manifest.permission.SEND_SMS,
    String[] permissions = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.CALL_PHONE,
            READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ANSWER_PHONE_CALLS,
    };

    MyDbHelper myDbHelper;
    ListView lv;
    String res;
    String[] permissionsList = {};

    //voice
    SessionManager session;
    ImageView instagram;
    ImageView twitter;
    ImageView facebook;
    ImageView linkedin;
    ImageView youtube;
    Button grant_permission;
    RelativeLayout grant_permission_layout;

    private FirebaseAnalytics mFirebaseAnalytics;
    String pFt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        search = findViewById(R.id.search);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        menu_name = header.findViewById(R.id.menu_name);
        navImageView = header.findViewById(R.id.navImageView);

        instagram = findViewById(R.id.instagram);
        twitter = findViewById(R.id.twitter);
        facebook = findViewById(R.id.facebook);
        linkedin = findViewById(R.id.linkedin);
        youtube = findViewById(R.id.youtube);

        grant_permission = findViewById(R.id.grant_permission);
        grant_permission_layout = findViewById(R.id.grant_permission_layout);

        mAuth = FirebaseAuth.getInstance();

        //btnSelectGif = (ImageButton) findViewById(R.id.gifButton);
        btnSelecReminder = findViewById(R.id.reminderButton);
        btnsosSettingsButton = findViewById(R.id.sosSettingsButton);
        //btnSelectStickers = (ImageButton) findViewById(R.id.stickerButton);
        schedularButton = findViewById(R.id.schedularButton);
        sos_button = findViewById(R.id.sos_button);
        btnSelecKeyboard = findViewById(R.id.keyboard);

        session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        pFt = user.get(SessionManager.KEY_PREMISSIONFIRST);
        // phone
        String mName = user.get(SessionManager.KEY_NAME);
        String mPhone = user.get(SessionManager.KEY_PHONE);
        String mEmail = user.get(SessionManager.KEY_EMAIL);
        menu_name.setText(mName);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setUserProperty("phone", mPhone);
        mFirebaseAnalytics.setUserProperty("email", mEmail);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }

        //Set onclicklistener to the list item.
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mayRequestContacts()) {
                    Intent intent = new Intent(MainActivity.this, SearchContact.class);
                    startActivity(intent);
                }
            }

        });

        instagram.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToUrl("https://www.instagram.com/ringlerr1");
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToUrl("https://twitter.com/RINGLERR1");
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToUrl("https://www.facebook.com/Ringlerr");
            }
        });

        linkedin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToUrl("https://in.linkedin.com/company/ringlerr");
            }
        });

        youtube.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToUrl("https://www.youtube.com/channel/UCnWQMls1gtiG2NFhCUcJMCQ");
            }
        });

        grant_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermissions()){
                    restartActivity();
                }
            }
        });

        //droidSpeech = new DroidSpeech(this, null);
        //droidSpeech.setOnDroidSpeechListener(this);
        //hideshowFragment("hide");
        //inferenceInterface = new TensorFlowInferenceInterface(getAssets(), "frozen_model.pb");
    }

    private void goToUrl (String url) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        Uri uriUrl = Uri.parse(url);
        Intent WebView = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(WebView);
    }

//    public void hideshowFragment(String hs){
//        FragmentManager fm = getSupportFragmentManager();
//        final Fragment fragment = fm.findFragmentById(R.id.fragment);
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.setCustomAnimations(android.R.animator.fade_in,
//                android.R.animator.fade_out);
//
//        if(hs.equals("hide")){
//            is_hide = true;
//            getSupportFragmentManager().beginTransaction().hide(fragment).commit();
//        }else{
//            is_hide = false;
//            getSupportFragmentManager().beginTransaction().show(fragment).commit();
//            //droidSpeech.closeDroidSpeechOperations();
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        // put your code here...
        // Get a handle to the list view
        lv = findViewById(R.id.a_Main);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        String mName = user.get(SessionManager.KEY_NAME);
        menu_name.setText(mName);

        // Start speech recognition
        //droidSpeech.startDroidSpeechRecognition();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //hideshowFragment("hide");
        //droidSpeech.closeDroidSpeechOperations();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            //redrict to login
            Intent mainintent = new Intent(this, WelcomeActivity.class);
            startActivity(mainintent);
            finish();
        } else {

            HashMap<String, String> user = session.getUserDetails();
            pFt = user.get(SessionManager.KEY_PREMISSIONFIRST);
            if (null == pFt) {
                session.addFirstP("1");
                try {
                    Intent intent = new Intent();
                    String manufacturer = android.os.Build.MANUFACTURER;
                    if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                        intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                    } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                        intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                    } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                        intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                    }else{
                        session.addFirstP("1");
                    }

                    List<ResolveInfo> list = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    if (list.size() > 0) {
                        this.startActivity(intent);
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            }


            if (run_once) {
                run_once = false;
                //  permissions  granted.
                checkPermissions();

                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                if (mayRequestContacts() && mayRequestManageCall()) {

                    Intent cbIntent = new Intent();
                    cbIntent.setClass(this, ContactBoundService.class);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(this, cbIntent);
                    } else {
                        startService(cbIntent);
                    }

                    //tabs
                    TabLayout tabLayout = findViewById(R.id.tabs);
                    ViewPager viewPager = findViewById(R.id.viewPager);
                    TabsPager tabsPager = new TabsPager(getSupportFragmentManager());

                    viewPager.setOffscreenPageLimit(2);
                    viewPager.setAdapter(tabsPager);
                    tabLayout.setupWithViewPager(viewPager);

                    String filePath = Environment.getExternalStorageDirectory() + "/ringerrr/profile/my_profile.jpeg";
                    File file = new File(filePath);
                    if (file.exists()){
                        RequestOptions requestOptions = RequestOptions.circleCropTransform()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true);
                        Glide.with(MainActivity.this)
                                .load(filePath)
                                .apply(requestOptions)
                                .into(navImageView);
                    }

                    // use this to start and trigger a service
                    Intent i = new Intent(this, MessageService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        this.startForegroundService(i);
                    } else {
                        this.startService(i);
                    }
                }else{
                    grant_permission_layout.setVisibility(View.VISIBLE);
                }


                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.addDrawerListener(toggle);
                toggle.syncState();

                NavigationView navigationView = findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(this);

//                FloatingActionButton fab = findViewById(R.id.fab);
//                fab.setOnClickListener(new View.OnClickListener() {
//                                           @Override
//                                           public void onClick(View view) {
//                                                hideshowFragment("show");
//                                           }
//                                       });

//                btnSelectGif.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        selectGifLibrary();
//                    }
//                });

                btnSelecReminder.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        selectReminder();
                    }
                });

                btnsosSettingsButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        selectSosSteeings();
                    }
                });

//                btnSelectStickers.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        selectStickerLibrary();
//                    }
//                });
//
                schedularButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        selectSchedular();
                    }
                });

                btnSelecKeyboard.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showKeyboard();
                    }
                });

                sos_button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        sosActivity();
                    }
                });
            }
        }
    }

    private void selectSchedular() {
        Intent schedul = new Intent(MainActivity.this, SchedulerDialog.class);
        startActivity(schedul);
    }

    private void showKeyboard() {
        Intent keyboard = new Intent(MainActivity.this, DialpadActivity.class);
        startActivity(keyboard);
    }

    private void sosActivity() {
        Intent sosActivity = new Intent(MainActivity.this, SosActivity.class);
        //sosActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(sosActivity);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(!is_hide){
            //hideshowFragment("hide");
        }else {
            //super.onBackPressed();
            this.moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_reminder) {
            Intent add_contacts = new Intent(MainActivity.this, ReminderList.class);
            startActivity(add_contacts);
        }

        if (id == R.id.action_add_Scheduler) {
            Intent add_scheduler = new Intent(MainActivity.this, SchedulerList.class);
            startActivity(add_scheduler);
        }

        if (id == R.id.action_block_list) {
            Intent block_lists = new Intent(MainActivity.this, BlockList.class);
            startActivity(block_lists);
        }

        if (id == R.id.action_notification) {
            Intent notiintent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(notiintent);
        }

        if (id == R.id.action_coupons) {
            Intent coupon_intent = new Intent(MainActivity.this, CouponsActivity.class);
            startActivity(coupon_intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {

            final Intent aboutintent = new Intent(MainActivity.this, AboutActivity.class);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(aboutintent);
                }
            }, 100);

        } else if (id == R.id.nav_settings) {

            final Intent settingintent = new Intent(MainActivity.this, MySettings.class);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(settingintent);
                    finish();
                }
            }, 100);

        } else if (id == R.id.nav_share) {

            share();

        } else if (id == R.id.nav_rate) {
            showRateDialog();
        }else if (id == R.id.action_follow_us){
            showFollowUsDialog();
        }else if(id == R.id.action_feedback){
            shareFeedback();
        }else if(id == R.id.action_app_guide){
            openIntro();
        }

        if (id != R.id.action_follow_us) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    public void showRateDialog() {
        RateusDialog alert = new RateusDialog();
        alert.showDialog(this);
    }

    public void shareFeedback(){
        String inURL = "https://wa.me/919560631652";
        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( inURL ) );
        startActivity( browse );
    }

    public void showFollowUsDialog(){
        LinearLayout v = findViewById(R.id.support_layout);
        if(is_menu_hide) {
            v.setVisibility(View.VISIBLE);
        }else{
            v.setVisibility(View.GONE);
        }
        is_menu_hide = !is_menu_hide;
    }

    private void openIntro(){
        final Intent intent1 = new Intent(MainActivity.this, IntroScreen.class);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent1);
            }
        }, 100);
    }

    private void share(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Try new calling app Ringlerr. https://play.google.com/store/apps/details?id=com.luncher.bounjour.ringlerr");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(MainActivity.this, p);
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
                    restartActivity();
                } else {
                    String u_permissions = "";
                    for (String per : permissionsList) {
                        u_permissions += "\n" + per;
                    }
                    // permissions list of don't granted permission
                }
                return;
            }
            case REQUEST_READ_CONTACTS:{
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mayRequestManageCall()) {
                        restartActivity();
                    }
                }
            }
            case REQUEST_READ_CALL_LOG:{
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mayRequestContacts()) {
                        restartActivity();
                    }
                }
            }
        }
    }

    private void restartActivity() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //contact read start

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    private boolean mayRequestManageCall() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CALL_LOG)) {
            requestPermissions(new String[]{READ_CALL_LOG}, REQUEST_READ_CALL_LOG);
        } else {
            requestPermissions(new String[]{READ_CALL_LOG}, REQUEST_READ_CALL_LOG);
        }
        return false;
    }


    private void selectGifLibrary() {
        boolean result = Utility.checkPermission(MainActivity.this);
        if (result) {
            Intent intent = new Intent(MainActivity.this, GridActivity.class);
            //Start details activity
            startActivityForResult(intent, PICK_ICON_REQUEST);
        }
    }

    private void selectThemeLibrary() {
        boolean result = Utility.checkPermission(MainActivity.this);
        if (result) {
            Intent intent = new Intent(MainActivity.this, ThemeActivity.class);
            //Start details activity
            startActivityForResult(intent, PICK_THEME_REQUEST);
        }

    }

    private void selectReminder() {

        final Intent intent = new Intent(MainActivity.this, ReminderSelfDialog.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("phone_no", "null");
        intent.putExtra("name", "null");
        //context.startActivity(intent1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        }, 100);
    }

    private void selectSosSteeings(){
        Intent schedul = new Intent(MainActivity.this, SosSettings.class);
        startActivity(schedul);
    }

    private void selectStickerLibrary() {

        boolean result = Utility.checkPermission(MainActivity.this);
        if (result) {
            Intent intent = new Intent(MainActivity.this, StickerActivity.class);
            //Start details activity
            startActivityForResult(intent, PICK_STICKER_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_ICON_REQUEST) {
                res = data.getExtras().getString("POS_ICON");
                //onSelectFromLibraryResult(res);
                choosePhoneNo(res, "gif_library");
            } else if (requestCode == PICK_THEME_REQUEST) {
                res = data.getExtras().getString("POS_THEME");
                //onSelectFromLibraryResult(res);
                choosePhoneNo(res, "theme");
            } else if (requestCode == PICK_STICKER_REQUEST) {
                res = data.getExtras().getString("POS_STICKER");
                //onSelectFromStickerResult(res);
                choosePhoneNo(res, "sticker");
            }
        }
    }

    private void choosePhoneNo(String res, String type) {
        Intent intent = new Intent(this, SelectContact.class);
        intent.putExtra("image", res);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                           int[] grantResults) {
//        if (requestCode == REQUEST_READ_CONTACTS) {
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getContacts();
//            }
//        }
//    }



}
