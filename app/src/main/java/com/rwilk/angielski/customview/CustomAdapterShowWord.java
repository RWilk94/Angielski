package com.rwilk.angielski.customview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rwilk.angielski.R;
import com.rwilk.angielski.database.Word;

import java.util.ArrayList;

/**
 * Created by RWilk on 27.02.2017.
 * Custom Row
 */
public class CustomAdapterShowWord extends ArrayAdapter<Word> {

    public ArrayList<Word> wordList;
    //public ImageButton imageButtonTrudneSlowko;
    //public DBHelper db;
    public CustomAdapterShowWord customAdapter = null;
    public Context context;

    public CustomAdapterShowWord(Context context, ArrayList<Word> englishWords) {
        super(context, R.layout.custom_row, englishWords);
        customAdapter = this;
        wordList = englishWords;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //if (convertView == null) {
        LayoutInflater wordsInflater = LayoutInflater.from(getContext());
        View customView = wordsInflater.inflate(R.layout.custom_row_show_word, parent, false);

        Word singleWord = getItem(position);
        TextView wordText = (TextView) customView.findViewById(R.id.textViewNapisWierszGora);
        ImageView soundImage = (ImageView) customView.findViewById(R.id.imageButtonSound);
        wordText.setText(singleWord.getEnglishWord());
        //imageButtonTrudneSlowko = (ImageButton) customView.findViewById(R.id.imageButtonTrudneSlowko);

        if (wordList.get(position).getRepeat() == 1)
            soundImage.setImageResource(R.drawable.potworek5);
        else {
            switch (wordList.get(position).getProgress() / 10) {
                case 0:
                    soundImage.setImageResource(R.drawable.potworek0);
                    break;
                case 1:
                    soundImage.setImageResource(R.drawable.potworek1);
                    break;
                case 2:
                    soundImage.setImageResource(R.drawable.potworek2);
                    break;
                case 3:
                    soundImage.setImageResource(R.drawable.potworek3);
                    break;
                case 4:
                    soundImage.setImageResource(R.drawable.potworek4);
                    break;
                case 5:
                    soundImage.setImageResource(R.drawable.potworek5);
                    break;
                default:
                    soundImage.setImageResource(R.drawable.potworek5);
                    break;
            }
        }
        /*db = new DBHelper(context);
        final int positionForMethod = position;
        imageButtonTrudneSlowko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wordList.get(positionForMethod).getDifficultWord() == 1) {
                    imageButtonTrudneSlowko.setImageResource(R.drawable.niebieski);
                    db.setDifficult(wordList.get(positionForMethod).getId());
                } else {
                    imageButtonTrudneSlowko.setImageResource(R.drawable.szary);
                    db.setDifficult(wordList.get(positionForMethod).getId());
                }
                Level.changeDifficultWord(wordList.get(positionForMethod).getId());
            }
        });*/

        TextView polishWord = (TextView) customView.findViewById(R.id.textViewNapisWierszDol);
        polishWord.setText(singleWord.getPolishWord());

        /*if (singleWord.getDifficultWord() == 1)
            imageButtonTrudneSlowko.setImageResource(R.drawable.niebieski);//imageButtonTrudneSlowko.setVisibility(View.VISIBLE);
        else
            imageButtonTrudneSlowko.setImageResource(R.drawable.szary);//imageButtonTrudneSlowko.setVisibility(View.INVISIBLE);
        */
        return customView;
        //} else return convertView;
    }
}
