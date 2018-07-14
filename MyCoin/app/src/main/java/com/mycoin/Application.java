package com.mycoin;

public class Application extends android.app.Application {

    public static String storedUsername;
    public static String storedUserPassword;
    public static String storedUserToken;
    public static int storedUserBudget;
    public static String storedUserAvatarUrl;



    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static String getStoredUsername() {
        return storedUsername;
    }

    public static void setStoredUsername(String storedUsername) {
        Application.storedUsername = storedUsername;
    }

    public static String getStoredUserPassword() {
        return storedUserPassword;
    }

    public static void setStoredUserPassword(String storedUserPassword) {
        Application.storedUserPassword = storedUserPassword;
    }

    public static String getStoredUserToken() {
        return storedUserToken;
    }

    public static void setStoredUserToken(String storedUserToken) {
        Application.storedUserToken = storedUserToken;
    }

    public static int getStoredUserBudget() {
        return storedUserBudget;
    }

    public static void setStoredUserBudget(int storedUserBudget) {
        Application.storedUserBudget = storedUserBudget;
    }

    public static String getStoredUserAvatarUrl() {
        return storedUserAvatarUrl;
    }

    public static void setStoredUserAvatarUrl(String storedUserAvatarUrl) {
        Application.storedUserAvatarUrl = storedUserAvatarUrl;
    }

}
