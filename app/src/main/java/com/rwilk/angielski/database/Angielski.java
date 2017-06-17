package com.rwilk.angielski.database;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * Created by wilkr on 13.04.2017.
 * Context to static method
 */
public class Angielski extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        Angielski.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return Angielski.context;
    }

    public static Activity getActivity() {return Angielski.getActivity(); }
}
