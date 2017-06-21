package com.rwilk.angielski.views.teaching.repeat;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rwilk.angielski.R;
import com.rwilk.angielski.database.Word;
import com.rwilk.angielski.views.RepetitionActivity;

import java.util.ArrayList;
import java.util.Random;

import static com.rwilk.angielski.R.layout.fragment_voice_answers;

/**
 * Created by wilkr on 13.04.2017.
 * Voice answers
 */
public class RVoiceAnswers extends Fragment {

    public TextView textViewVoiceAnswers;
    public TextView textViewVoiceCorrectAnswers;
    public Button buttonSpeakerOne;
    public Button buttonSpeakerTwo;
    public Button buttonSpeakerThree;
    public Button buttonSpeakerFour;
    public Button buttonNextPage;
    public ImageView imageViewCheckAnswer;

    public static ArrayList<Word> listOfWordsToStudy;
    public static ArrayList<Word> listOfAllWordFromLevel;
    public ArrayList<Word> wrongAnswers;
    public Word correctAnswer;
    public int positionToCorrectAnswer;

    public int markedAnswer = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(fragment_voice_answers, container, false);

        textViewVoiceAnswers = (TextView) view.findViewById(R.id.textViewVoiceAnswers);
        textViewVoiceCorrectAnswers = (TextView) view.findViewById(R.id.textViewVoiceCorrectAnswers);
        buttonSpeakerOne = (Button) view.findViewById(R.id.buttonSpeakerOne);
        buttonSpeakerTwo = (Button) view.findViewById(R.id.buttonSpeakerTwo);
        buttonSpeakerThree = (Button) view.findViewById(R.id.buttonSpeakerThree);
        buttonSpeakerFour = (Button) view.findViewById(R.id.buttonSpeakerFour);
        buttonNextPage = (Button) view.findViewById(R.id.buttonNextPageRepeat);
        imageViewCheckAnswer = (ImageView) view.findViewById(R.id.imageViewCheckAnswer);
        buildTeachingView(); //budujemy widok

