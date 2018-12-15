package com.luncher.bounjour.ringlerr;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.luncher.bounjour.ringlerr.activity.AboutActivity;
import com.luncher.bounjour.ringlerr.activity.BlockList;
import com.luncher.bounjour.ringlerr.activity.DialpadActivity;
import com.luncher.bounjour.ringlerr.activity.FollowUsDialog;
import com.luncher.bounjour.ringlerr.activity.MySettings;
import com.luncher.bounjour.ringlerr.activity.RateusDialog;
import com.luncher.bounjour.ringlerr.activity.ReminderSelfDialog;
import com.luncher.bounjour.ringlerr.activity.ReminderList;
import com.luncher.bounjour.ringlerr.activity.SchedulerDialog;
import com.luncher.bounjour.ringlerr.activity.SchedulerList;
import com.luncher.bounjour.ringlerr.activity.SearchContact;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import static android.Manifest.permission.READ_CONTACTS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;

    public final static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    private static final int REQUEST_READ_CONTACTS = 444;
    private ImageButton btnSelectGif;
    private ImageButton btnSelectStickers;
    private ImageButton schedularButton;
    private ImageButton btnSelecReminder;
    private ImageButton btnSelecKeyboard;
    private RelativeLayout keyboardLayout;
    private int PICK_ICON_REQUEST = 2, PICK_THEME_REQUEST = 3, PICK_STICKER_REQUEST = 5;

    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.

    private Boolean run_once = true;
    public EditText search;
    private boolean isUp;
    private int mVibrationLength;
    private TextView menu_name;
    private ImageView navImageView;
    private Boolean is_hide = true;
    private Boolean is_menu_hide = true;

    public String TAG = "DROIDSPEECH_LOG";

   //Manifest.permission.RECORD_AUDIO,
    String[] permissions = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.SEND_SMS,
    };

    MyDbHelper myDbHelper;
    private String[] mgs_arr = {};
    ArrayList<String> arrayList = new ArrayList<String>();
    ListView lv;
    String res;

    String isProfileVerified;
    Cursor cursor;
    int counter;
    String[] permissionsList = {};

    //voice
    SessionManager session;
    ImageView instagram;
    ImageView twitter;
    ImageView facebook;
    ImageView linkedin;
    ImageView youtube;

    private FirebaseAnalytics mFirebaseAnalytics;
    String pFt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        search = (EditText) findViewById(R.id.search);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        menu_name = (TextView) header.findViewById(R.id.menu_name);
        navImageView = (ImageView) header.findViewById(R.id.navImageView);

        instagram = (ImageView) findViewById(R.id.instagram);
        twitter = (ImageView) findViewById(R.id.twitter);
        facebook = (ImageView) findViewById(R.id.facebook);
        linkedin = (ImageView) findViewById(R.id.linkedin);
        youtube = (ImageView) findViewById(R.id.youtube);

        mAuth = FirebaseAuth.getInstance();

        //btnSelectGif = (ImageButton) findViewById(R.id.gifButton);
        btnSelecReminder = (ImageButton) findViewById(R.id.reminderButton);
        //btnSelectStickers = (ImageButton) findViewById(R.id.stickerButton);
        schedularButton = (ImageButton) findViewById(R.id.schedularButton);
        btnSelecKeyboard = (ImageButton) findViewById(R.id.keyboard);

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

//        myDbHelper = new MyDbHelper(MainActivity.this, null, null, 1);
//        isProfileVerified = myDbHelper.getIsProfileVerified();
//
//        if(isProfileVerified.equals("0")) {
//            //redrict to login
//            Intent mainintent = new Intent(this, WelcomeActivity.class);
//            startActivity(mainintent);
//            finish();
//        }else {
//
////            try {
////                Intent intent = new Intent();
////                String manufacturer = android.os.Build.MANUFACTURER;
////                if ("xiaomi".equalsIgnoreCase(manufacturer)) {
////                    intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
////                } else if ("oppo".equalsIgnoreCase(manufacturer)) {
////                    intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
////                } else if ("vivo".equalsIgnoreCase(manufacturer)) {
////                    intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
////                }
////
////                List<ResolveInfo> list = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
////                if  (list.size() > 0) {
////                    this.startActivity(intent);
////                }
////            } catch (Exception e) {
////                //Crashlytics.logException(e);
////            }
//
//            if(mayRequestContacts()){
//                getContacts();
//            }
//
//            // use this to start and trigger a service
//            Intent i = new Intent(this, MessageService.class);
//            this.startService(i);
//
//        }



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
                Intent intent = new Intent(MainActivity.this, SearchContact.class);
                startActivity(intent);
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
                goToUrl("https://www.youtube.com/channel/UC0rZCd13omkd2rxf1njno-w");
            }
        });


        //droidSpeech = new DroidSpeech(this, null);
        //droidSpeech.setOnDroidSpeechListener(this);
        //hideshowFragment("hide");
        //inferenceInterface = new TensorFlowInferenceInterface(getAssets(), "frozen_model.pb");
    }

    private void goToUrl (String url) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        lv = (ListView) findViewById(R.id.a_Main);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        String mName = user.get(SessionManager.KEY_NAME);
        menu_name.setText(mName);

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


            if (checkPermissions() && run_once) {
                run_once = false;
                //  permissions  granted.

                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                if (mayRequestContacts()) {

                    Intent cbIntent = new Intent();
                    cbIntent.setClass(this, ContactBoundService.class);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(this, cbIntent);
                    } else {
                        startService(cbIntent);
                    }

                    //tabs
                    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                    ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
                    TabsPager tabsPager = new TabsPager(getSupportFragmentManager());

                    viewPager.setOffscreenPageLimit(2);
                    viewPager.setAdapter(tabsPager);
                    tabLayout.setupWithViewPager(viewPager);
                }


                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.addDrawerListener(toggle);
                toggle.syncState();

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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
            }

            // use this to start and trigger a service
            Intent i = new Intent(this, MessageService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.startForegroundService(i);
            } else {
                this.startService(i);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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

        } else if (id == R.id.nav_notification) {

            Toast toast;
            toast = Toast.makeText(getApplicationContext(), "You don't have any new notification", Toast.LENGTH_LONG);
            toast.show();

        }  else if (id == R.id.nav_settings) {

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
        }

        if (id != R.id.action_follow_us) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    public void showRateDialog() {
        RateusDialog alert = new RateusDialog();
        alert.showDialog(this);
    }

    public void showFollowUsDialog(){
        LinearLayout v = (LinearLayout) findViewById(R.id.support_layout);
        if(is_menu_hide) {
            v.setVisibility(View.VISIBLE);
        }else{
            v.setVisibility(View.GONE);
        }
        is_menu_hide = !is_menu_hide;
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
