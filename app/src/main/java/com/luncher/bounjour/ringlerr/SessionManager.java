package com.luncher.bounjour.ringlerr;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luncher.bounjour.ringlerr.model.MyContact;
import com.luncher.bounjour.ringlerr.model.MyFrequent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_FT = "ft";
    public static final String KEY_FIRST = "first_time";
    public static final String KEY_PREMISSIONFIRST = "first_time_permission";
    public static final String KEY_TOKEN = "firebase_token";

    // Email address (make variable public to access from outside)
    public static final String KEY_PHONE = "phone";
    public static final String CONTACT_LIST = "contact_list";
    public static final String CONTACT_FREQUENT_LIST = "contact_frequent_list";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String phone){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        //editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_PHONE, phone);

        // commit changes
        editor.commit();
    }

    public void addName(String name){

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // commit changes
        editor.commit();
    }

    public void addToken(String token){

        // Storing name in pref
        editor.putString(KEY_TOKEN, token);

        // commit changes
        editor.commit();
    }

    public void addEmail(String email){

        // Storing name in pref
        editor.putString(KEY_EMAIL, email);

        // commit changes
        editor.commit();
    }

    public void addFirstTime(String ft){

            // Storing name in pref
            editor.putString(KEY_FT, ft);

            // commit changes
            editor.commit();
    }

    public void addFirstT(String ft){

            // Storing name in pref
            editor.putString(KEY_FIRST, ft);

            // commit changes
            editor.commit();
    }

    public void addFirstP(String ft){

            // Storing name in pref
            editor.putString(KEY_PREMISSIONFIRST, ft);

            // commit changes
            editor.commit();
    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user phone no
        user.put(KEY_PHONE, pref.getString(KEY_PHONE, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_FT, pref.getString(KEY_FT, null));
        user.put(KEY_FIRST, pref.getString(KEY_FIRST, null));
        user.put(KEY_PREMISSIONFIRST, pref.getString(KEY_PREMISSIONFIRST, null));
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));

        // return user
        return user;
    }

    public void setContacts(List<MyContact> list){

        Gson gson = new Gson();
        String json = gson.toJson(list);
        // Storing email in pref
        editor.putString(CONTACT_LIST, json);

        // commit changes
        editor.commit();
    }

    public List<MyContact> getContacts(){
        Gson gson = new Gson();
        String json = pref.getString(CONTACT_LIST, null);
        Type type = new TypeToken<List<MyContact>>() {}.getType();
        List<MyContact> arrayList = gson.fromJson(json, type);

        return arrayList;
    }

    public void setFrequentContacts(ArrayList<MyFrequent> list){

        Gson gson = new Gson();
        String json = gson.toJson(list);
        // Storing email in pref
        editor.putString(CONTACT_FREQUENT_LIST, json);

        // commit changes
        editor.commit();
    }

    public ArrayList<MyFrequent> getFrequentContacts(){
        Gson gson = new Gson();
        String json = pref.getString(CONTACT_FREQUENT_LIST, null);
        Type type = new TypeToken<ArrayList<MyFrequent>>() {}.getType();
        ArrayList<MyFrequent> arrayList = gson.fromJson(json, type);

        return arrayList;
    }
}
