package com.example.simeon_dee.mobileattendance;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AttendanceActivity extends AppCompatActivity {

    SQLiteDatabase mDatabase;
    AttendanceArrayAdapter2 attendanceArrayAdapter;
    List<Student> registeredStudents;
    List<Student> presentStudents;
    ListView list;
    Course selectedCourse;
    final String COURSE_ATTENDANCE_TABLE = "lectureAttendance";
    final String REGISTERED_COURSES_TABLE = "registeredCourses";
    final String USER_ACCOUNT_TABLE = "userAccounts";
    ArrayAdapter<Student> presentList;
    TextView attendanceHeading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_sheet_layout);

        try{
            if(mDatabase == null || !mDatabase.isOpen()) {
                mDatabase =
                        openOrCreateDatabase(LoginActivity.DBASE_NAME,MODE_PRIVATE,null);
            }

            createTableIfNotExist(COURSE_ATTENDANCE_TABLE);

            registeredStudents = new ArrayList<>();
            presentStudents = new ArrayList<>();

            selectedCourse = LecturerAttendanceQuery.selectedCourse;
            attendanceHeading = (TextView) findViewById(R.id.tvHeading);
            attendanceHeading.setText(attendanceHeading.getText()+ selectedCourse.mCode +
                    " " + selectedCourse.mLevel + " " + selectedCourse.mDept.toUpperCase());

            registeredStudents = loadRegisteredStudents();
            if(registeredStudents != null){
                attendanceArrayAdapter = new AttendanceArrayAdapter2(
                        AttendanceActivity.this,0, registeredStudents);
                list = (ListView) findViewById(R.id.lstCategory);
                list.setAdapter(attendanceArrayAdapter);
            }

        } catch (Exception e){
            displayDialog("Oops!...",
                    "Err Description: \n" + e.getMessage());
        }
    }

    private void createTableIfNotExist(String lectureAttendance) {
        mDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + lectureAttendance + "(\n" +
                " id INTEGER NOT NULL CONSTRAINT " + lectureAttendance +
                "_pk PRIMARY KEY AUTOINCREMENT, \n" +
                " matric varchar(15) NOT NULL, \n" +
                " department varchar(50) NOT NULL, \n" +
                " level varchar(25) NOT NULL, \n" +
                " session varchar(12) NOT NULL, \n" +
                " semester varchar(25) NOT NULL, \n" +
                " c_code varchar(10) NOT NULL, \n" +
                " c_lecturer varchar(25), \n" +
                " attendance_date DATETIME NOT NULL \n" +
                ");"
        );
    }

    private void removeFromRegisteredStudents(Student student) {
        for(int i = 0; i < presentStudents.size(); i++){
            if(presentStudents.get(i).getMatricNumber()
                    .equalsIgnoreCase(student.getMatricNumber())){
                presentStudents.remove(i);
            }
        }
    }

    private boolean studentAlreadyAdded(Student student) {
        for(Student studentItem: presentStudents){
            if(studentItem.getMatricNumber().equalsIgnoreCase(student.getMatricNumber())){
                return true;
            }
        }

        return false;
    }


    private List<Student> loadRegisteredStudents() {
        //Replace all codes here with data returned from Database
        List<Student> result = new ArrayList<>();

        //*****************************NEWLY ADDED*********************************************
//        String selectSQL = "SELECT reg.matric, acc.surname, acc.middlename, acc.firstname, " +
//                "acc.passport FROM " + REGISTERED_COURSES_TABLE + " AS reg " +
//                "INNER JOIN " + USER_ACCOUNT_TABLE + " AS acc " +
//                "ON reg.matric = acc.username " +
//                "WHERE " +
//                "reg.level = '" + selectedCourse.getLevel() + "' " +
//                "AND reg.session = '" + LecturerAttendanceQuery.mSession + "' " +
//                "AND reg.semester = '" + selectedCourse.getSemester() + "' " +
//                "AND reg.c_code = '" + selectedCourse.getCode() + "' " +
//                "AND reg.c_lecturer = '" + selectedCourse.getCourseLecturerID() + "' " +
//                "ORDER BY reg.matric;";
//
//        try{
//            Cursor regStudentDetails = mDatabase.rawQuery(selectSQL,null);
//            if(regStudentDetails.moveToFirst()){
//                do{
//                    Student student = new Student();
//                    student.setMatricNumber(regStudentDetails.getString(0));
//
//                    Name name = new Name();
//                        name.setSurname(regStudentDetails.getString(1));
//                        name.setMiddlename(regStudentDetails.getString(2));
//                        name.setFirstname(regStudentDetails.getString(3));
//
//                    student.setName(name);
//                    student.setPassport(regStudentDetails.getBlob(4));
//                    student.setLevel(selectedCourse.getLevel());
//                    student.setDepartment(selectedCourse.getDepartment());
//
//                    result.add(student);
//                } while (regStudentDetails.moveToNext());
//
//            }else{
//                return null;
//            }
//
//
//            return result;
//
//        } catch (Exception e){
//            displayDialog("OOps!...","Description: \n" + e.getMessage());
//            return null;
//        }

        //*****************************END OF NEWLY ADDED**************************************


        //Fetch students records from database into 'result'
        String selectSQL2 = "SELECT matric FROM " + REGISTERED_COURSES_TABLE + " WHERE " +
                "level = '" + selectedCourse.getLevel() + "' " +
                "AND session = '" + LecturerAttendanceQuery.mSession + "' " +
                "AND semester = '" + selectedCourse.getSemester() + "' " +
                "AND c_code = '" + selectedCourse.getCode() + "' " +
                "AND c_lecturer = '" + selectedCourse.getCourseLecturerID() + "';";

        try{
            Cursor results = mDatabase.rawQuery(selectSQL2,null);
            if(results.moveToFirst()){
                do{
                    Student student = new Student();
                    student.setMatricNumber(results.getString(0));

                    Cursor regStudentDetails =  mDatabase.rawQuery(
                            "SELECT surname, middlename, firstname, passport " +
                                    "FROM userAccounts WHERE username = '" +
                                    student.getMatricNumber() + "';", null);

                    if(regStudentDetails.moveToFirst()){
                        Name name = new Name();
                        name.setSurname(regStudentDetails.getString(0));
                        name.setMiddlename(regStudentDetails.getString(1));
                        name.setFirstname(regStudentDetails.getString(2));

                        student.setName(name);
                        student.setPassport(regStudentDetails.getBlob(3));
                        student.setLevel(selectedCourse.getLevel());
                        student.setDepartment(selectedCourse.getDepartment());
                    }

                    result.add(student);
                } while (results.moveToNext());
            }else{
                return null;
            }


            return result;

        } catch (Exception e){
            displayDialog("OOps!...","Description: \n" + e.getMessage());
            return null;
        }
    }

    public void submitButtonHandler(View view) {

        try{
            if (presentStudents != null && presentStudents.size() > 0){
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(AttendanceActivity.this);
                builder.setTitle("CONFIRM SAVE...");
                builder.setMessage("Are you sure you want to save attendance");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String formattedDate = df.format(calendar.getTime());

                        StringBuilder stringBuilder = new StringBuilder();
                        String sql = "";
                        sql = "INSERT INTO " + COURSE_ATTENDANCE_TABLE +
                                "(matric, department, level, " +
                                "session, semester, c_code, c_lecturer, attendance_date) " +
                                "VALUES";
                        stringBuilder.append(sql);

                        for(int count = 0; count < presentStudents.size(); count++) {
                            Student currentStudent = presentStudents.get(count);

                            sql = "('" + currentStudent.getMatricNumber() +
                                    "','" + selectedCourse.getDepartment() +
                                    "','" + selectedCourse.getLevel() +
                                    "','" + LecturerAttendanceQuery.mSession +
                                    "','" + selectedCourse.getSemester() +
                                    "','" + selectedCourse.getCode() +
                                    "','" + selectedCourse.getCourseLecturerID() +
                                    "', DATETIME('" + formattedDate + "')" +
                                    ")";

                            stringBuilder.append(sql);

                            //add a coma and newline until the end, then add a semicolon
                            if (count < (presentStudents.size() - 1)) {
                                stringBuilder.append(", \n");

                            } else if (count == (presentStudents.size() - 1)) {
                                stringBuilder.append(";");
                            }
                        }

                        mDatabase.execSQL(stringBuilder.toString());

                        displayDialog("Report","Attendance Successfully " +
                                "Submitted");
                        Intent intent =
                                new Intent(AttendanceActivity.this,
                                        LecturerMenuActivity.class);
                        startActivity(intent);

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                Dialog dialog = builder.create();
                dialog.show();

            }
            else{
                displayDialog("Report","No Attendance taken yet");
            }

        } catch (Exception e){
            displayDialog("Oops!...",
                    "Err. Description: \n" + e.getMessage());
        }

    }

    private void displayDialog(String dialogTitle, String dialogMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceActivity.this);
        builder.setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton("Ok",null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    //Array Adapter Class
    public class AttendanceArrayAdapter2 extends ArrayAdapter<Student> {
        private Context context;
        private List<Student> registeredStudents;
        public ArrayList<Student> presentList;


        public AttendanceArrayAdapter2(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);
            this.context = context;
            this.registeredStudents = objects;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            try {


                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

                View view = inflater.inflate(R.layout.attendance_custom_list_layout, null);


                TextView tvSerialNumber = (TextView) view.findViewById(R.id.serialNumber);
                TextView tvName = (TextView) view.findViewById(R.id.fullname);
                TextView tvMatric = (TextView) view.findViewById(R.id.matricNumber);
                final CheckBox chkAttend = (CheckBox) view.findViewById(R.id.presentAbsent);
                ImageView imgPassport = (ImageView) view.findViewById(R.id.imgPassport);

                final Student currentStudent = registeredStudents.get(position);

                tvSerialNumber.setText(String.valueOf(position + 1));
                tvName.setText(currentStudent.getName().toString());
                tvMatric.setText(currentStudent.getMatricNumber());

                Bitmap bitmap = DbImageBitmapConverterUtility.getBitmapImage(
                        currentStudent.getPassport());
                if(bitmap != null){
                    imgPassport.setImageBitmap(bitmap);
                }

                chkAttend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (chkAttend.isChecked() && !studentAlreadyAdded(currentStudent)) {
                            presentStudents.add(currentStudent);
                            Toast.makeText(AttendanceActivity.this,
                                    currentStudent.getMatricNumber() +
                                            " added to attendance list", Toast.LENGTH_LONG);
                        }
                        // else if checkbox is unchecked and the course has already been added, remove it
                        // from the registered courses list
                        else if (!chkAttend.isChecked() && studentAlreadyAdded(currentStudent)) {
                            removeFromRegisteredStudents(currentStudent);
                            Toast.makeText(AttendanceActivity.this,
                                    currentStudent.getMatricNumber() +
                                            " removed from attendance list", Toast.LENGTH_LONG);
                        }


                    }
                });

                return view;


            } catch (Exception ex) {
                displayDialog("Oops!..", "Description:\n" + ex.getMessage());
                return null;
            }
        }
    }

}
