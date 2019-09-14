package com.example.simeon_dee.mobileattendance;

/**
 * Created by SIMEON_DEE on 3/21/2018.
 */

public class User {
    private String mID, mFaculty, mDept, mEmail, mUserCategory;
    private Name mName;
    private byte[] mPassport;

    public User(String id, Name name, String faculty, String dept){
        this.mID= id;
        this.mName = name;
        this.mFaculty = faculty;
        this.mDept = dept;
    }

    public User(String id, Name name){
        this.mID = id;
        this.mName = name;
        this.mFaculty = "";
        this.mDept = "";
    }

    public User(){
        this.mID = "";
        this.mName = null;
        this.mFaculty = "";
        this.mDept = "";
    }



    public void setID(String id){ this.mID = id; }
    public String getID(){ return this.mID;    }

    public void setName(Name name){ this.mName = name;    }
    public Name getName(){ return this.mName;    }

    public void setFaculty(String faculty){ this.mFaculty = faculty;    }
    public String getFaculty(){ return this.mFaculty;    }

    public void setDepartment(String dept){ this.mDept = dept;    }
    public String getDepartment(){ return this.mDept;    }

    public void setEmail(String email){ this.mDept = email;    }
    public String getEmail(){ return this.mEmail;    }

    public void setUserCategory(String category){ this.mUserCategory = category;    }
    public String getUserCategory(){ return this.mUserCategory;    }

    public void setPassport(byte[] passport){ this.mPassport = passport; }
    public byte[] getPassport(){ return this.mPassport; }

    @Override
    public String toString() {
        return mID;
    }
}

