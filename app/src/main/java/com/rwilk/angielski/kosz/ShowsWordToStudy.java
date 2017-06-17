package com.rwilk.angielski.kosz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rwilk.angielski.R;
import com.rwilk.angielski.database.Word;

import static com.rwilk.angielski.R.layout.fragment_shows_word_to_study;

/**
 * Tutaj pokaze slowo do nauki
 * Created by wilkr on 12.04.2017.
 */

public class ShowsWordToStudy extends Fragment {

    private ImageView imageViewInfoAboutWord0;
    private ImageView imageViewInfoAboutWord1;
    private ImageView imageViewInfoAboutWord2;
    private ImageView imageViewInfoAboutWord3;
    private ImageView imageViewInfoAboutWord4;
    private ImageView imageViewInfoAboutWord5;
    //private ImageView imageViewInfoKsiazka;
    private TextView infoEnglishWord;
    private TextView infoPolishWord;
    private TextView infoPartOfSpeech;
    //private ImageView imageViewInfoDifficult;
    //private ImageView imageViewInfoRepeat;
    public static Word showWord;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(fragment_shows_word_to_study, container, false);

        imageViewInfoAboutWord0 = (ImageView) view.findViewById(R.id.imageViewInfoAboutWord0);
        imageViewInfoAboutWord1 = (ImageView) view.findViewById(R.id.imageViewInfoAboutWord1);
        imageViewInfoAboutWord2 = (ImageView) view.findViewById(R.id.imageViewInfoAboutWord2);
        imageViewInfoAboutWord3 = (ImageView) view.findViewById(R.id.imageViewInfoAboutWord3);
        imageViewInfoAboutWord4 = (ImageView) view.findViewById(R.id.imageViewInfoAboutWord4);
        imageViewInfoAboutWord5 = (ImageView) view.findViewById(R.id.imageViewInfoAboutWord5);
        //imageViewInfoKsiazka = (ImageView) view.findViewById(R.id.imageViewInfoKsiazka);
        infoEnglishWord = (TextView) view.findViewById(R.id.infoEnglishWord);
        infoPolishWord = (TextView) view.findViewById(R.id.infoPolishWord);
        infoPartOfSpeech = (TextView) view.findViewById(R.id.infoPartOfSpeech);
        //imageViewInfoDifficult = (ImageView) view.findViewById(R.id.imageViewInfoDifficult);
        //imageViewInfoRepeat = (ImageView) view.findViewById(R.id.imageViewInfoRepeat);

        if(showWord == null)onDestroy();
        else {
            setParametersOfWord(showWord);
            infoEnglishWord.setText(showWord.getEnglishWord());
            infoPolishWord.setText(showWord.getPolishWord());
            infoPartOfSpeech.setText(showWord.getPartOfSpeech());
        }


        return view;
    }

    public static ShowsWordToStudy newInstance(Word word){
        Bundle args = new Bundle();
        ShowsWordToStudy fragment = new ShowsWordToStudy();
        fragment.setArguments(args);
        showWord = word;//Settings.listOfShowedWord.get(Settings.listOfShowedWord.size()-1);
        return fragment;
    }

    public void setParametersOfWord(final Word word) {
        imageViewInfoAboutWord0.setImageResource(R.drawable.potworek0);
        if (word.getProgress() >= 10 || word.getRepeat() == 1)
            imageViewInfoAboutWord1.setImageResource(R.drawable.potworek1);
        else imageViewInfoAboutWord1.setImageResource(R.drawable.potworek1czarny);
        if (word.getProgress() >= 20 || word.getRepeat() == 1)
            imageViewInfoAboutWord2.setImageResource(R.drawable.potworek2);
        else imageViewInfoAboutWord2.setImageResource(R.drawable.potworek2czarny);
        if (word.getProgress() >= 30 || word.getRepeat() == 1)
            imageViewInfoAboutWord3.setImageResource(R.drawable.potworek3);
        else imageViewInfoAboutWord3.setImageResource(R.drawable.potworek3czarny);
        if (word.getProgress() >= 40 || word.getRepeat() == 1)
            imageViewInfoAboutWord4.setImageResource(R.drawable.potworek4);
        else imageViewInfoAboutWord4.setImageResource(R.drawable.potworek4czarny);
        if (word.getProgress() >= 50 || word.getRepeat() == 1)
            imageViewInfoAboutWord5.setImageResource(R.drawable.potworek5);
        else imageViewInfoAboutWord5.setImageResource(R.drawable.potworek5czarny);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
