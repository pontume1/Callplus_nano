package com.luncher.santanu.dailer;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by santanu on 19/11/17.
 */

public class MyDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bonjour_dial.db";

    public static final String TABLE_BONJOUR = "bonjour_mgs";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FROM = "msg_from";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_ISREAD = "is_read";

    public static final String TABLE_PROFILE = "ringer_profile";
    public static final String COLUMN_P_ID = "id";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_OTP = "otp";
    public static final String COLUMN_ISVERIFIED = "is_verified";

    public static final String TABLE_BLOCK = "ringlerr_block";
    public static final String COLUMN_BLOCK_ID = "id";
    public static final String COLUMN_NUMBER = "number";

    public static final String TABLE_RINGLERR_USER = "ringlerr_user";
    public static final String COLUMN_RUSER_ID = "id";
    public static final String COLUMN_RUSER_NUMBER = "number";


    public MyDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String query = " Create table "+ TABLE_BONJOUR +" ( "+ COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_FROM +" TEXT, "+ COLUMN_MESSAGE +" TEXT, " + COLUMN_IMAGE + " TEXT, " + COLUMN_TYPE + " TEXT, "+ COLUMN_ISREAD + " TEXT );";
        sqLiteDatabase.execSQL(query);

        String query_profile = " Create table "+ TABLE_PROFILE +" ( "+ COLUMN_P_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_PHONE +" TEXT, "+ COLUMN_OTP +" TEXT, "+ COLUMN_ISVERIFIED + " TEXT );";
        sqLiteDatabase.execSQL(query_profile);

        String query_block = " Create table "+ TABLE_BLOCK +" ( "+ COLUMN_BLOCK_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_NUMBER +" TEXT );";
        sqLiteDatabase.execSQL(query_block);

        String query_ringlerr_user = " Create table "+ TABLE_RINGLERR_USER +" ( "+ COLUMN_RUSER_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_RUSER_NUMBER +" TEXT );";
        sqLiteDatabase.execSQL(query_ringlerr_user);

    }

    public void addProfile(String otp, String from_phone){

        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE, from_phone);
        values.put(COLUMN_OTP, otp);
        values.put(COLUMN_ISVERIFIED, "0");
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PROFILE, null, values);
        db.close();

    }

    public void addRinglerrUser(String phone_number){

        ContentValues values = new ContentValues();
        values.put(COLUMN_RUSER_NUMBER, phone_number);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_RINGLERR_USER, null, values);
        db.close();

    }

    public Boolean checkRinglerrUser(String phone){
        String block_number = null;
        Boolean is_ringlerr_user = false;
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteCursor c = (SQLiteCursor) db.rawQuery("SELECT * FROM " + TABLE_RINGLERR_USER + " WHERE "+COLUMN_RUSER_NUMBER+" = '"+phone+"' LIMIT 1", null);

        if (c.moveToFirst()) {
            do {
                if(c.getString(c.getColumnIndexOrThrow(COLUMN_RUSER_NUMBER))!=null){
                    block_number = c.getString(c.getColumnIndexOrThrow(COLUMN_RUSER_NUMBER));
                    is_ringlerr_user = true;
                }

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return is_ringlerr_user;
    }

    public void addBlockNumber(String phone_number){

        ContentValues values = new ContentValues();
        values.put(COLUMN_NUMBER, phone_number);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_BLOCK, null, values);
        db.close();

    }

    public void removeBlockNumber(String from_phone){

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_BLOCK, COLUMN_NUMBER+ "= '" + from_phone + "'", null);
        db.close();

    }

    public String checkBlockNumber(String phone){
        String block_number = "null";
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteCursor c = (SQLiteCursor) db.rawQuery("SELECT * FROM " + TABLE_BLOCK + " WHERE "+COLUMN_NUMBER+" = '"+phone+"' LIMIT 1", null);

        if (c.moveToFirst()) {
            do {
                if(c.getString(c.getColumnIndexOrThrow(COLUMN_NUMBER))!=null){
                    block_number = c.getString(c.getColumnIndexOrThrow(COLUMN_NUMBER));
                }

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return block_number;
    }

    public void updateOtp(){

        ContentValues values = new ContentValues();
        values.put(COLUMN_ISVERIFIED, "1");
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_PROFILE, values, COLUMN_ISVERIFIED+"=0", null);
        db.close();
    }

    public void addMessages(String mgs, String from_phone, String image, String type){

        ContentValues values = new ContentValues();
        values.put(COLUMN_FROM, from_phone);
        values.put(COLUMN_MESSAGE, mgs);
        values.put(COLUMN_IMAGE, image);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_ISREAD, "0");
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_BONJOUR, null, values);
        db.close();

    }

    public void updateMessageRead(String id, String phone_no){

        ContentValues values = new ContentValues();
        values.put(COLUMN_ISREAD, "1");
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_BONJOUR, values, COLUMN_ID+"="+id, null);
        //db.close();

        values.put(COLUMN_ISREAD, "2");
        //SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_BONJOUR, values, COLUMN_FROM+"="+phone_no+" AND "+COLUMN_ISREAD+"=0", null);
        db.close();
    }

    public ArrayList<String> getMessages() {
        ArrayList list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteCursor c = (SQLiteCursor) db.rawQuery("SELECT * FROM " + TABLE_BONJOUR + " WHERE "+COLUMN_ISREAD+" = 1 ORDER BY "+COLUMN_ID+" DESC", null);

        if (c.moveToFirst()) {
            do {
                if(c.getString(c.getColumnIndexOrThrow(COLUMN_MESSAGE))!=null){
                    list.add(c.getString(c.getColumnIndexOrThrow(COLUMN_MESSAGE)));
                }

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    public String getIsProfileVerified() {
        String profile = "0";
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteCursor c = (SQLiteCursor) db.rawQuery("SELECT * FROM " + TABLE_PROFILE + " LIMIT 1", null);

        if (c.moveToFirst()) {
            do {
                if(c.getString(c.getColumnIndexOrThrow(COLUMN_ISVERIFIED))!=null){
                    profile = c.getString(c.getColumnIndexOrThrow(COLUMN_ISVERIFIED));
                }

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return profile;
    }

    public String getMyPhoneNo(){
        String myPhoneNo = "";
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteCursor c = (SQLiteCursor) db.rawQuery("SELECT * FROM " + TABLE_PROFILE + " LIMIT 1", null);

        if (c.moveToFirst()) {
            do {
                if(c.getString(c.getColumnIndexOrThrow(COLUMN_PHONE))!=null){
                    myPhoneNo = c.getString(c.getColumnIndexOrThrow(COLUMN_PHONE));
                }

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return myPhoneNo;
    }

    public ArrayList<String> getMessage(String phone_no) {
        String msg = null;
        String id;
        String image;
        String is_read;
        String type;
        ArrayList<String> mgslist = new ArrayList<String>();
        //String from_phone = getLastnCharacters(phone_no, 10);
        String query = "SELECT * FROM " + TABLE_BONJOUR + " WHERE "+COLUMN_FROM+" = '"+phone_no+"' ORDER BY "+COLUMN_ID+" DESC LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null);
        int num_row = c.getCount();
        if (c.moveToFirst()) {
            do {
                if(c.getString(c.getColumnIndexOrThrow(COLUMN_MESSAGE))!=null){
                    is_read = c.getString(c.getColumnIndexOrThrow(COLUMN_ISREAD));
                    if(is_read.equals("0")) {
                        msg = c.getString(c.getColumnIndexOrThrow(COLUMN_MESSAGE));
                        image = c.getString(c.getColumnIndexOrThrow(COLUMN_IMAGE));
                        type = c.getString(c.getColumnIndexOrThrow(COLUMN_TYPE));
                        id = c.getString(c.getColumnIndexOrThrow(COLUMN_ID));
                        updateMessageRead(id, phone_no);

                        mgslist.add(msg);
                        mgslist.add(image);
                        mgslist.add(type);
                    }
                }

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return mgslist;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public String getLastnCharacters(String inputString,
                                     int subStringLength){
        int length = inputString.length();
        if(length <= subStringLength){
            return inputString;
        }
        int startIndex = length-subStringLength;
        return inputString.substring(startIndex);
    }
}
