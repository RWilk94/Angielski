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
import com.rwilk.angielski.settings.Settings;
import com.rwilk.angielski.views.teaching.OneOfFourAnswers;
import com.rwilk.angielski.views.teaching.VoiceAnswers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Nauka, przewijane strony
 */
public class TeachingActivity extends AppCompatActivity {

    public SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public ProgressBar progressBar;

    public ArrayList<Word> listOfWordsToStudy = new ArrayList<>(); //listOfWordsToDatabase slowek, ktore mamy sie nauczyc
    public ArrayList<Word> listOfAllWordFromLevel = new ArrayList<>(); //listOfWordsToDatabase wszystkich slowek z poziomu
    public int sumOfProgress;
    public float incrementOfProgress;
    public TTSClass ttsClass;
    private float progress = 0;
    private int countOfPages = 0;

    public static boolean wrongAnswer = false;
    public static String lastWord = "";
    public static boolean changedProgress = false;

    //private Toolbar myToolbar;
    /**
     * TextView, gdzie zapisujemy punkty.
     */
    private TextView toolbarPoints;
    /**
     * Ilość dobrych odpowiedzi z rzędu.
     */
    private int combo = 0;
    private int points = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teaching);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar); //toolbar czyli pasek u gory
        myToolbar.setTitle("");
        toolbarPoints = (TextView) findViewById(R.id.toolbar_points);
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.teaching_activity_toolbar);

        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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
        if (listOfAllWordFromLevel == null)
            Toast.makeText(getApplicationContext(), R.string.error_while_loading_words, Toast.LENGTH_SHORT).show();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(mSectionsPagerAdapter);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);

        combo = 0;
        ttsClass = new TTSClass();
        prepareWordsToStudy();
        progress = 0;
        countOfPages = 0;
    }


    /**
     * Method call method which are responsibility for calculate increment of progress and etc.
     * Called method: findWordsToStudy, calculateTheSumOfProgress, calculateIncrementOfProgress.
     */
    public void prepareWordsToStudy() {
        findWordsToStudy();
        calculateTheSumOfProgress();
        calculateIncrementOfProgress();
    }

    /**
     * Method looking for words to study, or words with progress less than 50.
     */
    public void findWordsToStudy() {
        for (int i = 0; i < listOfAllWordFromLevel.size(); i++) {
            if (listOfAllWordFromLevel.get(i).getRepeat() != 1 && listOfAllWordFromLevel.get(i).getProgress() != 50) {
                listOfWordsToStudy.add(listOfAllWordFromLevel.get(i));
                if (listOfWordsToStudy.size() == Settings.getCountWordToStudy())
                    break;
            }
        }
        if (listOfWordsToStudy.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.know_all_words_from_level, Toast.LENGTH_SHORT).show();
            Level.disableTeachingButton = true;
            finish();
        }
    }

    /**
     * Method calculate how much times yet user must correctly learn words.
     */
    public void calculateTheSumOfProgress() {
        sumOfProgress = 0;
        for (Word x : listOfWordsToStudy) {
            if (x.getProgress() != 50) sumOfProgress += (50 - x.getProgress());
        }
    }

    /**
     * Method calculate single increment of progress when user correctly learn words.
     */
    public void calculateIncrementOfProgress() {
        if (sumOfProgress >= 100 || sumOfProgress == 0) incrementOfProgress = 10;
        else incrementOfProgress = (100.0f / (sumOfProgress / 10)) + 0.01f;
    }

    /**
     * Method changes the page after user's answer.
     *
     * @param view view
     */
    public void buttonZmienStrone(View view) {
        float trueIncrementOfProgress;

        if (wrongAnswer) {
            trueIncrementOfProgress = 0;
            wrongAnswer = false;
            combo = 0;
        } else {
            trueIncrementOfProgress = incrementOfProgress;
            points += 50 + 15 * combo++;
            toolbarPoints.setText(String.format(Locale.UK, "%d", points)); //Integer.toString(points));
            //textViewPointsAllTime.setText(String.format(Locale.UK, "%d", points.getAllTime()));
        }
        countOfPages++;
        progress += trueIncrementOfProgress;

        //final int progress = progressBar.getProgress() + trueIncrementOfProgress; //zmienna po to, bo progress na pasku dodawany jest dopiero po 0.5 sekundy a dzieki zmiennej jest od razu

        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", (int) progress);
        animation.setDuration(500); // 0.5 second
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progress >= 100 || countOfPages == 40) {
                            Level.updateListOfWordFromLevel(listOfWordsToStudy);
                            updatePoints(points);
                            finish();
                        } else {
                            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                        }
                    }
                });
            }
        }, 1000);
    }

    private void updatePoints(int points) {
        DBHelper db = new DBHelper(this, NewMainActivity.databaseVersion);
        db.updatePoints(points);
        db.close();
    }

    /**
     * Method call TextToSpeech and speak word.
     *
     * @param word the word to be spoken
     */
    public void ttsSpeech(String word) {
        ttsClass.speek(word);
    }

    /**
     * Method update progress of word when view of teaching is creating.
     *
     * @param id identity of word
     */
    public void updateListOfWordsToStudy(int id) {
        for (int i = 0; i < listOfWordsToStudy.size(); i++) {
            if (listOfWordsToStudy.get(i).getId() == id && listOfWordsToStudy.get(i).getProgress() < 50) {
                listOfWordsToStudy.get(i).setProgress(listOfWordsToStudy.get(i).getProgress() + 10);
                changedProgress = true;
                break;
            }
        }
    }

    /**
     * Method decrease progress of word when user's answer is incorrect.
     *
     * @param id identity of word
     */
    public void decreaseListOfWordsToStudy(int id) {
        for (int i = 0; i < listOfWordsToStudy.size(); i++) {
            if (listOfWordsToStudy.get(i).getId() == id && changedProgress) {
                listOfWordsToStudy.get(i).setProgress(listOfWordsToStudy.get(i).getProgress() - 10);
                changedProgress = false;
                break;
            }
        }
        wrongAnswer = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    /**
     * Class represented viewPager, or changes the fragments of view.
     */
    private class SectionsPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {//zaczyna sie od zera

            if (!listOfWordsToStudy.isEmpty()) {
                if (position % 2 == 0) {
                    return OneOfFourAnswers.newInstance(listOfWordsToStudy, listOfAllWordFromLevel);
                } else return VoiceAnswers.newInstance(listOfWordsToStudy, listOfAllWordFromLevel);
            } else {
                finish();
                return null;
            }
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
}