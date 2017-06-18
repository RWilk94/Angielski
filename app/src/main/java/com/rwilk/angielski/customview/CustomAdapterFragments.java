package com.rwilk.angielski.customview;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rwilk.angielski.DBHelper;
import com.rwilk.angielski.R;
import com.rwilk.angielski.database.Lesson;
import com.rwilk.angielski.database.Word;
import com.rwilk.angielski.views.Level;
import com.rwilk.angielski.views.NewMainActivity;

import java.util.ArrayList;

public class CustomAdapterFragments extends ArrayAdapter<Lesson> {

    private Context context;
    private String sectionName;
    //private int positionOfElement;
    public ArrayList<Word> wordListFromLevel;
    public static ArrayList<Lesson> listLessons;

    public CustomAdapterFragments(Context context, ArrayList<Lesson> levels) {
        super(context, R.layout.fragment_weather, levels);
        this.context = context;
        listLessons = levels;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater wordsInflater = LayoutInflater.from(getContext());
        View view = wordsInflater.inflate(R.layout.fragment_weather, parent, false);

        //LinearLayout linearLayoutWeatherHeader = (LinearLayout) view.findViewById(R.id.linearLayoutWeatherHeader);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBarCircle);
        TextView fragmentWeatherTextViewTop = (TextView) view.findViewById(R.id.fragmentWeatherTextViewTop);
        TextView fragmentWeatherTextViewBottom = (TextView) view.findViewById(R.id.fragmentWeatherTextViewBottom);

        /*DBHelper db = new DBHelper(getContext(), NewMainActivity.databaseVersion);
        //sectionName = db.getSectionName(position+1);
        progressBar.setProgress(db.getCompleted(position+1));
        db.close();*/

        progressBar.setProgress(listLessons.get(position).getProgress());
        //String text = "Lesson " + (position+1);
        fragmentWeatherTextViewTop.setText(listLessons.get(position).getTextViewTop());
        fragmentWeatherTextViewBottom.setText(listLessons.get(position).getTextViewBottom());

       // positionOfElement = position;

        /*linearLayoutWeatherHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper db = new DBHelper(getContext(), NewMainActivity.databaseVersion);
                wordListFromLevel = db.getAllWordsFromSectionX(position+1);
                db.close();
                Intent intent = new Intent(getContext(), Level.class);
                intent.putExtra("wordsFromLevel", wordListFromLevel);
                intent.putExtra("title", sectionName);
                intent.putExtra("subtitle", "Lista słówek");
                //setProgressBar();
                //getProgressBar();
                context.startActivity(intent);
            }
        });*/
        return view;
    }
}
