package com.rwilk.angielski.views.teaching;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rwilk.angielski.R;
import com.rwilk.angielski.database.Word;
import com.rwilk.angielski.views.TeachingActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import static com.rwilk.angielski.R.layout.fragment_voice_answers;

/**
 * Created by R. Wilk on 13.04.2017.
 * Class represented fragment, where are four voice answers. User chosen one of four and presses arrow to check answer.
 */

public class VoiceAnswers extends Fragment {

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

    /**
     * Method create new instance of VoiceAnswers fragment.
     *
     * @param wordsToStudy     list of words which user will learn
     * @param allWordFromLevel list of all words from level
     * @return fragment activity
     */
    public static VoiceAnswers newInstance(ArrayList<Word> wordsToStudy, ArrayList<Word> allWordFromLevel) {
        VoiceAnswers fragment = new VoiceAnswers();
        listOfWordsToStudy = wordsToStudy;
        listOfAllWordFromLevel = allWordFromLevel;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(fragment_voice_answers, container, false);

        textViewVoiceAnswers = (TextView) view.findViewById(R.id.textViewVoiceAnswers);
        textViewVoiceCorrectAnswers = (TextView) view.findViewById(R.id.textViewVoiceCorrectAnswers);
        buttonSpeakerOne = (Button) view.findViewById(R.id.buttonSpeakerOne);
        buttonSpeakerTwo = (Button) view.findViewById(R.id.buttonSpeakerTwo);
        buttonSpeakerThree = (Button) view.findViewById(R.id.buttonSpeakerThree);
        buttonSpeakerFour = (Button) view.findViewById(R.id.buttonSpeakerFour);
        buttonNextPage = (Button) view.findViewById(R.id.buttonNextPage);
        imageViewCheckAnswer = (ImageView) view.findViewById(R.id.imageViewCheckAnswer);

        buildTeachingView();
        return view;
    }

    public void buildTeachingView() {

        Collections.sort(listOfWordsToStudy, new Comparator<Word>() {  //sortujemy liste ze slowkami do nauki
            @Override
            public int compare(Word o1, Word o2) {
                return o1.getProgress() - o2.getProgress(); //jako kryterium jest progress -> od najmniejszego
            }
        });
        correctAnswer = listOfWordsToStudy.get(0);
        if (listOfWordsToStudy.size() >= 2 && TeachingActivity.lastWord.equals(correctAnswer.getEnglishWord()))
            correctAnswer = listOfWordsToStudy.get(1);

        ((TeachingActivity) getActivity()).updateListOfWordsToStudy(correctAnswer.getId());

        positionToCorrectAnswer = randomPositionToCorrectAnswer(); //wylosujemy miejsce gdzie wrzucimy poprawna odpowiedz
        wrongAnswers = randomWordToWrongAnswers(correctAnswer); //losujemy 4 slowka na bledne odpowiedzi //4 --> zeby nie bawic sie pozniej ze sprawdzaniem

        textViewVoiceAnswers.setText(correctAnswer.getPolishWord()); //ustawiamy wyraz
        textViewVoiceCorrectAnswers.setText(correctAnswer.getEnglishWord()); //ustawiamy textView na poprawna odpowiedz (angielskie slowko)
    }

    /**
     * Method randomizes place (position) where will be correct answer.
     * It will random from 0 to 3.
     *
     * @return place to correct answer.
     */
    public int randomPositionToCorrectAnswer() {
        return new Random().nextInt(((3) + 1));
    }

    /**
     * Method randomizes four words, which are be used like wrong answers.     *
     *
     * @param correctAnswer - word, that is the correct answer.
     * @return list of four words
     */
    public ArrayList<Word> randomWordToWrongAnswers(Word correctAnswer) {
        ArrayList<Word> listOfWrongAnswers = new ArrayList<>();
        Random random = new Random();
        int randomIndex;
        while (listOfWrongAnswers.size() != 4) {
            randomIndex = random.nextInt(((listOfAllWordFromLevel.size() - 1)) + 1);
            if ((listOfAllWordFromLevel.get(randomIndex).getId() != correctAnswer.getId())
                    && (!listOfWrongAnswers.contains(listOfAllWordFromLevel.get(randomIndex))))
                listOfWrongAnswers.add(listOfAllWordFromLevel.get(randomIndex));
        }
        return listOfWrongAnswers;
    }

