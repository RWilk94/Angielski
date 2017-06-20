package com.rwilk.angielski.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.rwilk.angielski.R;
import com.rwilk.angielski.database.DBHelper;
import com.rwilk.angielski.database.Word;

import java.util.ArrayList;

public class SelectOfRepeat extends Activity {

    private ImageButton imageButtonSelectOfRepeatAll;
    private ImageButton imageButtonSelectOfRepeatDifficult;
    private DBHelper db;
    private ArrayList<Word> listOfAllWordFromLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_select_of_repeat);

        imageButtonSelectOfRepeatAll = (ImageButton) findViewById(R.id.imageButtonSelectOfRepeatAll);
        imageButtonSelectOfRepeatDifficult = (ImageButton) findViewById(R.id.imageButtonSelectOfRepeatDifficult);

        Bundle bundle = getIntent().getExtras(); //przekazujemy liste slowek z poziomu z intenta po to, by nie tworzyc miliona klas
        if (bundle == null) {
            return;
        }
        listOfAllWordFromLevel = (ArrayList<Word>) bundle.get("Lista");

        //ustawiamy wyswietlanie activity od dolu na 0.3
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((width), (int) (height * 0.3));
        getWindow().setGravity(Gravity.BOTTOM);

        //db = new DBHelper(getBaseContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        imageButtonSelectOfRepeatAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //to mozna w nowym fredzie
                ArrayList<Word> listOfAllWordToRepetition = new ArrayList<>();
                for (int i = 0; i < listOfAllWordFromLevel.size(); i++) {
                    if (listOfAllWordFromLevel.get(i).getRepeat() == 0
                            && listOfAllWordFromLevel.get(i).getProgress() >= 50)
                        listOfAllWordToRepetition.add(listOfAllWordFromLevel.get(i));
                }
                if (listOfAllWordToRepetition.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Nie masz słów do nauki!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Intent intent = new Intent(SelectOfRepeat.this, RepetitionActivity.class);
                    intent.putExtra("Lista", listOfAllWordFromLevel);
                    intent.putExtra("ListaDoPowtorki", listOfAllWordToRepetition);
                    startActivity(intent);
                }
            }
        });

        imageButtonSelectOfRepeatDifficult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Word> listOfAllWordToRepetition = new ArrayList<>();
                for (int i = 0; i < listOfAllWordFromLevel.size(); i++) {
                    if (listOfAllWordFromLevel.get(i).getDifficultWord() == 1)
                        listOfAllWordToRepetition.add(listOfAllWordFromLevel.get(i));
                }
                if (listOfAllWordToRepetition.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Nie masz trudnych słówek!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Intent intent = new Intent(SelectOfRepeat.this, RepetitionActivity.class);
                    intent.putExtra("Lista", listOfAllWordFromLevel);
                    intent.putExtra("ListaDoPowtorki", listOfAllWordToRepetition);
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        //db.close();
        super.onDestroy();
    }
}
