package com.luncher.santanu.dailer;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luncher.santanu.dailer.model.MyContact;
import com.luncher.santanu.dailer.model.MyFrequent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        //user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user phone no
        user.put(KEY_PHONE, pref.getString(KEY_PHONE, null));

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
