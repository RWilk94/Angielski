package com.rwilk.angielski.kosz;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rwilk.angielski.R;

import static com.rwilk.angielski.R.layout.fragment_lesson_one;
import static com.rwilk.angielski.R.layout.fragment_lesson_two;

/**
 * Created by wilkr on 18.04.2017.
 */
public class LessonTwo extends Fragment {

    public ImageView imageView;
    public TextView lesson;
    public TextView englishName;
    public TextView polishName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(fragment_lesson_two, container, false);

        /*imageView = (ImageView)view.findViewById(R.id.imageViewLesson);
        lesson = (TextView)view.findViewById(R.id.textViewLesson);
        englishName = (TextView)view.findViewById(R.id.textViewGora);
        polishName = (TextView)view.findViewById(R.id.textViewDol);

        imageView.setImageResource(R.drawable.obraz2);
        lesson.setText("Lekcja 2");
        englishName.setText("Weather");
        polishName.setText("Pogoda");*/

        return view;
    }
}
