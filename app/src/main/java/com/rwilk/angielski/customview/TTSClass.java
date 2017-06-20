package com.rwilk.angielski.customview;

import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.rwilk.angielski.application.Angielski;

import java.util.Locale;

/**
 * Klasa odpowiedzialna za TextToSpeech
 */
public class TTSClass implements TextToSpeech.OnInitListener {

    private TextToSpeech textToSpeech;

    public TTSClass(){
        textToSpeech = new TextToSpeech(Angielski.getAppContext(), this);
    }

    public void speek(String word){
        textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null);
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
    }
}
