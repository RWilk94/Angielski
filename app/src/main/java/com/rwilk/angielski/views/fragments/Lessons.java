package com.rwilk.angielski.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.rwilk.angielski.DBHelper;
import com.rwilk.angielski.R;
import com.rwilk.angielski.customview.CustomAdapter;
import com.rwilk.angielski.customview.CustomAdapterFragments;
import com.rwilk.angielski.database.Lesson;
import com.rwilk.angielski.database.Word;
import com.rwilk.angielski.views.Level;
import com.rwilk.angielski.views.NewMainActivity;

import java.util.ArrayList;

import static com.rwilk.angielski.R.layout.fragment_lessons;
import static com.rwilk.angielski.R.layout.fragment_weather;

/**
 * ListView z fragmentami do klikania.
 * Created by wilkr on 22.05.2017.
 */

public class Lessons extends Fragment {

    public  ListView listViewWords;
    public static ArrayAdapter adapter;
    public static ArrayList<Lesson> listOfLessons;

    public static Lessons newInstance(){
        return new Lessons();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(fragment_lessons, container, false);

        listViewWords = (ListView) view.findViewById(R.id.listViewAllWords);//przypisanie listView

        DBHelper db = new DBHelper(getContext(), NewMainActivity.databaseVersion);
        //ArrayList<String> l
        listOfLessons = db.getAllSections();
        db.close();

        adapter = new CustomAdapterFragments(getContext(), listOfLessons);
        listViewWords.setAdapter(adapter);

        listViewWords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DBHelper db = new DBHelper(getContext(), NewMainActivity.databaseVersion);
                ArrayList<Word> wordListFromLevel = db.getAllWordsFromSectionX(position+1);
                String sectionName = db.getSectionName(position+1);
                db.setCompleted(position+1);
                //db.getCompleted(position+1);
                db.close();
                Intent intent = new Intent(getContext(), Level.class);
                intent.putExtra("wordsFromLevel", wordListFromLevel);
                intent.putExtra("title", sectionName);
                intent.putExtra("subtitle", "Lista słówek");

                //setProgressBar();
                //getProgressBar();

                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        DBHelper db = new DBHelper(getContext(), NewMainActivity.databaseVersion);
        CustomAdapterFragments.listLessons = db.getAllSections();
        db.close();
        adapter.notifyDataSetChanged();
    }

    /*@Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            DBHelper db = new DBHelper(getContext(), NewMainActivity.databaseVersion);
            //ArrayList<String> l
            listOfLessons = db.getAllSections();
            db.close();
            adapter = new CustomAdapterFragments(getContext(), listOfLessons);
            //adapter.notifyDataSetChanged();
            listViewWords.setAdapter(adapter);
            /*aktualizacja listOfLessons
                    notifyDataChanged
            setPoints();*/
      /*  }
    }*/


}
