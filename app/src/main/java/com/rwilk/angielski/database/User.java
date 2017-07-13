package com.rwilk.angielski.database;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;


@IgnoreExtraProperties
public class User implements Serializable{

    private String idUser; //parameter UID
    private String email;
    private long lastLogin;
    private long weekly;
    private long monthly;
    private long allTime;
    private long backup;

    //One To Many
    private ArrayList<Friend> friendList;

    public User(String idUser, String email, long lastLogin, long weekly, long monthly, long allTime, long backup) {
        this.idUser = idUser;
        this.email = email;
        this.lastLogin = lastLogin;
        this.weekly = weekly;
        this.monthly = monthly;
        this.allTime = allTime;
        this.backup = backup;
    }

    public User(){}

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

    public long getWeekly() {
        return weekly;
    }

    public void setWeekly(long weekly) {
        this.weekly = weekly;
    }

    public long getMonthly() {
        return monthly;
    }

    public void setMonthly(long monthly) {
        this.monthly = monthly;
    }

    public long getAllTime() {
        return allTime;
    }

    public void setAllTime(long allTime) {
        this.allTime = allTime;
    }

    public long getBackup() {
        return backup;
    }

    public void setBackup(long backup) {
        this.backup = backup;
    }

    public ArrayList<Friend> getFriendList() {
        return friendList;
    }

    public void setFriendList(ArrayList<Friend> friendList) {
        this.friendList = friendList;
    }
}
