package com.luncher.bounjour.ringlerr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class OtpActivity extends AppCompatActivity {

    private Button btnVerifyOtp;
    private EditText enter_otp;
    MyDbHelper myDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        btnVerifyOtp = (Button) findViewById(R.id.btn_verify_otp);
        enter_otp = (EditText) findViewById(R.id.inputOtp);

        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isVer = verifyOtp();
                View focusView = null;
                enter_otp.setError(null);
                if(isVer){
                    myDbHelper = new MyDbHelper(OtpActivity.this, null, null, 1);
                    myDbHelper.updateOtp();
                    goto_main();
                }else{
                    enter_otp.setError(getString(R.string.error_invalid_otp));
                    focusView = enter_otp;
                }
            }
        });
    }

    private void goto_main() {
        Intent mainintent = new Intent(this, MainActivity.class);
        startActivity(mainintent);
    }

    private boolean verifyOtp() {
        String otp = enter_otp.getText().toString().trim();

        if(otp.equals("123456")){
            return true;
        }else{
            return false;
        }
    }
}
