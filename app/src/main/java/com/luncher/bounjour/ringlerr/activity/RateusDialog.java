package com.luncher.bounjour.ringlerr.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.luncher.bounjour.ringlerr.R;

public class RateusDialog {
    final String APP_TITLE = "Ringlerr";
    final String APP_PNAME = "com.luncher.bounjour.ringlerr";

    public void showDialog(final Activity activity){
        final Dialog dialog = new Dialog(activity);

        dialog.setTitle("Rate "+ APP_TITLE);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.rate_us);

        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText("If you enjoy using " + APP_TITLE + ", please take a moment to rate it. Thanks for your support!");

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        Button no_thanks = (Button) dialog.findViewById(R.id.no_thanks);

        dialogButton.setText("Rate " + APP_TITLE);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                dialog.dismiss();
            }
        });

        no_thanks.setText("No, Thanks");
        no_thanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
