package com.luncher.santanu.dailer;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Query;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luncher.santanu.dailer.activity.BlockList;
import com.luncher.santanu.dailer.activity.ReminderSelfDialog;
import com.luncher.santanu.dailer.activity.ReminderList;
import com.luncher.santanu.dailer.activity.SearchContact;
import com.luncher.santanu.dailer.activity.addContact;
import com.luncher.santanu.dailer.model.User;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;

    public final static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    private static final int REQUEST_READ_CONTACTS = 444;
    private ImageButton btnSelectGif;
    private ImageButton btnSelectStickers;
    private ImageButton btnSelecReminder;
    private ImageButton btnSelecKeyboard;
    private RelativeLayout keyboardLayout;
    private int PICK_ICON_REQUEST = 2, PICK_THEME_REQUEST = 3, PICK_STICKER_REQUEST = 5;

    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.

    private Boolean run_once = true;
    public EditText search;
    private boolean isUp;
    private int mVibrationLength;
    private ViewHolder mViewHolder;

    String[] permissions = new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        search = (EditText) findViewById(R.id.search);
        mAuth = FirebaseAuth.getInstance();

        isUp = false;
        mViewHolder = new ViewHolder();
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

        btnSelectGif = (ImageButton) findViewById(R.id.gifButton);
        btnSelecReminder = (ImageButton) findViewById(R.id.reminderButton);
        btnSelectStickers = (ImageButton) findViewById(R.id.stickerButton);
        btnSelecKeyboard = (ImageButton) findViewById(R.id.keyboard);
        keyboardLayout = (RelativeLayout) findViewById(R.id.keyboardLayout);


//        // Get a handle to the list view
//        lv = (ListView) findViewById(R.id.a_Main);
//
//        //get messages
//        arrayList = myDbHelper.getMessages();
//
//        // Convert ArrayList to array
//        //mgs_arr = (String[]) arrayList.toArray();
//        lv.setAdapter(new ArrayAdapter<String>(MainActivity.this,
//                android.R.layout.simple_list_item_1, arrayList));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//
//            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
//                    == PackageManager.PERMISSION_DENIED) {
//
//                Log.d("permission", "permission denied to READ_PHONE_STATE - requesting it");
//                String[] permissions = {Manifest.permission.READ_PHONE_STATE};
//
//                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
//
//            }
//
//            if (checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS)
//                    == PackageManager.PERMISSION_DENIED) {
//
//                Log.d("permission", "permission denied to PROCESS_OUTGOING_CALLS - requesting it");
//                String[] permissions = {Manifest.permission.PROCESS_OUTGOING_CALLS};
//
//                requestPermissions(permissions, PERMISSION_REQUEST_OUTGOING_CALLS);
//
//            }
//
//            if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
//
//                Log.d("permission", "permission denied to CALL_PHONE - requesting it");
//                String[] permissions = {Manifest.permission.CALL_PHONE};
//
//                requestPermissions(permissions, PERMISSION_REQUEST_CALL_PHONE);
//
//            }
//
//            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                Log.d("permission", "permission denied to READ_EXTERNAL_STORAGE - requesting it");
//                //String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
//
//                //requestPermissions(permissions, PERMISSION_REQUEST_CALL_PHONE);
//                ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                        READ_STORAGE_PERMISSION_REQUEST_CODE);
//
//            }

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

