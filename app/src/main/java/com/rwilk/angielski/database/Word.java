package com.rwilk.angielski.database;

import java.io.Serializable;

/**
 * Created by RWilk on 22.09.2016.
 */
public class Word implements Serializable {

    private int id;
    private String polishWord;
    private String englishWord;
    private int progress;
    private int difficultWord;
    private int repeat;
    private int idSection;
    private int countingRepeats;
    private String partOfSpeech;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPolishWord() {
        return polishWord;
    }

    public void setPolishWord(String polishWord) {
        this.polishWord = polishWord;
    }

    public String getEnglishWord() {
        return englishWord;
    }

    public void setEnglishWord(String englishWord) {
        this.englishWord = englishWord;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getDifficultWord() {
        return difficultWord;
    }

    public void setDifficultWord(int difficultWord) {
        this.difficultWord = difficultWord;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public int getIdSection() {
        return idSection;
    }

    public void setIdSection(int idSection) {
        this.idSection = idSection;
    }

    public int getCountingRepeats() {
        return countingRepeats;
    }

    public void setCountingRepeats(int countingRepeats) {
        this.countingRepeats = countingRepeats;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }
}