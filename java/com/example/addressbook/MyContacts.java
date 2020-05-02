package com.example.addressbook;

import java.util.Date;

public class MyContacts {
    private String id;
    //联系人
    private String name;
    //电话号码
    private String number;
    //通话日期
    private String date;
    //通话时长
    private String duration;
    //通话类型 ：1.呼入， 2.呼出， 3.未接
    private Integer type;

    public MyContacts() {
    }

    public MyContacts(String id, String name, String number, String date, String duration, Integer type) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.date = date;
        this.duration = duration;
        this.type = type;
    }

    @Override
    public String toString() {
        return "MyContacts{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", date='" + date + '\'' +
                ", duration='" + duration + '\'' +
                ", type=" + type +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
