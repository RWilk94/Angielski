package com.rwilk.angielski.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.rwilk.angielski.R;
import com.rwilk.angielski.customview.CustomAdapter;
import com.rwilk.angielski.application.Angielski;
import com.rwilk.angielski.database.DBHelper;
import com.rwilk.angielski.database.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class Level extends AppCompatActivity implements TextToSpeech.OnInitListener {

    public static ArrayList<Word> wordListFromLevel;
    public static ArrayAdapter adapter;
    public ListView listViewWords;
    private String title;
    private String subtitle;
    private Button buttonLevelRepeat;
    private Button buttonLevelTeaching;

    public int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (title == null || subtitle == null) {
                title = "Nauka angielskiego";
                subtitle = "Lista słówek";
            }
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setSubtitle(subtitle);
        }
        listViewWords = (ListView) findViewById(R.id.listViewAllWords);//przypisanie listView


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Bundle bundle = getIntent().getExtras();
                if (bundle == null) {
                    if (wordListFromLevel == null || wordListFromLevel.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Błąd podczas ładowania słówek", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                } else {
                    wordListFromLevel = (ArrayList<Word>) bundle.get("wordsFromLevel");
                    if (wordListFromLevel == null || wordListFromLevel.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Błąd podczas ładowania słówek", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    title = bundle.getString("title");
                    subtitle = bundle.getString("subtitle");
                }
                adapter = new CustomAdapter(getApplicationContext(), wordListFromLevel);
                listViewWords.setAdapter(adapter);
            }
        });
        buttonLevelRepeat = (Button) findViewById(R.id.buttonLevelRepeat);
        buttonLevelTeaching = (Button) findViewById(R.id.buttonLevelTeaching);

        textToSpeech = new TextToSpeech(this, this);

    }


    public ProgressDialog progressDialog;

    public void showProgressDialog() {
        progressDialog = ProgressDialog.show(this,
                "TTS", "Connecting to TextToSpeech", true);
        // progressDialog.show();
    }

    public static void changeDifficultWord(final int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < wordListFromLevel.size(); i++) {
                    if (wordListFromLevel.get(i).getId() == id) {
                        if (wordListFromLevel.get(i).getDifficultWord() == 1)
                            wordListFromLevel.get(i).setDifficultWord(0);
                        else wordListFromLevel.get(i).setDifficultWord(1);
                    }
                }
            }
        }).start();
        adapter.notifyDataSetChanged();
    }

    public static void updateListOfWordFromLevel(ArrayList<Word> updatedWords) {

        Collections.sort(wordListFromLevel, new Comparator<Word>() {  //sortujemy liste ze slowkami do nauki
            @Override
            public int compare(Word o1, Word o2) {
                return o1.getId() - o2.getId(); //jako kryterium jest progress -> od najmniejszego
            }
        });

        Collections.sort(updatedWords, new Comparator<Word>() {  //sortujemy liste ze slowkami do nauki
            @Override
            public int compare(Word o1, Word o2) {
                return o1.getId() - o2.getId(); //jako kryterium jest progress -> od najmniejszego
            }
        });

        for (int i = 0, j = 0; i < wordListFromLevel.size() && j < updatedWords.size(); i++) {
            if (wordListFromLevel.get(i).getId() == updatedWords.get(j).getId()) {
                wordListFromLevel.set(i, updatedWords.get(j));
                j++;
            }
        }
        adapter.notifyDataSetChanged();
        updateListOfWordFromLevelInDatabase(updatedWords);
    }

    private static void updateListOfWordFromLevelInDatabase(ArrayList<Word> updatedWords) {

        DBHelper db = new DBHelper(Angielski.getAppContext(), NewMainActivity.databaseVersion);
        db.updateListOfWordAfterLearning(updatedWords);
        db.close();
    }

    public static void changeRepeatWord(final int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < wordListFromLevel.size(); i++) {
                    if (wordListFromLevel.get(i).getId() == id) {
                        if (wordListFromLevel.get(i).getRepeat() == 1)
                            wordListFromLevel.get(i).setRepeat(0);
                        else wordListFromLevel.get(i).setRepeat(1);
                    }
                }
            }
        }).start();
        adapter.notifyDataSetChanged();
    }


    public static boolean disableTeachingButton = false;

    @Override
    protected void onResume() {
        super.onResume();

        if (disableTeachingButton)
            buttonLevelTeaching.setClickable(false);


        listViewWords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final String speak = wordListFromLevel.get(position).getEnglishWord();
                textToSpeech.speak(speak, TextToSpeech.QUEUE_FLUSH, null);

            }
        });

        listViewWords.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view,
                                                   final int position, long l) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Level.this, InfoAboutWord.class);
                                intent.putExtra("pozycjaItemu", position);
                                intent.putExtra("Lista", wordListFromLevel);
                                startActivity(intent);

                            }
                        }).start();
                        return true;
                    }
                }
        );

        buttonLevelRepeat.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Level.this, SelectOfRepeat.class);
                                intent.putExtra("Lista", wordListFromLevel);
                                startActivity(intent);
                            }
                        }).start();
                    }
                }

        );

        buttonLevelTeaching.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Level.this, TeachingActivity.class);
                                intent.putExtra("Lista", wordListFromLevel);
                                startActivity(intent);
                            }
                        }).start();
                    }
                }

        );
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {


        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        //ttsClass = null;
        super.onDestroy();
    }

    TextToSpeech textToSpeech;

    @Override
    public void onInit(final int status) {

        new Thread(new Runnable() {
            public void run() {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        }).start();
    }


}
