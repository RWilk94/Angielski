package com.rwilk.angielski;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rwilk.angielski.database.Lesson;
import com.rwilk.angielski.database.Points;
import com.rwilk.angielski.database.Word;
import com.rwilk.angielski.views.NewMainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;


/**
 * Created by RWilk on 23.02.2017.
 * Baza danych.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static boolean databaseExists = false;

    // Database Name
    private static final String DATABASE = "my_db"; //nazwa bazy danych

    // Table Names
    private static final String TABLE_WORDS = "words";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_POINTS = "points";
    private static final String TABLE_FRIENDS = "friends";
    private static final String TABLE_SECTIONS = "sections";

    // Table Words column names
    private static final String ID_WORD = "idWord";
    private static final String POLISH_WORD = "polishWord";
    private static final String ENGLISH_WORD = "englishWord";
    private static final String PROGRESS = "progress";
    private static final String DIFFICULT = "difficult";
    private static final String REPEAT = "repeat";
    private static final String ID_SECTION = "idSection";
    private static final String COUNTING_REPEATS = "countingRepeats";
    private static final String PART_OF_SPEECH = "partOfSpeech";

    // Table Users column names
    private static final String ID_USER = "idUser";
    private static final String EMAIL = "email";
    private static final String LAST_LOGIN = "lastLogin";
    private static final String BACKUP = "backup";

    // Table Points column names
    private static final String ID_POINTS = "idPoints";
    private static final String WEEKLY = "weekly";
    private static final String MONTHLY = "monthly";
    private static final String LAST_UPDATE = "lastUpdate"; //data ostatniego update punktów
    private static final String All_TIME = "allTime";

    // Table Friends column names
    private static final String ID_FRIENDS = "idFriends";
    private static final String NAME = "name";

    // Table Sections column names
    //id + name
    //private static final String ID_SECTION = "idSections";
    private static final String COMPLETED = "completed";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "(" +
            ID_USER + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            EMAIL + " TEXT NOT NULL UNIQUE, " +
            LAST_LOGIN + " INTEGER, " +
            BACKUP + " INTEGER" + ");";

    private static final String CREATE_TABLE_POINTS = "CREATE TABLE IF NOT EXISTS " + TABLE_POINTS + "(" +
            ID_POINTS + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            WEEKLY + " INTEGER DEFAULT 0, " +
            MONTHLY + " INTEGER DEFAULT 0, " +
            LAST_UPDATE + " INTEGER DEFAULT 0, " +
            All_TIME + " INTEGER DEFAULT 0" + ");";

    private static final String CREATE_TABLE_FRIENDS = "CREATE TABLE IF NOT EXISTS " + TABLE_FRIENDS + "(" +
            ID_FRIENDS + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME + " TEXT NOT NULL, " +
            WEEKLY + " INTEGER DEFAULT 0, " +
            MONTHLY + " INTEGER DEFAULT 0, " +
            All_TIME + " INTEGER DEFAULT 0" + ");";

    private static final String CREATE_TABLE_WORDS = "CREATE TABLE IF NOT EXISTS " + TABLE_WORDS + "(" +
            ID_WORD + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            POLISH_WORD + " TEXT NOT NULL, " +
            ENGLISH_WORD + " TEXT NOT NULL, " +
            ID_SECTION + " INTEGER NOT NULL, " +
            PROGRESS + " INTEGER DEFAULT 0, " +
            DIFFICULT + " INTEGER DEFAULT 0, " +
            REPEAT + " INTEGER DEFAULT 0, " +
            PART_OF_SPEECH + " TEXT NOT NULL, " +
            COUNTING_REPEATS + " INTEGER DEFAULT 0 " + ");";

    private static final String CREATE_TABLE_SECTIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_SECTIONS + "(" +
            ID_SECTION + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME + " TEXT NOT NULL, " +
            COMPLETED + " INTEGER DEFAULT 0" + ")";


    public DBHelper(Context context, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE, factory, version);
    }

    /**
     * Konstruktor dla CustomAdaptera. Zmienna istnieje omija dodawanie rekordow w metodzie onCreate;
     *
     * @param context context
     */
    public DBHelper(Context context, int version) {
        super(context, DATABASE, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_WORDS);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_POINTS);
        db.execSQL(CREATE_TABLE_FRIENDS);
        db.execSQL(CREATE_TABLE_SECTIONS);

        if (NewMainActivity.listOfWordsToDatabase == null)
            return;

        ArrayList<String> word = new ArrayList<>();
        for (int i = 0; i < NewMainActivity.listOfWordsToDatabase.size(); i++) {
            if (!word.contains(NewMainActivity.listOfWordsToDatabase.get(i).getSection())) {
                db.execSQL("INSERT INTO " + TABLE_SECTIONS + " (" + NAME + ") VALUES (" +
                        NewMainActivity.listOfWordsToDatabase.get(i).getSection() + " );"
                );
                word.add(NewMainActivity.listOfWordsToDatabase.get(i).getSection());
            }
        }
        for (int i = 0; i < NewMainActivity.listOfWordsToDatabase.size(); i++) {
            db.execSQL("INSERT INTO " + TABLE_WORDS + " (" +
                    POLISH_WORD + ", " + ENGLISH_WORD + ", " + PART_OF_SPEECH + ", " + ID_SECTION + ")" +
                    " values (" + NewMainActivity.listOfWordsToDatabase.get(i).getSql() + ", "
                    + "( SELECT s." + ID_SECTION + " FROM " + TABLE_SECTIONS + " as s WHERE s." + NAME
                    + " = " + NewMainActivity.listOfWordsToDatabase.get(i).getSection()
                    + ")" + ");"
            );
        }
        db.execSQL("INSERT INTO " + TABLE_POINTS + " (" +
                WEEKLY + " )" +
                " values ( 0 );"
        );

        databaseExists = true;
        //db.close();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + TABLE_WORDS + " ;");
        db.execSQL("drop table if exists " + TABLE_SECTIONS + " ;");
        db.execSQL("drop table if exists " + TABLE_POINTS + " ;");
        if (NewMainActivity.listOfWordsToDatabase != null)
            onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_WORDS + " ;");
        db.execSQL("drop table if exists " + TABLE_SECTIONS + " ;");
        db.execSQL("drop table if exists " + TABLE_POINTS + " ;");
        if (NewMainActivity.listOfWordsToDatabase != null)
            onCreate(db);
    }


    /**
     * Funkcja sluzy do pobierania slowek z wybranego poziomu, gdzie poziom podajemy jako argument.
     *
     * @param xLevel nazwa poziomu, z ktorego pobieramy slowka
     * @return listOfWordsToDatabase slowek z wybranego poziomu
     */
    public ArrayList<Word> getAllWordsFromLevelX(String xLevel) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Word> words = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("select * from " + TABLE_WORDS + " as w, " + TABLE_SECTIONS + " as s" +
                    " WHERE s." + ID_SECTION + " = w." + ID_SECTION + " and s." + NAME + " ='" + xLevel + "'", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Word word = setCursor(cursor);
                words.add(word);
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return words;
        } catch (Exception e) {
            Log.e("DatabaseError", "DatabaseError: getAllWordsFromLevelX " + e);
            return null;
        }
    }

    /**
     * Metoda aktualizuje progress w słówkach z danego poziomu, które są podawane jako parametr.
     *
     * @param updatedWords lista słówek, których progress jest aktualizowany
     * @return true jeśli operacja się powiedzie, false jeśli błąd
     */
    public boolean updateListOfWordAfterLearning(ArrayList<Word> updatedWords) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < updatedWords.size(); i++) {
            try {
                //Cursor cursor = db.rawQuery("select * from " + TABLE_WORDS + " where " + ID_WORD + "=" + updatedWords.get(i).getId() + ";", null);
                //cursor.moveToFirst();

                ContentValues cv = new ContentValues();
                if (updatedWords.get(i).getProgress() > 50)
                    updatedWords.get(i).setProgress(50); //zabezpieczenie przed maxem
                cv.put(PROGRESS, updatedWords.get(i).getProgress());

                db.update(TABLE_WORDS, cv, ID_WORD + " = ?", new String[]{Integer.toString(updatedWords.get(i).getId())});
                //cursor.close();
            } catch (Exception e) {
                Log.e("Database Error", "Database Error: updateListOfWordAfterLearning " + e);
                return false;
            }
        }
        db.close();
        return true;
    }

    /**
     * Metoda ustawia parametr difficult słówka podanego jako parametr. Jeśli difficult = 0 to ustawiamy na 1.
     * Jeśli difficult = 1 to ustawiamy 0.
     *
     * @param id id słówka, któremu chcemy zmienić parametr difficult
     * @return true jeśli zmiana się udała, false jeśli błąd
     */
    public boolean setDifficult(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            Cursor cursor = db.rawQuery("select * from " + TABLE_WORDS + " where " + ID_WORD + " =" + id + ";", null);
            cursor.moveToFirst();
            int difficult = cursor.getInt(cursor.getColumnIndex(DIFFICULT));
            if (difficult == 0) difficult = 1;
            else difficult = 0;

            ContentValues cv = new ContentValues();
            cv.put(DIFFICULT, difficult);

            db.update(TABLE_WORDS, cv, ID_WORD + " = ?", new String[]{Integer.toString(id)});
            cursor.close();
            db.close();
            return true;
        } catch (Exception e) {
            Log.e("Database Error", "Database Error: setDifficult " + e);
            return false;
        }
    }

    /**
     * Metoda zwraca wartość parametru difficult słowa, którego ID podajemy jako parametr.
     *
     * @param id id słówka, którego parametr difficult chcemy pobrać
     * @return parametr difficult (0 lub 1) danego słowa, lub -1 jeśli błąd.
     */
    public int getDifficult(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + TABLE_WORDS + " where " + ID_WORD + "=" + id + ";", null);
            cursor.moveToFirst();
            Word word = setCursor(cursor);
            cursor.close();
            db.close();
            return word.getDifficultWord();
        } catch (Exception e) {
            Log.e("Database Error", "Database Error: getDifficult" + e);
            return -1;
        }
    }

    /**
     * Metoda zwraca wartość parametru repeat słowa, którego ID podajemy jako parametr.
     *
     * @param id id słówka, którego parametr repeat chcemy pobrać
     * @return parametr repeat (0 lub 1) danego słowa, lub -1 jeśli błąd.
     */
    public int getRepeat(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + TABLE_WORDS + " where " + ID_WORD + "=" + id + ";", null);
            cursor.moveToFirst();
            Word word = setCursor(cursor);
            cursor.close();
            db.close();
            return word.getRepeat();
        } catch (Exception e) {
            Log.e("Database Error", "Database Error: getRepeat" + e);
            return -1;
        }
    }

    /**
     * Metoda ustawia parametr repeat słówka podanego jako parametr. Jeśli repeat = 0 to ustawiamy na 1.
     * Jeśli repeat = 1 to ustawiamy 0.
     *
     * @param id id słówka, któremu chcemy zmienić parametr repeat
     * @return true jeśli zmiana się udała, false jeśli błąd
     */
    public boolean setRepeat(int id, int section) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            Cursor cursor = db.rawQuery("select * from " + TABLE_WORDS + " where " + ID_WORD + "=" + id + ";", null);
            cursor.moveToFirst();
            int repeat = cursor.getInt(cursor.getColumnIndex(REPEAT));
            if (repeat == 0) repeat = 1;
            else repeat = 0;

            ContentValues cv = new ContentValues();
            cv.put(REPEAT, repeat);

            db.update(TABLE_WORDS, cv, ID_WORD + " = ?", new String[]{Integer.toString(id)});
            setCompleted(section);
            cursor.close();
            db.close();
            return true;
        } catch (Exception e) {
            Log.e("Database Error", "Database Error: setRepeat " + e);
            db.close();
            return false;
        }
    }

    /**
     * Metoda aktualizuje ilość zdobytych punktów. Aktualizuje rankingi: tygodniowy, mieszięczny i ogólny.
     *
     * @param points ilość punktów zdobytych w danej sesji nauki
     * @return true jeśli zmiana się udała, false jeśli błąd
     */
    public boolean updatePoints(int points) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            Cursor cursor = db.rawQuery("select * from " + TABLE_POINTS + " where " + ID_POINTS + "= 1 ;", null);
            cursor.moveToFirst();
            int weekly = cursor.getInt(cursor.getColumnIndex(WEEKLY));
            int monthly = cursor.getInt(cursor.getColumnIndex(MONTHLY));
            int allTime = cursor.getInt(cursor.getColumnIndex(All_TIME));
            long lastUpdate = cursor.getLong(cursor.getColumnIndex(LAST_UPDATE));

            Calendar calendarLastUpdate = Calendar.getInstance();
            calendarLastUpdate.setTimeInMillis(lastUpdate);

            // Resetujemy tygodniowy ranking, jeśli czas od ostatniej aktualizacji punktów jest większy niż 7dni
            if (Calendar.getInstance().getTimeInMillis() - lastUpdate >= TimeUnit.DAYS.toMillis(7)) {
                weekly = 0;
            }
            /* Jeśli od ostatniego updatu zmienił się miesiąc lub jeśli minęło więcej niż 31 dni */
            if (Calendar.getInstance().get(Calendar.MONTH) != calendarLastUpdate.get(Calendar.MONTH)
                    || Calendar.getInstance().getTimeInMillis() - calendarLastUpdate.getTimeInMillis() >= TimeUnit.DAYS.toMillis(31)) {
                monthly = 0;
            }
            /* Resetujemy tygodniowy licznik, jeśli poprzednio nie był poniedziałek a teraz jest poniedziałek */
            //Lub jeśli poprzednio był wyższy nr. dnia niż jest teraz, np. był czwartek a teraz jest środa
            //Oznacza to, że nie logowaliśmy się prawie tydzień i minął poniedziałek
            if ((calendarLastUpdate.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
                    && Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                    || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) < calendarLastUpdate.get(Calendar.DAY_OF_WEEK)) {
                weekly = 0;
            }

            weekly += points;
            monthly += points;
            allTime += points;
            ContentValues cv = new ContentValues();
            cv.put(WEEKLY, weekly);
            cv.put(MONTHLY, monthly);
            cv.put(All_TIME, allTime);
            cv.put(LAST_UPDATE, Calendar.getInstance().getTimeInMillis());
            db.update(TABLE_POINTS, cv, ID_POINTS + " = ?", new String[]{Integer.toString(1)});
            cursor.close();
            db.close();
            return true;
        } catch (Exception e) {
            Log.e("Database Error", "Database Error: setPoints " + e);
            db.close();
            return false;
        }
    }

    /**
     * Metoda zwraca liczbę punktów użytkownika. Obiekt Points zawiera rankingi: tygodniowy, miesięczny, ogólny.
     *
     * @return liczba punktów użytkownika.
     */
    public Points getPoints() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + TABLE_POINTS + " where " + ID_POINTS + "= 1;", null);
            cursor.moveToFirst();
            Points points = new Points();
            points.setIdPoints(cursor.getInt(cursor.getColumnIndex(ID_POINTS)));
            points.setWeekly(cursor.getInt(cursor.getColumnIndex(WEEKLY)));
            points.setMonthly(cursor.getInt(cursor.getColumnIndex(MONTHLY)));
            points.setAllTime(cursor.getInt(cursor.getColumnIndex(All_TIME)));
            cursor.close();
            db.close();
            return points;
        } catch (Exception e) {
            Log.e("Database Error", "Database Error: getRepeat" + e);
            db.close();
            return null;
        }
    }

    /**
     * Metoda zwraca progress w jakim jest ukonczony dany rozdział.
     *
     * @param section id sekcji, z której chcemy pobrać punkty
     * @return procent ukończenia danego rozdziału lub -1 w przypadku błędu
     */
    public int getCompleted(int section) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + TABLE_SECTIONS + " where " + ID_SECTION + "= " + section + ";", null);
            cursor.moveToFirst();
            int completed = cursor.getInt(cursor.getColumnIndex(COMPLETED));
            System.out.println("Completed " + completed);
            cursor.close();
            db.close();
            return completed;
        } catch (Exception e) {
            Log.e("Database Error", "Database Error: getCompleted" + e);
            db.close();
            return -1;
        }
    }

    /**
     * /**
     * Aktualizuje progress w danym rozdziale.
     *
     * @param section id rozdziału, który aktualizujemy.
     */
    public void setCompleted(int section) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            //select count(idWord)
            //from words w
            //where w.idSection = 4 && w.progress = 50
            Cursor cursor = db.rawQuery("select count(idWord) from " + TABLE_WORDS + " where " + ID_SECTION + "=" + section + " " +
                    "and (progress = 50 or repeat = 1);", null);
            cursor.moveToFirst();
            int completed = cursor.getInt(0); //zwraca 11, czyli liczbe
            System.out.println(completed);

            cursor = db.rawQuery("select count(idWord) from " + TABLE_WORDS + " where " + ID_SECTION + "=" + section + ";", null);
            cursor.moveToFirst();
            int maxSection = cursor.getInt(0); //zwraca max
            System.out.println("Blablabla " + maxSection);
            double wynik = completed * 100 / maxSection;
            System.out.println("Completed " + completed);
            ContentValues cv = new ContentValues();
            cv.put(COMPLETED, (int) wynik);
            db.update(TABLE_SECTIONS, cv, ID_SECTION + " = ?", new String[]{Integer.toString(section)});
            cursor.close();
            db.close();
            //return section/maxSection;
        } catch (Exception e) {
            Log.e("Database Error", "Database Error: setCompleted " + e);
            db.close();
            //return -1;
        }
    }

    /*public ArrayList<Word> searchSimilarWords(String searching) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Word> words = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("select * from " + TABLE_WORDS + " where " + ENGLISH_WORD + " LIKE '%" + searching
                    + "%';", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Word word = setCursor(cursor);
                words.add(word);
                cursor.moveToNext();
            }
            cursor.close();
            //db.close();
            return words;
        } catch (Exception e) {
            Log.e("DatabaseError", "DatabaseError: getAllWordsLevelOne " + e);
            return null;
        } finally {
            db.close();
        }
    }*/


    private Word setCursor(Cursor cursor) {
        Word word = new Word();
        word.setId(cursor.getInt(cursor.getColumnIndex(ID_WORD)));
        word.setPolishWord(cursor.getString(cursor.getColumnIndex(POLISH_WORD)));
        word.setEnglishWord(cursor.getString(cursor.getColumnIndex(ENGLISH_WORD)));
        word.setProgress(cursor.getInt(cursor.getColumnIndex(PROGRESS)));
        word.setDifficultWord(cursor.getInt(cursor.getColumnIndex(DIFFICULT)));
        word.setIdSection(cursor.getInt(cursor.getColumnIndex(ID_SECTION)));
        word.setRepeat(cursor.getInt(cursor.getColumnIndex(REPEAT)));
        word.setPartOfSpeech(cursor.getString(cursor.getColumnIndex(PART_OF_SPEECH)));
        word.setCountingRepeats(cursor.getInt(cursor.getColumnIndex(COUNTING_REPEATS)));
        return word;
    }

    public String getSectionName(int section) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + TABLE_SECTIONS + " where " + ID_SECTION + "= " + section + ";", null);
            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            cursor.close();
            db.close();
            return name;
        } catch (Exception e) {
            Log.e("Database Error", "Database Error: getSectionName" + e);
            db.close();
            return null;
        }
    }

    public ArrayList<Lesson> getAllSections() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Lesson> listOfSections = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("select * from " + TABLE_SECTIONS + ";", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Lesson lesson = new Lesson();
                String section = cursor.getString(cursor.getColumnIndex(NAME));
                int progress = cursor.getInt(cursor.getColumnIndex(COMPLETED));
                int id = cursor.getInt(cursor.getColumnIndex(ID_SECTION));
                lesson.setTextViewBottom(section);
                String textTop = "Lekcja " + String.valueOf(id);
                lesson.setTextViewTop(textTop);
                lesson.setProgress(progress);
                listOfSections.add(lesson);
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return listOfSections;
        } catch (Exception e) {
            Log.e("Database Error", "Database Error: getAllSections" + e);
            db.close();
            return null;
        }
    }

    public ArrayList<Word> getAllWordsFromSectionX(int xSection) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Word> words = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("select * from " + TABLE_WORDS +
                    " WHERE " + ID_SECTION + " =" + xSection + ";", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Word word = setCursor(cursor);
                words.add(word);
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
            return words;
        } catch (Exception e) {
            Log.e("DatabaseError", "DatabaseError: getAllWordsFromLevelX " + e);
            return null;
        }
    }

}
