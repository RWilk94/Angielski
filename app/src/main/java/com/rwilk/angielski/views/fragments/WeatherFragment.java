package com.rwilk.angielski.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.rwilk.angielski.DBHelper;
import com.rwilk.angielski.R;
import com.rwilk.angielski.database.Word;
import com.rwilk.angielski.views.Level;
import com.rwilk.angielski.views.MainActivity;
import com.rwilk.angielski.views.NewMainActivity;

import java.util.ArrayList;

import static com.rwilk.angielski.R.layout.fragment_weather;

/**
 * Created by wilkr on 23.03.2017.
 * Klasa opisująca pogodę
 */
public class WeatherFragment extends Fragment {

    //private LinearLayout linearLayoutWeatherBody;
    private LinearLayout linearLayoutWeatherHeader;
    //private static ArrayList<Word> allWordsFromLevel;
    public ArrayList<Word> wordListFromLevel;

    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(fragment_weather, container, false);

        //linearLayoutWeatherBody = (LinearLayout) view.findViewById(R.id.linearLayoutWeatherBody);
        linearLayoutWeatherHeader = (LinearLayout) view.findViewById(R.id.linearLayoutWeatherHeader);

        progressBar = (ProgressBar)view.findViewById(R.id.progressBarCircle); //progressBarCircle

        return view;
    }

    private void setProgressBar(){

        DBHelper db = new DBHelper(getContext(), NewMainActivity.databaseVersion);
        //wordListFromLevel.get(0).getIdSection();
        db.setCompleted(wordListFromLevel.get(0).getIdSection());
        db.close();


    }

    private void getProgressBar(){
        DBHelper db = new DBHelper(getContext(), NewMainActivity.databaseVersion);
        //wordListFromLevel.get(0).getIdSection();
        int progress =  db.getCompleted(wordListFromLevel.get(0).getIdSection());
        System.out.println("Progress" + progress);
        progressBar.setProgress(progress);
        db.close();
    }

    /*public static WeatherFragment newInstance(ArrayList<Word> allWordFromLevel) {
        Bundle args = new Bundle();
        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);
        allWordsFromLevel = allWordFromLevel;
        return fragment;
    }*/

    @Override
    public void onStart() {
        super.onStart();

        linearLayoutWeatherHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wordListFromLevel = ((NewMainActivity)getActivity()).getListaSlowek("Zwierzeta");
                Intent intent = new Intent(getActivity(), Level.class);
                intent.putExtra("wordsFromLevel", wordListFromLevel);
                intent.putExtra("title", "Ciało ludzkie");
                //intent.putExtra("subtitle", "Części ciała");
                setProgressBar();
                getProgressBar();
                startActivity(intent);
            }
        });


        /*linearLayoutWeatherHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearLayoutWeatherBody.getVisibility() == View.VISIBLE) {
                    TranslateAnimation animate = new TranslateAnimation(linearLayoutWeatherBody.getWidth(), linearLayoutWeatherHeader.getWidth(), linearLayoutWeatherBody.getHeight(), linearLayoutWeatherHeader.getHeight()); //z x; do x; z y; do y;
                    animate.setDuration(300);
                    animate.setFillAfter(true);
                    linearLayoutWeatherBody.startAnimation(animate);
                    linearLayoutWeatherBody.setVisibility(View.GONE);
                } else {
                    //tutaj okreslamy kierunek animacji
                    TranslateAnimation animate = new TranslateAnimation(0, 0, -50, 0); //z x; do x; z y; do y;
                    animate.setDuration(300);
                    animate.setFillAfter(false);
                    linearLayoutWeatherBody.startAnimation(animate);
                    linearLayoutWeatherBody.setVisibility(View.VISIBLE);
                }
            }
        });*/
    }

}
