package com.luncher.bounjour.ringlerr.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.luncher.bounjour.ringlerr.MyOutgoingCustomDialog;
import com.luncher.bounjour.ringlerr.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class DialpadActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private ViewHolder mViewHolder;

    public DialpadActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialpad);
        mViewHolder = new ViewHolder();
    }

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
                    //mViewHolder.phoneNumber.setCursorVisible(false);
                }
            }
        } else { // remove the whole selection
            cur = cur.subSequence(0, start).toString() + cur.subSequence(end, cur.length()).toString();
            mViewHolder.phoneNumber.setText(cur);
            mViewHolder.phoneNumber.setSelection(end - (end - start));
            if (cur.length() == 0) {
                //mViewHolder.phoneNumber.setCursorVisible(false);
            }
        }
    }

    private void onCharacterPressed(char digit) {
        CharSequence cur = mViewHolder.phoneNumber.getText();

        int start = mViewHolder.phoneNumber.getSelectionStart();
        int end = mViewHolder.phoneNumber.getSelectionEnd();
        int len = cur.length();


        if (cur.length() == 0) {
            //mViewHolder.phoneNumber.setCursorVisible(false);
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

        final Intent intent1 = new Intent(DialpadActivity.this, MyOutgoingCustomDialog.class);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
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
