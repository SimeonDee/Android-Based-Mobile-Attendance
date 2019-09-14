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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LecturerAttendanceQuery extends AppCompatActivity {

    SQLiteDatabase mDatabase;
    Spinner spinFac, spinDept, spinLev, spinSess,spinSem,spinLoadedCourses;
    Button btLoad, btNext;
    LinearLayout linearLayoutContinue;

    String lecturerID, mDepartment, mSemester, mLevel;
    static  String mSession;
    static Course selectedCourse;
    final String COURSES_TABLE = "courses";
    static List<Course> lecturerCourses;
    private ArrayAdapter<String> deptSpinnerArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_attendance_query);

        spinFac = (Spinner) findViewById(R.id.spinFac);
        spinDept = (Spinner) findViewById(R.id.spinDept);
        spinLev = (Spinner) findViewById(R.id.spinLev);
        spinSess = (Spinner) findViewById(R.id.spinSes);
        spinSem = (Spinner) findViewById(R.id.spinSem);
        spinLoadedCourses = (Spinner) findViewById(R.id.spinLoadedCourses);
        linearLayoutContinue = (LinearLayout) findViewById(R.id.linearLayoutContinue);

        loadSessionsIntoSpinner();

        try{
            if(mDatabase == null || !mDatabase.isOpen()){
                mDatabase = openOrCreateDatabase(LoginActivity.DBASE_NAME,MODE_PRIVATE,null);
            }

            spinFac.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedFaculty = (String) adapterView.getSelectedItem();

                    loadDepartments(selectedFaculty);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Toast.makeText(LecturerAttendanceQuery.this,
                            "You have to select your faculty", Toast.LENGTH_SHORT).show();
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
        spinSess.setAdapter(spinnerAdapter);

        //create a session string of the form 2018/2019
        spinnerAdapter.add("--select session--");
        for(int i = year; i >= (year - 10); i--){
            String session = String.valueOf(i) + "/" + String.valueOf(i+1);
            spinnerAdapter.add(session);
            spinnerAdapter.notifyDataSetChanged();
        }
    }

    public void loadDepartments(String faculty){
        spinDept.setAdapter(null);
        deptSpinnerArrayAdapter = new ArrayAdapter<String>(LecturerAttendanceQuery.this,
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
        }

        spinDept.setAdapter(deptSpinnerArrayAdapter);
    }

    public void btLoadLecturerCoursesHandler(View view) {
        try{
            //get courses for lecturer
            lecturerCourses = new ArrayList<>();
            lecturerID = LoginActivity.currentUser.getID();
            mDepartment = spinDept.getSelectedItem().toString();
            mLevel = spinLev.getSelectedItem().toString();
            mSession = spinSess.getSelectedItem().toString();
            mSemester = spinSem.getSelectedItem().toString();

            if(inputsAreCorrect(mDepartment,mLevel,mSession,mSemester)){
                String selectSQL = "SELECT * FROM " + COURSES_TABLE + " WHERE " +
                        "c_lecturer = '" + lecturerID + "' AND department = '" +
                        mDepartment + "' AND semester = '" +
                        mSemester + "' AND level = '" + mLevel + "';";

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
                    linearLayoutContinue.setVisibility(View.VISIBLE);

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
        ArrayAdapter<String> courseCodesSpinnerAdapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,
                        android.R.id.text1);
        courseCodesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinLoadedCourses.setAdapter(courseCodesSpinnerAdapter);

        for(Course courseItem: lecturerCourses){
            courseCodesSpinnerAdapter.add(courseItem.getCode());
            courseCodesSpinnerAdapter.notifyDataSetChanged();
        }

    }

    private boolean inputsAreCorrect(String dept, String level, String session, String semester) {

        if(dept== null || dept.isEmpty() ||
                dept.equalsIgnoreCase("--select department--")){

            Toast.makeText(LecturerAttendanceQuery.this,"Department not selected",
                    Toast.LENGTH_LONG).show();
            spinDept.requestFocus();
            return false;
        }

        if(level == null || level.isEmpty() ||
                level.equalsIgnoreCase("--select level--")){

            Toast.makeText(LecturerAttendanceQuery.this,"Level not selected",
                    Toast.LENGTH_LONG).show();
            spinLev.requestFocus();
            return false;
        }

        if(session == null || session.isEmpty() ||
                level.equalsIgnoreCase("--select session--")){

            Toast.makeText(LecturerAttendanceQuery.this,"Session not selected",
                    Toast.LENGTH_LONG).show();
            spinSess.requestFocus();
            return false;
        }

        if(semester == null || semester.isEmpty() ||
                semester.equalsIgnoreCase("--select semester--")){

            Toast.makeText(LecturerAttendanceQuery.this,"Semester not selected",
                    Toast.LENGTH_LONG).show();
            spinSem.requestFocus();
            return false;
        }

        return true;
    }


    public void btContinueClickHandler(View view) {
        try{
            if(spinLoadedCourses.getSelectedItem().toString().isEmpty() ||
                    spinLoadedCourses.getSelectedItem() == null){
                displayDialog("Information","No course selected");
                //spinLoadedCourses.requestFocus();
                return;
            }

            String selectedCode = spinLoadedCourses.getSelectedItem().toString();
            for(Course course: lecturerCourses){
                if(selectedCode.equalsIgnoreCase(course.getCode())){
                    selectedCourse = course;
                    break;
                }
            }

            Intent intent =
                    new Intent(LecturerAttendanceQuery.this,AttendanceActivity.class);
            startActivity(intent);

        } catch (Exception e){
            displayDialog("Oops!", "Description: \n" + e.getMessage());
        }

    }

    private void displayDialog(String dialogTitle, String dialogMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LecturerAttendanceQuery.this);
        builder.setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton("Ok",null);
        Dialog dialog = builder.create();
        dialog.show();
    }
}
