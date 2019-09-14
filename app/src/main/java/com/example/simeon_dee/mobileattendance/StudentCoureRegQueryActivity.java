package com.example.simeon_dee.mobileattendance;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StudentCoureRegQueryActivity extends AppCompatActivity {

    SQLiteDatabase mDatabase;
    private Spinner spinSession, spinLevel, spinSemester;
    private ListView lstCourses;
    //private LinearLayout linearLayoutCourses;

    CourseAdapterView courseAdapterView;
    Student currentStudent;
    List<Course> mRegisteredCourses;
    ArrayList<Course> mAvailableCourses;
    final String COURSE_TABLE_NAME = "courses";
    final String REGISTERED_COURSES_TABLE = "registeredCourses";
    String session, level, semester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_coure_reg_query_params_layout);

        spinSession = (Spinner) findViewById(R.id.spinSession);
        spinLevel = (Spinner) findViewById(R.id.spinLevel);
        spinSemester = (Spinner) findViewById(R.id.spinSemester);
        lstCourses = (ListView) findViewById(R.id.lstCourses);
        //linearLayoutCourses = (LinearLayout) findViewById(R.id.linearLayoutCourses);

        loadSessionsIntoSpinner();

        if ((mDatabase == null || !mDatabase.isOpen())) {
            mDatabase = openOrCreateDatabase(LoginActivity.DBASE_NAME, MODE_PRIVATE, null);
        }

        mRegisteredCourses = new ArrayList<>();

