package com.example.simeon_dee.mobileattendance;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ReportIndividualDetailsActivity extends AppCompatActivity {
    ImageView imgPassport;
    TextView tvMatric, tvSurname, tvMiddlename, tvFirstname,tvTotalAttendance;
    AttendedStudent currentStudent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_individual_details);

        imgPassport = (ImageView) findViewById(R.id.imgThePassport);
        tvMatric = (TextView) findViewById(R.id.tvMatricNumber);
        tvSurname = (TextView) findViewById(R.id.tvSurname);
        tvMiddlename = (TextView) findViewById(R.id.tvMiddlename);
        tvFirstname = (TextView) findViewById(R.id.tvFirstname);
        tvTotalAttendance = (TextView) findViewById(R.id.tvTotalAttendance);

        try{

            currentStudent = AttendanceReport.selectedStudent;

            if(currentStudent != null){

                Bitmap bitmap = DbImageBitmapConverterUtility.getBitmapImage(
                        currentStudent.getPassport());
                if(bitmap != null){
                    imgPassport.setImageBitmap(bitmap);
                }

                tvMatric.setText(currentStudent.getMatricNumber());
                tvSurname.setText(currentStudent.getName().getSurname());
                tvMiddlename.setText(currentStudent.getName().getMiddlename());
                tvFirstname.setText(currentStudent.getName().getFirstname());
                tvTotalAttendance.setText(String.valueOf(currentStudent.getTotalAttendance()));

            }
            else{
                displayDialog("Oops!...", "Problem displaying student details");
                Intent intent = new Intent(ReportIndividualDetailsActivity.this,
                        AttendanceReport.class);
                startActivity(intent);
            }

        } catch (Exception e){
            displayDialog("Oops!...", "Description: " + e.getMessage());
        }

    }

    private void displayDialog(String dialogTitle, String dialogMessage) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(ReportIndividualDetailsActivity.this);
        builder.setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton("Ok",null);
        Dialog dialog = builder.create();
        dialog.show();
    }

}
