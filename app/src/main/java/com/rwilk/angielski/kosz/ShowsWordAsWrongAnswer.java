package com.rwilk.angielski.kosz;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.TextView;

import com.rwilk.angielski.R;
import com.rwilk.angielski.database.Word;

/**
 * Created by wilkr on 14.04.2017.
 */
public class ShowsWordAsWrongAnswer extends Activity {

    public TextView infoEnglishWord;
    public TextView infoPolishWord;
    public TextView infoPartOfSpeech;

    public static Word word = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_show_word_as_wrong_answer);

        infoEnglishWord = (TextView) findViewById(R.id.infoEnglishWord);
        infoPolishWord = (TextView) findViewById(R.id.infoPolishWord);
        infoPartOfSpeech = (TextView) findViewById(R.id.infoPartOfSpeech);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((width),(int)(height*0.3));
        getWindow().setGravity(Gravity.BOTTOM);



        if(word!=null) {
            infoEnglishWord.setText(word.getEnglishWord());
            infoPolishWord.setText(word.getPolishWord());
            infoPartOfSpeech.setText(word.getPartOfSpeech());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
