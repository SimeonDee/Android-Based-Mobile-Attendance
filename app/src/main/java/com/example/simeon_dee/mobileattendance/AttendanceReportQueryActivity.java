package com.example.simeon_dee.mobileattendance;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AttendanceReportQueryActivity extends AppCompatActivity {

    SQLiteDatabase mDatabase;
    final String COURSE_ATTENDANCE_TABLE = "lectureAttendance";
    final String COURSES_TABLE = "courses";

    Spinner spinRepFac, spinRepDept, spinRepLev, spinRepSess,spinRepSem,spinRepLoadedCourses;
    Button btLoad, btNext;
    DatePicker dtpFromDate, dtpToDate;
    CheckBox chkDateRange;
    LinearLayout linearLayoutDateRange, linearLayoutRepContinue;


    String lecturerID, mDepartment, mSemester, mLevel;
    static  String mSession, addDateCriteriaSQL = "";
    static Course selectedCourse;
    static List<Course> lecturerCourses;
    static String fromDate = "", toDate="";
    private ArrayAdapter<String> deptSpinnerArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_report_query);

        spinRepFac = (Spinner) findViewById(R.id.spinRepFac);
        spinRepDept = (Spinner) findViewById(R.id.spinRepDept);
        spinRepLev = (Spinner) findViewById(R.id.spinRepLev);
        spinRepSess = (Spinner) findViewById(R.id.spinRepSes);
        spinRepSem = (Spinner) findViewById(R.id.spinRepSem);
        spinRepLoadedCourses = (Spinner) findViewById(R.id.spinRepLoadedCourses);

        chkDateRange = (CheckBox) findViewById(R.id.chkDateRange);
        dtpFromDate = (DatePicker) findViewById(R.id.dtpFromDate);
        dtpToDate = (DatePicker) findViewById(R.id.dtpToDate);
        linearLayoutRepContinue = (LinearLayout) findViewById(R.id.linearLayoutRepContinue);
        linearLayoutDateRange = (LinearLayout) findViewById(R.id.linearDateQuery);

        loadSessionsIntoSpinner();

        try{
            if(mDatabase == null || !mDatabase.isOpen()){
                mDatabase = openOrCreateDatabase(LoginActivity.DBASE_NAME,MODE_PRIVATE,null);
            }

            spinRepFac.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedFaculty = (String) adapterView.getSelectedItem();

                    loadDepartments(selectedFaculty);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Toast.makeText(AttendanceReportQueryActivity.this,
                            "You have to select your faculty", Toast.LENGTH_SHORT).show();
                }
            });

            chkDateRange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(chkDateRange.isChecked()){
                        linearLayoutDateRange.setVisibility(View.VISIBLE);
                    }
                    else{
                        linearLayoutDateRange.setVisibility(View.GONE);
                    }
                }
            });

        } catch (Exception e){
            displayDialog("Oops!",
                    "Description: \n" + e.getMessage());
        }
    }

    private void loadSessionsIntoSpinner() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,
                        android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinRepSess.setAdapter(spinnerAdapter);

        //create a session string of the form 2018/2019
        spinnerAdapter.add("--select session--");
        for(int i = year; i >= (year - 10); i--){
            String session = String.valueOf(i) + "/" + String.valueOf(i+1);
            spinnerAdapter.add(session);
            spinnerAdapter.notifyDataSetChanged();
        }
    }

    public void loadDepartments(String faculty){
        spinRepDept.setAdapter(null);
        deptSpinnerArrayAdapter = new ArrayAdapter<String>(
                AttendanceReportQueryActivity.this,
                android.R.layout.simple_spinner_item, android.R.id.text1);

        switch (faculty.trim())
        {
            case "Applied Science":
                deptSpinnerArrayAdapter.add("--select department--");
                deptSpinnerArrayAdapter.add("Computer Science");
                deptSpinnerArrayAdapter.add("Food Technology");
                deptSpinnerArrayAdapter.add("Maths and Statistics");
                deptSpinnerArrayAdapter.add("Science Lab Tech");
                break;
            case "Business Management":
                deptSpinnerArrayAdapter.add("--select department--");
                deptSpinnerArrayAdapter.add("Accountancy");
                deptSpinnerArrayAdapter.add("Banking and Finance");
                deptSpinnerArrayAdapter.add("Business Admin");
                deptSpinnerArrayAdapter.add("Insurance");
                deptSpinnerArrayAdapter.add("Marketing");

                break;
            case "Communications":
                deptSpinnerArrayAdapter.add("--select department--");
                deptSpinnerArrayAdapter.add("Mass Communication");
                deptSpinnerArrayAdapter.add("Library Science");
                deptSpinnerArrayAdapter.add("Office Tech Mangmt");

                break;
            case "Engineering":
                deptSpinnerArrayAdapter.add("--select department--");
                deptSpinnerArrayAdapter.add("Civil Engineering");
                deptSpinnerArrayAdapter.add("Computer Engineering");
                deptSpinnerArrayAdapter.add("Elect. Elect.");
                deptSpinnerArrayAdapter.add("Computer Engineering");

                break;
            case "Environmental":
                deptSpinnerArrayAdapter.add("--select department--");
                deptSpinnerArrayAdapter.add("Architecture");
                deptSpinnerArrayAdapter.add("Building Tech");
                deptSpinnerArrayAdapter.add("Quantity Surveying");
                deptSpinnerArrayAdapter.add("Surveying and Geo-info");

                break;
            case "General Studies":
                deptSpinnerArrayAdapter.add("--select department--");
                deptSpinnerArrayAdapter.add("Languages");
                deptSpinnerArrayAdapter.add("Humanities and Soc. Sci.");
                break;
            case "ICT Center":
                deptSpinnerArrayAdapter.add("--select department--");
                deptSpinnerArrayAdapter.add("ICT");
        }

        spinRepDept.setAdapter(deptSpinnerArrayAdapter);
    }

    public void btLoadLecturerCoursesHandler(View view) {
        try{
            //get courses for lecturer
            lecturerCourses = new ArrayList<>();
            lecturerID = LoginActivity.currentUser.getID();
            mDepartment = spinRepDept.getSelectedItem().toString();
            mLevel = spinRepLev.getSelectedItem().toString();
            mSession = spinRepSess.getSelectedItem().toString();
            mSemester = spinRepSem.getSelectedItem().toString();

            if(inputsAreCorrect(mDepartment,mLevel,mSession,mSemester)){
                String selectSQL = "SELECT * FROM " + COURSES_TABLE + " " +
                        "WHERE " +
                        "c_lecturer = '" + lecturerID + "' AND department = '" +
                        mDepartment + "' AND semester = '" +
                        mSemester + "' AND level = '" + mLevel + "' " +
                        "ORDER BY c_code;";

                Cursor results = mDatabase.rawQuery(selectSQL,null);

                //if courses loaded successfully
                if(results.moveToFirst()){
                    do{
                        Course course = new Course();
                        course.setFaculty(results.getString(1));
                        course.setDepartment(results.getString(2));
                        course.setLevel(results.getString(3));
                        course.setSemester(results.getString(4));
                        course.setCode(results.getString(5));
                        course.setCourseTitle(results.getString(6));
                        course.setUnit(results.getInt(7));
                        course.setCourseLecturer(results.getString(8));

                        lecturerCourses.add(course);
                    } while (results.moveToNext());

                    loadCourseCodes(lecturerCourses);
                    linearLayoutRepContinue.setVisibility(View.VISIBLE);

                }else{
                    displayDialog("No Course",
                            "There are no course(s) assigned to you yet.\n" +
                                    "Please visit ICT Center for Enquiry");
                }

                if(results != null && !results.isClosed()) results.close();
            }

        } catch (Exception e){
            displayDialog("Oops!", "Desription:\n"
                    + e.getMessage());
        }
    }

    private void loadCourseCodes(List<Course> lecturerCourses) {
        //ArrayAdapter<String> courseCodesSpinnerAdapter =
        //          new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,
        //          android.R.id.text1);
        //          courseCodesSpinnerAdapter.
        //        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> courseCodesSpinnerAdapter = new ArrayAdapter<String>(
                AttendanceReportQueryActivity.this,
                android.R.layout.simple_spinner_item, android.R.id.text1);

        spinRepLoadedCourses.setAdapter(null);

        for(Course courseItem: lecturerCourses){
            courseCodesSpinnerAdapter.add(courseItem.getCode());
        }

        spinRepLoadedCourses.setAdapter(courseCodesSpinnerAdapter);

    }

    private boolean inputsAreCorrect(String dept, String level, String session, String semester) {

        if(dept== null || dept.isEmpty() ||
                dept.equalsIgnoreCase("--select department--")){

            Toast.makeText(AttendanceReportQueryActivity.this,"Department not selected",
                    Toast.LENGTH_LONG).show();
            spinRepDept.requestFocus();
            return false;
        }

        if(level == null || level.isEmpty() ||
                level.equalsIgnoreCase("--select level--")){

            Toast.makeText(AttendanceReportQueryActivity.this,"Level not selected",
                    Toast.LENGTH_LONG).show();
            spinRepLev.requestFocus();
            return false;
        }

        if(session == null || session.isEmpty() ||
                level.equalsIgnoreCase("--select session--")){

            Toast.makeText(AttendanceReportQueryActivity.this,"Session not selected",
                    Toast.LENGTH_LONG).show();
            spinRepSess.requestFocus();
            return false;
        }

        if(semester == null || semester.isEmpty() ||
                semester.equalsIgnoreCase("--select semester--")){

            Toast.makeText(AttendanceReportQueryActivity.this,"Semester not selected",
                    Toast.LENGTH_LONG).show();
            spinRepSem.requestFocus();
            return false;
        }

        return true;
    }

    public void btContinueClickHandler(View view) {
        if(spinRepLoadedCourses.getSelectedItem().toString().isEmpty() ||
                spinRepLoadedCourses.getSelectedItem() == null){
            displayDialog("Information","No course selected");
            //spinRepLoadedCourses.requestFocus();
            return;
        }

        String selectedCode = spinRepLoadedCourses.getSelectedItem().toString();
        for(Course courseItem: lecturerCourses){
            if(selectedCode.equalsIgnoreCase(courseItem.getCode())){
                selectedCourse = courseItem;
            }
        }

        if(chkDateRange.isChecked()){
            if(dateInputsAreCorrect(dtpFromDate,dtpToDate)){
                int year,month,day;
                year = dtpFromDate.getYear();
                month = dtpFromDate.getMonth();
                day = dtpFromDate.getDayOfMonth();
                fromDate = year + "-" + padZero(month) + "-" + padZero(day);

                year = dtpToDate.getYear();
                month = dtpToDate.getMonth();
                day = dtpToDate.getDayOfMonth();
                toDate = year + "-" + padZero(month) + "-" + padZero(day);

                addDateCriteriaSQL = "AND attendance_date BETWEEN DATE('" + fromDate + "')" +
                        " AND DATE('" + toDate + "') ";
            }

        } else {
            addDateCriteriaSQL = "";
        }

        startActivity(new Intent(AttendanceReportQueryActivity.this,
                AttendanceReport.class));
    }

    private Boolean dateInputsAreCorrect(DatePicker dtpFromDate, DatePicker dtpToDate) {
        try{
            if (dtpFromDate == null || dtpFromDate.getYear() < 1){
                Toast.makeText(AttendanceReportQueryActivity.this,
                        "From date Field is required", Toast.LENGTH_LONG).show();
                dtpFromDate.requestFocus();
                return false;
            }

            if (dtpToDate == null || dtpToDate.getYear() < 1){
                Toast.makeText(AttendanceReportQueryActivity.this,
                        "To Date Field is Required", Toast.LENGTH_LONG).show();
                dtpToDate.requestFocus();
                return false;
            }

            return true;

        } catch (Exception e){
            displayDialog("Oops!","Description:\n" + e.getMessage());
            return false;
        }
    }


    private String padZero(int number){
        //if number is above 9, return it as string, otherwise pad zero
        String ans = (number > 9) ? String.valueOf(number) : "0" + number;
        return ans;
    }

    private void displayDialog(String dialogTitle, String dialogMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceReportQueryActivity.this);
        builder.setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton("Ok",null);
        Dialog dialog = builder.create();
        dialog.show();
    }
}
