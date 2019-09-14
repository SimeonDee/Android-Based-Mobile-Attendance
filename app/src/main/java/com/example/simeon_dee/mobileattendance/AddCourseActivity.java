package com.example.simeon_dee.mobileattendance;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class AddCourseActivity extends AppCompatActivity {

    SQLiteDatabase mDatabase;
    final String COURSE_TABLE_NAME = "courses";
    Spinner spinFaculty, spinDept,spinLevel,spinSemester;
    EditText etCode, etTitle, etUnit, etLecturerID;
    Button btRegister, btGetCourse;
    LinearLayout linearLayoutCourseDetails;
    Course course;

    ArrayAdapter<String> deptSpinnerArrayAdapter;
    String fac, dept, level, semester, code,title,unit,lecturer;

    String tempTitle, tempLecturer;
    int tempUnit, courseID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_course_layout);

        spinFaculty = (Spinner) findViewById(R.id.spinFac);
        spinDept = (Spinner) findViewById(R.id.spinDept);
        spinLevel = (Spinner) findViewById(R.id.spinLev);
        spinSemester = (Spinner) findViewById(R.id.spinSem);
        etCode = (EditText) findViewById(R.id.etCourseCode);
        etTitle = (EditText) findViewById(R.id.etCourseTitle);
        etUnit= (EditText) findViewById(R.id.etCourseUnit);
        etLecturerID = (EditText) findViewById(R.id.etCourseLecturerID);
        btRegister = (Button) findViewById(R.id.btRegisterCourse);
        btGetCourse = (Button) findViewById(R.id.btGetCourse);
        linearLayoutCourseDetails = (LinearLayout) findViewById(R.id.linearCourseDetails);

        btRegister.setText(AdminMenu.operationType);

        if(AdminMenu.operationType.equalsIgnoreCase("Update Course") ||
                AdminMenu.operationType.equalsIgnoreCase("Delete Course")){
            btGetCourse.setVisibility(View.VISIBLE);

            linearLayoutCourseDetails.setVisibility(View.INVISIBLE);
        }

        try{
            spinFaculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedFaculty = (String) adapterView.getSelectedItem();

                    loadDepartments(selectedFaculty);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            if(mDatabase == null || !mDatabase.isOpen()){
                mDatabase = openOrCreateDatabase(LoginActivity.DBASE_NAME,MODE_PRIVATE,null);
            }
            createTable(COURSE_TABLE_NAME);

        } catch (Exception e) {
            displayDialog("Oops!",
                    "Description: " + e.getMessage());
        }

    }

    private void loadDepartments(String selectedFaculty) {
        spinDept.setAdapter(null);
        deptSpinnerArrayAdapter = new ArrayAdapter<String>(AddCourseActivity.this,
                android.R.layout.simple_spinner_item, android.R.id.text1);

        switch (selectedFaculty.trim())
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

    private void createTable(String tableName) {
        mDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS " + tableName + "(\n" +
                        " id INTEGER NOT NULL CONSTRAINT " + tableName +
                        "_pk PRIMARY KEY AUTOINCREMENT, \n" +
                        " faculty varchar(100) NOT NULL, \n" +
                        " department varchar(100) NOT NULL, \n" +
                        " level varchar(15) NOT NULL, \n" +
                        " semester varchar(25) NOT NULL, \n" +
                        " c_code varchar(10) NOT NULL, \n" +
                        " c_title varchar(50) NOT NULL, \n" +
                        " c_unit INTEGER NOT NULL, \n" +
                        " c_lecturer varchar(25) \n" +
                        ");"

        );
    }


    public void registerCourseButtonHandler(View view) {
        getInputs();
        if(inputsAreCorrect(fac,dept,level,semester,code,title,unit,lecturer)){
            course = new Course(fac,dept,level,semester,code,title,Integer.parseInt(unit),lecturer);
            String operationType = btRegister.getText().toString();

            if(operationType.equalsIgnoreCase("Add Course")){
                //add new course if it is not already in the database
                addNewCourseIfNotExists(course);
            }
            else if(operationType.equalsIgnoreCase("Update Course")){
                //Update an Already Existing Course
                if(courseID > 0) {
                    updateCourse(course,courseID);
                }

            }
            else if(operationType.equalsIgnoreCase("Delete Course")) {
                if(courseID > 0) {
                    deleteCourse(course,courseID);
                }
            }

        }

    }

    private void addNewCourseIfNotExists(Course course) {
        if(courseAlreadyExists(course) > 0){

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(AddCourseActivity.this);

            builder.setTitle("ALREADY EXISTING COURSE")
                    .setMessage("That course code already exists for that class\n" +
                            " Assigned to Staff " + tempLecturer + ".\n" +
                            "Do you want to Edit the Existing Course?")
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            etTitle.setText(tempTitle);
                            etUnit.setText(String.valueOf(tempUnit));
                            etLecturerID.setText(tempLecturer);
                            btRegister.setText("Update Course");

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            etCode.setError("Course Already Exits");
                        }
                    });
            Dialog dialog = builder.create();
            dialog.show();

        }
        else{
            addNewCourse(course);
        }
    }

    private int courseAlreadyExists(Course course) {
        int recordFound;
        String selectSQL = "SELECT * FROM " + COURSE_TABLE_NAME + " " +
                "WHERE " +
                "faculty = '" + course.getFaculty() + "' AND " +
                "department = '" +  course.getDepartment() + "' AND " +
                "level = '" +  course.getLevel() + "' AND " +
                "semester = '" +  course.getSemester() + "' AND " +
                "c_code = '" +  course.getCode() + "';";
        try{
            Cursor results = mDatabase.rawQuery(selectSQL,null);

            //If course already exists, ask if user wants to update course
            if(results.moveToFirst()){
                recordFound = results.getInt(0);
                tempTitle = results.getString(6);
                tempUnit = results.getInt(7);
                tempLecturer = results.getString(8);


            }
            else {
                recordFound = 0;
            }

            //Close the cursor if not yet closed
            if(!results.isClosed()) results.close();

            return recordFound;

        } catch (Exception e){
            displayDialog("Oops...","Description: \n" + e.getMessage());
            recordFound = 0;
            return recordFound;
        }

    }

    private void addNewCourse(Course course) {
        try{

            String insertSQL = "INSERT INTO " + COURSE_TABLE_NAME + " \n" +
                    "(faculty, department, level, semester, c_code, c_title, c_unit, c_lecturer)\n" +
                    " VALUES('" +
                    course.getFaculty() + "','" + course.getDepartment() + "','" +
                    course.getLevel() + "','" + course.getSemester() + "','" +
                    course.getCode() + "','" + course.getCourseTitle() + "'," +
                    course.getUnit() + ",'" + course.getCourseLecturerID() +
                    "');";

            mDatabase.execSQL(insertSQL);
            displayDialog("Success Report","Course Added Successfully");
            clearInputs();

        } catch (Exception e){
            displayDialog("Problem Adding Course to Database",
                    "Description:\n" + e.getMessage());
        }

    }


    private void clearInputs() {
        etCode.setText("");
        etTitle.setText("");
        etUnit.setText("");
        etLecturerID.setText("");
        etCode.requestFocus();
    }

    private void updateCourse(Course course, int id) {
        try{

            String updateSQL = "UPDATE " + COURSE_TABLE_NAME + "\n" +
                    "SET faculty = '" + course.getFaculty() + "',\n" +
                    "department = '" +  course.getDepartment() + "',\n" +
                    "level = '" +  course.getLevel() + "',\n" +
                    "semester = '" +  course.getSemester() + "',\n" +
                    "c_code = '" +  course.getCode() + "',\n" +
                    "c_title = '" +  course.getCourseTitle() + "',\n" +
                    "c_unit = " +  course.getUnit() + ",\n" +
                    "c_lecturer = '" +  course.getCourseLecturerID() + "' " +
                    "WHERE id = " + id + ";";

            mDatabase.execSQL(updateSQL);
            displayDialog("Success Report","Course Successfully Updated");
            btRegister.setText("Register Course");
            clearInputs();

        } catch (Exception e){
            displayDialog("Problem Updating Course",
                    "Description:" + e.getMessage());
        }
    }

    private void deleteCourse(Course course, int id){
        try{

            String updateSQL = "DELETE FROM " + COURSE_TABLE_NAME + "\n" +
                    "WHERE id = " + id + ";";

            mDatabase.execSQL(updateSQL);
            displayDialog("Success Report","Course Successfully Updated");
            btRegister.setText("Register Course");
            clearInputs();

        } catch (Exception e){
            displayDialog("Problem Updating Course",
                    "Description:" + e.getMessage());
        }
    }

    private void getInputs() {
        fac = spinFaculty.getSelectedItem().toString();
        dept = spinDept.getSelectedItem().toString();
        level = spinLevel.getSelectedItem().toString();
        semester = spinSemester.getSelectedItem().toString();
        code = etCode.getText().toString().toUpperCase();
        title = etTitle.getText().toString();
        unit = etUnit.getText().toString();
        lecturer = etLecturerID.getText().toString().toUpperCase();
    }

    private boolean inputsAreCorrect(String fac, String dept, String level,
                                     String semester, String code, String title,
                                     String unit, String lecturer) {
        if(fac == null || fac.isEmpty() ||
                fac.equalsIgnoreCase("--select faculty--")){

            Toast.makeText(AddCourseActivity.this,"Faculty not selected",
                    Toast.LENGTH_LONG).show();
            spinFaculty.requestFocus();
            return false;
        }

        if(dept == null || dept.isEmpty() ||
                dept.equalsIgnoreCase("--select department--")){

            Toast.makeText(AddCourseActivity.this,"Department not selected",
                    Toast.LENGTH_LONG).show();
            spinDept.requestFocus();
            return false;
        }

        if(level == null || level.isEmpty() ||
                level.equalsIgnoreCase("--select level--")){

            Toast.makeText(AddCourseActivity.this,"Level not selected",
                    Toast.LENGTH_LONG).show();
            spinLevel.requestFocus();
            return false;
        }

        if(semester == null || semester.isEmpty() ||
                semester.equalsIgnoreCase("--select semester--")){

            Toast.makeText(AddCourseActivity.this,"Semester not selected",
                    Toast.LENGTH_LONG).show();
            spinSemester.requestFocus();
            return false;
        }

        if(code == null || code.isEmpty()){
            etCode.setError("Required Field or Invalid Format");
            etCode.requestFocus();
            return false;
        }

        if(title == null || title.isEmpty()){
            etTitle.setError("Required Field");
            etTitle.requestFocus();
            return false;
        }

        if(unit == null || unit.isEmpty()){
            etUnit.setError("Required Field");
            etUnit.requestFocus();
            return false;
        }

        if(lecturer == null || lecturer.isEmpty()){
            etLecturerID.setError("Required Field");
            etLecturerID.requestFocus();
            return false;
        }

        return true;
    }

    private boolean inputsAreCorrect(String fac, String dept, String level,
                                     String semester, String code) {
        if (fac == null || fac.isEmpty() ||
                fac.equalsIgnoreCase("--select faculty--")) {

            Toast.makeText(AddCourseActivity.this, "Faculty not selected",
                    Toast.LENGTH_LONG).show();
            spinFaculty.requestFocus();
            return false;
        }

        if (dept == null || dept.isEmpty() ||
                dept.equalsIgnoreCase("--select department--")) {

            Toast.makeText(AddCourseActivity.this, "Department not selected",
                    Toast.LENGTH_LONG).show();
            spinDept.requestFocus();
            return false;
        }

        if (level == null || level.isEmpty() ||
                level.equalsIgnoreCase("--select level--")) {

            Toast.makeText(AddCourseActivity.this, "Level not selected",
                    Toast.LENGTH_LONG).show();
            spinLevel.requestFocus();
            return false;
        }

        if (semester == null || semester.isEmpty() ||
                semester.equalsIgnoreCase("--select semester--")) {

            Toast.makeText(AddCourseActivity.this, "Semester not selected",
                    Toast.LENGTH_LONG).show();
            spinSemester.requestFocus();
            return false;
        }

        if (code == null || code.isEmpty()) {
            etCode.setError("Required Field or Invalid Format");
            etCode.requestFocus();
            return false;
        }

        return true;
    }
    private void displayDialog(String dialogTitle, String dialogMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddCourseActivity.this);
        builder.setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton("Ok",null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    public void btGetCourseClickHandler(View view) {
        getInputs();
        if(inputsAreCorrect(fac,dept,level,semester,code)){
            Course inputCourse = new Course(fac,dept,level,semester,code);
            courseID = courseAlreadyExists(inputCourse);
            if(courseID > 0){
                linearLayoutCourseDetails.setVisibility(View.VISIBLE);
                etTitle.setText(tempTitle);
                etUnit.setText(String.valueOf(tempUnit));
                etLecturerID.setText(tempLecturer);
            }
        }

    }
}
