package com.example.simeon_dee.mobileattendance;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AdminMenu extends AppCompatActivity {

    public static String operationType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        operationType = "";
    }

    public void btAddUpdateDeleteClickHandler(View view) {
        int viewID = view.getId();
        switch (viewID){
            case R.id.btAddNewCourse:
                operationType = "Add Course";
                break;
            case R.id.btUpdateCourse:
                operationType = "Update Course";
                break;
            case R.id.btDeleteCourse:
                operationType = "Delete Course";
                break;
        }

        startActivity(new Intent(AdminMenu.this,
                AddCourseActivity.class));

    }

    public void btDeleteDatabaseClickHandler(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminMenu.this);
        builder.setMessage("Are you sure you want to delete the Database?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(deleteDatabase("AttendanceDBase")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AdminMenu.this);
                    builder1.setMessage("Database Deleted Successfully");
                    Dialog dialog = builder1.create();
                    dialog.show();

                    Intent intent = new Intent(AdminMenu.this,SplashActivity.class);
                    startActivity(intent);

                } else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AdminMenu.this);
                    builder1.setMessage("Database Deletion Failed");
                    Dialog dialog = builder1.create();
                    dialog.show();
                }

            }
        });

        Dialog dialog = builder.create();
        dialog.show();



    }
}
