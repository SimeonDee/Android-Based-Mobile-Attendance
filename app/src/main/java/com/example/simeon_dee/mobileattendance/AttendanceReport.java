package com.example.simeon_dee.mobileattendance;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AttendanceReport extends AppCompatActivity {

    SQLiteDatabase mDatabase;

    final String USER_ACCOUNT_TABLE_NAME = "userAccounts";
    final String COURSE_ATTENDANCE_TABLE = "lectureAttendance";
    public static AttendedStudent selectedStudent = null;
    //final String REGISTERED_COURSES_TABLE = "registeredCourses";
    List<AttendedStudent> students;
    TextView mReportTitle;
    ListView myList;

    //AttendanceArrayAdapter attendanceArrayAdapter;
    //AttendanceReportArrayAdapter2 attendanceReportArrayAdapter;
    //AttendRepAdapter attendanceReportArrayAdapter;
//    AttendanceReportAdapter attendanceReportAdapter;
    AttendanceReportArrayAdapterTester attendanceReportArrayAdapterTester;
    Course currentCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_report);

        mReportTitle = (TextView) findViewById(R.id.tvHeading);
        myList = (ListView) findViewById(android.R.id.list);

        currentCourse = AttendanceReportQueryActivity.selectedCourse;

        if(!AttendanceReportQueryActivity.fromDate.equalsIgnoreCase("") &&
                !AttendanceReportQueryActivity.fromDate.equalsIgnoreCase("")) {

            mReportTitle.setText("ATTENDANCE REPORT OF " + currentCourse.mLevel + " " +
                    currentCourse.mDept + " FOR " + currentCourse.getCode() +
                    " BETWEEN " + AttendanceReportQueryActivity.fromDate + " AND " +
                    AttendanceReportQueryActivity.toDate);
        }
        else {
            mReportTitle.setText("ATTENDANCE REPORT OF " + currentCourse.mLevel + " " +
                    currentCourse.mDept + " FOR " + currentCourse.getCode());
        }


        try {

            if (mDatabase == null || !mDatabase.isOpen()) {
                mDatabase = openOrCreateDatabase(LoginActivity.DBASE_NAME,
                        MODE_PRIVATE, null);
            }
            students = new ArrayList<>();
            students = loadStudents();
            if(students != null && students.size() > 0){
                attendanceReportArrayAdapterTester = new AttendanceReportArrayAdapterTester(
                        AttendanceReport.this,
                        0,students);

                myList.setAdapter(attendanceReportArrayAdapterTester);
//                myList.setAdapter(new AttendanceReportArrayAdapterTester(AttendanceReport.this,
  //                      R.layout.custom_attendance_report,students));
            }

            myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedStudent = null;
                    selectedStudent =
                            (AttendedStudent) adapterView.getItemAtPosition(i);

                    Intent intent = new Intent(AttendanceReport.this,
                            ReportIndividualDetailsActivity.class);
                    startActivity(intent);
                }
            });

        } catch (Exception e){
            displayDialog("Oops!", "Description:\n" + e.getMessage());
        }

    }

    private List<AttendedStudent> loadStudents() {
        List<AttendedStudent> studentsList = new ArrayList<>();

        try{
            String sql = "SELECT ac.username, ac.surname, ac.middlename, ac.firstname, " +
                    "ac.passport, count(c_att.matric) AS totalAttendance " +
                    "FROM " + USER_ACCOUNT_TABLE_NAME + " AS ac " +
                    "INNER JOIN " + COURSE_ATTENDANCE_TABLE + " AS c_att " +
                    "ON ac.username = c_att.matric " +
                    "WHERE c_att.c_code = '" + currentCourse.getCode() + "' " +
                    "AND c_att.c_lecturer = '" + currentCourse.getCourseLecturerID() + "' " +
                    "AND c_att.session = '" + AttendanceReportQueryActivity.mSession + "' " +
                    "AND c_att.department = '" + currentCourse.getDepartment() + "' " +
                    "AND c_att.level = '" + currentCourse.getLevel() + "' " +
                    addDateRangeStrIfAvailable() +
                    "GROUP BY c_att.matric ORDER BY c_att.matric;";

            Cursor results = mDatabase.rawQuery(sql,null);
            if(results.moveToFirst()){
                do{
                    AttendedStudent attendedStudent = new AttendedStudent();
                    attendedStudent.setMatricNumber(results.getString(0));

                    Name thename = new Name();
                        thename.setSurname(results.getString(1));
                        thename.setMiddlename(results.getString(2));
                        thename.setFirstname(results.getString(3));

                    attendedStudent.setName(thename);
                    attendedStudent.setPassport(results.getBlob(4));
                    attendedStudent.setTotalAttendance(results.getInt(5));

                    studentsList.add(attendedStudent);

                } while (results.moveToNext());
            }
            else {
                displayDialog("Oops!", "Sorry, no attendance taken yet");
            }

            return studentsList;

        } catch (Exception e){
            displayDialog("Oops!","Description:\n" + e.getMessage());
            return null;
        }

    }

    private String addDateRangeStrIfAvailable() {
        String dateCriteria = AttendanceReportQueryActivity.addDateCriteriaSQL;
        if(dateCriteria.isEmpty() || dateCriteria == null) {
            return "";
        }
        else{
            return dateCriteria;
        }
    }

    public void displayDialog(String dialogTitle, String dialogMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceReport.this);
        builder.setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton("Ok",null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    public void exitButtonClickHandler(View view) {
        Intent intent = new Intent(AttendanceReport.this,LecturerMenuActivity.class);
        startActivity(intent);
    }


    //*****************************************************************
//
//        class AttendRepAdapter extends ArrayAdapter<AttendedStudent>{
//            Context context;
//            List<AttendedStudent> mStudents;
//
//            public AttendRepAdapter(@NonNull Context context, int resource, @NonNull List<AttendedStudent> objects) {
//                super(context, resource, objects);
//                this.context = context;
//                this.mStudents = objects;
//            }
//
//            @NonNull
//            @Override
//            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//
//                try{
//                    LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(
//                            Activity.LAYOUT_INFLATER_SERVICE);
//                    View v = layoutInflater.inflate(R.layout.custom_attendance_report,null);
//
//                    TextView tvSN = (TextView) v.findViewById(R.id.tvSNumber);
//                    TextView tvMat = (TextView) v.findViewById(R.id.tvMt);
//                    TextView tvAttend = (TextView) v.findViewById(R.id.tvAtt);
//                    ImageView imgPspt = (ImageView) v.findViewById(R.id.imgPspt);
//
//                    final AttendedStudent curStudent = mStudents.get(position);
//
//                    tvSN.setText(String.valueOf(position + 1));
//                    tvMat.setText(curStudent.getMatricNumber());
//                    imgPspt.setImageBitmap(DbImageBitmapConverterUtility.
//                            getBitmapImage(curStudent.getPassport()));
//                    tvAttend.setText(curStudent.getTotalAttendance());
//
//                    return v;
//
//                } catch (Exception e) {
//                    displayDialog("Oops!...","Description:\n" + e.getMessage());
//                    return null;
//                }
//
//            }
//
//        }

        //**********************************************************
//    public class AttendanceReportAdapter extends ArrayAdapter<Student> {
//
//        private Context context;
//        private List<AttendedStudent> presentStudents;
//        public ArrayList<Student> presentList;
//
//
//        public AttendanceReportAdapter(@NonNull Context context, int resource, @NonNull List objects) {
//            super(context, resource, objects);
//            this.context = context;
//            this.presentStudents = objects;
//        }
//
//        @NonNull
//        @Override
//        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//
//            LayoutInflater inflater =
//                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//
//            View view = inflater.inflate(R.layout.custom_attendance_report_layout, null);
//
//
//            TextView tvSerialNumber = (TextView) view.findViewById(R.id.tvSNumber);
//            //TextView tvName = (TextView) view.findViewById(R.id.fullname);
//            TextView tvMatric = (TextView) view.findViewById(R.id.tvMt);
//            //final CheckBox chkAttend = (CheckBox) view.findViewById(R.id.presentAbsent);
//            ImageView imgPassport = (ImageView) view.findViewById(R.id.imgPspt);
//            TextView tvAttendance = (TextView) view.findViewById(R.id.tvAtt);
//
//            final AttendedStudent currentStudent = presentStudents.get(position);
//
//
//            tvSerialNumber.setText(String.valueOf(position + 1));
//            //tvName.setText(currentStudent.getName().toString());
//            tvMatric.setText(currentStudent.getMatricNumber());
//            imgPassport.setImageBitmap(
//                    DbImageBitmapConverterUtility.getBitmapImage(currentStudent.getPassport()));
//            tvAttendance.setText(currentStudent.getTotalAttendance());
//
//            return view;
//        }
//
//    }
    //****************************************************
    //****************ARRAY ADAPTER SUB-CLASS HERE********************************************

//    class AttendanceReportArrayAdapter2 extends ArrayAdapter<AttendedStudent> {
//
//        private Context mContext;
//        private List<AttendedStudent> mObjects;
//        public AttendanceReportArrayAdapter2(@NonNull Context context, int resource,
//                                            @NonNull List objects) {
//            super(context, resource, objects);
//            this.mContext = context;
//            this.mObjects = objects;
//
//        }
//
//        @NonNull
//        @Override
//        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//
//            try{
//
//
//                LayoutInflater inflater =
//                        (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//                View view = inflater.inflate(R.layout.custom_attendance_report,null);
//
//                AttendedStudent currentStudent = mObjects.get(position);
//
//                TextView tvSerialNumber = (TextView) view.findViewById(R.id.tvSNumber);
//                ImageView imgPassport = (ImageView) view.findViewById(R.id.imgPspt);
//                TextView tvMatric = (TextView) view.findViewById(R.id.tvMt);
//                TextView tvAttendance= (TextView) view.findViewById(R.id.tvAtt);
//
//                tvSerialNumber.setText(String.valueOf(position + 1));
//                imgPassport.setImageBitmap(
//                        DbImageBitmapConverterUtility.getBitmapImage(currentStudent.getPassport()));
//                tvMatric.setText(currentStudent.getMatricNumber());
//                tvAttendance.setText(currentStudent.getTotalAttendance());
//
//                return view;
//
//            } catch (Exception e){
//                displayDialog("Oops!","Description:\n" + e.getMessage());
//                return null;
//            }
//        }
//
//    }


}
