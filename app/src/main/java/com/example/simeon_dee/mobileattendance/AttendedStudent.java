package com.example.simeon_dee.mobileattendance;

/**
 * Created by SIMEON_DEE on 6/4/2018.
 */

public class AttendedStudent extends Student{
    private int mTotalAttendance;

    public AttendedStudent(String matric, Name name, String faculty, String dept, String level,
                           String program, byte[] passport,  int totalAttendance){
        super.setID(matric);
        super.setName(name);
        super.setFaculty(faculty);
        super.setDepartment(dept);
        super.setLevel(level);
        super.setProgram(program);
        super.setPassport(passport);
        mTotalAttendance = totalAttendance;
    }

    public AttendedStudent(Student student, int totalAttendance) {
        super.setID(student.getID());
        super.setName(student.getName());
        super.setFaculty(student.getFaculty());
        super.setDepartment(student.getDepartment());
        super.setLevel(student.getLevel());
        super.setProgram(student.getProgram());
        super.setPassport(student.getPassport());
        mTotalAttendance = totalAttendance;
    }

    public AttendedStudent(){
        super.setID("");
        super.setName(null);
        super.setFaculty("");
        super.setDepartment("");
        super.setLevel("");
        super.setProgram("");
        super.setPassport(null);
        mTotalAttendance = 0;
    }

    public AttendedStudent(String matric, byte[] passport, int totalAttendance){
        super.setID(matric);
        super.setName(null);
        super.setFaculty("");
        super.setDepartment("");
        super.setLevel("");
        super.setProgram("");
        super.setPassport(passport);
        mTotalAttendance = totalAttendance;
    }

    public void setTotalAttendance(int totalAttendance){ mTotalAttendance = totalAttendance; }
    public int getTotalAttendance(){ return mTotalAttendance; }

}