        return view;
    }

    public static RVoiceAnswers newInstance(ArrayList<Word> wordsToStudy, ArrayList<Word> allWordFromLevel) {
        Bundle args = new Bundle();
        RVoiceAnswers fragment = new RVoiceAnswers();
        fragment.setArguments(args);
        listOfWordsToStudy = wordsToStudy;
        listOfAllWordFromLevel = allWordFromLevel;
        return fragment;
    }

    public void buildTeachingView() {
        if (RepetitionActivity.sizeOfWordsToRepeat < listOfWordsToStudy.size())
            correctAnswer = listOfWordsToStudy.get(randomCorrectAnswer(listOfWordsToStudy.size() - RepetitionActivity.sizeOfWordsToRepeat));
        else if (!listOfWordsToStudy.isEmpty()) correctAnswer = listOfWordsToStudy.get(0);
        else correctAnswer = RepetitionActivity.lastWord;
        ((RepetitionActivity) getActivity()).updateListOfWordsToStudy(correctAnswer);

        //jednak jesli odpowiedzielismy zle to bierzemy inne slowko //resetuje sie po poprawnej odpowiedzi albo jak zrobi kolo
        positionToCorrectAnswer = randomPositionToCorrectAnswer(); //wylosujemy miejsce gdzie wrzucimy poprawna odpowiedz
        wrongAnswers = randomWordToWrongAnswers(correctAnswer); //losujemy 4 slowka na bledne odpowiedzi //4 --> zeby nie bawic sie pozniej ze sprawdzaniem
        textViewVoiceAnswers.setText(correctAnswer.getPolishWord()); //ustawiamy wyraz
        textViewVoiceCorrectAnswers.setText(correctAnswer.getEnglishWord()); //ustawiamy textView na poprawna odpowiedz (angielskie slowko)
    }

    public int randomCorrectAnswer(int sizeOfList) {
        return new Random().nextInt(((sizeOfList - 1) + 1)); //powinno losowac od 0 od rozmiaru listy
    }

    public int randomPositionToCorrectAnswer() {
        return new Random().nextInt(((3) + 1));
    } //powinno losowac 0-3

    public ArrayList<Word> randomWordToWrongAnswers(Word correctAnswer) {
        ArrayList<Word> listOfWrongAnswers = new ArrayList<>();
        Random random = new Random();
        int randomIndex;
        while (listOfWrongAnswers.size() != 4) {
            randomIndex = random.nextInt(((listOfAllWordFromLevel.size() - 1)) + 1);
            if ((listOfAllWordFromLevel.get(randomIndex).getId()==correctAnswer.getId())
                    || (listOfWrongAnswers.contains(listOfAllWordFromLevel.get(randomIndex))));
                else listOfWrongAnswers.add(listOfAllWordFromLevel.get(randomIndex));
        }
        return listOfWrongAnswers;
    }

    @Override
    public void onStart() { //jedna z metod w cyklu zycia aplikacji android
        super.onStart();    //konstruktor z nadpisanej metody

        buttonSpeakerOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (positionToCorrectAnswer == 0) {
                    ((RepetitionActivity) getActivity()).ttsSpeech(correctAnswer.getEnglishWord());
                } else {  //wywolujemy metode z klasy TeachingActivity
                    ((RepetitionActivity) getActivity()).ttsSpeech(wrongAnswers.get(0).getEnglishWord());
                }
                markedAnswer = 0; //zaznaczona odpowiedz to 0
                setNormalBackground(markedAnswer); //ustawiamy normalne tlo dla odpowiedzi innych niz zaznaczona, bo np. wczesniej mogla byc zaznaczona inna
                buttonSpeakerOne.setTextColor(getResources().getColor(R.color.white)); //zmieniamy kolor textu
                buttonSpeakerOne.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_voice_marked)); //tlo zaznaczenia
            }
        });

        buttonSpeakerTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionToCorrectAnswer == 1) {
                    ((RepetitionActivity) getActivity()).ttsSpeech(correctAnswer.getEnglishWord());
                } else {
                    ((RepetitionActivity) getActivity()).ttsSpeech(wrongAnswers.get(1).getEnglishWord());
                }
                markedAnswer = 1;
                setNormalBackground(markedAnswer);
                buttonSpeakerTwo.setTextColor(getResources().getColor(R.color.white));
                buttonSpeakerTwo.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_voice_marked));
            }
        });

        buttonSpeakerThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionToCorrectAnswer == 2) {
                    ((RepetitionActivity) getActivity()).ttsSpeech(correctAnswer.getEnglishWord());
                } else {
                    ((RepetitionActivity) getActivity()).ttsSpeech(wrongAnswers.get(2).getEnglishWord());
                }
                markedAnswer = 2;
                setNormalBackground(markedAnswer);
                buttonSpeakerThree.setTextColor(getResources().getColor(R.color.white));
                buttonSpeakerThree.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_voice_marked));
            }
        });

        buttonSpeakerFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionToCorrectAnswer == 3) {
                    ((RepetitionActivity) getActivity()).ttsSpeech(correctAnswer.getEnglishWord());
                } else {
                    ((RepetitionActivity) getActivity()).ttsSpeech(wrongAnswers.get(3).getEnglishWord());
                }
                markedAnswer = 3;
                setNormalBackground(markedAnswer);
                buttonSpeakerFour.setTextColor(getResources().getColor(R.color.white));
                buttonSpeakerFour.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_voice_marked));
            }
        });

        imageViewCheckAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //przycisk do sprawdzania odpowiedzi
                if (markedAnswer == -1) return; //jesli nie jest zaznaczona zadna odpowiedz to nie rob nic
                setGreenBackground(positionToCorrectAnswer); //ustawiamy zielone tlo dla poprawnej odpowiedzi
                setRedBackground(positionToCorrectAnswer, markedAnswer); //ustawiamy czerwone tlo, jesli zaznaczylismy bledna odpowiedz
                buttonNextPage.callOnClick(); //wywolujemy button zmieniajacy strone
            }
        });
    }

    private void setNormalBackground(int zaznaczonyImageButton) { //ustawiamy normalne tlo i kolor tekstu dla wszystkich odpowiedzi poza zaznaczona
        if (zaznaczonyImageButton != 0) {
            buttonSpeakerOne.setTextColor(getResources().getColor(R.color.colorPrimary));
            buttonSpeakerOne.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four));
        }
        if (zaznaczonyImageButton != 1) {
            buttonSpeakerTwo.setTextColor(getResources().getColor(R.color.colorPrimary));
            buttonSpeakerTwo.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four));
        }
        if (zaznaczonyImageButton != 2) {
            buttonSpeakerThree.setTextColor(getResources().getColor(R.color.colorPrimary));
            buttonSpeakerThree.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four));
        }
        if (zaznaczonyImageButton != 3) {
            buttonSpeakerFour.setTextColor(getResources().getColor(R.color.colorPrimary));
            buttonSpeakerFour.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four));
        }
    }

    private void setGreenBackground(int miejsceOdpowiedzi) { //ustawiamy zielone tlo dla poprawnej odpowiedzi
        if (miejsceOdpowiedzi == 0) {
            buttonSpeakerOne.setTextColor(getResources().getColor(R.color.white));
            buttonSpeakerOne.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four_correct));
        } else if (miejsceOdpowiedzi == 1) {
            buttonSpeakerTwo.setTextColor(getResources().getColor(R.color.white));
            buttonSpeakerTwo.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four_correct));
        } else if (miejsceOdpowiedzi == 2) {
            buttonSpeakerThree.setTextColor(getResources().getColor(R.color.white));
            buttonSpeakerThree.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four_correct));
        } else if (miejsceOdpowiedzi == 3) {
            buttonSpeakerFour.setTextColor(getResources().getColor(R.color.white));
            buttonSpeakerFour.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four_correct));
        }
        //wyświetlamy poprawną odpowiedź
        textViewVoiceCorrectAnswers.setTextColor(ContextCompat.getColor(getContext(), R.color.green800));
        textViewVoiceCorrectAnswers.setVisibility(View.VISIBLE);//pokazujemy napis z poprawnym tlumaczeniem
    }

    private void setRedBackground(int miejsceOdpowiedzi, int zaznaczonyButton) {
        if (miejsceOdpowiedzi == zaznaczonyButton) { //jesli nasza odpowiedz jest prawidlowa
            return;
            //nizej jesli odpowiedz jest bledna
        } else if (zaznaczonyButton == 0) //czerwone tlo dla blednej odpowiedzi
            buttonSpeakerOne.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four_wrong));
        else if (zaznaczonyButton == 1)
            buttonSpeakerTwo.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four_wrong));
        else if (zaznaczonyButton == 2)
            buttonSpeakerThree.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four_wrong));
        else if (zaznaczonyButton == 3)
            buttonSpeakerFour.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four_wrong));

        textViewVoiceCorrectAnswers.setTextColor(ContextCompat.getColor(getContext(), R.color.redWrongAnswer));
        textViewVoiceCorrectAnswers.setVisibility(View.VISIBLE);//pokazujemy napis z poprawnym tlumaczeniem
        ((RepetitionActivity) getActivity()).decreaseListOfWordsToStudy(correctAnswer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
