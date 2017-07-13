package com.rwilk.angielski.database;

import java.io.Serializable;
import java.util.ArrayList;


public class Section implements Serializable {

    private int idSection;
    private String name;
    private int completed;
    private ArrayList<Word> wordList;

    public int getIdSection() {
        return idSection;
    }

    public void setIdSection(int idSection) {
        this.idSection = idSection;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public ArrayList<Word> getWordList() {
        return wordList;
    }

    public void setWordList(ArrayList<Word> wordList) {
        this.wordList = wordList;
    }
}