//            try {
//                Intent intent = new Intent();
//                String manufacturer = android.os.Build.MANUFACTURER;
//                if ("xiaomi".equalsIgnoreCase(manufacturer)) {
//                    intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
//                } else if ("oppo".equalsIgnoreCase(manufacturer)) {
//                    intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
//                } else if ("vivo".equalsIgnoreCase(manufacturer)) {
//                    intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
//                }
//
//                List<ResolveInfo> list = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//                if  (list.size() > 0) {
//                    this.startActivity(intent);
//                }
//            } catch (Exception e) {
//                //Crashlytics.logException(e);
//            }


            if (checkPermissions() && run_once) {
                run_once = false;
                //  permissions  granted.

                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                if (mayRequestContacts()) {

                    Intent cbIntent = new Intent();
                    cbIntent.setClass(this, ContactBoundService.class);
                    startService(cbIntent);

                    //tabs
                    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                    ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
                    TabsPager tabsPager = new TabsPager(getSupportFragmentManager());

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

                btnSelectGif.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        selectGifLibrary();
                    }
                });

                btnSelecReminder.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        selectReminder();
                    }
                });

                btnSelectStickers.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        selectStickerLibrary();
                    }
                });

                btnSelecKeyboard.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (isUp) {
                            slideDown(keyboardLayout);
                        } else {
                            slideUp(keyboardLayout);
                        }
                        isUp = !isUp;
                    }
                });
            }

            // use this to start and trigger a service
            Intent i = new Intent(this, MessageService.class);
            this.startService(i);

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_dnd) {
//            Intent add_contacts = new Intent(MainActivity.this, addContact.class);
//            startActivity(add_contacts);
        }

        if (id == R.id.action_add_reminder) {
            Intent add_contacts = new Intent(MainActivity.this, ReminderList.class);
            startActivity(add_contacts);
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        // put your code here...
        // Get a handle to the list view
        lv = (ListView) findViewById(R.id.a_Main);

//        myDbHelper = new MyDbHelper(MainActivity.this, null, null, 1);
//        arrayList = myDbHelper.getMessages();

        // Convert ArrayList to array
        //mgs_arr = (String[]) arrayList.toArray();
//        lv.setAdapter(new ArrayAdapter<String>(MainActivity.this,
//                android.R.layout.simple_list_item_1, arrayList));

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

    //keyboard
    // slide the view from below itself to the current position
    public void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void onClick(View v) {
//        final Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
//        new Thread() {
//            @Override
//            public void run() {
//                vibrator.vibrate(mVibrationLength);
//            }
//        }.start();


        switch (v.getId()) {
            case R.id.Button0:
                onCharacterPressed('0');
                break;
            case R.id.Button1:
                onCharacterPressed('1');
                break;
            case R.id.Button2:
                onCharacterPressed('2');
                break;
            case R.id.Button3:
                onCharacterPressed('3');
                break;
            case R.id.Button4:
                onCharacterPressed('4');
                break;
            case R.id.Button5:
                onCharacterPressed('5');
                break;
            case R.id.Button6:
                onCharacterPressed('6');
                break;
            case R.id.Button7:
                onCharacterPressed('7');
                break;
            case R.id.Button8:
                onCharacterPressed('8');
                break;
            case R.id.Button9:
                onCharacterPressed('9');
                break;
            case R.id.ButtonStar:
                onCharacterPressed('*');
                break;
            case R.id.ButtonHash:
                onCharacterPressed('#');
                break;
            case R.id.ButtonDelete:
                onDeletePressed();
                break;
            case R.id.ButtonCall:
                if (mViewHolder.phoneNumber.getText().length() != 0) {
                    callNumberAndFinish(mViewHolder.phoneNumber.getText());
                }
                break;
            case R.id.ButtonCallRinglerr:
                if (mViewHolder.phoneNumber.getText().length() != 0) {
                    callWithRinglerr(mViewHolder.phoneNumber.getText());
                }
                break;
            case R.id.ButtonContract:
                //disableDialer();
                break;
            case R.id.EditTextPhoneNumber:
                mViewHolder.phoneNumber.setCursorVisible(true);
                break;
        }
    }

    private void onDeletePressed() {
        CharSequence cur = mViewHolder.phoneNumber.getText();
        int start = mViewHolder.phoneNumber.getSelectionStart();
        int end = mViewHolder.phoneNumber.getSelectionEnd();
        if (start == end) { // remove the item behind the cursor
            if (start != 0) {
                cur = cur.subSequence(0, start - 1).toString() + cur.subSequence(start, cur.length()).toString();
                mViewHolder.phoneNumber.setText(cur);
                mViewHolder.phoneNumber.setSelection(start - 1);
                if (cur.length() == 0) {
                    mViewHolder.phoneNumber.setCursorVisible(false);
                }
            }
        } else { // remove the whole selection
            cur = cur.subSequence(0, start).toString() + cur.subSequence(end, cur.length()).toString();
            mViewHolder.phoneNumber.setText(cur);
            mViewHolder.phoneNumber.setSelection(end - (end - start));
            if (cur.length() == 0) {
                mViewHolder.phoneNumber.setCursorVisible(false);
            }
        }
    }

    private void onCharacterPressed(char digit) {
        CharSequence cur = mViewHolder.phoneNumber.getText();

        int start = mViewHolder.phoneNumber.getSelectionStart();
        int end = mViewHolder.phoneNumber.getSelectionEnd();
        int len = cur.length();


        if (cur.length() == 0) {
            mViewHolder.phoneNumber.setCursorVisible(false);
        }

        cur = cur.subSequence(0, start).toString() + digit + cur.subSequence(end, len).toString();
        mViewHolder.phoneNumber.setText(cur);
        mViewHolder.phoneNumber.setSelection(start + 1);
    }

    private void callNumberAndFinish(CharSequence number) {
        if (number == null || number.length() == 0) {
            Toast.makeText(this, "No Phone Number", Toast.LENGTH_SHORT).show();
            return;
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number.toString()));
            int simSlot = 1; //getDefaultSimSlot(context);
            callIntent.putExtra("com.android.phone.force.slot", true);
            callIntent.putExtra("com.android.phone.extra.slot", simSlot);
            startActivity(callIntent);
        }

    }

    public void callWithRinglerr(CharSequence phone){

        if (phone == null || phone.length() == 0) {
            Toast.makeText(this, "No Phone Number", Toast.LENGTH_SHORT).show();
            return;
        }

        final Intent intent1 = new Intent(MainActivity.this, MyOutgoingCustomDialog.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent1.putExtra("phone_no", phone.toString());
        intent1.putExtra("sim", 1);
        intent1.putExtra("name", "");
        //context.startActivity(intent1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent1);
            }
        }, 100);
    }

    private class ViewHolder {
        //public final ListView contactList;
        public final EditText phoneNumber;
        public final ImageButton button0;
        public final ImageButton button1;
        public final ImageButton button2;
        public final ImageButton button3;
        public final ImageButton button4;
        public final ImageButton button5;
        public final ImageButton button6;
        public final ImageButton button7;
        public final ImageButton button8;
        public final ImageButton button9;
        public final ImageButton buttonDelete;
        //public final View dialerView;
        //public final View dialerExpandMenu;

        public ViewHolder() {
            //contactList = (ListView)findViewById(R.id.ContactListView);
            phoneNumber = (EditText)findViewById(R.id.EditTextPhoneNumber);
            button0 = (ImageButton)findViewById(R.id.Button0);
            button1 = (ImageButton)findViewById(R.id.Button1);
            button2 = (ImageButton)findViewById(R.id.Button2);
            button3 = (ImageButton)findViewById(R.id.Button3);
            button4 = (ImageButton)findViewById(R.id.Button4);
            button5 = (ImageButton)findViewById(R.id.Button5);
            button6 = (ImageButton)findViewById(R.id.Button6);
            button7 = (ImageButton)findViewById(R.id.Button7);
            button8 = (ImageButton)findViewById(R.id.Button8);
            button9 = (ImageButton)findViewById(R.id.Button9);
            buttonDelete = (ImageButton)findViewById(R.id.ButtonDelete);
            //dialerView = (View)findViewById(R.id.DialerView);
            //dialerExpandMenu = (View)findViewById(R.id.DialerExpandMenu);

        }
    }

}