//        lstCourses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                CheckBox chkCourse = (CheckBox) view.findViewById(R.id.chkSelectCourse);
//                Course course = null;
//                course = (Course) adapterView.getSelectedItem();
//
//                /* course = new Course(code,title,Integer.parseInt(unit)); */
//
//                //if checked and the course has not been added already, add it to registered courses list
//                if (chkCourse.isChecked() && !courseAlreadyAdded(course)) {
//                    mRegisteredCourses.add(course);
//                }
//                // else if checkbox is unchecked and the course has already been added, remove it
//                // from the registered courses list
//                else if (!chkCourse.isChecked() && courseAlreadyAdded(course)) {
//                    removeFromRegisteredCourses(course);
//                }
//
//            }
//        });

    }

    public void loadCoursesButtonHandler(View view) {
        level = spinLevel.getSelectedItem().toString();
        session = spinSession.getSelectedItem().toString();
        semester = spinSemester.getSelectedItem().toString();

        try {
            if (inputsAreCorrect(level, session, semester)) {
                //Load all courses for the level and the year before (for C.O.)
                currentStudent = new Student(LoginActivity.currentUser, level);

                mAvailableCourses = loadRegisteredCourses(currentStudent);

                if (mAvailableCourses != null) {
                    //linearLayoutCourses.setVisibility(View.VISIBLE);

//                    courseArrayAdapter =
//                            new CourseArrayAdapter2(StudentCoureRegQueryActivity.this,
//                                    0, mAvailableCourses);
                    courseAdapterView =
                            new CourseAdapterView(StudentCoureRegQueryActivity.this,
                                    0,mAvailableCourses);

                    lstCourses.setAdapter(courseAdapterView);

                } else {
                    //linearLayoutCourses.setVisibility(View.INVISIBLE);
                }
            }

        } catch (Exception e) {
            displayDialog("Oops!", "Description:\n" + e.getMessage());
        }
    }

    private void loadSessionsIntoSpinner() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                        android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinSession.setAdapter(spinnerAdapter);

        spinnerAdapter.add("--select session--");
        //create a session string of the form 2018/2019
        for (int i = year; i >= (year - 10); i--) {
            String session = String.valueOf(i) + "/" + String.valueOf(i + 1);
            spinnerAdapter.add(session);
            spinnerAdapter.notifyDataSetChanged();
        }
    }

    private boolean inputsAreCorrect(String level, String session, String semester) {
        if (level == null || level.isEmpty() ||
                level.equalsIgnoreCase("--select level--")) {

            Toast.makeText(StudentCoureRegQueryActivity.this, "Level not selected",
                    Toast.LENGTH_LONG).show();
            spinLevel.requestFocus();
            return false;
        }

        if (session == null || session.isEmpty() ||
                level.equalsIgnoreCase("--select session--")) {
            Toast.makeText(StudentCoureRegQueryActivity.this, "Session not selected",
                    Toast.LENGTH_LONG).show();
            spinSession.requestFocus();
            return false;
        }

        if (semester == null || semester.isEmpty() ||
                semester.equalsIgnoreCase("--select semester--")) {

            Toast.makeText(StudentCoureRegQueryActivity.this, "Semester not selected",
                    Toast.LENGTH_LONG).show();
            spinSemester.requestFocus();
            return false;
        }

        return true;
    }

    private ArrayList<Course> loadRegisteredCourses(Student student) {
        ArrayList<Course> courses = new ArrayList<>();

        //load courses based on whether student is a fresher or staylite
        // to allow for carry-over registrations
        String levelCriteria = "";
        switch (student.getLevel()) {
            case "ND II (F/T)":
                levelCriteria = student.getLevel() +
                        "' OR level = 'ND I (F/T)";
                break;
            case "ND YR II (P/T)":
                levelCriteria = student.getLevel() +
                        "' OR level = 'ND YR I (P/T)";
                break;
            case "ND YR III (P/T)":
                levelCriteria = student.getLevel() +
                        "' OR level = 'ND YR II (P/T)'" +
                        " OR level = 'ND YR I (P/T)";
                break;
            case "HND II (F/T)":
                levelCriteria = student.getLevel() +
                        "' OR level = 'HND I (F/T)";
                break;
            case "ND I (F/T)":
            case "ND YR I (P/T)":
            case "HND I (F/T)":
                levelCriteria = student.getLevel();
                break;
        }


        try {
            String selectSQL = "SELECT * FROM " + COURSE_TABLE_NAME +
                    " WHERE faculty = '" + student.getFaculty() + "' AND " +
                    "department = '" + student.getDepartment() + "' AND " +
                    "level = '" + levelCriteria + "' AND " +
                    "semester = '" + spinSemester.getSelectedItem().toString() + "' " +
                    "ORDER BY c_code DESC;";

            Cursor results = mDatabase.rawQuery(selectSQL, null);

            //check if results has records and then load all available courses
            if (results.moveToFirst()) {
                do {
                    String fac, dept, level, semester, code, title, lecturer;
                    int unit;

                    fac = results.getString(1);
                    dept = results.getString(2);
                    level = results.getString(3);
                    semester = results.getString(4);
                    code = results.getString(5);
                    title = results.getString(6);
                    unit = results.getInt(7);
                    lecturer = results.getString(8);

                    Course course = new Course(fac,dept,level,semester,code,title,unit,lecturer);

                    courses.add(course);

                } while (results.moveToNext());

            } else {

                displayDialog("Error Report",
                        "No Course(s) availble for this class yet.\n" +
                                "Kindly visit the ICT Center");
                courses = null;
            }

            if (results != null && !results.isClosed()) results.close();
            return courses;

        } catch (Exception e) {
            displayDialog("Err. Report",
                    "Description: " + e.getMessage());
            return null;
        }
    }

    private void displayDialog(String dialogTitle, String dialogMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                StudentCoureRegQueryActivity.this);
        builder.setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton("Ok", null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    public void registerCoursesButtonHandler(View view) {
        if (mRegisteredCourses != null && mRegisteredCourses.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            int counter = 0;
            for(Course course : mRegisteredCourses) {
                stringBuilder.append(++counter + ". " + course.getCode() + "\n");
            }

            //Display a confirmation dialog
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(StudentCoureRegQueryActivity.this);
            builder.setTitle("LIST OF SELECTED COURSES TO REGISTER");
            builder.setMessage(stringBuilder.toString());

            builder.setPositiveButton("|Proceed >>|", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                     registerSelection();
                }
            });

            builder.setNegativeButton("|<< Back|", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            Dialog dialog = builder.create();
            dialog.show();

        } else {
            displayDialog("Oops!",
                    "You have not made any course selection. \n " +
                            "No Course to register");
        }

    }

    private void registerSelection() {
        createTableIfNotExist(REGISTERED_COURSES_TABLE);


        try {

            for (Course courseItem : mRegisteredCourses) {
                String matric, code, title, lecturer;
                int unit;
                matric = currentStudent.getMatricNumber();
                code = courseItem.getCode();
                unit = courseItem.getUnit();
                lecturer = courseItem.getCourseLecturerID();
                title = courseItem.getCourseTitle();


                String insertSQL = "INSERT INTO " + REGISTERED_COURSES_TABLE +
                        "(matric, level, session, semester, c_code, c_title,c_unit, c_lecturer) " +
                        "VALUES('" + matric + "', '" + level + "', '" + session + "', '" +
                        semester + "', '" + code + "', '" + title + "', " + unit + ", '" +
                        lecturer + "');";


                /*If a particular course is not already registered for the current session,
                  register it. */

                if (!courseAlreadyRegistered(currentStudent.getMatricNumber(), level, session,
                        semester, code)) {

                    mDatabase.execSQL(insertSQL);

                } else {
                    displayDialog("Registration Report",
                            code + " Already registered for " + semester +
                                    " semester " + session + " session");

                }

            }

            displayDialog("Success Report",
                    "Courses Successfully Registered for " + semester +
                            " semester " + session + " session");

            startActivity(new Intent(StudentCoureRegQueryActivity.this,
                    LoginActivity.class));
            this.finish();

        } catch (Exception e) {
                displayDialog("Oops!", "Description:\n" +
                        e.getMessage());
        }
        finally {
            if (mDatabase.isOpen()) mDatabase.close();
        }

    }

    private boolean courseAlreadyRegistered(String matricNumber, String level,
                                            String session, String semester, String code) {
        Boolean reply = false;
        String selectSQL = "SELECT * FROM " + REGISTERED_COURSES_TABLE + " " +
                "WHERE matric = '" + matricNumber + "' " +
                "AND level = '" + level + "' " +
                "AND session = '" + session + "' " +
                "AND semester = '" + semester + "' " +
                "AND c_code = '" + code + "';";

        Cursor result = mDatabase.rawQuery(selectSQL, null);
        //if record already exists return true
        if (result.moveToFirst()) {
            reply = true;
        }

        if (result != null && !result.isClosed()) result.close();

        return reply;
    }

    private void createTableIfNotExist(String registeredCourses) {
        mDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + registeredCourses + "(\n" +
                " id INTEGER NOT NULL CONSTRAINT " + registeredCourses +
                "_pk PRIMARY KEY AUTOINCREMENT, \n" +
                " matric varchar(15) NOT NULL, \n" +
                " level varchar(15) NOT NULL, \n" +
                " session varchar(12) NOT NULL, \n" +
                " semester varchar(25) NOT NULL, \n" +
                " c_code varchar(10) NOT NULL, \n" +
                " c_title varchar(50) NOT NULL, \n" +
                " c_unit INTEGER NOT NULL, \n" +
                " c_lecturer varchar(25) \n" +
                ");"
        );

    }

    private void removeFromRegisteredCourses(Course course) {
        for (int i = 0; i < mRegisteredCourses.size(); i++) {
            if (mRegisteredCourses.get(i).getCode().equalsIgnoreCase(course.getCode())) {
                mRegisteredCourses.remove(i);
            }
        }
    }

    private boolean courseAlreadyAdded(Course course) {
        for (Course courseItem : mRegisteredCourses) {
            if (courseItem.getCode().equalsIgnoreCase(course.getCode())) {
                return true;
            }
        }

        return false;
    }


    class CourseAdapterView extends ArrayAdapter<Course>{
        Context mContext;
        List<Course> courses;

        public CourseAdapterView(@NonNull Context context, int resource, @NonNull List<Course> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.courses = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater =
                    (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View row  = inflater.inflate(R.layout.courses_custom_list_layout,null);

            TextView tvSerialNumber = (TextView) row.findViewById(R.id.tvSN);
            TextView tvCode = (TextView) row.findViewById(R.id.tvCod);
            TextView tvTitle = (TextView) row.findViewById(R.id.tvTit);
            TextView tvUnit = (TextView) row.findViewById(R.id.tvUni);
            final CheckBox chkSelectedCourse = (CheckBox) row.findViewById(R.id.chkSelectCourse);

            final Course myCourse = courses.get(position);
            tvSerialNumber.setText(String.valueOf(position + 1));
            tvCode.setText(myCourse.getCode());
            tvTitle.setText(myCourse.getCourseTitle());
            tvUnit.setText(String.valueOf(myCourse.getUnit()));
            
            chkSelectedCourse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(chkSelectedCourse.isChecked() && !courseAlreadyAdded(myCourse)){
                        mRegisteredCourses.add(myCourse);
                        Toast.makeText(mContext, myCourse.getCode() + " added to list",
                                Toast.LENGTH_LONG).show();
                    }
                    else if(!chkSelectedCourse.isChecked() && courseAlreadyAdded(myCourse)){
                        removeFromRegisteredCourses(myCourse);
                        Toast.makeText(mContext, myCourse.getCode() + " removed from list",
                                Toast.LENGTH_LONG).show();
                    }
                        
                }
            });
            
            return row;
        }


    }
}