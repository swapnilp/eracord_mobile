package com.example.vidhiraj.sample;

/**
 * Created by Lenovo on 11/30/2016.
 */

public class TimeTableData {
    private int id;
    private String classDivision;
    private String startTime;
    private String endTime;
    private String className;
    private String subject;

    public TimeTableData(int id, String className, String subject, String classDivision, String startTime, String endTime) {
        this.id = id;
        this.className = className;
        this.subject = subject;
        this.classDivision = classDivision;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }


    public String getClassDivision() {
        return classDivision;
    }

    public void setClassDivision(String classDivision) {
        this.classDivision = classDivision;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
