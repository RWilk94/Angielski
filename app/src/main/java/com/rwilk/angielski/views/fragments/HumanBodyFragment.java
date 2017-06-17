package com.rwilk.angielski.views.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rwilk.angielski.DBHelper;
import com.rwilk.angielski.R;
import com.rwilk.angielski.database.Word;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import static com.rwilk.angielski.R.layout.fragment_human_body;

/**
 * Created by RWilk on 18.03.2017.
 * Jeden poziom, HumanBody
 */
public class HumanBodyFragment extends Fragment {

    private LinearLayout linearLayoutHumanBody;
    private LinearLayout linearLayoutHumanBodyHeader;
    private static ArrayList<Word> allWordsFromLevel;

    public static HumanBodyFragment newInstance(){
        return new HumanBodyFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(fragment_human_body, container, false);

        //linearLayoutHumanBody = (LinearLayout) view.findViewById(R.id.linearLayoutHumanBody);
        linearLayoutHumanBodyHeader = (LinearLayout) view.findViewById(R.id.linearLayoutHumanBodyHeader);

        return view;
    }

    public static HumanBodyFragment newInstance(ArrayList<Word> allWordFromLevel) {
        Bundle args = new Bundle();
        HumanBodyFragment fragment = new HumanBodyFragment();
        fragment.setArguments(args);
        allWordsFromLevel = allWordFromLevel;
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        /*linearLayoutHumanBodyHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearLayoutHumanBody.getVisibility() == View.VISIBLE) {
                    TranslateAnimation animate = new TranslateAnimation(linearLayoutHumanBody.getWidth(), linearLayoutHumanBodyHeader.getWidth(), linearLayoutHumanBody.getHeight(), linearLayoutHumanBodyHeader.getHeight()); //z x; do x; z y; do y;
                    animate.setDuration(300);
                    animate.setFillAfter(true);
                    linearLayoutHumanBody.startAnimation(animate);
                    linearLayoutHumanBody.setVisibility(View.GONE);
                } else {
                    //tutaj okreslamy kierunek animacji
                    TranslateAnimation animate = new TranslateAnimation(0, 0, 0, 0); //z x; do x; z y; do y;
                    animate.setDuration(300);
                    animate.setFillAfter(false);
                    linearLayoutHumanBody.startAnimation(animate);
                    linearLayoutHumanBody.setVisibility(View.VISIBLE);
                }
            }
        });*/

        /*linearLayoutHumanBodyHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportDB();
            }
        });

        linearLayoutHumanBodyHeader.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DBHelper db = new DBHelper(getContext());
                db.dbReload();
                db.close();



                return false;
            }
        });*/

    }



}
