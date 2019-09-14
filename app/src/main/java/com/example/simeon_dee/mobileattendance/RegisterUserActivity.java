package com.example.simeon_dee.mobileattendance;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class RegisterUserActivity extends AppCompatActivity {
    SQLiteDatabase mDatabase;
    EditText etUsername,etPassword,etEmail, etConfirmPassword;
    Spinner spinUserType;
    String uname,pswd, confirmPswd, email,userType;
    final String USER_ACCOUNT_TABLE_NAME = "userAccounts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        try{
            if(mDatabase == null || !mDatabase.isOpen()){
                mDatabase = openOrCreateDatabase(LoginActivity.DBASE_NAME,MODE_PRIVATE,null);
            }
            createTable(USER_ACCOUNT_TABLE_NAME);

            etUsername = (EditText) findViewById(R.id.uname);
            etPassword = (EditText) findViewById(R.id.pswd);
            etConfirmPassword = (EditText) findViewById(R.id.comfirmpswd);
            etEmail = (EditText) findViewById(R.id.email);
            spinUserType = (Spinner) findViewById(R.id.spinUserType);

        } catch (Exception e){
            Dialog dialog = new AlertDialog.Builder(RegisterUserActivity.this)
                    .setTitle("Problem Connecting to database")
                    .setMessage("Cannot Connect to database \n Description: \n " + e.getMessage())
                    .create();
            dialog.show();
        }


    }

    private void createTable(String tableName) {
        mDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS " + tableName + "(\n" +
                        " username varchar(15) NOT NULL CONSTRAINT " + tableName +
                        "_pk PRIMARY KEY, \n" +
                        " password varchar(100) NOT NULL, \n" +
                        " email varchar(200), \n" +
                        " userCategory varchar(15) NOT NULL, \n" +
                        " surname varchar(50), \n" +
                        " middlename varchar(50), \n" +
                        " firstname varchar(50), \n" +
                        " faculty varchar(50), \n" +
                        " department varchar(50), \n" +
                        " passport blob \n" +
                        ");"

        );
    }

    public void createUserButtonClickedHandler(View view) {
        //Add codes to perform actions and start intent if user is created successfully
        uname = etUsername.getText().toString().toUpperCase();
        pswd = etPassword.getText().toString();
        confirmPswd = etConfirmPassword.getText().toString();
        email = etEmail.getText().toString();
        userType = spinUserType.getSelectedItem().toString();

        try
        {
            if(inputsAreCorrect(uname,pswd,confirmPswd,email,userType)){
                Cursor cursor = mDatabase.rawQuery("SELECT * FROM userAccounts WHERE username='" +
                        uname + "' and password = '" + pswd + "';", null);

                if(cursor.moveToFirst()){

                    Toast.makeText(RegisterUserActivity.this,"User Already Exists",
                            Toast.LENGTH_LONG).show();
                    etUsername.setError("Already Existing User");
                    etUsername.requestFocus();

                }else{
                    String insertSQL = "INSERT INTO userAccounts\n" +
                            "(username,password,email,userCategory)\n" +
                            "VALUES(?,?,?,?);";
                    mDatabase.execSQL(insertSQL,new String[]{uname,pswd,email,userType});
                    displayDialog("Success",
                            "Account Successfully Initiated\n Tap on \'Next-->\'" +
                            " button to continue.");

                    LoginActivity.currentUser.setID(uname);
                    LoginActivity.currentUser.setEmail(email);
                    LoginActivity.currentUser.setUserCategory(userType);

                    if(mDatabase.isOpen()){ mDatabase.close(); }
                    Intent intent = new Intent(RegisterUserActivity.this,
                            PassportUploadActivity.class);
                    startActivity(intent);
                }

                cursor.close();

            }

        }catch (Exception e){
            Toast.makeText(RegisterUserActivity.this,e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }



    }

    private void displayDialog(String dialogTitle, String dialogMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterUserActivity.this);
        builder.setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton("Ok",null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    private boolean inputsAreCorrect(String uname, String pswd, String confirmPswd, String email, String userType) {
        if(uname == null || uname.isEmpty() || !uname.contains("/")){
            etUsername.setError("Required Field or Invalid Format");
            etUsername.requestFocus();
            return false;
        }

        if(pswd == null || pswd.isEmpty()){
            etPassword.setError("Required Field");
            etPassword.requestFocus();
            return false;
        }

        if(confirmPswd == null || pswd.isEmpty()){
            etConfirmPassword.setError("Required Field");
            etConfirmPassword.requestFocus();
            return false;
        }

        if(email == null || email.isEmpty()){
            etEmail.setError("Required Field");
            etEmail.requestFocus();
            return false;
        }

        if(userType == null || userType.isEmpty() ||
                userType.equalsIgnoreCase("--select user type--")){

            Toast.makeText(RegisterUserActivity.this,"User Type not Selected",
                    Toast.LENGTH_LONG).show();
            etUsername.requestFocus();
            return false;
        }

        if(!pswd.equalsIgnoreCase(confirmPswd)){

            Toast.makeText(RegisterUserActivity.this,"Please retype your password\n" +
                            "Password mismatch",
                    Toast.LENGTH_LONG).show();
            etPassword.setError("Password Mismatch");
            etPassword.setText("");
            etConfirmPassword.setText("");
            etConfirmPassword.requestFocus();
            return false;
        }

        return true;

    }
}
