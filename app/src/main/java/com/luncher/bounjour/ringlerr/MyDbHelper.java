package com.luncher.bounjour.ringlerr;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.Html;

import com.luncher.bounjour.ringlerr.model.Scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    public static final String COLUMN_TALK_TIME = "talk_time";
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
    public static final String COLUMN_RUSER_PIC = "picture";

    public static final String TABLE_RINGLERR_SCHEDULER = "ringlerr_scheduler";
    public static final String COLUMN_SCHEDULE_ID = "id";
    public static final String COLUMN_SCHEDULE_TYPE = "s_type";
    public static final String COLUMN_SCHEDULE_AGO = "ago";
    public static final String COLUMN_SCHEDULE_NUMBER = "number";
    public static final String COLUMN_SCHEDULE_NAME = "name";
    public static final String COLUMN_SCHEDULE_MSG = "message";
    public static final String COLUMN_SCHEDULE_TIME = "time";
    public static final String COLUMN_SCHEDULE_ISDONE = "is_done";
    public static final String COLUMN_SCHEDULE_ISMANUAL = "is_manual";

    public static final String TABLE_RINGLERR_REMINDER = "ringlerr_reminder";
    public static final String COLUMN_REMINDER_ID = "id";
    public static final String COLUMN_REMINDER_MESSAGE = "message";
    public static final String COLUMN_REMINDER_TIME = "time";
    public static final String COLUMN_REMINDER_SHARED_WITH = "shared_with";
    public static final String COLUMN_REMINDER_KEY = "shared_key";

    public static final String TABLE_CHAT_NOTIFICATION = "ringlerr_chat_notification";
    public static final String COLUMN_NOTIFICATION_ID = "id";
    public static final String COLUMN_NOTIFICATION_NUMBER = "number";
    public static final String COLUMN_NOTIFICATION_MSG = "message";

    Context mycontext;

    public MyDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        mycontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String query = " Create table "+ TABLE_BONJOUR +" ( "+ COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_FROM +" TEXT, "+ COLUMN_MESSAGE +" TEXT, " + COLUMN_IMAGE + " TEXT, " + COLUMN_TYPE + " TEXT, " + COLUMN_TALK_TIME + " TEXT, "+ COLUMN_ISREAD + " TEXT );";
        sqLiteDatabase.execSQL(query);

        String query_profile = " Create table "+ TABLE_PROFILE +" ( "+ COLUMN_P_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_PHONE +" TEXT, "+ COLUMN_OTP +" TEXT, "+ COLUMN_ISVERIFIED + " TEXT );";
        sqLiteDatabase.execSQL(query_profile);

        String query_block = " Create table "+ TABLE_BLOCK +" ( "+ COLUMN_BLOCK_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_NUMBER +" TEXT );";
        sqLiteDatabase.execSQL(query_block);

        String query_ringlerr_scheduler = " Create table "+ TABLE_RINGLERR_SCHEDULER +" ( "+ COLUMN_SCHEDULE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_SCHEDULE_NUMBER +" TEXT, "+ COLUMN_SCHEDULE_TYPE +" TEXT, "+ COLUMN_SCHEDULE_AGO +" TEXT, "+ COLUMN_SCHEDULE_NAME +" TEXT, "+ COLUMN_SCHEDULE_MSG + " TEXT, "+ COLUMN_SCHEDULE_TIME +" TEXT, "+ COLUMN_SCHEDULE_ISDONE + " TEXT, "+COLUMN_SCHEDULE_ISMANUAL + " TEXT );";
        sqLiteDatabase.execSQL(query_ringlerr_scheduler);

        String query_ringlerr_user = " Create table "+ TABLE_RINGLERR_USER +" ( "+ COLUMN_RUSER_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_RUSER_NUMBER +" TEXT );";
        sqLiteDatabase.execSQL(query_ringlerr_user);

        String query_ringlerr_reminder = " Create table "+ TABLE_RINGLERR_REMINDER +" ( "+ COLUMN_REMINDER_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_REMINDER_MESSAGE +" TEXT, "+ COLUMN_REMINDER_TIME +" TEXT, "+ COLUMN_REMINDER_SHARED_WITH +" TEXT, "+COLUMN_REMINDER_KEY+" TEXT );";
        sqLiteDatabase.execSQL(query_ringlerr_reminder);

        String query_ringlerr_notification = " Create table "+ TABLE_CHAT_NOTIFICATION +" ( "+ COLUMN_NOTIFICATION_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_NOTIFICATION_NUMBER +" TEXT, "+ COLUMN_NOTIFICATION_MSG +" TEXT );";
        sqLiteDatabase.execSQL(query_ringlerr_notification);
    }

    public void addChatNotification(String number, String message){

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTIFICATION_NUMBER, number);
        values.put(COLUMN_NOTIFICATION_MSG, message);
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_CHAT_NOTIFICATION, null, values);
        db.close();
    }

    public void removeNotification(String number){

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CHAT_NOTIFICATION, COLUMN_NOTIFICATION_NUMBER+ "= '" + number + "'", null);
        db.close();

    }

    public int getNotificationCount(){
        String query = "SELECT * FROM " + TABLE_CHAT_NOTIFICATION;
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null);
        int num_row = c.getCount();

        return num_row;
    }

    public int getIndivNotificationCount(String phone){
        String query = "SELECT * FROM " + TABLE_CHAT_NOTIFICATION + " WHERE "+COLUMN_NOTIFICATION_NUMBER+"='"+phone+"'";
        int num_row = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        try {

            SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null);
            num_row = c.getCount();
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            db.close();
        }

        return num_row;
    }

    public String getNotificationMessage(){

        String notificationMsg = "";
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            SQLiteCursor c = (SQLiteCursor) db.rawQuery("SELECT * FROM " + TABLE_CHAT_NOTIFICATION, null);

            if (c.moveToFirst()) {
                do {
                    if(c.getString(c.getColumnIndexOrThrow(COLUMN_NOTIFICATION_NUMBER))!=null){
                        String number = c.getString(c.getColumnIndexOrThrow(COLUMN_NOTIFICATION_NUMBER));
                        String message = c.getString(c.getColumnIndexOrThrow(COLUMN_NOTIFICATION_MSG));
                        notificationMsg += getContactName(mycontext, number)+" : "+message+"\n";
                    }

                } while (c.moveToNext());
            }
            c.close();
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            db.close();
        }

        return notificationMsg;
    }

    public long addReminder(String message, Long time, String shared_with, String key){

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMINDER_MESSAGE, message);
        values.put(COLUMN_REMINDER_TIME, time);
        values.put(COLUMN_REMINDER_SHARED_WITH, shared_with);
        values.put(COLUMN_REMINDER_KEY, key);
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_RINGLERR_REMINDER, null, values);
        db.close();

        return id;
    }

    public int updateReminder(String key, String message, Long time, String shared_with){

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMINDER_MESSAGE, message);
        values.put(COLUMN_REMINDER_TIME, time);
        values.put(COLUMN_REMINDER_SHARED_WITH, shared_with);
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_RINGLERR_REMINDER, values, COLUMN_REMINDER_KEY+"='"+key+"'", null);
        db.close();

        String query = "SELECT * FROM " + TABLE_RINGLERR_REMINDER + " WHERE "+COLUMN_REMINDER_KEY+"='"+key+"'";
        db = this.getReadableDatabase();
        SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null);
        int id = 0;
        if (c.moveToFirst()) {
            id = c.getInt(c.getColumnIndex(COLUMN_REMINDER_ID));
        }
        db.close();
        return id;

    }

    public long updateReminderAccepted(String key, String shared_with){

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMINDER_SHARED_WITH, shared_with);
        SQLiteDatabase db = getWritableDatabase();
        long id = db.update(TABLE_RINGLERR_REMINDER, values, COLUMN_REMINDER_KEY+"="+key, null);
        db.close();

        return id;
    }

    public long addSchedule(String name, String type, String ago, String phone, String message, Long time, String is_manual){

        ContentValues values = new ContentValues();
        values.put(COLUMN_SCHEDULE_NUMBER, phone);
        values.put(COLUMN_SCHEDULE_NAME, name);
        values.put(COLUMN_SCHEDULE_TYPE, type);
        values.put(COLUMN_SCHEDULE_AGO, ago);
        values.put(COLUMN_SCHEDULE_MSG, message);
        values.put(COLUMN_SCHEDULE_TIME, time);
        values.put(COLUMN_SCHEDULE_ISDONE, "0");
        values.put(COLUMN_SCHEDULE_ISMANUAL, is_manual);
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_RINGLERR_SCHEDULER, null, values);
        db.close();

        return id;

    }

    public void updateSchedule(int id, String name, String type, String ago, String phone, String message, Long time, String is_manual){

        ContentValues values = new ContentValues();
        values.put(COLUMN_SCHEDULE_NUMBER, phone);
        values.put(COLUMN_SCHEDULE_NAME, name);
        values.put(COLUMN_SCHEDULE_TYPE, type);
        values.put(COLUMN_SCHEDULE_AGO, ago);
        values.put(COLUMN_SCHEDULE_MSG, message);
        values.put(COLUMN_SCHEDULE_TIME, time);
        values.put(COLUMN_SCHEDULE_ISDONE, "0");
        values.put(COLUMN_SCHEDULE_ISMANUAL, is_manual);
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_RINGLERR_SCHEDULER, values, COLUMN_SCHEDULE_ID+"="+id, null);
        db.close();

    }

    public List<Scheduler> getAllScheduler(){
        String number;
        String name;
        String type;
        String ago;
        String message;
        String time;
        String is_done;
        String is_manual;
        Integer id;
        List<Scheduler> schedulelist = new ArrayList<Scheduler>();

        String query = "SELECT * FROM " + TABLE_RINGLERR_SCHEDULER + " ORDER BY "+COLUMN_SCHEDULE_ID+" DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null);
        int num_row = c.getCount();
        if (c.moveToFirst()) {
            do {
                if(c.getString(c.getColumnIndexOrThrow(COLUMN_MESSAGE))!=null){
                    number = c.getString(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_NUMBER));
                    name = c.getString(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_NAME));
                    type = c.getString(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_TYPE));
                    ago = c.getString(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_AGO));
                    message = c.getString(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_MSG));
                    time = c.getString(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_TIME));
                    is_done = c.getString(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_ISDONE));
                    is_manual = c.getString(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_ISMANUAL));
                    id = c.getInt(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_ID));

                    Scheduler s = new Scheduler();
                    s.setPhone(number);
                    s.setName(name);
                    s.setSpinnerType(type);
                    s.setAgo(ago);
                    s.setMessage(message);
                    s.setSTime(Long.valueOf(time));
                    s.setIsDone(is_done);
                    s.setIsManual(is_manual);
                    s.setSid(id);

                    schedulelist.add(s);
                }

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return schedulelist;
    }

    public void removeScheduler(int id){

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_RINGLERR_SCHEDULER, COLUMN_SCHEDULE_ID+ "= '" + id + "'", null);
        db.close();

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

    public void deleteRinglerrUser(String phone_number){

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete(TABLE_RINGLERR_USER, COLUMN_RUSER_NUMBER+ "='" + phone_number + "'", null);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

    }

    public void addRinglerrUserPic(String phone_number, String picture){

        ContentValues values = new ContentValues();
        values.put(COLUMN_RUSER_PIC, picture);
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_RINGLERR_USER, values, COLUMN_RUSER_NUMBER+"='"+phone_number+"'", null);
        db.close();

    }

    public Boolean checkRinglerrUser(String phone){
        String block_number = null;
        Boolean is_ringlerr_user = false;
        SQLiteDatabase db = this.getReadableDatabase();

        try {


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
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            db.close();
        }

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

    public long addMessages(String mgs, String from_phone, String image, String type, String talk_time){

        ContentValues values = new ContentValues();
        values.put(COLUMN_FROM, from_phone);
        values.put(COLUMN_MESSAGE, mgs);
        values.put(COLUMN_IMAGE, image);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_TALK_TIME, talk_time);
        values.put(COLUMN_ISREAD, "0");
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_BONJOUR, null, values);
        db.close();

        return id;

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
        String talk_time;
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
                        talk_time = c.getString(c.getColumnIndexOrThrow(COLUMN_TALK_TIME));
                        id = c.getString(c.getColumnIndexOrThrow(COLUMN_ID));
                        updateMessageRead(id, phone_no);

                        mgslist.add(msg);
                        mgslist.add(image);
                        mgslist.add(type);
                        mgslist.add(talk_time);
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

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return phoneNumber;
        }
        String contactName = phoneNumber;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }
}
