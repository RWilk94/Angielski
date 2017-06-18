package com.rwilk.angielski.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rwilk.angielski.DBHelper;
import com.rwilk.angielski.R;
import com.rwilk.angielski.database.Word;
import com.rwilk.angielski.views.Level;
import com.rwilk.angielski.views.NewMainActivity;

import java.util.ArrayList;

import static com.rwilk.angielski.R.layout.fragment_weather;

/**
 * Created by wilkr on 23.03.2017.
 * Klasa opisująca pogodę
 */
public class WeatherFragment extends Fragment {

    private LinearLayout linearLayoutWeatherHeader;
    public ArrayList<Word> wordListFromLevel;

    private ProgressBar progressBar;
    private TextView fragmentWeatherTextViewTop;
    private TextView fragmentWeatherTextViewBottom;
    private static int idSection = -1;
    private String sectionName = "";


    public static WeatherFragment newInstance(int id) {
        WeatherFragment fragment = new WeatherFragment();
        idSection = id;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(fragment_weather, container, false);
        linearLayoutWeatherHeader = (LinearLayout) view.findViewById(R.id.linearLayoutWeatherHeader);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBarCircle); //progressBarCircle
        fragmentWeatherTextViewTop = (TextView)view.findViewById(R.id.fragmentWeatherTextViewTop);
        fragmentWeatherTextViewBottom = (TextView)view.findViewById(R.id.fragmentWeatherTextViewBottom);

        if(idSection > 0) {
            DBHelper db = new DBHelper(getContext(), NewMainActivity.databaseVersion);
            sectionName = db.getSectionName(idSection);
            db.close();
        }
        String text = "Lesson " + idSection;
        fragmentWeatherTextViewTop.setText(text);
        fragmentWeatherTextViewBottom.setText(sectionName);
        return view;
    }

    /*private void setProgressBar(){
        DBHelper db = new DBHelper(getContext(), NewMainActivity.databaseVersion);
        //wordListFromLevel.get(0).getIdSection();
        db.setCompleted(wordListFromLevel.get(0).getIdSection());
        db.close();
    }*/

    private void getProgressBar(){
        DBHelper db = new DBHelper(getContext(), NewMainActivity.databaseVersion);
        //wordListFromLevel.get(0).getIdSection();
        int progress =  db.getCompleted(wordListFromLevel.get(0).getIdSection());
        System.out.println("Progress" + progress);
        progressBar.setProgress(progress);
        db.close();
    }



    @Override
    public void onStart() {
        super.onStart();

        linearLayoutWeatherHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wordListFromLevel = ((NewMainActivity)getActivity()).getListaSlowek(sectionName);
                Intent intent = new Intent(getActivity(), Level.class);
                intent.putExtra("wordsFromLevel", wordListFromLevel);
                intent.putExtra("title", sectionName);
                intent.putExtra("subtitle", "Lista słówek");
                //setProgressBar();
                //getProgressBar();
                startActivity(intent);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            getProgressBar();
        }
    }

}
