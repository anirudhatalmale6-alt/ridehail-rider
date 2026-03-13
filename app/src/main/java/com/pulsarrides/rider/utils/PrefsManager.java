package com.pulsarrides.rider.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {

    private final SharedPreferences prefs;

    public PrefsManager(Context context) {
        prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Token
    public void setToken(String token) {
        prefs.edit().putString(Constants.KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(Constants.KEY_TOKEN, null);
    }

    public boolean isLoggedIn() {
        return getToken() != null && !getToken().isEmpty();
    }

    // User ID
    public void setUserId(int id) {
        prefs.edit().putInt(Constants.KEY_USER_ID, id).apply();
    }

    public int getUserId() {
        return prefs.getInt(Constants.KEY_USER_ID, -1);
    }

    // User Name
    public void setUserName(String name) {
        prefs.edit().putString(Constants.KEY_USER_NAME, name).apply();
    }

    public String getUserName() {
        return prefs.getString(Constants.KEY_USER_NAME, "");
    }

    // User Phone
    public void setUserPhone(String phone) {
        prefs.edit().putString(Constants.KEY_USER_PHONE, phone).apply();
    }

    public String getUserPhone() {
        return prefs.getString(Constants.KEY_USER_PHONE, "");
    }

    // User Role
    public void setUserRole(String role) {
        prefs.edit().putString(Constants.KEY_USER_ROLE, role).apply();
    }

    public String getUserRole() {
        return prefs.getString(Constants.KEY_USER_ROLE, "");
    }

    // Save full user
    public void saveUser(int id, String name, String phone, String role) {
        prefs.edit()
                .putInt(Constants.KEY_USER_ID, id)
                .putString(Constants.KEY_USER_NAME, name)
                .putString(Constants.KEY_USER_PHONE, phone)
                .putString(Constants.KEY_USER_ROLE, role)
                .apply();
    }

    // Clear all
    public void clear() {
        prefs.edit().clear().apply();
    }
}