    /**
     * Method in Android lifecycle. Contains onClickListeners for all button in this view.
     */
    @Override
    public void onStart() {
        super.onStart();

        buttonSpeakerOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (positionToCorrectAnswer == 0) {
                    ((TeachingActivity) getActivity()).ttsSpeech(correctAnswer.getEnglishWord());
                } else {
                    ((TeachingActivity) getActivity()).ttsSpeech(wrongAnswers.get(0).getEnglishWord());
                }
                markedAnswer = 0;
                setNormalBackground(markedAnswer);
                buttonSpeakerOne.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                buttonSpeakerOne.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_voice_marked));
            }
        });

        buttonSpeakerTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionToCorrectAnswer == 1) {
                    ((TeachingActivity) getActivity()).ttsSpeech(correctAnswer.getEnglishWord());
                } else {
                    ((TeachingActivity) getActivity()).ttsSpeech(wrongAnswers.get(1).getEnglishWord());
                }
                markedAnswer = 1;
                setNormalBackground(markedAnswer);
                buttonSpeakerTwo.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                buttonSpeakerTwo.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_voice_marked));
            }
        });

        buttonSpeakerThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionToCorrectAnswer == 2) {
                    ((TeachingActivity) getActivity()).ttsSpeech(correctAnswer.getEnglishWord());
                } else {
                    ((TeachingActivity) getActivity()).ttsSpeech(wrongAnswers.get(2).getEnglishWord());
                }
                markedAnswer = 2;
                setNormalBackground(markedAnswer);
                buttonSpeakerThree.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                buttonSpeakerThree.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_voice_marked));
            }
        });

        buttonSpeakerFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionToCorrectAnswer == 3) {
                    ((TeachingActivity) getActivity()).ttsSpeech(correctAnswer.getEnglishWord());
                } else {
                    ((TeachingActivity) getActivity()).ttsSpeech(wrongAnswers.get(3).getEnglishWord());
                }
                markedAnswer = 3;
                setNormalBackground(markedAnswer);
                buttonSpeakerFour.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                buttonSpeakerFour.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_voice_marked));
            }
        });

        imageViewCheckAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (markedAnswer == -1)
                    return; //jesli nie jest zaznaczona zadna odpowiedz to nie rob nic
                setGreenBackground(positionToCorrectAnswer); //ustawiamy zielone tlo dla poprawnej odpowiedzi
                setRedBackground(positionToCorrectAnswer, markedAnswer); //ustawiamy czerwone tlo, jesli zaznaczylismy bledna odpowiedz
                buttonNextPage.callOnClick(); //wywolujemy button zmieniajacy strone
            }
        });
    }

    /**
     * Method sets default background and color to all imageButton without current clicked. It's called when clicked on imageButton.
     *
     * @param markedImageButton position of clicked imageButton
     */
    private void setNormalBackground(int markedImageButton) { //ustawiamy normalne tlo i kolor tekstu dla wszystkich odpowiedzi poza zaznaczona
        if (markedImageButton != 0) {
            buttonSpeakerOne.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            buttonSpeakerOne.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four));
        }
        if (markedImageButton != 1) {
            buttonSpeakerTwo.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            buttonSpeakerTwo.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four));
        }
        if (markedImageButton != 2) {
            buttonSpeakerThree.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            buttonSpeakerThree.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four));
        }
        if (markedImageButton != 3) {
            buttonSpeakerFour.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            buttonSpeakerFour.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four));
        }
    }

    /**
     * Method sets green background and font color, when user marked correct answer and checked.
     *
     * @param placeOfCorrectAnswer position of correct answer.
     */
    private void setGreenBackground(int placeOfCorrectAnswer) { //ustawiamy zielone tlo dla poprawnej odpowiedzi
        if (placeOfCorrectAnswer == 0) {
            buttonSpeakerOne.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            buttonSpeakerOne.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four_correct));
        } else if (placeOfCorrectAnswer == 1) {
            buttonSpeakerTwo.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            buttonSpeakerTwo.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four_correct));
        } else if (placeOfCorrectAnswer == 2) {
            buttonSpeakerThree.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            buttonSpeakerThree.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four_correct));
        } else if (placeOfCorrectAnswer == 3) {
            buttonSpeakerFour.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            buttonSpeakerFour.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four_correct));
        }
        textViewVoiceCorrectAnswers.setTextColor(ContextCompat.getColor(getContext(), R.color.green800));
        textViewVoiceCorrectAnswers.setVisibility(View.VISIBLE);//pokazujemy napis z poprawnym tlumaczeniem
    }

    /**
     * Method sets red background and font color, when user marked wrong answer and checked.
     *
     * @param placeOfCorrectAnswer position of correct answer.
     * @param markedImageButton    position of marked answer.
     */
    private void setRedBackground(int placeOfCorrectAnswer, int markedImageButton) {
        if (placeOfCorrectAnswer == markedImageButton) { //jesli nasza odpowiedz jest prawidlowa
            TeachingActivity.changedProgress = false;
            return;
            //nizej jesli odpowiedz jest bledna
        } else if (markedImageButton == 0) //czerwone tlo dla blednej odpowiedzi
            buttonSpeakerOne.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four_wrong));
        else if (markedImageButton == 1)
            buttonSpeakerTwo.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four_wrong));
        else if (markedImageButton == 2)
            buttonSpeakerThree.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four_wrong));
        else if (markedImageButton == 3)
            buttonSpeakerFour.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four_wrong));
        textViewVoiceCorrectAnswers.setTextColor(ContextCompat.getColor(getContext(), R.color.redWrongAnswer));
        textViewVoiceCorrectAnswers.setVisibility(View.VISIBLE);
        ((TeachingActivity) getActivity()).decreaseListOfWordsToStudy(correctAnswer.getId());
    }

    /**
     * Method in Android lifecycle.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
