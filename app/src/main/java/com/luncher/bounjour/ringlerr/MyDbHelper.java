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

import com.luncher.bounjour.ringlerr.model.Blocks;
import com.luncher.bounjour.ringlerr.model.Message;
import com.luncher.bounjour.ringlerr.model.MyRecent;
import com.luncher.bounjour.ringlerr.model.Notification;
import com.luncher.bounjour.ringlerr.model.Reminder;
import com.luncher.bounjour.ringlerr.model.Scheduler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santanu on 19/11/17.
 */

public class MyDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 11;
    private static final String DATABASE_NAME = "bonjour_dial.db";

    private static final String TABLE_BONJOUR = "bonjour_mgs";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FROM = "msg_from";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_TALK_TIME = "talk_time";
    private static final String COLUMN_ISREAD = "is_read";
    private static final String COLUMN_ATTACH = "attach_to";
    private static final String COLUMN_DATETIME = "datetime";

    private static final String TABLE_PROFILE = "ringer_profile";
    private static final String COLUMN_P_ID = "id";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_OTP = "otp";
    private static final String COLUMN_ISVERIFIED = "is_verified";

    private static final String TABLE_BLOCK = "ringlerr_block";
    private static final String COLUMN_BLOCK_ID = "id";
    private static final String COLUMN_NUMBER = "number";

    private static final String TABLE_RINGLERR_USER = "ringlerr_user";
    private static final String COLUMN_RUSER_ID = "id";
    private static final String COLUMN_RUSER_NUMBER = "number";
    private static final String COLUMN_RUSER_PIC = "picture";

    private static final String TABLE_RINGLERR_SCHEDULER = "ringlerr_scheduler";
    private static final String COLUMN_SCHEDULE_ID = "id";
    private static final String COLUMN_SCHEDULE_TYPE = "s_type";
    private static final String COLUMN_SCHEDULE_AGO = "ago";
    private static final String COLUMN_SCHEDULE_NUMBER = "number";
    private static final String COLUMN_SCHEDULE_NAME = "name";
    private static final String COLUMN_SCHEDULE_MSG = "message";
    private static final String COLUMN_SCHEDULE_TIME = "time";
    private static final String COLUMN_SCHEDULE_ISDONE = "is_done";
    private static final String COLUMN_SCHEDULE_ISMANUAL = "is_manual";

    private static final String TABLE_RINGLERR_REMINDER = "ringlerr_reminder";
    private static final String COLUMN_REMINDER_ID = "id";
    private static final String COLUMN_REMINDER_MESSAGE = "message";
    private static final String COLUMN_REMINDER_TIME = "time";
    private static final String COLUMN_REMINDER_SHARED_WITH = "shared_with";
    private static final String COLUMN_REMINDER_KEY = "shared_key";
    private static final String COLUMN_REMINDER_FROM = "from_number";
    private static final String COLUMN_REMINDER_TIME_AGO = "time_ago";
    private static final String COLUMN_REMINDER_RESPONSE = "response";

    private static final String TABLE_CHAT_NOTIFICATION = "ringlerr_chat_notification";
    private static final String COLUMN_NOTIFICATION_ID = "id";
    private static final String COLUMN_NOTIFICATION_NUMBER = "number";
    private static final String COLUMN_NOTIFICATION_MSG = "message";

    private static final String TABLE_SOS_SETTINGS = "ringlerr_sos_settings";
    private static final String COLUMN_SOS_SETTINGS_ID = "id";
    private static final String COLUMN_SOS_SETTINGS_PRIMARY_NUMBER = "primary_number";
    private static final String COLUMN_SOS_SETTINGS_SECONDARY_NUMBER = "secondary_number";
    private static final String COLUMN_SOS_SETTINGS_MSG = "message";

    private static final String TABLE_NOTIFICATIONS = "ringlerr_notifications";
    private static final String COLUMN_NOTIFICATIONS_ID = "id";
    private static final String COLUMN_NOTIFICATIONS_FROM = "from_noti";
    private static final String COLUMN_NOTIFICATIONS_DESCRIPTION = "description";
    private static final String COLUMN_NOTIFICATIONS_TIME = "time";
    private static final String COLUMN_NOTIFICATIONS_TYPE = "type";
    private static final String COLUMN_NOTIFICATIONS_SEEN = "is_seen";
    private static final String COLUMN_NOTIFICATIONS_REMKEY = "remkey";

    private static final String TABLE_CALL_LOGS = "ringlerr_call_logs";
    private static final String COLUMN_CALL_LOGS_ID = "id";
    private static final String COLUMN_CALL_LOGS_NAME = "name";
    private static final String COLUMN_CALL_LOGS_NUMBER = "number";
    private static final String COLUMN_CALL_LOGS_TYPE = "type";
    private static final String COLUMN_CALL_LOGS_TIME = "time";
    private static final String COLUMN_CALL_LOGS_LOGID = "_id";

    private static final String TABLE_CONTACTS = "ringlerr_contacts";
    private static final String COLUMN_CONTACTS_ID = "contact_id";
    private static final String COLUMN_CONTACTID = "ContactId";
    private static final String COLUMN_CONTACTS_NUMBER = "contact_number";
    private static final String COLUMN_CONTACTS_NAME = "contact_name";
    private static final String COLUMN_CONTACTS_TYPE = "contact_type";
    private static final String COLUMN_CONTACTS_EMAIL = "contact_email";
    private static final String COLUMN_CONTACTS_COMPANY = "contact_company";
    private static final String COLUMN_CONTACTS_DEPARTMENT = "contact_department";
    private static final String COLUMN_CONTACTS_JOB = "contact_jobs";
    private static final String COLUMN_CONTACTS_ADDRESS = "contact_address";
    private static final String COLUMN_CONTACTS_WEBSITE = "contact_website";

    private static final String TABLE_CUSTOM_MESSAGE = "custom_messages";
    private static final String COLUMN_CUSTOM_MESSAGE_ID = "id";
    private static final String COLUMN_CUSTOM_MESSAGE_MSG = "custom_message";

    private Context mycontext;

    public MyDbHelper(Context context, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        mycontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String query_table_contact = " Create table "+ TABLE_CONTACTS +" ( "+ COLUMN_CONTACTS_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_CONTACTID +" TEXT, "+ COLUMN_CONTACTS_NUMBER +" TEXT, " + COLUMN_CONTACTS_NAME + " TEXT, " + COLUMN_CONTACTS_TYPE + " TEXT, " + COLUMN_CONTACTS_EMAIL + " TEXT, "+ COLUMN_CONTACTS_COMPANY + " TEXT, "+ COLUMN_CONTACTS_DEPARTMENT + " TEXT, "+ COLUMN_CONTACTS_JOB + " TEXT, "+ COLUMN_CONTACTS_ADDRESS + " TEXT, "+ COLUMN_CONTACTS_WEBSITE + " TEXT );";
        sqLiteDatabase.execSQL(query_table_contact);

        String query = " Create table "+ TABLE_BONJOUR +" ( "+ COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_FROM +" TEXT, "+ COLUMN_MESSAGE +" TEXT, " + COLUMN_IMAGE + " TEXT, " + COLUMN_TYPE + " TEXT, " + COLUMN_TALK_TIME + " TEXT, "+ COLUMN_ATTACH + " TEXT, "+ COLUMN_ISREAD + " TEXT, "+ COLUMN_DATETIME + " TEXT );";
        sqLiteDatabase.execSQL(query);

        String query_profile = " Create table "+ TABLE_PROFILE +" ( "+ COLUMN_P_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_PHONE +" TEXT, "+ COLUMN_OTP +" TEXT, "+ COLUMN_ISVERIFIED + " TEXT );";
        sqLiteDatabase.execSQL(query_profile);

        String query_block = " Create table "+ TABLE_BLOCK +" ( "+ COLUMN_BLOCK_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_NUMBER +" TEXT );";
        sqLiteDatabase.execSQL(query_block);

        String query_ringlerr_scheduler = " Create table "+ TABLE_RINGLERR_SCHEDULER +" ( "+ COLUMN_SCHEDULE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_SCHEDULE_NUMBER +" TEXT, "+ COLUMN_SCHEDULE_TYPE +" TEXT, "+ COLUMN_SCHEDULE_AGO +" TEXT, "+ COLUMN_SCHEDULE_NAME +" TEXT, "+ COLUMN_SCHEDULE_MSG + " TEXT, "+ COLUMN_SCHEDULE_TIME +" TEXT, "+ COLUMN_SCHEDULE_ISDONE + " TEXT, "+COLUMN_SCHEDULE_ISMANUAL + " TEXT );";
        sqLiteDatabase.execSQL(query_ringlerr_scheduler);

        String query_ringlerr_user = " Create table "+ TABLE_RINGLERR_USER +" ( "+ COLUMN_RUSER_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_RUSER_NUMBER +" TEXT );";
        sqLiteDatabase.execSQL(query_ringlerr_user);

        String query_ringlerr_reminder = " Create table "+ TABLE_RINGLERR_REMINDER +" ( "+ COLUMN_REMINDER_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_REMINDER_MESSAGE +" TEXT, "+ COLUMN_REMINDER_TIME +" TEXT, "+ COLUMN_REMINDER_SHARED_WITH +" TEXT, "+COLUMN_REMINDER_KEY+" TEXT, "+COLUMN_REMINDER_FROM+" TEXT, "+COLUMN_REMINDER_TIME_AGO+" TEXT, "+COLUMN_REMINDER_RESPONSE+" TEXT );";
        sqLiteDatabase.execSQL(query_ringlerr_reminder);

        String query_ringlerr_notification = " Create table "+ TABLE_CHAT_NOTIFICATION +" ( "+ COLUMN_NOTIFICATION_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_NOTIFICATION_NUMBER +" TEXT, "+ COLUMN_NOTIFICATION_MSG +" TEXT );";
        sqLiteDatabase.execSQL(query_ringlerr_notification);

        String ringlerr_notification = " Create table "+ TABLE_NOTIFICATIONS +" ( "+ COLUMN_NOTIFICATIONS_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_NOTIFICATIONS_FROM +" TEXT, "+ COLUMN_NOTIFICATIONS_DESCRIPTION +" TEXT, "+ COLUMN_NOTIFICATIONS_TIME +" TEXT, "+ COLUMN_NOTIFICATIONS_TYPE +" TEXT, "+ COLUMN_NOTIFICATIONS_SEEN +" TEXT, "+ COLUMN_NOTIFICATIONS_REMKEY +" TEXT );";
        sqLiteDatabase.execSQL(ringlerr_notification);

        String ringlerr_call_log = " Create table "+ TABLE_CALL_LOGS +" ( "+ COLUMN_CALL_LOGS_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_CALL_LOGS_NAME +" TEXT, "+ COLUMN_CALL_LOGS_NUMBER +" TEXT, "+ COLUMN_CALL_LOGS_TYPE +" TEXT, "+ COLUMN_CALL_LOGS_TIME +" TEXT, "+ COLUMN_CALL_LOGS_LOGID +" TEXT );";
        sqLiteDatabase.execSQL(ringlerr_call_log);

        String query_ringlerr_sos_settings = " Create table "+ TABLE_SOS_SETTINGS +" ( "+ COLUMN_SOS_SETTINGS_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_SOS_SETTINGS_PRIMARY_NUMBER +" TEXT, "+ COLUMN_SOS_SETTINGS_SECONDARY_NUMBER +" TEXT, "+ COLUMN_SOS_SETTINGS_MSG +" TEXT );";
        sqLiteDatabase.execSQL(query_ringlerr_sos_settings);

        String query_custom_messages = " Create table "+ TABLE_CUSTOM_MESSAGE +" ( "+ COLUMN_CUSTOM_MESSAGE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_CUSTOM_MESSAGE_MSG +" TEXT );";
        sqLiteDatabase.execSQL(query_custom_messages);
    }

    public void addSosSetting(String number, String second_number, String message){

        ContentValues values = new ContentValues();
        values.put(COLUMN_SOS_SETTINGS_PRIMARY_NUMBER, number);
        values.put(COLUMN_SOS_SETTINGS_SECONDARY_NUMBER, second_number);
        values.put(COLUMN_SOS_SETTINGS_MSG, message);
        ArrayList settings = getSosSetting();
        SQLiteDatabase db = getWritableDatabase();
        if (!settings.isEmpty()) {
            db.update(TABLE_SOS_SETTINGS, values, COLUMN_SOS_SETTINGS_ID+"=?", new String[] {1+""});
        }else {
            db.insert(TABLE_SOS_SETTINGS, null, values);
        }
        db.close();
    }

    public ArrayList<String> getSosSetting() {
        String id;
        String number;
        String secondary_number;
        String message;
        ArrayList<String> mgslist = new ArrayList<String>();
        String query = "SELECT * FROM " + TABLE_SOS_SETTINGS;
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null);
        int num_row = c.getCount();
        if (c.moveToFirst()) {
            do {
                if(c.getString(c.getColumnIndexOrThrow(COLUMN_SOS_SETTINGS_ID))!=null){
                    id = c.getString(c.getColumnIndexOrThrow(COLUMN_SOS_SETTINGS_ID));
                    number = c.getString(c.getColumnIndexOrThrow(COLUMN_SOS_SETTINGS_PRIMARY_NUMBER));
                    secondary_number = c.getString(c.getColumnIndexOrThrow(COLUMN_SOS_SETTINGS_SECONDARY_NUMBER));
                    message = c.getString(c.getColumnIndexOrThrow(COLUMN_SOS_SETTINGS_MSG));

                    mgslist.add(id);
                    mgslist.add(number);
                    mgslist.add(secondary_number);
                    mgslist.add(message);
                }

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return mgslist;
    }

    void addChatNotification(String number, String message){

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

    int getNotificationCount(){
        String query = "SELECT * FROM " + TABLE_CHAT_NOTIFICATION;
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null);
        int num_row = c.getCount();

        return num_row;
    }

    int getIndivNotificationCount(String phone){
        String query = "SELECT * FROM " + TABLE_CHAT_NOTIFICATION + " WHERE "+COLUMN_NOTIFICATION_NUMBER+"='"+phone+"'";
        int num_row = 0;
        try (SQLiteDatabase db = this.getReadableDatabase()) {

            SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null);
            num_row = c.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return num_row;
    }

    String getNotificationMessage(){

        String notificationMsg = "";

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            SQLiteCursor c = (SQLiteCursor) db.rawQuery("SELECT * FROM " + TABLE_CHAT_NOTIFICATION, null);

            if (c.moveToFirst()) {
                do {
                    if (c.getString(c.getColumnIndexOrThrow(COLUMN_NOTIFICATION_NUMBER)) != null) {
                        String number = c.getString(c.getColumnIndexOrThrow(COLUMN_NOTIFICATION_NUMBER));
                        String message = c.getString(c.getColumnIndexOrThrow(COLUMN_NOTIFICATION_MSG));
                        notificationMsg += getContactName(mycontext, number) + " : " + message + "\n";
                    }

                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return notificationMsg;
    }

    public List<Reminder> getAllReminders(){
        int id;
        String message;
        String reminderKey;
        Long time;
        String shared_with;
        int time_ago;
        String response;
        String from;
        List<Reminder> ReminderList = new ArrayList<>();
        Long crLong = System.currentTimeMillis() - (24 * 7 * 60 * 60 * 1000);
        Long prLong = System.currentTimeMillis();

        String query = "SELECT * FROM " + TABLE_RINGLERR_REMINDER  + " WHERE "+COLUMN_REMINDER_TIME+" >= "+crLong+" ORDER BY "+COLUMN_REMINDER_TIME+" DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        try (SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null)) {
            if (c.moveToFirst()) {
                do {
                    if (c.getString(c.getColumnIndexOrThrow(COLUMN_REMINDER_ID)) != null) {
                        id = c.getInt(c.getColumnIndexOrThrow(COLUMN_REMINDER_ID));
                        message = c.getString(c.getColumnIndexOrThrow(COLUMN_REMINDER_MESSAGE));
                        time = c.getLong(c.getColumnIndexOrThrow(COLUMN_REMINDER_TIME));
                        shared_with = c.getString(c.getColumnIndexOrThrow(COLUMN_REMINDER_SHARED_WITH));
                        time_ago = c.getInt(c.getColumnIndexOrThrow(COLUMN_REMINDER_TIME_AGO));
                        response = c.getString(c.getColumnIndexOrThrow(COLUMN_REMINDER_RESPONSE));
                        from = c.getString(c.getColumnIndexOrThrow(COLUMN_REMINDER_FROM));
                        reminderKey = c.getString(c.getColumnIndexOrThrow(COLUMN_REMINDER_KEY));

                        Reminder s = new Reminder();
                        s.setId(id);
                        s.setMessage(message);
                        s.setTime(time);
                        s.setShared_with(shared_with);
                        s.setRemindAgo(time_ago);
                        s.setIs_accepted(response);
                        s.setFrom(from);
                        s.setReminderKey(reminderKey);

                        ReminderList.add(s);
                    }

                } while (c.moveToNext());
            }
        }
        db.close();

        return ReminderList;
    }

    public List<Reminder> getReminderByKey(String key){
        int id;
        String message;
        Long time;
        String shared_with;
        int time_ago;
        String response;
        String from;
        List<Reminder> ReminderList = new ArrayList<>();
        Long crLong = System.currentTimeMillis();

        String query = "SELECT * FROM " + TABLE_RINGLERR_REMINDER + " WHERE "+COLUMN_REMINDER_KEY+" = '"+key+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        try (SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null)) {
            if (c.moveToFirst()) {
                do {
                    if (c.getString(c.getColumnIndexOrThrow(COLUMN_REMINDER_ID)) != null) {
                        id = c.getInt(c.getColumnIndexOrThrow(COLUMN_REMINDER_ID));
                        message = c.getString(c.getColumnIndexOrThrow(COLUMN_REMINDER_MESSAGE));
                        time = c.getLong(c.getColumnIndexOrThrow(COLUMN_REMINDER_TIME));
                        shared_with = c.getString(c.getColumnIndexOrThrow(COLUMN_REMINDER_SHARED_WITH));
                        time_ago = c.getInt(c.getColumnIndexOrThrow(COLUMN_REMINDER_TIME_AGO));
                        response = c.getString(c.getColumnIndexOrThrow(COLUMN_REMINDER_RESPONSE));
                        from = c.getString(c.getColumnIndexOrThrow(COLUMN_REMINDER_FROM));

                        Reminder s = new Reminder();
                        s.setId(id);
                        s.setMessage(message);
                        s.setTime(time);
                        s.setShared_with(shared_with);
                        s.setRemindAgo(time_ago);
                        s.setIs_accepted(response);

                        ReminderList.add(s);
                    }

                } while (c.moveToNext());
            }
        }
        db.close();

        return ReminderList;
    }

    public List<Reminder> getAllUpcomingReminder(){
        int id;
        String message;
        Long time;
        String shared_with;
        int time_ago;
        String response;
        String from;
        List<Reminder> ReminderList = new ArrayList<>();
        Long crLong = System.currentTimeMillis();

        String query = "SELECT * FROM " + TABLE_RINGLERR_REMINDER + " WHERE "+COLUMN_REMINDER_TIME+" >= "+crLong;
        SQLiteDatabase db = this.getReadableDatabase();
        try (SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null)) {
            if (c.moveToFirst()) {
                do {
                    if (c.getString(c.getColumnIndexOrThrow(COLUMN_REMINDER_ID)) != null) {
                        id = c.getInt(c.getColumnIndexOrThrow(COLUMN_REMINDER_ID));
                        message = c.getString(c.getColumnIndexOrThrow(COLUMN_REMINDER_MESSAGE));
                        time = c.getLong(c.getColumnIndexOrThrow(COLUMN_REMINDER_TIME));
                        shared_with = c.getString(c.getColumnIndexOrThrow(COLUMN_REMINDER_SHARED_WITH));
                        time_ago = c.getInt(c.getColumnIndexOrThrow(COLUMN_REMINDER_TIME_AGO));
                        response = c.getString(c.getColumnIndexOrThrow(COLUMN_REMINDER_RESPONSE));
                        from = c.getString(c.getColumnIndexOrThrow(COLUMN_REMINDER_FROM));

                        Reminder s = new Reminder();
                        s.setId(id);
                        s.setMessage(message);
                        s.setTime(time);
                        s.setShared_with(shared_with);
                        s.setRemindAgo(time_ago);
                        s.setIs_accepted(response);

                        ReminderList.add(s);
                    }

                } while (c.moveToNext());
            }
        }
        db.close();

        return ReminderList;
    }

    public Boolean isReminderExists(String reminderKey){
        Boolean is_shown = false;

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            try(SQLiteCursor c = (SQLiteCursor) db.rawQuery("SELECT * FROM " + TABLE_RINGLERR_REMINDER + " WHERE " + COLUMN_REMINDER_KEY + " = '" + reminderKey + "' LIMIT 1", null)) {
                if (c.moveToFirst()) {
                    do {
                        if (c.getString(c.getColumnIndexOrThrow(COLUMN_REMINDER_KEY)) != null) {
                            is_shown = true;
                        }

                    } while (c.moveToNext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return is_shown;
    }

    public long addReminder(String message, Long time, String shared_with, String key, int time_ago, int resposne, String from){

        String bnumber = checkBlockNumber(from);
        Boolean isReminderExists = isReminderExists(key);

        if(isReminderExists || !bnumber.equals("null")){
            return 0;
        }else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_REMINDER_MESSAGE, message);
            values.put(COLUMN_REMINDER_TIME, time);
            values.put(COLUMN_REMINDER_SHARED_WITH, shared_with);
            values.put(COLUMN_REMINDER_KEY, key);
            values.put(COLUMN_REMINDER_TIME_AGO, time_ago);
            values.put(COLUMN_REMINDER_RESPONSE, resposne);
            values.put(COLUMN_REMINDER_FROM, from);
            SQLiteDatabase db = getWritableDatabase();
            long id = db.insert(TABLE_RINGLERR_REMINDER, null, values);
            db.close();

            return id;
        }
    }

    public void removeReminder(String key){

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_RINGLERR_REMINDER, COLUMN_REMINDER_KEY+ "= '" + key + "'", null);
        db.close();

    }

    public int updateReminder(String key, String message, Long time, String shared_with, int time_ago){

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMINDER_MESSAGE, message);
        values.put(COLUMN_REMINDER_TIME, time);
        values.put(COLUMN_REMINDER_SHARED_WITH, shared_with);
        values.put(COLUMN_REMINDER_TIME_AGO, time_ago);
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_RINGLERR_REMINDER, values, COLUMN_REMINDER_KEY+"='"+key+"'", null);
        db.close();

        int id = 0;
        String query = "SELECT * FROM " + TABLE_RINGLERR_REMINDER + " WHERE "+COLUMN_REMINDER_KEY+"='"+key+"'";
        db = this.getReadableDatabase();
        try (SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null)) {

            if (c.moveToFirst()) {
                id = c.getInt(c.getColumnIndex(COLUMN_REMINDER_ID));
            }
        }
        db.close();
        return id;

    }

    public int updateReminderAccRej(String key, String message, Long time, String shared_with, int time_ago, String resposne){

        ContentValues values = new ContentValues();
        values.put(COLUMN_REMINDER_MESSAGE, message);
        values.put(COLUMN_REMINDER_TIME, time);
        values.put(COLUMN_REMINDER_SHARED_WITH, shared_with);
        values.put(COLUMN_REMINDER_TIME_AGO, time_ago);
        values.put(COLUMN_REMINDER_RESPONSE, resposne);
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_RINGLERR_REMINDER, values, COLUMN_REMINDER_KEY+"='"+key+"'", null);
        db.close();

        int id = 0;
        String query = "SELECT * FROM " + TABLE_RINGLERR_REMINDER + " WHERE "+COLUMN_REMINDER_KEY+"='"+key+"'";
        db = this.getReadableDatabase();
        try (SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null)) {

            if (c.moveToFirst()) {
                id = c.getInt(c.getColumnIndex(COLUMN_REMINDER_ID));
            }
        }
        db.close();
        return id;

    }

    public long addContact(int contactId, String phone_number, String name, String contact_type, String contact_email, String company, String department, String jobs, String address, String website){

        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACTID, contactId);
        values.put(COLUMN_CONTACTS_NUMBER, phone_number);
        values.put(COLUMN_CONTACTS_NAME, name);
        values.put(COLUMN_CONTACTS_TYPE, contact_type);
        values.put(COLUMN_CONTACTS_EMAIL, contact_email);
        values.put(COLUMN_CONTACTS_COMPANY, company);
        values.put(COLUMN_CONTACTS_DEPARTMENT, department);
        values.put(COLUMN_CONTACTS_JOB, jobs);
        values.put(COLUMN_CONTACTS_ADDRESS, address);
        values.put(COLUMN_CONTACTS_WEBSITE, website);
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_CONTACTS, null, values);
        db.close();

        return id;
    }

    public void updateContact(int contactId, String phone_number, String name, String contact_type, String contact_email, String company, String department, String jobs, String address, String website){

        Boolean is_exist = checkContactPhoneNumber(contactId);
        if(is_exist) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CONTACTS_NUMBER, phone_number);
            values.put(COLUMN_CONTACTS_NAME, name);
            values.put(COLUMN_CONTACTS_TYPE, contact_type);
            values.put(COLUMN_CONTACTS_EMAIL, contact_email);
            values.put(COLUMN_CONTACTS_COMPANY, company);
            values.put(COLUMN_CONTACTS_DEPARTMENT, department);
            values.put(COLUMN_CONTACTS_JOB, jobs);
            values.put(COLUMN_CONTACTS_ADDRESS, address);
            values.put(COLUMN_CONTACTS_WEBSITE, website);
            SQLiteDatabase db = getWritableDatabase();
            db.update(TABLE_CONTACTS, values, COLUMN_CONTACTID + "=" + contactId, null);
            db.close();
        }else{
            addContact(contactId, phone_number, name, contact_type, contact_email, company, department, jobs, address, website);
        }
    }

    public String[] SelectContactData(int contactId) {
        // TODO Auto-generated method stub
        String arrData[] = new String[9];
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            try(SQLiteCursor c = (SQLiteCursor) db.rawQuery("SELECT * FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_CONTACTID + " = " + contactId + " LIMIT 1", null)) {
                if (c.moveToFirst()) {
                    do {
                        if (c.getString(c.getColumnIndexOrThrow(COLUMN_CONTACTID)) != null) {
                            arrData[0] = c.getString(c.getColumnIndexOrThrow(COLUMN_CONTACTS_NUMBER));
                            arrData[1] = c.getString(c.getColumnIndexOrThrow(COLUMN_CONTACTS_NAME));
                            arrData[2] = c.getString(c.getColumnIndexOrThrow(COLUMN_CONTACTS_TYPE));
                            arrData[3] = c.getString(c.getColumnIndexOrThrow(COLUMN_CONTACTS_EMAIL));
                            arrData[4] = c.getString(c.getColumnIndexOrThrow(COLUMN_CONTACTS_COMPANY));
                            arrData[5] = c.getString(c.getColumnIndexOrThrow(COLUMN_CONTACTS_DEPARTMENT));
                            arrData[6] = c.getString(c.getColumnIndexOrThrow(COLUMN_CONTACTS_JOB));
                            arrData[7] = c.getString(c.getColumnIndexOrThrow(COLUMN_CONTACTS_ADDRESS));
                            arrData[8] = c.getString(c.getColumnIndexOrThrow(COLUMN_CONTACTS_WEBSITE));
                        }

                    } while (c.moveToNext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrData;
    }

    public Boolean checkContactPhoneNumber(int contactId){
        Boolean _is_exist = false;

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            try(SQLiteCursor c = (SQLiteCursor) db.rawQuery("SELECT * FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_CONTACTID + " = " + contactId + " LIMIT 1", null)) {
                if (c.moveToFirst()) {
                    do {
                        if (c.getString(c.getColumnIndexOrThrow(COLUMN_CONTACTID)) != null) {
                            _is_exist = true;
                        }

                    } while (c.moveToNext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return _is_exist;
    }
//    public long updateReminderAccepted(String key, String shared_with){
//
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_REMINDER_SHARED_WITH, shared_with);
//        SQLiteDatabase db = getWritableDatabase();
//        long id = db.update(TABLE_RINGLERR_REMINDER, values, COLUMN_REMINDER_KEY+"="+key, null);
//        db.close();
//
//        return id;
//    }

    public void addCustomMessage(String message){
        ContentValues values = new ContentValues();
        values.put(COLUMN_CUSTOM_MESSAGE_MSG, message);
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_CUSTOM_MESSAGE, null, values);
        db.close();
    }

    public ArrayList<String> allCustomMessages(){
        // TODO Auto-generated method stub
        ArrayList<String> arrData = new ArrayList<String>();
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            try(SQLiteCursor c = (SQLiteCursor) db.rawQuery("SELECT * FROM " + TABLE_CUSTOM_MESSAGE +" ORDER BY "+COLUMN_CUSTOM_MESSAGE_ID+" DESC", null)) {
                if (c.moveToFirst()) {
                    do {
                        if (c.getString(c.getColumnIndexOrThrow(COLUMN_CUSTOM_MESSAGE_MSG)) != null) {
                            String massage = c.getString(c.getColumnIndexOrThrow(COLUMN_CUSTOM_MESSAGE_MSG));
                            arrData.add(massage);
                        }

                    } while (c.moveToNext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrData;
    }

    public void  delMessagebyString(String message){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CUSTOM_MESSAGE, COLUMN_CUSTOM_MESSAGE_MSG+ "= '" + message + "'", null);
        db.close();
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

    public long addNotification(String number, String message, String time, int type, String reminderKey){
        //type 1 = chat and message, 2 = scheduler
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTIFICATIONS_FROM, number);
        values.put(COLUMN_NOTIFICATIONS_DESCRIPTION, message);
        values.put(COLUMN_NOTIFICATIONS_TIME, time);
        values.put(COLUMN_NOTIFICATIONS_TYPE, type);
        values.put(COLUMN_NOTIFICATIONS_SEEN, 0);
        values.put(COLUMN_NOTIFICATIONS_REMKEY, reminderKey);
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_NOTIFICATIONS, null, values);
        db.close();

        return id;
    }

    public List<Notification> getAllNotification(){
        String from;
        String description;
        String time;
        String key;
        Integer type;
        Integer seen;
        List<Notification> notificationList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NOTIFICATIONS + " ORDER BY "+COLUMN_NOTIFICATIONS_ID+" DESC LIMIT 50";
        SQLiteDatabase db = this.getReadableDatabase();
        try (SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null)) {
            if (c.moveToFirst()) {
                do {
                    if (c.getString(c.getColumnIndexOrThrow(COLUMN_NOTIFICATIONS_DESCRIPTION)) != null) {
                        from = c.getString(c.getColumnIndexOrThrow(COLUMN_NOTIFICATIONS_FROM));
                        description = c.getString(c.getColumnIndexOrThrow(COLUMN_NOTIFICATIONS_DESCRIPTION));
                        time = c.getString(c.getColumnIndexOrThrow(COLUMN_NOTIFICATIONS_TIME));
                        type = c.getInt(c.getColumnIndexOrThrow(COLUMN_NOTIFICATIONS_TYPE));
                        seen = c.getInt(c.getColumnIndexOrThrow(COLUMN_NOTIFICATIONS_SEEN));
                        key = c.getString(c.getColumnIndexOrThrow(COLUMN_NOTIFICATIONS_REMKEY));

                        Notification s = new Notification();
                        s.setFrom(from);
                        s.setDescription(description);
                        s.setSeen(seen);
                        s.setType(type);
                        s.setSTime(Long.valueOf(time));
                        s.setKey(key);

                        notificationList.add(s);
                    }

                } while (c.moveToNext());
            }
        }
        db.close();

        return notificationList;
    }

    public Boolean checkReminderNotification(String reminderKey){
        Boolean is_shown = false;

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            try(SQLiteCursor c = (SQLiteCursor) db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS + " WHERE " + COLUMN_NOTIFICATIONS_REMKEY + " = '" + reminderKey + "' LIMIT 1", null)) {
                if (c.moveToFirst()) {
                    do {
                        if (c.getString(c.getColumnIndexOrThrow(COLUMN_NOTIFICATIONS_REMKEY)) != null) {
                            is_shown = true;
                        }

                    } while (c.moveToNext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return is_shown;
    }

    public List<Notification> getSearchNotification(String search_string){
        String from;
        String description;
        String time;
        int type;
        Integer seen;
        List<Notification> notificationList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NOTIFICATIONS + " WHERE "+COLUMN_NOTIFICATIONS_DESCRIPTION+" Like '%"+search_string+"%'  ORDER BY "+COLUMN_NOTIFICATIONS_ID+" DESC LIMIT 50";
        SQLiteDatabase db = this.getReadableDatabase();
        try(SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null)) {
            if (c.moveToFirst()) {
                do {
                    if (c.getString(c.getColumnIndexOrThrow(COLUMN_NOTIFICATIONS_DESCRIPTION)) != null) {
                        from = c.getString(c.getColumnIndexOrThrow(COLUMN_NOTIFICATIONS_FROM));
                        description = c.getString(c.getColumnIndexOrThrow(COLUMN_NOTIFICATIONS_DESCRIPTION));
                        time = c.getString(c.getColumnIndexOrThrow(COLUMN_NOTIFICATIONS_TIME));
                        type = c.getInt(c.getColumnIndexOrThrow(COLUMN_NOTIFICATIONS_TYPE));
                        seen = c.getInt(c.getColumnIndexOrThrow(COLUMN_NOTIFICATIONS_SEEN));

                        Notification s = new Notification();
                        s.setFrom(from);
                        s.setDescription(description);
                        s.setSeen(seen);
                        s.setType(type);
                        s.setSTime(Long.valueOf(time));

                        notificationList.add(s);
                    }

                } while (c.moveToNext());
            }
        }
        db.close();

        return notificationList;
    }

    public long addCalllog(String number, String name, Long time, String type, Integer _id){
        //type 1 = chat and message, 2 = scheduler
        ContentValues values = new ContentValues();
        values.put(COLUMN_CALL_LOGS_NUMBER, number);
        values.put(COLUMN_CALL_LOGS_NAME, name);
        values.put(COLUMN_CALL_LOGS_TYPE, type);
        values.put(COLUMN_CALL_LOGS_TIME, time);
        values.put(COLUMN_CALL_LOGS_LOGID, _id);
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_CALL_LOGS, null, values);
        db.close();

        return id;
    }

    public List<MyRecent> getAllCallLog() {
        String number;
        String name;
        String type;
        Long time;
        Integer _id;
        List<MyRecent> callLoglist = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_CALL_LOGS + " ORDER BY "+COLUMN_CALL_LOGS_ID+" DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                if(c.getString(c.getColumnIndexOrThrow(COLUMN_CALL_LOGS_ID))!=null){
                    number = c.getString(c.getColumnIndexOrThrow(COLUMN_CALL_LOGS_NUMBER));
                    name = c.getString(c.getColumnIndexOrThrow(COLUMN_CALL_LOGS_NAME));
                    type = c.getString(c.getColumnIndexOrThrow(COLUMN_CALL_LOGS_TYPE));
                    time = c.getLong(c.getColumnIndexOrThrow(COLUMN_CALL_LOGS_TIME));
                    _id = c.getInt(c.getColumnIndexOrThrow(COLUMN_CALL_LOGS_LOGID));

                    MyRecent s = new MyRecent();
                    s.setNumber(number);
                    s.setName(name);
                    s.setType(type);
                    s.setTime(time);
                    s.set_id(_id);

                    callLoglist.add(s);
                }

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return callLoglist;
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
        List<Scheduler> schedulelist = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_RINGLERR_SCHEDULER + " ORDER BY "+COLUMN_SCHEDULE_ID+" DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null);
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

    public List<Scheduler> getAllUpcomingScheduler(){
        String number;
        String name;
        String type;
        String ago;
        String message;
        String time;
        String is_done;
        String is_manual;
        Integer id;
        List<Scheduler> schedulelist = new ArrayList<>();
        Long crLong = System.currentTimeMillis();

        String query = "SELECT * FROM " + TABLE_RINGLERR_SCHEDULER + " WHERE "+COLUMN_SCHEDULE_TIME+" >= "+crLong;
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null);
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

//    public void addProfile(String otp, String from_phone){
//
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_PHONE, from_phone);
//        values.put(COLUMN_OTP, otp);
//        values.put(COLUMN_ISVERIFIED, "0");
//        SQLiteDatabase db = getWritableDatabase();
//        db.insert(TABLE_PROFILE, null, values);
//        db.close();
//
//    }

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

//    public void addRinglerrUserPic(String phone_number, String picture){
//
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_RUSER_PIC, picture);
//        SQLiteDatabase db = getWritableDatabase();
//        db.update(TABLE_RINGLERR_USER, values, COLUMN_RUSER_NUMBER+"='"+phone_number+"'", null);
//        db.close();
//
//    }

    public Boolean checkRinglerrUser(String phone){
        Boolean is_ringlerr_user = false;

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            try(SQLiteCursor c = (SQLiteCursor) db.rawQuery("SELECT * FROM " + TABLE_RINGLERR_USER + " WHERE " + COLUMN_RUSER_NUMBER + " = '" + phone + "' LIMIT 1", null)) {
                if (c.moveToFirst()) {
                    do {
                        if (c.getString(c.getColumnIndexOrThrow(COLUMN_RUSER_NUMBER)) != null) {
                            is_ringlerr_user = true;
                        }

                    } while (c.moveToNext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return is_ringlerr_user;
    }

    public List<Blocks> getAllBlocks(){
        String phone_no;
        List<Blocks> blockLists = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_BLOCK + " ORDER BY "+COLUMN_NUMBER+" DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        try (SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null)) {
            if (c.moveToFirst()) {
                do {
                    if (c.getString(c.getColumnIndexOrThrow(COLUMN_NUMBER)) != null) {
                        phone_no = c.getString(c.getColumnIndexOrThrow(COLUMN_NUMBER));

                        Blocks s = new Blocks();
                        s.setBlock_no(phone_no);

                        blockLists.add(s);
                    }

                } while (c.moveToNext());
            }
        }
        db.close();
        return blockLists;
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
        try(SQLiteCursor c = (SQLiteCursor) db.rawQuery("SELECT * FROM " + TABLE_BLOCK + " WHERE "+COLUMN_NUMBER+" = '"+phone+"' LIMIT 1", null)) {

            if (c.moveToFirst()) {
                do {
                    if (c.getString(c.getColumnIndexOrThrow(COLUMN_NUMBER)) != null) {
                        block_number = c.getString(c.getColumnIndexOrThrow(COLUMN_NUMBER));
                    }

                } while (c.moveToNext());
            }
        }
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

    public long addMessages(String mgs, String from_phone, String image, String type, String talk_time, Long datetime, String attach_to){

        ContentValues values = new ContentValues();
        values.put(COLUMN_FROM, from_phone);
        values.put(COLUMN_MESSAGE, mgs);
        values.put(COLUMN_IMAGE, image);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_TALK_TIME, talk_time);
        values.put(COLUMN_ISREAD, "0");
        values.put(COLUMN_DATETIME, datetime);
        values.put(COLUMN_ATTACH, attach_to);
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

    public void removeMessage(int id){

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_BONJOUR, COLUMN_ID+ "= " + id, null);
        db.close();

    }

    public List<Message> getAllMessages(String attach_no){

        int id;
        String from;
        String to;
        String message;
        String image;
        String type;
        String seen;
        String datetime;
        String talk_time;
        String mPhone = getMyPhoneNo();
        List<Message> messageList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_BONJOUR + " WHERE "+COLUMN_ATTACH+" = '"+attach_no+"' ORDER BY "+COLUMN_ID+" DESC LIMIT 50";
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteCursor c = (SQLiteCursor) db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                if(c.getString(c.getColumnIndexOrThrow(COLUMN_MESSAGE))!=null){
                    id = c.getInt(c.getColumnIndexOrThrow(COLUMN_ID));
                    from = c.getString(c.getColumnIndexOrThrow(COLUMN_FROM));
                    to = mPhone;
                    message = c.getString(c.getColumnIndexOrThrow(COLUMN_MESSAGE));
                    image = c.getString(c.getColumnIndexOrThrow(COLUMN_IMAGE));
                    type = c.getString(c.getColumnIndexOrThrow(COLUMN_TYPE));
                    talk_time = c.getString(c.getColumnIndexOrThrow(COLUMN_TALK_TIME));
                    seen = c.getString(c.getColumnIndexOrThrow(COLUMN_ISREAD));
                    datetime = c.getString(c.getColumnIndexOrThrow(COLUMN_DATETIME));

                    if(from.equals(attach_no) && type.equals("snap")) {
                       continue;
                    }else{
                        Message s = new Message();
                        s.setId(id);
                        s.setFrom(from);
                        s.setTo(to);
                        s.setMessage(message);
                        s.setSeen(seen);
                        s.setImage(image);
                        s.setType(type);
                        s.setTalk_time(talk_time);
                        s.setDateAndTime(datetime);

                        messageList.add(s);
                    }
                }

            } while (c.moveToNext());
        }
        c.close();
        db.close();

        return messageList;
    }

//    public String getIsProfileVerified() {
//        String profile = "0";
//        SQLiteDatabase db = this.getReadableDatabase();
//        SQLiteCursor c = (SQLiteCursor) db.rawQuery("SELECT * FROM " + TABLE_PROFILE + " LIMIT 1", null);
//
//        if (c.moveToFirst()) {
//            do {
//                if(c.getString(c.getColumnIndexOrThrow(COLUMN_ISVERIFIED))!=null){
//                    profile = c.getString(c.getColumnIndexOrThrow(COLUMN_ISVERIFIED));
//                }
//
//            } while (c.moveToNext());
//        }
//        c.close();
//        db.close();
//        return profile;
//    }

    private String getMyPhoneNo() {
        String myPhoneNo = "";
        SQLiteDatabase db = this.getReadableDatabase();
        try(SQLiteCursor c = (SQLiteCursor) db.rawQuery("SELECT * FROM " + TABLE_PROFILE + " LIMIT 1", null)){
            if (c.moveToFirst()) {
                do {
                    if (c.getString(c.getColumnIndexOrThrow(COLUMN_PHONE)) != null) {
                        myPhoneNo = c.getString(c.getColumnIndexOrThrow(COLUMN_PHONE));
                    }

                } while (c.moveToNext());
            }
        }
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
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            String query_ringlerr_sos_settings = " Create table " + TABLE_SOS_SETTINGS + " ( " + COLUMN_SOS_SETTINGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_SOS_SETTINGS_PRIMARY_NUMBER + " TEXT, " + COLUMN_SOS_SETTINGS_SECONDARY_NUMBER + " TEXT, " + COLUMN_SOS_SETTINGS_MSG + " TEXT );";
            sqLiteDatabase.execSQL(query_ringlerr_sos_settings);
        }
        if (oldVersion < 3) {

            String ringlerr_notification = " Create table "+ TABLE_NOTIFICATIONS +" ( "+ COLUMN_NOTIFICATIONS_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_NOTIFICATIONS_FROM +" TEXT, "+ COLUMN_NOTIFICATIONS_DESCRIPTION +" TEXT, "+ COLUMN_NOTIFICATIONS_TIME +" TEXT, "+ COLUMN_NOTIFICATIONS_TYPE +" TEXT, "+ COLUMN_NOTIFICATIONS_SEEN +" TEXT , "+ COLUMN_NOTIFICATIONS_REMKEY +" TEXT );";
            sqLiteDatabase.execSQL(ringlerr_notification);
        }

        if (5 > oldVersion) {
            sqLiteDatabase.execSQL("ALTER TABLE "+TABLE_BONJOUR+" ADD COLUMN "+COLUMN_DATETIME+" TEXT DEFAULT 0");
            sqLiteDatabase.execSQL("ALTER TABLE "+TABLE_BONJOUR+" ADD COLUMN "+COLUMN_ATTACH+" TEXT DEFAULT 0");
        }

        if (6 > oldVersion && oldVersion >= 3) {
            sqLiteDatabase.execSQL("ALTER TABLE "+TABLE_NOTIFICATIONS+" ADD COLUMN "+COLUMN_NOTIFICATIONS_TYPE+" TEXT DEFAULT 0");
        }

        if (7 > oldVersion) {
            String ringlerr_call_log = " Create table "+ TABLE_CALL_LOGS +" ( "+ COLUMN_CALL_LOGS_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_CALL_LOGS_NAME +" TEXT, "+ COLUMN_CALL_LOGS_NUMBER +" TEXT, "+ COLUMN_CALL_LOGS_TYPE +" TEXT, "+ COLUMN_CALL_LOGS_TIME +" TEXT, "+ COLUMN_CALL_LOGS_LOGID +" TEXT );";
            sqLiteDatabase.execSQL(ringlerr_call_log);
        }

        if (8 > oldVersion) {
            if(oldVersion >= 3) {
                sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_NOTIFICATIONS + " ADD COLUMN " + COLUMN_NOTIFICATIONS_REMKEY + " TEXT DEFAULT 0");
            }
            sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_RINGLERR_REMINDER + " ADD COLUMN " + COLUMN_REMINDER_TIME_AGO + " TEXT DEFAULT 0");
            sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_RINGLERR_REMINDER + " ADD COLUMN " + COLUMN_REMINDER_FROM + " TEXT DEFAULT 0");
        }

        if (9 > oldVersion) {
            sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_RINGLERR_REMINDER + " ADD COLUMN " + COLUMN_REMINDER_RESPONSE + " TEXT DEFAULT 0");
        }

        if (10 > oldVersion) {
            String query_table_contact = " Create table "+ TABLE_CONTACTS +" ( "+ COLUMN_CONTACTS_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_CONTACTID +" TEXT, "+ COLUMN_CONTACTS_NUMBER +" TEXT, " + COLUMN_CONTACTS_NAME + " TEXT, " + COLUMN_CONTACTS_TYPE + " TEXT, " + COLUMN_CONTACTS_EMAIL + " TEXT, "+ COLUMN_CONTACTS_COMPANY + " TEXT, "+ COLUMN_CONTACTS_DEPARTMENT + " TEXT, "+ COLUMN_CONTACTS_JOB + " TEXT, "+ COLUMN_CONTACTS_ADDRESS + " TEXT, "+ COLUMN_CONTACTS_WEBSITE + " TEXT );";
            sqLiteDatabase.execSQL(query_table_contact);
        }

        if(11 > oldVersion){
            String query_custom_messages = " Create table "+ TABLE_CUSTOM_MESSAGE +" ( "+ COLUMN_CUSTOM_MESSAGE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_CUSTOM_MESSAGE_MSG +" TEXT );";
            sqLiteDatabase.execSQL(query_custom_messages);
        }

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
