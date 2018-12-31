package com.luncher.bounjour.ringlerr.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.luncher.bounjour.ringlerr.R;

public class FollowUsDialog {
    final String APP_TITLE = "Ringlerr";
    Activity myActivity;

    public void showDialog(final Activity activity){
        final Dialog dialog = new Dialog(activity);
        myActivity = activity;

        dialog.setTitle("Rate "+ APP_TITLE);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.follow_us);

        ImageView instagram = (ImageView) dialog.findViewById(R.id.instagram);
        ImageView twitter = (ImageView) dialog.findViewById(R.id.twitter);
        ImageView facebook = (ImageView) dialog.findViewById(R.id.facebook);
        ImageView linkedin = (ImageView) dialog.findViewById(R.id.linkedin);
        ImageView youtube = (ImageView) dialog.findViewById(R.id.youtube);

        instagram.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToUrl("http://www.instagimg.com/user/ringlerr1/8686787805");
                dialog.dismiss();
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToUrl("https://twitter.com/RINGLERR1");
                dialog.dismiss();
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToUrl("https://www.facebook.com/Ringlerr");
                dialog.dismiss();
            }
        });

        linkedin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToUrl("https://in.linkedin.com/company/ringlerr");
                dialog.dismiss();
            }
        });

        youtube.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToUrl("https://www.youtube.com/channel/UC0rZCd13omkd2rxf1njno-w");
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent WebView = new Intent(Intent.ACTION_VIEW, uriUrl);
        myActivity.startActivity(WebView);
    }
}
