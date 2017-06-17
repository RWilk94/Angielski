package com.rwilk.angielski.views.teaching.repeat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rwilk.angielski.R;
import com.rwilk.angielski.database.Word;
import com.rwilk.angielski.views.RepetitionActivity;

import java.util.ArrayList;
import java.util.Random;

import static com.rwilk.angielski.R.layout.fragment_one_of_four_answers;

/**
 * Jedna z czterech odpowiedzi - metoda nauki.
 * Created by wilkr on 30.03.2017.
 */
@SuppressWarnings("deprecation")
public class ROneOfFourAnswers extends Fragment {

    private Button buttonOneOfFourFirst;
    private Button buttonOneOfFourSecond;
    private Button buttonOneOfFourThird;
    private Button buttonOneOfFourFourth;
    private Button buttonNextPage;
    private TextView textViewOneOfFourWord;

    private static ArrayList<Word> listOfWordsToStudy;
    private static ArrayList<Word> listOfAllWordFromLevel;

    public ArrayList<Word> wrongAnswers;
    public static Word correctAnswer;
    public int positionToCorrectAnswer;
    //public DBHelper db;
    //public static int indexOfWordToView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(fragment_one_of_four_answers, container, false);
        buttonOneOfFourFirst = (Button) view.findViewById(R.id.buttonOneOfFourFirst);
        buttonOneOfFourSecond = (Button) view.findViewById(R.id.buttonOneOfFourSecond);
        buttonOneOfFourThird = (Button) view.findViewById(R.id.buttonOneOfFourThird);
        buttonOneOfFourFourth = (Button) view.findViewById(R.id.buttonOneOfFourFourth);
        buttonNextPage = (Button) view.findViewById(R.id.buttonNextPageRepeat);
        textViewOneOfFourWord = (TextView) view.findViewById(R.id.textViewOneOfFourWord);

