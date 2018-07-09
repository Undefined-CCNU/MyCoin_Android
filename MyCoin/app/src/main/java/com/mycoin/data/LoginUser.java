package com.mycoin.data;

public class LoginUser {

    private String username;
    private String password;
    private int day;
    private int month;

    public LoginUser(String username, String password, int month, int day){
        this.username = username;
        this.password = password;
        this.day = day;
        this.month = month;
    }
}
