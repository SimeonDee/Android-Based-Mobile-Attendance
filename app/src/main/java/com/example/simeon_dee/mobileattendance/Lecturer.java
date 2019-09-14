package com.example.simeon_dee.mobileattendance;

/**
 * Created by SIMEON_DEE on 3/21/2018.
 */

public class Lecturer extends User {

    public Lecturer(String staffID, Name name, String faculty, String dept){
        super.setID(staffID);
        super.setName(name);
        super.setFaculty(faculty);
        super.setDepartment(dept);
    }

    public Lecturer(String staffID, Name name){
        super.setID(staffID);
        super.setName(name);
        super.setFaculty("");
        super.setDepartment("");
    }

    public Lecturer(String staffID){
        super.setID(staffID);
        super.setName(null);
        super.setFaculty("");
        super.setDepartment("");
    }

    public Lecturer(){
        super.setID("");
        super.setName(null);
        super.setFaculty("");
        super.setDepartment("");
    }

    void setStaffID(String id){ super.setID(id);    }
    String getStaffID() { return super.getID();  }

}
