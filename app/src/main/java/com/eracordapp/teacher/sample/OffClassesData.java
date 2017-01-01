package com.eracordapp.teacher.sample;

/**
 * Created by Lenovo on 12/11/2016.
 */

public class OffClassesData {
    public String name;
    public String date;
    public String teacher_name;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
public  OffClassesData()
{

}
    public OffClassesData(String name, String date, String teacher_name) {
        this.name = name;
        this.date = date;
        this.teacher_name = teacher_name;
    }

    public String getTeacher_name() {

        return teacher_name;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
