package com.rwilk.angielski.customview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.rwilk.angielski.R;
import com.rwilk.angielski.database.DBHelper;
import com.rwilk.angielski.database.Word;
import com.rwilk.angielski.views.Level;
import com.rwilk.angielski.views.NewMainActivity;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<Word> {

    private ArrayList<Word> wordList;
    private ImageButton imageButtonTrudneSlowko;
    public DBHelper db;
    public Context context;

    public CustomAdapter(Context context, ArrayList<Word> englishWords) {
        super(context, R.layout.custom_row, englishWords);
        wordList = englishWords;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        LayoutInflater wordsInflater = LayoutInflater.from(getContext());
        View customView = wordsInflater.inflate(R.layout.custom_row, parent, false);

        Word singleWord = getItem(position);
        TextView wordText = (TextView) customView.findViewById(R.id.textViewNapisWierszGora);
        ImageView soundImage = (ImageView) customView.findViewById(R.id.imageButtonSound);
        assert singleWord != null;
        wordText.setText(singleWord.getEnglishWord());
        imageButtonTrudneSlowko = (ImageButton) customView.findViewById(R.id.imageButtonTrudneSlowko);

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
        db = new DBHelper(context, NewMainActivity.databaseVersion);
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
        });

        TextView polishWord = (TextView) customView.findViewById(R.id.textViewNapisWierszDol);
        polishWord.setText(singleWord.getPolishWord());

        if (singleWord.getDifficultWord() == 1)
            imageButtonTrudneSlowko.setImageResource(R.drawable.niebieski);
        else
            imageButtonTrudneSlowko.setImageResource(R.drawable.szary);

        return customView;
    }
}
