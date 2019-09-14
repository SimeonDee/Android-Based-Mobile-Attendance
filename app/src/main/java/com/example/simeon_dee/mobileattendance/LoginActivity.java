package com.example.simeon_dee.mobileattendance;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    public static User currentUser;
    private EditText etUsername, etPassword;
    String username, password;
    SQLiteDatabase mDatabase;
    public static final String DBASE_NAME = "AttendanceDBase";
    public static final String USER_ACCOUNTS_TABLE_NAME = "userAccounts";

    Boolean detailsFilled = false, passportUploaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        etUsername = (EditText) findViewById(R.id.username);
        etPassword = (EditText) findViewById(R.id.password);
        currentUser = new User();

        try{
            if(mDatabase == null){
                mDatabase = openOrCreateDatabase(DBASE_NAME,MODE_PRIVATE,null);
            }

        } catch (Exception e){
           displayDialog("Problem Connecting to database",
                   "Description: \n " + e.getMessage());
        }
    }

    public void btLoginHandler(View view) {
        try{
            username = etUsername.getText().toString().toUpperCase();
            password = etPassword.getText().toString();

            if(isInputCorrect(username,password)) {

                if (isUserAuthenticated(username, password)) {

                    //------------------------------------------------
                    //Remember to navigate to appropriate Activity based on user category
                    //-------------------------------------------------
                    if (detailsFilled) {
                        if (passportUploaded) {
                            String uType = currentUser.getUserCategory().toUpperCase();
                            switch (uType){
                                case "STUDENT":
                                startActivity(new Intent(LoginActivity.this,
                                        StudentCoureRegQueryActivity.class));
                                break;

                                case "STAFF":
                                    startActivity(new Intent(LoginActivity.this,
                                        LecturerMenuActivity.class));
                                    break;

                                case "ADMIN":
                                    startActivity(new Intent(LoginActivity.this,
                                        AdminMenu.class));
                                    break;
                            }

//                            if (uType.equalsIgnoreCase("STUDENT")) {
//                                startActivity(new Intent(LoginActivity.this,
//                                        StudentCoureRegQueryActivity.class));
//
//                            }
//                            else if (uType.equalsIgnoreCase("STAFF")) {
//                                startActivity(new Intent(LoginActivity.this,
//                                        LecturerMenuActivity.class));
//
//                            }
//                            else if (uType.equalsIgnoreCase("ADMIN")) {
//
//                                //*********************
//                                //navigate accordingly
//                                //********************
//                                startActivity(new Intent(LoginActivity.this,
//                                        AddCourseActivity.class));
//                            }

                        }
                        else {
                            displayDialog("Information",
                                    "Please complete your registration");
                            startActivity(new Intent(LoginActivity.this,
                                    PassportUploadActivity.class));
                        }

                    }
                    else {

                        displayDialog("Information",
                                "Please complete your registration");
                        startActivity(new Intent(LoginActivity.this,
                                StudentDetailsRegActivity.class));
                    }

                }
                else {
                    //User cannot be authenticated i.e. not previously registered
                    displayDialog("Record Not Found", "User does not exist. \n " +
                            "Check Username or Password, OR \n " +
                            "Register as A New User");
                    etUsername.requestFocus();
                }
            }

        } catch (Exception e){
            displayDialog("Oops!", "Description: \n" + e.getMessage());
            Log.d("LOGIN_ERROR",e.getMessage());
        }
    }

    private void displayDialog(String dialogTitle, String dialogMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton("Ok",null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    private boolean isUserAuthenticated(String username, String password) {
        Cursor results = null;
        try {
            //Connect to database and fetch records to compare
            results = mDatabase.rawQuery("SELECT * FROM " +
                            USER_ACCOUNTS_TABLE_NAME + " WHERE " +
                            "username = '" + username + "' and password = '" + password + "';",
                    null);

            //check if record exists
            if (results.moveToFirst()) {
                String uname, utype;
                uname = results.getString(0);
                utype = results.getString(3);
                currentUser.setID(uname);    // 0: index of username field
                currentUser.setUserCategory(utype);//3: index of userCategory field

                //Check if students details has been entered
                String udetails = results.getString(4);
                if (udetails != null) {
                    String fac, dept;
                    fac = results.getString(7);
                    dept = results.getString(8);

                    currentUser.setFaculty(fac);
                    currentUser.setDepartment(dept);
                    detailsFilled = true;
                }

                //Check if passport has been uploaded
                byte[] passport = results.getBlob(9);
                if (passport != null) {       //9: index of passport field
                    passportUploaded = true;
                }

                if(results != null) results.close();
                return true;
            }else{
                return false;
            }

        } catch (Exception e){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Database Problem")
                    .setMessage("Error Fetching Data from Database. \n Description: \n " +
                            e.getMessage());
            Dialog dialog = builder.create();
            dialog.show();
            return false;
        }

    }

    private boolean isInputCorrect(String username, String password) {
        if(username == null || username.isEmpty()){
            etUsername.setError("Required Field");
            etUsername.requestFocus();
            Toast.makeText(LoginActivity.this,"Supply your username",Toast.LENGTH_LONG)
                    .show();
            return false;

        }else if(!username.contains("/")){
            etUsername.setError("Invalid username format");
            etUsername.requestFocus();
            Toast.makeText(LoginActivity.this,"Please check your username format",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if(password == null || password.isEmpty()){
            etPassword.setError("Required Field");
            etPassword.requestFocus();
            Toast.makeText(LoginActivity.this,"Supply your password",Toast.LENGTH_LONG)
                    .show();
            return false;
        }

        return true;
    }

    public void signUpLinkClickHandler(View view) {
        Intent intent = new Intent(LoginActivity.this,RegisterUserActivity.class);
        startActivity(intent);
    }
}
