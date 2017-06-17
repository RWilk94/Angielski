package com.rwilk.angielski.kosz;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rwilk.angielski.R;
import com.rwilk.angielski.DBHelper;
import com.rwilk.angielski.database.Word;
import com.rwilk.angielski.views.Level;
import com.rwilk.angielski.views.MainActivity;

import java.util.ArrayList;

import static com.rwilk.angielski.R.layout.fragment_lesson_one;

/**
 * Created by wilkr on 18.04.2017.
 * dasdas
 */
public class LessonOne extends Fragment {

    public ImageView imageView;
    public TextView lesson;
    public TextView englishName;
    public TextView polishName;
    public TextView textViewProgress;
    public ArrayList<Word> wordListFromLevel;
    public DBHelper db;
    int progress = 10;

    public SharedPreferences sharedPreferences;
   // public SharedPreferences.Editor editor;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(fragment_lesson_one, container, false);
        sharedPreferences = this.getActivity().getSharedPreferences("com.rwilk.angielski", Context.MODE_PRIVATE); //private bo nie udostepnia tego na zewnatrz
        imageView = (ImageView) view.findViewById(R.id.imageViewLesson);
        textViewProgress = (TextView) view.findViewById(R.id.textViewProgress);

        textViewProgress.setText(String.valueOf(sharedPreferences.getInt("LessonOne", 0)));
        /*lesson = (TextView) view.findViewById(R.id.textViewLesson);
        englishName = (TextView) view.findViewById(R.id.textViewGora);
        polishName = (TextView) view.findViewById(R.id.textViewDol);

        //imageView.setImageResource(R.drawable.obraz);
        lesson.setText("Lekcja 1");
        englishName.setText("Human Body");
        polishName.setText("Ciało ludzkie");*/



        /*Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                wordListFromLevel = db.getAllWordsFromLevelX("Ciało ludzkie");
                for(Word i : wordListFromLevel){
                    if(i.getProgress()>=50 || i.getRepeat()==1)progress++;
                }
                progress /= wordListFromLevel.size();
            }
        });
        thread.start();*/

       // textViewProgress.setText(String.valueOf(progress));




        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //db = new DBHelper(getActivity());
                wordListFromLevel = ((MainActivity)getActivity()).getListaSlowek("Zwierzeta");

                Intent intent = new Intent(getActivity(), Level.class);
                //wordListFromLevel = db.getAllWordsFromLevelX("Zwierzeta");
                //db.close();
                intent.putExtra("wordsFromLevel", wordListFromLevel);
                intent.putExtra("title", "Ciało ludzkie");
                //intent.putExtra("subtitle", "Części ciała");
                startActivity(intent);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
