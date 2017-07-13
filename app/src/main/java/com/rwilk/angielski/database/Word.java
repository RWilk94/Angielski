package com.rwilk.angielski.database;

import java.io.Serializable;


public class Word implements Serializable {

    private int id;
    private String polishWord;
    private String englishWord;
    private int idSection;
    private int progress;
    private int difficult;
    private int repeat;
    private String partOfSpeech;
    private long timeToRepeat;
    private int countingRepeats;

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

    public int getIdSection() {
        return idSection;
    }

    public void setIdSection(int idSection) {
        this.idSection = idSection;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getDifficult() {
        return difficult;
    }

    public void setDifficult(int difficult) {
        this.difficult = difficult;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public long getTimeToRepeat() {
        return timeToRepeat;
    }

    public void setTimeToRepeat(long timeToRepeat) {
        this.timeToRepeat = timeToRepeat;
    }

    public int getCountingRepeats() {
        return countingRepeats;
    }

    public void setCountingRepeats(int countingRepeats) {
        this.countingRepeats = countingRepeats;
    }
}