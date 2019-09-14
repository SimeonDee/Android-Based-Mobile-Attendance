package com.example.simeon_dee.mobileattendance;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by SIMEON_DEE on 6/4/2018.
 */

public class AttendanceReportArrayAdapterTester extends ArrayAdapter<AttendedStudent> {

    private Context mContext;
    private List<AttendedStudent> mObjects;

    public AttendanceReportArrayAdapterTester(@NonNull Context context, int resource,
                                              @NonNull List objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mObjects = objects;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        try{

            //View view = convertView;
            LayoutInflater inflater =
                    (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//            if(view == null){
//                view = inflater.inflate(R.layout.custom_attendance_report_layout,null);
//            }
//            else{
//                view = convertView;
//            }
            View view = inflater.inflate(R.layout.custom_attendance_report_layout,null);



            AttendedStudent currentStudent = mObjects.get(position);

            TextView tvSerialNumber = (TextView) view.findViewById(R.id.tvSNumber);
            ImageView imgPassport = (ImageView) view.findViewById(R.id.imgPspt);
            TextView tvMatric = (TextView) view.findViewById(R.id.tvMt);
            TextView tvAttendance= (TextView) view.findViewById(R.id.tvAtt);

            tvSerialNumber.setText(String.valueOf(position + 1));

            Bitmap bitmap = DbImageBitmapConverterUtility.getBitmapImage(
                    currentStudent.getPassport());
            if(bitmap != null){
                imgPassport.setImageBitmap(bitmap);
            }

            tvMatric.setText(currentStudent.getMatricNumber());
            tvAttendance.setText(String.valueOf(currentStudent.getTotalAttendance()));

            return view;

        } catch (Exception e){
            displayDialog("Oops!","Description:\n" + e.getMessage());
            return null;
        }
    }

    private void displayDialog(String dialogTitle, String dialogMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton("Ok",null);
        Dialog dialog = builder.create();
        dialog.show();
    }
}
