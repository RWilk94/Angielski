package com.rwilk.angielski.database;

import java.io.Serializable;

/**
 * Created by wilkr on 22.05.2017.
 */

public class WordSQL implements Serializable {

    private String sql;
    private String section;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }
}
