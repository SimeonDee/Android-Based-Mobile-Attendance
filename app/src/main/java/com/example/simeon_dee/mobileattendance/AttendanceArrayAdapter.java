package com.example.simeon_dee.mobileattendance;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by SIMEON_DEE on 3/16/2018.
 */
public class AttendanceArrayAdapter extends ArrayAdapter<Student> {
    private Context context;
    private List<Student> registeredStudents;
    public ArrayList<Student> presentList;


    public AttendanceArrayAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.context = context;
        this.registeredStudents = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        try{
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.attendance_custom_list_layout, null);


            TextView tvSerialNumber = (TextView) view.findViewById(R.id.serialNumber);
            TextView tvName = (TextView) view.findViewById(R.id.fullname);
            TextView tvMatric = (TextView) view.findViewById(R.id.matricNumber);
            CheckBox chkAttend = (CheckBox) view.findViewById(R.id.presentAbsent);
            ImageView imgPassport = (ImageView) view.findViewById(R.id.imgPassport);

            Student currentStudent = registeredStudents.get(position);

            tvSerialNumber.setText(String.valueOf(position + 1));
            tvName.setText(currentStudent.getName().toString());
            tvMatric.setText(currentStudent.getMatricNumber());
            imgPassport.setImageBitmap(
                    DbImageBitmapConverterUtility.getBitmapImage(currentStudent.getPassport()));


//        chkAttend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(chkAttend.isChecked()){
//                    if(presentList == null || !presentList.contains(currentStudent)){
//                        presentList.add(currentStudent);
//                    }
//                }
//                else{
//                    if(presentList.contains(currentStudent)){
//                        presentList.remove(currentStudent);
//                    }
//                }
//            }
//        });

            return view;
        } catch (Exception ex){
            displayDialog("Oops!...", "Description:\n"+ ex.getMessage());
            return null;
        }

    }

    private void displayDialog(String dialogTitle, String dialogMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton("Ok",null);
        Dialog dialog = builder.create();
        dialog.show();
    }

}
