package com.mycoin;

public class Application extends android.app.Application {

    public static String storedUsername;
    public static String storedUserPassword;
    public static String storedUserToken;
    public static int storedUserBudget;



    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void getUserName(String call) {
        storedUsername = call;
    }

    public String setUserName(){
        return storedUsername;
    }

    public void getUserPassword(String s) {
        storedUserPassword = s;
    }

    public String setUserPassword() {
        return storedUserPassword;
    }

    public void getUserToken(String response){
        storedUserToken = response;
    }

    public String setUserToken(){
        return  storedUserToken;
    }

    public void getUserBudget(int budget) {
        storedUserBudget = budget;
    }

    public int setUserBudget() {
        return storedUserBudget;
    }
}
