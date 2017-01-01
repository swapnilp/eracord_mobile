package com.eracordapp.teacher.sample;

/**
 * Created by teacher on 10-08-2016.
 */
public class ClassData {
    public String name;
    public String subject;
    public String division;
    public  int image;
    public int id;

    public ClassData()
    { }

    public ClassData(String name, String subject,int image,int id, String division) {
        this.name = name;
        this.subject = subject;
        this.image=image;
        this.division = division;
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }
    public String getSubject() {
        return subject;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setImage(int image) {
        this.image = image;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDivision() {
        return division;
    }
    public void setDivision(String division) {
        this.division = division;
    }
}
