package com.eracordapp.vidhiraj.sample;

/**
 * Created by lenovo on 22/08/2016.
 */
public class StudentData {
    public String stud_name;
    public int stud_hostel;
    public String image_url;
    public StudentData()
{

}
    public StudentData(String stud_name, int stud_hostel, String image_url, String stud_class_name) {
        this.stud_name = stud_name;
        this.stud_hostel = stud_hostel;
        this.image_url = image_url;
        this.stud_class_name = stud_class_name;
    }

    public String getImageUrl() {
        return image_url;
    }
    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }

    public String getStud_name() {
        return stud_name;
    }

    public void setStud_name(String stud_name) {
        this.stud_name = stud_name;
    }

    public int isStud_hostel() {
        return stud_hostel;
    }

    public void setStud_hostel(int stud_hostel) {
        this.stud_hostel = stud_hostel;
    }

    public String getStud_class_name() {
        return stud_class_name;
    }

    public void setStud_class_name(String stud_class_name) {
        this.stud_class_name = stud_class_name;
    }

    public String stud_class_name;
}
