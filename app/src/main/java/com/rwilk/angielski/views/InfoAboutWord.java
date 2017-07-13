package com.rwilk.angielski.views;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rwilk.angielski.R;
import com.rwilk.angielski.customview.TTSClass;
import com.rwilk.angielski.database.DBHelper;
import com.rwilk.angielski.database.Word;

import java.util.ArrayList;

import static com.rwilk.angielski.R.layout.fragment_info_about_word;

/**
 * Klasa wyswietla informacje o slowku po dlugim przytrzymaniu na liscie.
 */
public class InfoAboutWord extends AppCompatActivity {

    public SectionsPagerAdapter mSectionsPagerAdapter;
    private static ArrayList<Word> wordListFromLevel;  //listOfWordsToDatabase slowek
    public ViewPager mViewPager;
    public TTSClass ttsClass;  //textToSpeech

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_about_word);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mViewPager = (ViewPager) findViewById(R.id.container);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Info about word");
        }
        Bundle bundle = getIntent().getExtras();//Przekazanie listy slowek do tego activity...
        if (bundle == null) {
            return;
        }
        int pozycjaItemu = bundle.getInt("pozycjaItemu"); //przesylamy pozycje slowka, ktore kliknelismy
        wordListFromLevel = (ArrayList<Word>) bundle.get("Lista");  //przesylamy liste slowek z wybranego poziomu

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), wordListFromLevel.size()); //konstruktor
        mViewPager.setAdapter(mSectionsPagerAdapter); //ustawienie adaptera

        mViewPager.setOnPageChangeListener(mSectionsPagerAdapter); //listener zmiany strony  //to chyba jest zbedne?
        mViewPager.setCurrentItem(pozycjaItemu, false); //ustawienie aktualnej pozycji strony

        ttsClass = new TTSClass();
    }

    public void ttsSpeech(String word) {
        ttsClass.speek(word);
    }

    public void onClickArrowPrevious(View view) {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
    }

    public void onClickArrowNext(View view) {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ttsClass = null;
    }

    public static class InfoAboutWordFragment extends Fragment {

        public InfoAboutWordFragment() {
        }
        private ImageView imageViewInfoKsiazka;
        private ProgressBar progressBar;
        private TextView infoEnglishWord;
        private TextView infoPolishWord;
        private TextView infoPartOfSpeech;
        private DBHelper db;
        private Word currentWord;

        private ImageView imageViewInfoDifficult;
        private ImageView imageViewInfoRepeat;

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(fragment_info_about_word, container, false);
            imageViewInfoKsiazka = (ImageView) view.findViewById(R.id.imageViewInfoKsiazka);
            infoEnglishWord = (TextView) view.findViewById(R.id.infoEnglishWord);
            infoPolishWord = (TextView) view.findViewById(R.id.infoPolishWord);
            infoPartOfSpeech = (TextView) view.findViewById(R.id.infoPartOfSpeech);
            imageViewInfoDifficult = (ImageView) view.findViewById(R.id.imageViewInfoDifficult);
            imageViewInfoRepeat = (ImageView) view.findViewById(R.id.imageViewInfoRepeat);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

            int index = getArguments().getInt("licznik"); //odbieramy index slowka w liscie
            if (index == wordListFromLevel.size()) index = 0;

            infoEnglishWord.setText(wordListFromLevel.get(index).getEnglishWord()); //ustawiamy trzy pola tekstowe
            infoPolishWord.setText(wordListFromLevel.get(index).getPolishWord());
            infoPartOfSpeech.setText(wordListFromLevel.get(index).getPartOfSpeech());

            imageViewInfoKsiazka.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((InfoAboutWord) getActivity()).ttsSpeech(infoEnglishWord.getText().toString()); //wywolanie TextToSpeech
                }
            });

            currentWord = wordListFromLevel.get(index);
            setParametersOfWord(currentWord);
            db = new DBHelper(getContext(), NewMainActivity.databaseVersion);

            setImageOfDifficultWord(currentWord.getId()); //ustawiamy obrazek trudnego slowka
            setImageOfBrainRepeat(currentWord.getId());  //ustawiamy obrazek mozgu czyli powtarzanie

            return view;
        }

        private void setImageOfDifficultWord(int id) {
            if (db.getDifficult(id) == 1)
                imageViewInfoDifficult.setImageResource(R.drawable.niebieski);
            else imageViewInfoDifficult.setImageResource(R.drawable.szary);
        }

        private void setImageOfBrainRepeat(int id) {
            if (db.getRepeat(id) == 1)
                imageViewInfoRepeat.setImageResource(R.drawable.mozg_szary);
            else imageViewInfoRepeat.setImageResource(R.drawable.mozg);
        }

        @Override
        public void onStart() {
            super.onStart();
            //metody onClick dla trudnego slowka i dla mozgu
            imageViewInfoDifficult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.setDifficult(currentWord.getId()); //ustawiamy w bazie danych
                    currentWord.setDifficult(db.getDifficult(currentWord.getId())); //ustawiamy w parametrach slowka
                    Level.changeDifficultWord(currentWord.getId()); //zmieniamy obrazek na liscie w Levelu
                    setImageOfDifficultWord(currentWord.getId()); //ustawiamy obrazek dla trudnego slowka
                }
            });

            imageViewInfoRepeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.setRepeat(currentWord.getId(), currentWord.getIdSection()); //ustawiamy w bazie danych
                    currentWord.setRepeat(db.getRepeat(currentWord.getId())); //ustawiamy w parametrach slowka
                    setParametersOfWord(currentWord); //ustawiamy wszystkie obrazki progressu
                    Level.changeRepeatWord(currentWord.getId()); //zmieniamy parametry slowka w liscie w levelu...
                    //...zeby pozniej nie brac tego slowka do nauki
                    setImageOfBrainRepeat(currentWord.getId());//ustawiamy obrazek mozgu czyli powtarzanie
                }
            });
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        public void setParametersOfWord(Word word) {
            int progressToSet;
            if (word.getRepeat() == 1) progressToSet = 50;
            else progressToSet = word.getProgress();
            if (progressBar.getProgress() == 100) progressBar.setProgress(0);

            if (android.os.Build.VERSION.SDK_INT >= 11) { //animacja updejtowania progressBara //w sumie to mozna od razu trzaskac bo minimum sdk jest wieksze
                ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progressToSet * 2);
                animation.setDuration(500); // 0.5 second
                animation.setInterpolator(new DecelerateInterpolator());
                animation.start();
            } else progressBar.setProgress(progressToSet * 2);

            if (word.getRepeat() == 1) imageViewInfoKsiazka.setImageResource(R.drawable.potworek5);
            else {
                switch (word.getProgress()) {
                    case 0:
                        imageViewInfoKsiazka.setImageResource(R.drawable.potworek0);
                        break;
                    case 10:
                        imageViewInfoKsiazka.setImageResource(R.drawable.potworek1);
                        break;
                    case 20:
                        imageViewInfoKsiazka.setImageResource(R.drawable.potworek2);
                        break;
                    case 30:
                        imageViewInfoKsiazka.setImageResource(R.drawable.potworek3);
                        break;
                    case 40:
                        imageViewInfoKsiazka.setImageResource(R.drawable.potworek4);
                        break;
                    case 50:
                        imageViewInfoKsiazka.setImageResource(R.drawable.potworek5);
                        break;
                    default:
                }
            }
        }

        @Override
        public void onDestroy() {
            db.close();
            super.onDestroy();
        }
    }

    //Tego nizej nie ruszac
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {

        private int listSize;

        public SectionsPagerAdapter(FragmentManager fm, int listSize) {
            super(fm);
            this.listSize = listSize;
        }

        @Override
        public Fragment getItem(int position) {
            InfoAboutWordFragment info = new InfoAboutWordFragment();

            Bundle args = new Bundle();
            int index = position - 1;
            if (position == 0) {
                index = listSize - 1;
            } else if (position == listSize + 1) {
                index = 0;
            }
            args.putInt("licznik", index + 1);
            info.setArguments(args);
            return info;
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

        @Override
        public int getCount() {
            return listSize;
        }
    } //koniec klasy SectionsPagerAdapter

}
