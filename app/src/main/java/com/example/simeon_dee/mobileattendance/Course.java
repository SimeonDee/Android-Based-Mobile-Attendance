package com.example.simeon_dee.mobileattendance;

/**
 * Created by SIMEON_DEE on 3/15/2018.
 */

public class Course {

    String mCode, mTitle, mSemester, mLevel, mDept, mFaculty, mCourseLecturerID;
    int mUnit;

    public Course(String faculty, String dept,  String level, String semester, String code,
                  String title, int unit, String courseLecturerID){
        this.mCode = code;
        this.mTitle = title;
        this.mUnit = unit;
        this.mSemester = semester;
        this.mLevel = level;
        this.mDept = dept;
        this.mFaculty = faculty;
        this.mCourseLecturerID = courseLecturerID;
    }

    public Course(String faculty, String dept, String level, String semester, String code){
        this.mFaculty = faculty;
        this.mDept = dept;
        this.mLevel = level;
        this.mSemester = semester;
        this.mCode = code;
        this.mTitle = "";
        this.mUnit = 0;
        this.mCourseLecturerID = "";
    }

    public Course(String code, String title, int unit){
        this.mFaculty = "";
        this.mDept = "";
        this.mLevel = "";
        this.mSemester = "";
        this.mCode = code;
        this.mTitle = title;
        this.mUnit = unit;
        this.mCourseLecturerID = "";
    }

    public Course(){
        this.mFaculty = "";
        this.mDept = "";
        this.mLevel = "";
        this.mSemester = "";
        this.mCode = "";
        this.mTitle = "";
        this.mUnit = 0;
        this.mCourseLecturerID = "";
    }

    void setCode(String code){ mCode = code; }

    String getCode(){ return mCode; }

    void setCourseTitle(String title){ mTitle = title; }

    String getCourseTitle(){ return mTitle; }

    void setUnit(int unit){ mUnit = unit; }

    int getUnit(){ return mUnit; }

    void setSemester(String semester){ mSemester = semester; }

    String getSemester(){ return mSemester; }

    void setLevel(String level){ mLevel = level; }

    String getLevel(){ return mLevel; }

    void setDepartment(String dept){ mDept = dept; }

    String getDepartment(){ return mDept; }

    void setFaculty(String faculty){ mFaculty = faculty; }

    String getFaculty(){ return mFaculty; }

    void setCourseLecturer(String lecturerID){ this.mCourseLecturerID = lecturerID; }
    String getCourseLecturerID(){ return this.mCourseLecturerID; }

    @Override
    public String toString() {
        return mCode;
    }

}
