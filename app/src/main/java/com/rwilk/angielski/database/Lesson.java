package com.rwilk.angielski.database;

import java.io.Serializable;

/**
 * Created by wilkr on 17.06.2017.
 */

public class Lesson implements Serializable {

    String textViewTop;
    String textViewBottom;
    int progress;

    public String getTextViewTop() {
        return textViewTop;
    }

    public void setTextViewTop(String textViewTop) {
        this.textViewTop = textViewTop;
    }

    public String getTextViewBottom() {
        return textViewBottom;
    }

    public void setTextViewBottom(String textViewBottom) {
        this.textViewBottom = textViewBottom;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
