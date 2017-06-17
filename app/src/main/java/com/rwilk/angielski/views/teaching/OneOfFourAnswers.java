package com.rwilk.angielski.views.teaching;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rwilk.angielski.R;
import com.rwilk.angielski.database.Word;
import com.rwilk.angielski.views.TeachingActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import static com.rwilk.angielski.R.layout.fragment_one_of_four_answers;

/**
 * Jedna z czterech odpowiedzi - metoda nauki.
 * Created by wilkr on 30.03.2017.
 */

public class OneOfFourAnswers extends Fragment {

    private Button buttonOneOfFourFirst;
    private Button buttonOneOfFourSecond;
    private Button buttonOneOfFourThird;
    private Button buttonOneOfFourFourth;
    private Button buttonNextPage;
    private TextView textViewOneOfFourWord;

    private static ArrayList<Word> listOfWordsToStudy;
    private static ArrayList<Word> listOfAllWordFromLevel;

    public ArrayList<Word> wrongAnswers;
    public Word correctAnswer;
    public int positionToCorrectAnswer;

    /**
     * Method create new instance of OneOfFourAnswers fragment.
     * @param wordsToStudy list of words which user will learn
     * @param allWordFromLevel list of all words from level
     * @return fragment activity
     */
    public static OneOfFourAnswers newInstance(ArrayList<Word> wordsToStudy, ArrayList<Word> allWordFromLevel) {
        OneOfFourAnswers fragment = new OneOfFourAnswers();
        listOfWordsToStudy = wordsToStudy;
        listOfAllWordFromLevel = allWordFromLevel;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(fragment_one_of_four_answers, container, false);
        buttonOneOfFourFirst = (Button) view.findViewById(R.id.buttonOneOfFourFirst);
        buttonOneOfFourSecond = (Button) view.findViewById(R.id.buttonOneOfFourSecond);
        buttonOneOfFourThird = (Button) view.findViewById(R.id.buttonOneOfFourThird);
        buttonOneOfFourFourth = (Button) view.findViewById(R.id.buttonOneOfFourFourth);
        buttonNextPage = (Button) view.findViewById(R.id.buttonNextPage);
        textViewOneOfFourWord = (TextView) view.findViewById(R.id.textViewOneOfFourWord);

        buildTeachingView();
        return view;
    }

    /**
     * Komentarz do metody...
     */
    private void buildTeachingView() {
        Collections.sort(listOfWordsToStudy, new Comparator<Word>() {  //sortujemy liste ze slowkami do nauki
            @Override
            public int compare(Word o1, Word o2) {
                return o1.getProgress() - o2.getProgress(); //jako kryterium jest progress -> od najmniejszego
            }
        });

        correctAnswer = listOfWordsToStudy.get(0);
        if (listOfWordsToStudy.size() >= 2 && TeachingActivity.lastWord.equals(correctAnswer.getEnglishWord()))
            correctAnswer = listOfWordsToStudy.get(1);

            TeachingActivity.lastWord = correctAnswer.getEnglishWord();

            //update na zapas
            ((TeachingActivity) getActivity()).updateListOfWordsToStudy(correctAnswer.getId());

            positionToCorrectAnswer = randomPositionToCorrectAnswer(); //wylosujemy miejsce gdzie wrzucimy poprawna odpowiedz
            wrongAnswers = randomWordToWrongAnswers(correctAnswer); //losujemy 4 slowka na bledne odpowiedzi //4 --> zeby nie bawic sie pozniej ze sprawdzaniem
            textViewOneOfFourWord.setText(correctAnswer.getPolishWord());
            setCorrectAnswer(positionToCorrectAnswer, correctAnswer.getEnglishWord());
            setWrongAnswers(positionToCorrectAnswer, wrongAnswers);
        }

    public int randomPositionToCorrectAnswer() {
        return new Random().nextInt(((3) + 1));
    }   //powinno losowac 0-3

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

    public void setCorrectAnswer(int positionOfCorrectAnswer, String englishWord) { //ustawiamy text w buttonie jako poprawna odpowiedz
        if (positionOfCorrectAnswer == 0) buttonOneOfFourFirst.setText(englishWord);
        else if (positionOfCorrectAnswer == 1) buttonOneOfFourSecond.setText(englishWord);
        else if (positionOfCorrectAnswer == 2) buttonOneOfFourThird.setText(englishWord);
        else if (positionOfCorrectAnswer == 3) buttonOneOfFourFourth.setText(englishWord);
    }

