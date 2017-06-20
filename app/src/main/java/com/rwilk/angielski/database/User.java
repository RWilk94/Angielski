package com.rwilk.angielski.database;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;


@IgnoreExtraProperties
public class User {

    private String idUser;
    private String email;
    private String name;
    private long lastLogin;
    private int backup;
    //One To Many
    private ArrayList<Friend> friendList;

    public User() {
    }

    public User(String idUser, String email, String name) {
        this.idUser = idUser;
        this.email = email;
        this.name = name;
        this.lastLogin = System.currentTimeMillis();
        this.backup = 0;
        this.friendList = null;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public int getBackup() {
        return backup;
    }

    public void setBackup(int backup) {
        this.backup = backup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
