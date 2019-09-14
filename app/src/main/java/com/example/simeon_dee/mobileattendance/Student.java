package com.example.simeon_dee.mobileattendance;

/**
 * Created by SIMEON_DEE on 3/15/2018.
 */

public class Student extends User {
    String mLevel, mProgram;

    public Student(String matric, Name name, String faculty, String dept, String level,
                   String program){
        super.setID(matric);
        super.setName(name);
        super.setFaculty(faculty);
        super.setDepartment(dept);
        this.mLevel = level;
        this.mProgram = program;
    }

    public Student(String matric, Name name){
        super.setID(matric);
        super.setName(name);
        super.setFaculty("");
        super.setDepartment("");
        this.mLevel = "";
        this.mProgram = "";
    }

    public Student(User user, String level){
        super.setID(user.getID());
        super.setName(user.getName());
        super.setFaculty(user.getFaculty());
        super.setDepartment(user.getDepartment());
        this.mLevel = level;
        this.mProgram = "";
    }

    public Student(String matric){
        super.setID(matric);
        super.setName(null);
        super.setFaculty("");
        super.setDepartment("");
        this.mLevel = "";
        this.mProgram = "";
    }

    public Student(){
        super.setID("");
        super.setName(null);
        super.setFaculty("");
        super.setDepartment("");
        this.mLevel = "";
        this.mProgram = "";
    }

    void setMatricNumber(String matric){ super.setID(matric); }

    String getMatricNumber(){ return super.getID();    }

//    void setName(Name name){ super.setName(name);    }
//
//    Name getName(){ return super.getName();    }
//
//    void setFaculty(String faculty){ super.setFaculty(faculty);    }
//
//    String getFaculty(){ return super.getFaculty();    }
//
//    void setDepartment(String dept){ super.setDepartment(dept);    }
//
//    String getDepartment(){ return super.getDepartment();    }

    void setLevel(String level){ this.mLevel = level;    }

    String getLevel(){ return this.mLevel;    }

    void setProgram(String program){ this.mProgram = program;    }

    String getProgram(){ return this.mProgram;    }

    @Override
    public String toString() {
        return super.getID();
    }
}