    public void setWrongAnswers(int positionOfCorrectAnswer, ArrayList<Word> listOfThreeWrongAnswers) { //ustawiamy pozostale buttony jako bledne odpowiedzi
        if (positionOfCorrectAnswer != 0) //mozna sie bawic z indexem, ale nie trzeba
            buttonOneOfFourFirst.setText(listOfThreeWrongAnswers.get(0).getEnglishWord());
        if (positionOfCorrectAnswer != 1)
            buttonOneOfFourSecond.setText(listOfThreeWrongAnswers.get(1).getEnglishWord());
        if (positionOfCorrectAnswer != 2)
            buttonOneOfFourThird.setText(listOfThreeWrongAnswers.get(2).getEnglishWord());
        if (positionOfCorrectAnswer != 3)
            buttonOneOfFourFourth.setText(listOfThreeWrongAnswers.get(3).getEnglishWord());
    }

    //w sumie to ta metoda moglaby byc bez parametru
    public void setGreenBakcground(int correctAnswerPos) {//metoda ustawia zielone tlo w miejscu poprawnej odpowiedzi
        if (correctAnswerPos == 0) { //ustawia kolor tekstu na bialy
            buttonOneOfFourFirst.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four_correct));
            buttonOneOfFourFirst.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else if (correctAnswerPos == 1) {
            buttonOneOfFourSecond.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four_correct));
            buttonOneOfFourSecond.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else if (correctAnswerPos == 2) {
            buttonOneOfFourThird.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four_correct));
            buttonOneOfFourThird.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else if (correctAnswerPos == 3) {
            buttonOneOfFourFourth.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four_correct));
            buttonOneOfFourFourth.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }
        //to sie wykona w przypadku blednej odpowiedzi
        ((TeachingActivity) getActivity()).decreaseListOfWordsToStudy(correctAnswer.getId());
    }


    @Override
    public void onStart() {
        super.onStart();
        buttonOneOfFourFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionToCorrectAnswer == 0) {
                    buttonOneOfFourFirst.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four_correct));
                    ((TeachingActivity) getActivity()).ttsSpeech(correctAnswer.getEnglishWord());
                    TeachingActivity.changedProgress = false;
                } else {
                    buttonOneOfFourFirst.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four_wrong));
                    setGreenBakcground(positionToCorrectAnswer);
                }
                buttonOneOfFourFirst.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                buttonOneOfFourFirst.setClickable(false);
                buttonNextPage.callOnClick();
            }
        });
        buttonOneOfFourSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionToCorrectAnswer == 1) {
                    buttonOneOfFourSecond.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four_correct));
                    ((TeachingActivity) getActivity()).ttsSpeech(correctAnswer.getEnglishWord());
                    TeachingActivity.changedProgress = false;
                } else {
                    buttonOneOfFourSecond.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four_wrong));
                    setGreenBakcground(positionToCorrectAnswer);
                }
                buttonOneOfFourSecond.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                buttonOneOfFourSecond.setClickable(false);
                buttonNextPage.callOnClick();
            }
        });
        buttonOneOfFourThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionToCorrectAnswer == 2) {
                    buttonOneOfFourThird.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four_correct));
                    ((TeachingActivity) getActivity()).ttsSpeech(correctAnswer.getEnglishWord());
                    TeachingActivity.changedProgress = false;
                } else {
                    buttonOneOfFourThird.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four_wrong));
                    setGreenBakcground(positionToCorrectAnswer);
                }
                buttonOneOfFourThird.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                buttonOneOfFourThird.setClickable(false);
                buttonNextPage.callOnClick();
            }
        });
        buttonOneOfFourFourth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionToCorrectAnswer == 3) {
                    buttonOneOfFourFourth.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four_correct));
                    ((TeachingActivity) getActivity()).ttsSpeech(correctAnswer.getEnglishWord());
                    TeachingActivity.changedProgress = false;
                } else {
                    buttonOneOfFourFourth.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_one_of_four_wrong));
                    setGreenBakcground(positionToCorrectAnswer);
                }
                buttonOneOfFourFourth.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                buttonNextPage.callOnClick();
                buttonOneOfFourFourth.setClickable(false);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}