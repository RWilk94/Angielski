package com.rwilk.angielski.database;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Friend {

    private int idFriend;
    private String UID;
    private String email;
    private String name;
    //wiele do jednego
    private int idUser;

    public Friend(int idFriend, String UID, String email, String name, int idUser) {
        this.idFriend = idFriend;
        this.UID = UID;
        this.email = email;
        this.name = name;
        this.idUser = idUser;
    }

    public int getIdFriend() {
        return idFriend;
    }

    public void setIdFriend(int idFriend) {
        this.idFriend = idFriend;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
}
