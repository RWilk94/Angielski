package com.rwilk.angielski.customview;

import android.app.ProgressDialog;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ProgressBar;

import com.rwilk.angielski.database.Angielski;

import java.util.Locale;

/**
 * Created by wilkr on 13.04.2017.
 * Klasa odpowiedzialna za TextToSpeech
 */
public class TTSClass implements TextToSpeech.OnInitListener {

    private TextToSpeech textToSpeech;

    public TTSClass(){
        Angielski angielski = new Angielski();
        textToSpeech = new TextToSpeech(angielski.getAppContext(), this);
    }

    public void speek(String word){
        textToSpeech.speak(word, TextToSpeech.QUEUE_ADD, null);
    }

    public void speekSlowly(String word){
        textToSpeech.setSpeechRate(0.5f);
        textToSpeech.speak(word, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Log.d("TextToSpeech", "Success!");
            textToSpeech.setLanguage(Locale.UK);
        }
        isReady = true;
    }

    public static boolean isReady = false;
}
