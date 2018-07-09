package com.mycoin.data;

public class CodeUser {
    private String email;
    private String username;
    private String captcha;
    private String password;

    public CodeUser(String email, String username, String captcha, String password) {
        this.email = email;
        this.username = username;
        this.captcha = captcha;
        this.password = password;
    }
}
