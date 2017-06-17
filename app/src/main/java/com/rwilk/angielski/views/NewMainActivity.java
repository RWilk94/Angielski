package com.rwilk.angielski.views;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.rwilk.angielski.DBHelper;
import com.rwilk.angielski.R;
import com.rwilk.angielski.database.Word;
import com.rwilk.angielski.database.WordSQL;
import com.rwilk.angielski.views.fragments.About;
import com.rwilk.angielski.views.fragments.Lessons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Blabla
 * Created by wilkr on 21.05.2017.
 */
public class NewMainActivity extends AppCompatActivity {

    public static int databaseVersion = 2;
    public static ArrayList<WordSQL> listOfWordsToDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.toolbar_title);
            getSupportActionBar().setSubtitle(R.string.toolbar_subtitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getLogo());
        }
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.mozg);
        tabLayout.getTabAt(1).setIcon(R.drawable.settings);
        createDatabase();

    }

    private Drawable getLogo() {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ksiazka);
        assert (drawable) != null;
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        return new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 70, 70, true));
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return Lessons.newInstance();
                //return About.newInstance();
                case 1:
                    //return Study.newInstance();
                    return About.newInstance();
                case 2:
                    //return Skills.newInstance();
                    return null;
                case 3:
                    //return Info.newInstance();
                    return null;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public void closeActivity() {
        finish();
    }


    public ArrayList<Word> getListaSlowek(String x) {
        DBHelper db = new DBHelper(getApplicationContext(), NewMainActivity.databaseVersion);
        ArrayList<Word> lista = db.getAllWordsFromLevelX(x);
        db.close();
        return lista;
    }


    public ArrayList<WordSQL> odczytZPliku() {
        BufferedReader reader = null;
        String polishWord = "", englishWord = "", partOfSpeech = "", section = "";
        ArrayList<WordSQL> listaSlowZPliku = new ArrayList<>();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("words.txt"), "UTF-8"));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                if (mLine.isEmpty()) continue;
                else if (mLine.substring(0, 3).equalsIgnoreCase("//*")) {
                    section = "'" + mLine.substring(3) + "'";
                } else if (mLine.substring(0, 2).equalsIgnoreCase("//")) {
                    partOfSpeech = mLine.substring(2);
                } else {
                    int tabulator = mLine.indexOf("\t"); //znajdujemy tabulator
                    if (tabulator != -1) {
                        polishWord = mLine.substring(0, tabulator); //wycinamy polskie słowo
                        englishWord = mLine.substring(tabulator + 1, mLine.length());
                    }
                    String sql = "'" + polishWord + "', '" + englishWord + "', '"  + partOfSpeech + "'";
                    WordSQL wordSQL = new WordSQL();
                    wordSQL.setSql(sql);
                    wordSQL.setSection(section);
                    listaSlowZPliku.add(wordSQL);
                }
            }
        } catch (IOException e) {
            System.out.println("Error ---------------------------- Error");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Error ---------------------------- Error2");
                }
            }
        }
        return listaSlowZPliku;
    }

    private void createDatabase() {
        DBHelper db;
        System.out.println("DB " + DBHelper.databaseExists);
        if (!DBHelper.databaseExists) {
            listOfWordsToDatabase = odczytZPliku();
        }
        db = new DBHelper(getApplicationContext(), null, databaseVersion);
        System.out.println("DB " + DBHelper.databaseExists);
        db.updatePoints(0);
        db.close();
    }
}
