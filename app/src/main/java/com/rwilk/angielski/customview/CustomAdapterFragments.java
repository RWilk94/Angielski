package com.rwilk.angielski.customview;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rwilk.angielski.R;
import com.rwilk.angielski.database.Lesson;

import java.util.ArrayList;

public class CustomAdapterFragments extends ArrayAdapter<Lesson> {

    public static ArrayList<Lesson> listLessons;

    public CustomAdapterFragments(Context context, ArrayList<Lesson> levels) {
        super(context, R.layout.fragment_weather, levels);
        listLessons = levels;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater wordsInflater = LayoutInflater.from(getContext());
        View view = wordsInflater.inflate(R.layout.fragment_weather, parent, false);

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBarCircle);
        TextView fragmentWeatherTextViewTop = (TextView) view.findViewById(R.id.fragmentWeatherTextViewTop);
        TextView fragmentWeatherTextViewBottom = (TextView) view.findViewById(R.id.fragmentWeatherTextViewBottom);

        progressBar.setProgress(listLessons.get(position).getProgress());
        fragmentWeatherTextViewBottom.setText(listLessons.get(position).getTextViewBottom());
        fragmentWeatherTextViewTop.setText(listLessons.get(position).getTextViewTop());
        return view;
    }
}
