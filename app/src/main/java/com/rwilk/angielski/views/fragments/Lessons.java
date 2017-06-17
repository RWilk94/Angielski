package com.rwilk.angielski.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rwilk.angielski.R;

/**
 * Blabla
 * Created by wilkr on 22.05.2017.
 */

public class Lessons extends Fragment {

    public static Lessons newInstance(){
        return new Lessons();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_lessons, container, false);
    }
}
