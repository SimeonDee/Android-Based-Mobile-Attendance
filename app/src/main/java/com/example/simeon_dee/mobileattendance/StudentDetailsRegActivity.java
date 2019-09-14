package com.example.simeon_dee.mobileattendance;

import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class StudentDetailsRegActivity extends AppCompatActivity {

    EditText mSurname, mMiddlename, mFirstname;
    Spinner facultySpinner;
    Spinner departmentSpinner;
    ArrayAdapter<String> deptSpinnerArrayAdapter;
    SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details_reg);

        mSurname = (EditText) findViewById(R.id.edSurname);
        mMiddlename = (EditText) findViewById(R.id.edMiddlename);
        mFirstname = (EditText) findViewById(R.id.edFirstname);
        facultySpinner = (Spinner) findViewById(R.id.spinFaculty);
        departmentSpinner = (Spinner) findViewById(R.id.spinDepartment);

        try{
            facultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedFaculty = (String) adapterView.getSelectedItem();

                    loadDepartments(selectedFaculty);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Toast.makeText(StudentDetailsRegActivity.this,
                            "You have to select your faculty", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e){
            displayDialog("Oops!", "Description:\n" + e.getMessage());
        }
    }

    public void loadDepartments(String faculty){
        departmentSpinner.setAdapter(null);
        deptSpinnerArrayAdapter = new ArrayAdapter<String>(StudentDetailsRegActivity.this,
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
                deptSpinnerArrayAdapter.add("ICT Center");
                break;
        }

        departmentSpinner.setAdapter(deptSpinnerArrayAdapter);
    }

    public void continueButtonClickHandler(View view) {
        String surname, middlename, firstname, faculty, dept;
        surname = mSurname.getText().toString();
        middlename = mMiddlename.getText().toString();
        firstname = mFirstname.getText().toString();
        faculty = facultySpinner.getSelectedItem().toString();
        dept = departmentSpinner.getSelectedItem().toString();

        try{

            if(inputsAreCorrect(surname,middlename,firstname,faculty,dept)) {
                if ((mDatabase == null || !mDatabase.isOpen())) {
                    mDatabase = openOrCreateDatabase(LoginActivity.DBASE_NAME, MODE_PRIVATE, null);
                }

                String updateSQl = "UPDATE userAccounts \n" +
                        "SET surname = '" + surname + "', " +
                        "middlename = '" + middlename + "', " +
                        "firstname = '" + firstname + "', " +
                        "faculty = '" + faculty + "', " +
                        "department = '" + dept + "' " +
                        "WHERE username = '" + LoginActivity.currentUser.getID() + "';";
                mDatabase.execSQL(updateSQl);

                if (mDatabase.isOpen()) {
                    mDatabase.close();
                }

                displayDialog("Success", "Records Updated Successfully\n" +
                        " Tap on \'Continue\' button to Continue Registeration.");

                LoginActivity.currentUser.setName(new Name(surname,middlename,firstname));
                LoginActivity.currentUser.setDepartment(dept);
                LoginActivity.currentUser.setFaculty(faculty);

                switch(LoginActivity.currentUser.getUserCategory().toUpperCase()){
                    case "STAFF":
                    case "ADMIN":
                        startActivity(new Intent(StudentDetailsRegActivity.this,
                                LoginActivity.class));
                        break;
                    default:        //case "STUDENT"
                        startActivity(new Intent(StudentDetailsRegActivity.this,
                                StudentCoureRegQueryActivity.class));
                        break;

                }

            }

        } catch (Exception e){
            displayDialog("Err. Report","Description: " + e.getMessage());
        }
    }

    private void displayDialog(String dialogTitle, String dialogMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StudentDetailsRegActivity.this);
        builder.setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton("Ok",null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    private boolean inputsAreCorrect(String surname, String middlename, String firstname,
                                     String faculty, String dept) {
        if(surname == null || surname.isEmpty()){
            mSurname.setError("Required Field");
            mSurname.requestFocus();
            return false;
        }

        if(firstname == null || firstname.isEmpty()){
            mFirstname.setError("Required Field");
            mFirstname.requestFocus();
            return false;
        }

        if(faculty == null || faculty.isEmpty() ||
                faculty.equalsIgnoreCase("--select faculty--")){

            Toast.makeText(StudentDetailsRegActivity.this,"Faculty not selected",
                    Toast.LENGTH_LONG).show();
            facultySpinner.requestFocus();
            return false;
        }

        if(dept == null || dept.isEmpty() ||
                dept.equalsIgnoreCase("--select department--")){

            Toast.makeText(StudentDetailsRegActivity.this,"Department not selected",
                    Toast.LENGTH_LONG).show();
            departmentSpinner.requestFocus();
            return false;
        }

        return true;
    }
}
