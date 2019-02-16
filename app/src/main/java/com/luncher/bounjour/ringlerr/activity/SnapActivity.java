package com.luncher.bounjour.ringlerr.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.asksira.bsimagepicker.BSImagePicker;
import com.asksira.bsimagepicker.Utils;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class SnapActivity extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.OnMultiImageSelectedListener {

    BSImagePicker singleSelectionPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        singleSelectionPicker = new BSImagePicker.Builder("com.luncher.bounjour.ringlerr.fileprovider")
                .setSpanCount(3) //Default: 3. This is the number of columns
                .setGridSpacing(Utils.dp2px(2)) //Default: 2dp. Remember to pass in a value in pixel.
                .setPeekHeight(Utils.dp2px(800)) //Default: 360dp. This is the initial height of the dialog.
                //.hideGalleryTile() //Default: show. Set this if you don't want to further let user select from a gallery app. In such case, I suggest you to set maximum displaying images to Integer.MAX_VALUE.
                .setTag("snap_picker")//Default: true. Set this if you do not want the picker to dismiss right after selection. But then you will have to dismiss by yourself.
                .build();

        FragmentManager fragMgr = getSupportFragmentManager();
        singleSelectionPicker.show(fragMgr, "picker");

    }

    @Override
    public void onResume(){
        super.onResume();

        singleSelectionPicker.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //do whatever you want when dialog is dismissed
                finish();;
            }
        });
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed(); commented this line in order to disable back press
        //Write your code here

    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {

    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        Intent data = new Intent();
        data.putExtra("POS_SNAP", uri);
        setResult(Activity.RESULT_OK, data);

        finish();
    }



}
