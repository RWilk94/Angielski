package com.rwilk.angielski.settings;

import com.rwilk.angielski.database.Word;

import java.util.ArrayList;

/**
 * Klasa ustawień. Do dodania w przyszłości.
 */
public class Settings {
    //public static boolean readPolishWord = true;
    public static int countWordToStudy = 5;
    public static ArrayList<Word> listOfShowedWord;

    public static Word currentWord;

    public static void setCountWordToStudy(int count){
        countWordToStudy = count;
    }

    public static int getCountWordToStudy(){
        return countWordToStudy;
    }


}
