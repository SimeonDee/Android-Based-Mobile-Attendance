package com.example.simeon_dee.mobileattendance;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

public class SplashActivity extends AppCompatActivity {

    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    public void onClickLaunchHandler(View view) {
        progressBar.setVisibility(View.VISIBLE);
        for(int i=1; i<=2000000;i++){}
        progressBar.setVisibility(View.INVISIBLE);

//        if(deleteDatabase("AttendanceDBase")) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
//            builder.setMessage("Database Deleted Successfully");
//            Dialog dialog = builder.create();
//            dialog.show();
//        } else {
//            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
//            builder.setMessage("Database Deletion Failed");
//            Dialog dialog = builder.create();
//            dialog.show();
//        }


        Intent launchLoginPageIntent = new Intent(this,LoginActivity.class);
        startActivity(launchLoginPageIntent);
    }
}
