package com.example.ewtapp;

public class UserData {
    String Phone_no,username,password;
    String electricity,water;

    public UserData(String Phone_no,String username, String password) {
        this.Phone_no=Phone_no;
        this.username = username;
        this.password = password;
        this.electricity=electricity;
        this.water=water;

    }

    public UserData() {

    }


    public String getPhone_no() {

        return Phone_no;
    }

    public String getUsername() {

        return username;
    }

    public String getPassword() {

        return password;
    }

    public String getElectricity() {

        return electricity;
    }

    public String getWater() {

        return water;
    }

    public void setPhone_no(String Phone_no) {

        this.Phone_no = Phone_no;
    }
    public void setUsername(String username) {

        this.username = username;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public void setElectricity(String electricity) {

        this.electricity = electricity;
    }

    public void setWater(String water) {

        this.water= water;
    }


}

