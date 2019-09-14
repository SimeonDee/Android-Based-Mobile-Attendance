package com.example.simeon_dee.mobileattendance;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LecturerMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_menu);
    }

    public void btTakeAttendanceClickHandler(View view) {
        startActivity(new Intent(LecturerMenuActivity.this,
                LecturerAttendanceQuery.class));
    }

    public void btGetAttendanceReportsClickHandler(View view) {
        startActivity(new Intent(LecturerMenuActivity.this,
                AttendanceReportQueryActivity.class));
    }
}
