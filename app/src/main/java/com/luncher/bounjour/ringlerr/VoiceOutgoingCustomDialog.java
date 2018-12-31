package com.luncher.bounjour.ringlerr;

/**
 * Created by santanu on 11/11/17.
 */

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.UUID;


public class VoiceOutgoingCustomDialog extends Activity {
    TextView name;
    TextView phone;
    TextView counter;
    String phone_no;
    String message;
    String image = "none";
    ImageButton dialog_ok;


    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<String> texts = new ArrayList<>();
        String projectId = "";
        String sessionId = UUID.randomUUID().toString();
        String languageCode = "en-IN";
        projectId = "dailer-f8664";
        texts.add("hello");

        try {


        } catch (Exception e) {
            System.out.println("Usage:");
            System.out.println("mvn exec:java -DDetectIntentTexts "
                    + "-Dexec.args=\"--projectId PROJECT_ID --sessionId SESSION_ID "
                    + "'hello' 'book a meeting room' 'Mountain View' 'tomorrow' '10 am' '2 hours' "
                    + "'10 people' 'A' 'yes'\"\n");

            System.out.println("Commands: text");
            System.out.println("\t--projectId <projectId> - Project/Agent Id");
            System.out.println("\tText: \"hello\" \"book a meeting room\" \"Mountain View\" \"tomorrow\" "
                    + "\"10am\" \"2 hours\" \"10 people\" \"A\" \"yes\"");
            System.out.println("Optional Commands:");
            System.out.println("\t--languageCode <languageCode> - Language Code of the query (Defaults "
                    + "to \"en-US\".)");
            System.out.println("\t--sessionId <sessionId> - Identifier of the DetectIntent session "
                    + "(Defaults to a random UUID.)");
        }


    }



}

