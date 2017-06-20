package com.rwilk.angielski.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * Klasa dziedziczy z Application. Dzięki temu można uzyskać context do statycznych metod.
 */
public class Angielski extends Application {

    public static Context context;

    public void onCreate() {
        super.onCreate();
        Angielski.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return Angielski.context;
    }

    public static Activity getActivity() {
        return Angielski.getActivity();
    }
}
