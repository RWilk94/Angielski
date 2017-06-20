package com.rwilk.angielski.database;

import java.io.Serializable;

/**
 * Obiekt z bazy danych - punkty.
 */

public class Points implements Serializable {

    private int idPoints;
    private int weekly;
    private int monthly;
    private int allTime;

    public int getIdPoints() {
        return idPoints;
    }

    public void setIdPoints(int idPoints) {
        this.idPoints = idPoints;
    }

    public int getWeekly() {
        return weekly;
    }

    public void setWeekly(int weekly) {
        this.weekly = weekly;
    }

    public int getMonthly() {
        return monthly;
    }

    public void setMonthly(int monthly) {
        this.monthly = monthly;
    }

    public int getAllTime() {
        return allTime;
    }

    public void setAllTime(int allTime) {
        this.allTime = allTime;
    }
}