        //db = new DBHelper(getContext()); //dostep do bazy danych
        buildTeachingView();
        return view;
    }

    public static ROneOfFourAnswers newInstance(ArrayList<Word> wordsToStudy, ArrayList<Word> allWordFromLevel) {
        Bundle args = new Bundle();
        ROneOfFourAnswers fragment = new ROneOfFourAnswers();
        fragment.setArguments(args);
        listOfWordsToStudy = wordsToStudy;
        listOfAllWordFromLevel = allWordFromLevel;
        return fragment;

    }

    public void buildTeachingView() {
        /*Collections.sort(listOfWordsToStudy, new Comparator<Word>() {  //sortujemy liste ze slowkami do nauki
            @Override
            public int compare(Word o1, Word o2) {
                return o1.getProgress() - o2.getProgress(); //jako kryterium jest progress -> od najmniejszego
            }
        });
        correctAnswer = listOfWordsToStudy.get(TeachingActivity.indexOfWordToStudy);//bierzemy slowko z najmniejszym progressem*/
        //jednak jesli odpowiedzielismy zle to bierzemy inne slowko //resetuje sie po poprawnej odpowiedzi albo jak zrobi kolo

        if (RepetitionActivity.sizeOfWordsToRepeat < listOfWordsToStudy.size())
            correctAnswer = listOfWordsToStudy.get(randomCorrectAnswer(listOfWordsToStudy.size() - RepetitionActivity.sizeOfWordsToRepeat));
        else if (!listOfWordsToStudy.isEmpty()) correctAnswer = listOfWordsToStudy.get(0);
        else correctAnswer = RepetitionActivity.lastWord;

        ((RepetitionActivity) getActivity()).updateListOfWordsToStudy(correctAnswer);

        positionToCorrectAnswer = randomPositionToCorrectAnswer(); //wylosujemy miejsce gdzie wrzucimy poprawna odpowiedz
        wrongAnswers = randomWordToWrongAnswers(correctAnswer); //losujemy 4 slowka na bledne odpowiedzi //4 --> zeby nie bawic sie pozniej ze sprawdzaniem
        textViewOneOfFourWord.setText(correctAnswer.getPolishWord());
        setCorrectAnswer(positionToCorrectAnswer, correctAnswer.getEnglishWord());
        setWrongAnswers(positionToCorrectAnswer, wrongAnswers);
    }

    public int randomCorrectAnswer(int sizeOfList) {
        return new Random().nextInt(((sizeOfList - 1) + 1)); //powinno losowac od 0 od rozmiaru listy
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
            /*if (!(listOfAllWordFromLevel.get(randomIndex).equals(correctAnswer))
                    && !(listOfWrongAnswers.contains(listOfAllWordFromLevel.get(randomIndex)))) {
                listOfWrongAnswers.add(listOfAllWordFromLevel.get(randomIndex));
            }//jesli wylosowane slowko nie jest poprawna odpowiedzia i nie zostalo juz wylosowane to dodajemy*/
            if ((listOfAllWordFromLevel.get(randomIndex).getId() == correctAnswer.getId())
                    || (listOfWrongAnswers.contains(listOfAllWordFromLevel.get(randomIndex)))) ;
            else listOfWrongAnswers.add(listOfAllWordFromLevel.get(randomIndex));

            //listOfAllWordFromLevel.get(randomIndex).getId()==correctAnswer.getId()
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
    public void setGreenBakcground() {//metoda ustawia zielone tlo w miejscu poprawnej odpowiedzi
        if (positionToCorrectAnswer == 0) { //ustawia kolor tekstu na bialy
            buttonOneOfFourFirst.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four_correct));
            buttonOneOfFourFirst.setTextColor(getResources().getColor(R.color.white));
        } else if (positionToCorrectAnswer == 1) {
            buttonOneOfFourSecond.setTextColor(getResources().getColor(R.color.white));
            buttonOneOfFourSecond.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four_correct));
        } else if (positionToCorrectAnswer == 2) {
            buttonOneOfFourThird.setTextColor(getResources().getColor(R.color.white));
            buttonOneOfFourThird.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four_correct));
        } else if (positionToCorrectAnswer == 3) {
            buttonOneOfFourFourth.setTextColor(getResources().getColor(R.color.white));
            buttonOneOfFourFourth.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four_correct));
        }
        ((RepetitionActivity) getActivity()).decreaseListOfWordsToStudy(correctAnswer);
        //to sie wykona w przypadku blednej odpowiedzi
        //TeachingActivity.setIndexOfWordToStudy(listOfWordsToStudy.size());
    }

    /*public void setPropertiesAfterCorrectAnswer(){
        db.setProgress(correctAnswer.getId()); //zmieniamy progress w bazie danych
        Level.changeProgressWord(correctAnswer.getId()); //zmieniamy progress na liscie w levelu
        //((Level)getActivity()).changeProgressWord(correctAnswer.getId());
        ((TeachingActivity) getActivity()).updateListOfWordsToStudy(correctAnswer.getId()); //zmieniamy progress w liscie przekazywanych slowek do nauki
        TeachingActivity.indexOfWordToStudy = 0; //resetujemy index slowa do nauki
    }*/

    @Override
    public void onStart() {
        super.onStart();
        buttonOneOfFourFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionToCorrectAnswer == 0) {
                    buttonOneOfFourFirst.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four_correct));
                    ((RepetitionActivity) getActivity()).ttsSpeech(correctAnswer.getEnglishWord());
                    //setPropertiesAfterCorrectAnswer();
                } else {
                    buttonOneOfFourFirst.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four_wrong));
                    setGreenBakcground();
                }
                buttonOneOfFourFirst.setTextColor(getResources().getColor(R.color.white));
                buttonOneOfFourFirst.setClickable(false);
                buttonNextPage.callOnClick();
            }
        });
        buttonOneOfFourSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionToCorrectAnswer == 1) {
                    buttonOneOfFourSecond.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four_correct));
                    ((RepetitionActivity) getActivity()).ttsSpeech(correctAnswer.getEnglishWord());
                    //setPropertiesAfterCorrectAnswer();
                } else {
                    buttonOneOfFourSecond.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four_wrong));
                    setGreenBakcground();
                }
                buttonOneOfFourSecond.setTextColor(getResources().getColor(R.color.white));
                buttonOneOfFourSecond.setClickable(false);
                buttonNextPage.callOnClick();
            }
        });
        buttonOneOfFourThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionToCorrectAnswer == 2) {
                    buttonOneOfFourThird.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four_correct));
                    ((RepetitionActivity) getActivity()).ttsSpeech(correctAnswer.getEnglishWord());
                    //setPropertiesAfterCorrectAnswer();
                } else {
                    buttonOneOfFourThird.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four_wrong));
                    setGreenBakcground();
                }
                buttonOneOfFourThird.setTextColor(getResources().getColor(R.color.white));
                buttonOneOfFourThird.setClickable(false);
                buttonNextPage.callOnClick();
            }
        });
        buttonOneOfFourFourth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionToCorrectAnswer == 3) {
                    buttonOneOfFourFourth.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four_correct));
                    ((RepetitionActivity) getActivity()).ttsSpeech(correctAnswer.getEnglishWord());
                    //setPropertiesAfterCorrectAnswer();
                } else {
                    buttonOneOfFourFourth.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_one_of_four_wrong));
                    setGreenBakcground();
                }
                buttonOneOfFourFourth.setTextColor(getResources().getColor(R.color.white));
                buttonNextPage.callOnClick();
                buttonOneOfFourFourth.setClickable(false);
            }
        });
    }

    @Override
    public void onDestroy() {
        // db.close();
        super.onDestroy();
    }
}