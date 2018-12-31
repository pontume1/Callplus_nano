package com.luncher.bounjour.ringlerr.activity;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.luncher.bounjour.ringlerr.R;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;

public class editContact extends AppCompatActivity {

    private EditText displayNameEditor;
    private EditText phoneNumberEditor;
    private String phone;
    private String name;
    private Integer contact_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phone_contact);

        setTitle("Edit Contact");

        displayNameEditor = (EditText)findViewById(R.id.add_phone_contact_display_name);

        phoneNumberEditor = (EditText)findViewById(R.id.add_phone_contact_number);

        phone = getIntent().getExtras().getString("phone");
        name = getIntent().getExtras().getString("name");
        contact_id = getContactIDFromNumber(phone);
        //contact_id = getRawContactIdByName(name);

        displayNameEditor.setText(name);
        phoneNumberEditor.setText(phone);

        // Initialize phone type dropdown spinner.
        final Spinner phoneTypeSpinner = (Spinner)findViewById(R.id.add_phone_contact_type);
        String phoneTypeArr[] = {"Mobile", "Home", "Work"};
        ArrayAdapter<String> phoneTypeSpinnerAdaptor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, phoneTypeArr);
        phoneTypeSpinner.setAdapter(phoneTypeSpinnerAdaptor);

        // Click this button to save user input phone contact info.
        Button savePhoneContactButton = (Button) findViewById(R.id.add_phone_contact_save_button);
        Button cancle_button = (Button)findViewById(R.id.cancle_button);
        savePhoneContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ContentResolver contentResolver = getContentResolver();

                if(contact_id > -1) {
                    phone = phoneNumberEditor.getText().toString();
                    name = displayNameEditor.getText().toString();
                    String phoneTypeStr = (String)phoneTypeSpinner.getSelectedItem();

                    int phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;

                    if("home".equalsIgnoreCase(phoneTypeStr))
                    {
                        phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
                    }else if("mobile".equalsIgnoreCase(phoneTypeStr))
                    {
                        phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
                    }else if("work".equalsIgnoreCase(phoneTypeStr))
                    {
                        phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
                    }
                    // Update mobile phone number.
                    //updatePhoneNumber(contentResolver, contact_id, phoneContactType, phone, name);

                    Boolean ret_d = updateContact(name, phone, "", contact_id.toString());
                    if(ret_d){

                    }
                }

                Toast.makeText(getApplicationContext(),"Contact Updated" , Toast.LENGTH_LONG).show();

                finish();
            }
        });

        cancle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /* Update phone number with raw contact id and phone type.*/
    private void updatePhoneNumber(ContentResolver contentResolver, long rawContactId, int phoneType, String newPhoneNumber, String newName)
    {
        // Create content values object.
        ContentValues contentValues = new ContentValues();

        // Put new phone number value.
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNumber);

        // Put new name value.
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, newName);

        // Create query condition, query with the raw contact id.
        StringBuffer whereClauseBuf = new StringBuffer();

        // Specify the update contact id.
        whereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID);
        whereClauseBuf.append(" = ");
        whereClauseBuf.append(rawContactId);

        // Specify the row data mimetype to phone mimetype( vnd.android.cursor.item/phone_v2 )
        whereClauseBuf.append(" and ");
        whereClauseBuf.append(ContactsContract.Data.MIMETYPE);
        whereClauseBuf.append(" = '");
        String mimetype = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
        whereClauseBuf.append(mimetype);
        whereClauseBuf.append("'");

        // Specify phone type.
        whereClauseBuf.append(" and ");
        whereClauseBuf.append(ContactsContract.CommonDataKinds.Phone.TYPE);
        whereClauseBuf.append(" = ");
        whereClauseBuf.append(phoneType);

        // Update phone info through Data uri.Otherwise it may throw java.lang.UnsupportedOperationException.
        Uri dataUri = ContactsContract.Data.CONTENT_URI;

        String ec = whereClauseBuf.toString();

        // Get update data count.
        int updateCount = contentResolver.update(dataUri, contentValues, whereClauseBuf.toString(), null);
    }

    public int getContactIDFromNumber(String contactNumber)
    {
        int phoneContactID = new Random().nextInt();
        if(contactNumber == null || contactNumber.equals("")){
            phoneContactID = -1;
        }else {
            contactNumber = Uri.encode(contactNumber);
            Cursor contactLookupCursor = getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, contactNumber), new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
            while (contactLookupCursor.moveToNext()) {
                phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            contactLookupCursor.close();
        }

        return phoneContactID;
    }

    // ListPhoneContactsActivity use this method to start this activity.
    public static void start(Context context)
    {
        Intent intent = new Intent(context, editContact.class);
        context.startActivity(intent);
    }

    /* Get raw contact id by contact given name and family name.
     *  Return raw contact id.
     * */
    private long getRawContactIdByName(String contactName)
    {
        ContentResolver contentResolver = getContentResolver();

        // Query raw_contacts table by display name field ( given_name family_name ) to get raw contact id.

        // Create query column array.
        String queryColumnArr[] = {ContactsContract.RawContacts._ID};

        // Create where condition clause.
        String whereClause = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + " = '" + contactName + "'";

        // Query raw contact id through RawContacts uri.
        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;

        // Return the query cursor.
        Cursor cursor = contentResolver.query(rawContactUri, queryColumnArr, whereClause, null, null);

        long rawContactId = -1;

        if(cursor!=null)
        {
            // Get contact count that has same display name, generally it should be one.
            int queryResultCount = cursor.getCount();
            // This check is used to avoid cursor index out of bounds exception. android.database.CursorIndexOutOfBoundsException
            if(queryResultCount > 0)
            {
                // Move to the first row in the result cursor.
                cursor.moveToFirst();
                // Get raw_contact_id.
                rawContactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
            }
        }

        return rawContactId;
    }

    /**
     * @param name name of the contact
     * @param number mobile phone number of contact
     * @param email work email address of contact
     * @param ContactId id of the contact which you want to update
     * @return true if contact is updated successfully<br/>
     *         false if contact is not updated <br/>
     *         false if phone number contains any characters(It should contain only digits)<br/>
     *         false if email Address is invalid <br/><br/>
     *
     *  You can pass any one among the 3 parameters to update a contact.Passing all three parameters as <b>null</b> will not update the contact
     *  <br/><br/><b>Note: </b>This method requires permission <b>android.permission.WRITE_CONTACTS</b><br/>
     */

    public boolean updateContact(String name, String number, String email,String ContactId)
    {
        boolean success = true;
        String phnumexp = "^[0-9]*$";

        try
        {
            name = name.trim();
            email = email.trim();
            number = number.trim();

            if(name.equals("")&&number.equals("")&&email.equals(""))
            {
                success = false;
            }
            else if((number.equals("")) )
            {
                success = false;
            }
            else if( (!email.equals("")) && (!isEmailValid(email)) )
            {
                success = false;
            }
            else
            {
                ContentResolver contentResolver  = getContentResolver();

                String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";

                String[] emailParams = new String[]{ContactId, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};
                String[] nameParams = new String[]{ContactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
                String[] numberParams = new String[]{ContactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};

                ArrayList<ContentProviderOperation> ops = new ArrayList<android.content.ContentProviderOperation>();

                if(!email.equals(""))
                {
                    ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                            .withSelection(where,emailParams)
                            .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                            .build());
                }

                if(!name.equals(""))
                {
                    ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                            .withSelection(where,nameParams)
                            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                            .build());
                }

                if(!number.equals(""))
                {

                    ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                            .withSelection(where,numberParams)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                            .build());
                }
                contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    private boolean isEmailValid(String email)
    {
        String emailAddress = email.toString().trim();
        if (emailAddress == null)
            return false;
        else if (emailAddress.equals(""))
            return false;
        else if (emailAddress.length() <= 6)
            return false;
        else {
            String expression = "^[a-z][a-z|0-9|]*([_][a-z|0-9]+)*([.][a-z|0-9]+([_][a-z|0-9]+)*)?@[a-z][a-z|0-9|]*\\.([a-z][a-z|0-9]*(\\.[a-z][a-z|0-9]*)?)$";
            CharSequence inputStr = emailAddress;
            Pattern pattern = Pattern.compile(expression,
                    Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches())
                return true;
            else
                return false;
        }
    }

    private boolean match(String stringToCompare,String regularExpression)
    {
        boolean success = false;
        Pattern pattern = Pattern.compile(regularExpression);
        Matcher matcher = pattern.matcher(stringToCompare);
        if(matcher.matches())
            success =true;
        return success;
    }
}

