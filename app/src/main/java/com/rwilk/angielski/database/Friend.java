package com.rwilk.angielski.database;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Klasa mapuje obiekt z bazy danych - tabelę Friends.
 */
@IgnoreExtraProperties
public class Friend implements Serializable{

    private int idFriend;
    private String name;
    private String email;
    private long weekly;
    private long monthly;
    private long allTime;
    private User idUser; //Do ktorego usera jest przypisany przyjaciel

    public int getIdFriend() {
        return idFriend;
    }

    public void setIdFriend(int idFriend) {
        this.idFriend = idFriend;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public User getIdUser() {
        return idUser;
    }

    public void setIdUser(User idUser) {
        this.idUser = idUser;
    }
}
