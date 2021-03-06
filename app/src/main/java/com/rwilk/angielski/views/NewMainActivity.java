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

import com.rwilk.angielski.database.DBHelper;
import com.rwilk.angielski.R;
import com.rwilk.angielski.database.User;
import com.rwilk.angielski.database.Word;
import com.rwilk.angielski.database.WordSQL;
import com.rwilk.angielski.file.CheckIfUserIsLogin;
import com.rwilk.angielski.views.fragments.About;
import com.rwilk.angielski.views.fragments.Lessons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Rafał Wilk.
 * NewMainActivity jest to gółwna klasa, uruchamiana jest po ekranie logowania.
 * Elementami widoku są: ViewPager, Toolbar i fragment, w którym są umieszczane lekcje do nauki.
 */
public class NewMainActivity extends AppCompatActivity {

    /** Wersja bazy danych. Zmienna używana przy tworzeniu obiektu klasy DBHelper i dostępie do bazy danych.  */
    public static int databaseVersion = 6;

    /**
     * Metoda wywoływana w momencie, kiedy widok (activity) jest tworzony.
     * This method receives the parameter savedInstanceState, which is a Bundle object containing the activity's previously saved state.
     * If the activity has never existed before, the value of the Bundle object is null.
     * @param savedInstanceState  object containing the activity's previously saved state
     */
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
        //checkIfUserIsLogin();
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.mozg);
        tabLayout.getTabAt(1).setIcon(R.drawable.settings);


    }


    /**
     * Metoda przygotowuje logo, które jest umieszczone na pasku (Toolbar).
     * @return logo o wymiarach 70x70
     */
    private Drawable getLogo() {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ksiazka);
        assert (drawable) != null;
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        return new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 70, 70, true));
    }

    /**
     * Klasa opisująca zachowania PageAdaptera.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        /**
         * Domyślny konstruktor
         * @param fm FragmentManager; w kodzie używamy getSupportFragmentManager;
         */
        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Metoda odpowiada za stworzenie odpowiedniego widoku w zależności, która strona SectionsPagerAdapter jest wyświetlana.
         * @param position numer strony SectionsPagerAdapter
         * @return fragment zawierający odpowiedni widok
         */
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return Lessons.newInstance();
                case 1:
                    return About.newInstance();
                default:
                    return null;
            }
        }

        /**
         * Metoda zwraca liczbę stron w SectionsPagerAdapter.
         * @return liczba stron w SectionsPagerAdapter.
         */
        @Override
        public int getCount() {
            return 2;
        }
    }

    /**
     * Metoda zamyka NewMainActivity i wraca do LoginActivity.
     */
    public void closeActivity() {
        finish();
    }


    /**
     * Metoda zwraca się do bazy danych i wyciąga z niej wszystkie słówka z konkretnego poziomu.
     * @param x poziom, z którego chcemy wyciągnąć słówka.
     * @return ArrayList<Word> - lista słówek z konkretnego poziomu.
     */
    public ArrayList<Word> getListaSlowek(String x) {
        DBHelper db = new DBHelper(getApplicationContext(), NewMainActivity.databaseVersion);
        ArrayList<Word> lista = db.getAllWordsFromLevelX(x);
        db.close();
        return lista;
    }

    /**
     * Bardzo ważna metoda. Sprawdza czy użytkownik jest zalogowany.
     * Odpowiada za synchronizację.
     */



}
