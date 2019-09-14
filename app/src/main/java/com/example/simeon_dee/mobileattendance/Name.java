package com.example.simeon_dee.mobileattendance;

/**
 * Created by SIMEON_DEE on 3/15/2018.
 */
public class Name {
    String mSurname, mMiddlename, mFirstname;

    public Name(String surname, String middleName, String firstName) {
        this.mSurname = surname;
        this.mMiddlename = middleName;
        this.mFirstname = firstName;
    }

    public Name() {
        this.mSurname = "";
        this.mMiddlename = "";
        this.mFirstname = "";
    }

    void setSurname(String sName) {
        this.mSurname = sName;
    }

    String getSurname() { return this.mSurname; }

    void setMiddlename(String mName) {
        this.mMiddlename = mName;
    }

    String getMiddlename() {
        return this.mSurname;
    }

    void setFirstname(String fName) {
        this.mFirstname = fName;
    }

    String getFirstname() {
        return this.mFirstname;
    }

    @Override
    public String toString() {
        return mSurname + " " + mMiddlename + " " + mFirstname;
    }
}
