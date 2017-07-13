package com.rwilk.angielski.views;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rwilk.angielski.database.DBHelper;
import com.rwilk.angielski.R;
import com.rwilk.angielski.customview.TTSClass;
import com.rwilk.angielski.database.Word;
import com.rwilk.angielski.views.teaching.repeat.ROneOfFourAnswers;
import com.rwilk.angielski.views.teaching.repeat.RVoiceAnswers;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RepetitionActivity extends AppCompatActivity {

    public SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public ProgressBar progressBar;
    public ArrayList<Word> listOfWordsToStudy;
    public ArrayList<Word> listOfAllWordFromLevel;
    public TTSClass ttsClass;
    public float incrementOfProgress;
    private float progress = 0;
    private int countOfPages = 0;

    public static int indexOfWordToStudy = 0;
    private TextView toolbarPoints;
    public static int sizeOfWordsToRepeat = 0;
    public static Word lastWord = null;

    private int combo = 0;
    private int points = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teaching);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        toolbarPoints = (TextView) findViewById(R.id.toolbar_points);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Powtórz słówka");

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        listOfAllWordFromLevel = setItems(bundle.get("Lista"));
        listOfWordsToStudy = setItems(bundle.get("ListaDoPowtorki"));

        if (listOfAllWordFromLevel == null || listOfWordsToStudy == null)
            Toast.makeText(getApplicationContext(), R.string.error_while_loading_words, Toast.LENGTH_SHORT).show();


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(mSectionsPagerAdapter);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        ttsClass = new TTSClass();

        incrementOfProgress = (100.0f / (listOfWordsToStudy.size())) + 0.01f;

        indexOfWordToStudy = 0; //zabezpieczenie przed stara wartoscia, bo pole jest static
        setRepeatInWord();
        combo = 0;
        progress = 0;
        countOfPages = 0;
    }

    /**
     * Ustawiamy wszystkim słowom zero w polu countingRepeats.
     */
    private void setRepeatInWord() {
        for (Word w : listOfWordsToStudy) {
            w.setCountingRepeats(0);
        }
        sizeOfWordsToRepeat = 0;
        lastWord = null;
    }

    public static boolean wrongAnswer = false;

    public void updateListOfWordsToStudy(Word word) {
        if (listOfWordsToStudy.size() > 1) {
            listOfWordsToStudy.remove(word);
        } else if (!listOfWordsToStudy.isEmpty()) {
            lastWord = listOfWordsToStudy.get(0);
            listOfWordsToStudy.remove(word);
        }
    }

    public void decreaseListOfWordsToStudy(Word word) {
        listOfWordsToStudy.add(listOfWordsToStudy.size(), word);
        listOfWordsToStudy.get(listOfWordsToStudy.size() - 1).setCountingRepeats(1);
        wrongAnswer = true;
        sizeOfWordsToRepeat++;
    }

    //bardzo wazna metoda
    public void ttsSpeech(String word) {
        ttsClass.speek(word);
    }

    //public static int wordIndex = 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {//zaczyna sie od zera
            //if (position == 0) wordIndex = 0;

            if (position == 0 || position % 2 == 0) {
                return RVoiceAnswers.newInstance(listOfWordsToStudy, listOfAllWordFromLevel);
            } else return ROneOfFourAnswers.newInstance(listOfWordsToStudy, listOfAllWordFromLevel);
        }

        @Override
        public int getCount() {
            return 40;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    public void buttonZmienStroneRepeat(View view) {

        //Trzeba naprawić cały moduł powtórek, żeby trzaskać po dwa-trzy razy słowo.

        float trueIncrementOfProgress; // = incrementOfProgress;
        if (wrongAnswer) {
            trueIncrementOfProgress = 0;
            wrongAnswer = false;
            combo = 0;
        } else {
            trueIncrementOfProgress = incrementOfProgress;
            points += 50 + 15 * combo++;
            toolbarPoints.setText(Integer.toString(points));
        }
        countOfPages++;
        progress += trueIncrementOfProgress;
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", (int) progress);
        animation.setDuration(500); // 0.5 second
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
        if (progress >= 100 || countOfPages == 40) {//zamykamy activity po sekundzie
            Timer swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updatePoints(points);
                            finish();
                        }
                    });
                }
            }, 1000);
        } else {
            Timer swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                        }
                    });
                }
            }, 1000);
        }
    }

    private void updatePoints(int points) {
        DBHelper db = new DBHelper(this, NewMainActivity.databaseVersion);
        //db.updatePoints(points);
        db.close();
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }

    /**
     * Method shows the dialog, which asks user if want to exit.
     */
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
        builder.setMessage(R.string.you_will_lose_your_progress)
                .setTitle(R.string.are_you_want_to_exit);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Metoda jest stworzona, ponieważ bezpośrednie przypisanie: listOfAllWordFromLevel = (ArrayList<Word>) bundle.get("Lista");
     * Powodowało warning: Unchecked cast
     *
     * @param var listOfWordsToDatabase przekazana przez bundle
     * @return listOfWordsToDatabase obiektow
     */
    public ArrayList<Word> setItems(Object var) {
        ArrayList<Word> result = new ArrayList<>();
        if (var instanceof List) {
            for (int i = 0; i < ((List<?>) var).size(); i++) {
                Object item = ((List<?>) var).get(i);
                if (item instanceof Word)
                    result.add((Word) item);
            }
        }
        return result;
    }

}