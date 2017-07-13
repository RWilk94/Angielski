package com.rwilk.angielski.database.firebase;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rwilk.angielski.application.Angielski;
import com.rwilk.angielski.database.DBHelper;
import com.rwilk.angielski.database.User;
import com.rwilk.angielski.views.LoginActivity;
import com.rwilk.angielski.views.NewMainActivity;

public class FirebaseServer{

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private User userInFirebase;

    //public FirebaseServer(){}

    public FirebaseServer(Context context) {
        FirebaseApp.initializeApp(context);
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance().getReference("Users/");
    }

    /**
     * Metoda łączy się z bazą danych Firebase i pobiera usera wg. adresu email pobranego z jego konta.
     * Jeśli znajdzie to wywołuje metodę onSuccess.
     * @param listener słuchacz zdarzeń, który reaguje w zależności od
     */
    private void getUserFromFirebase(final OnGetDataListener listener) {
        userInFirebase = null;
        listener.onStart();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mAuth != null && mAuth.getCurrentUser() != null) {
                    userInFirebase = dataSnapshot.child(mAuth.getCurrentUser().getUid()).getValue(User.class);
                }
                if (userInFirebase != null){
                    //jeśli użytkownik jest już w bazie danych
                    listener.onSuccess(userInFirebase);
                }else {
                    //jeśli użytkownika nie ma w bazie danych Firebase
                    listener.userDoesNotExist();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseServer", "getUserFromFirebase + onCancelled + getUserFromFirebase");
                listener.onFailed();
            }
        });
    }


    public void firstLoginToFirebase(){
        getUserFromFirebase(new OnGetDataListener() {
            @Override
            public void onStart() {
                //Toast.makeText(Angielski.getAppContext(), "Start", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(User user) {
                //jeśli użytkownik jest już w bazie danych Firebase to aktualizujemy tylko lastLogin.
                mDatabase.child(mAuth.getCurrentUser().getUid()).child("lastLogin").setValue(System.currentTimeMillis());
                //sprawdzamy czy istnieje lokalny użytkownik
                DBHelper db = new DBHelper(Angielski.getAppContext(), NewMainActivity.databaseVersion);
                User localUser = db.getUser(mAuth.getCurrentUser().getUid());
                //jeśli nie istnieje lokalny to dodajemy go do bazy danych z danymi z firebase.
                if (localUser == null){
                    user.setLastLogin(System.currentTimeMillis());
                    db.insertOrUpdateUser(user);
                //jeśli istnieje lokalny to:
                } else {
                    //1. Na serwerze jest nowsza wersja bazy danych
                    if (user.getAllTime() > localUser.getAllTime()){
                        /**  Pytanie o baze danych  i wtedy update punktow albo nie  **/
                        db.insertOrUpdateUser(user);
                    //2. Na serwerze jest taka sama wersja
                    } else if (user.getAllTime() == localUser.getAllTime()){
                        db.insertOrUpdateUser(user);
                        //nie robimy nic
                    //3. Aktualizujemy punkty na serwerze.
                    } else {
                        mDatabase.child(mAuth.getCurrentUser().getUid()).child("allTime").setValue(localUser.getAllTime());
                        mDatabase.child(mAuth.getCurrentUser().getUid()).child("weekly").setValue(localUser.getWeekly());
                        mDatabase.child(mAuth.getCurrentUser().getUid()).child("monthly").setValue(localUser.getMonthly());
                    }
                }
                db.close();
            }

            @Override
            public void userDoesNotExist() {
                Toast.makeText(Angielski.getAppContext(), "userDoesNotExist", Toast.LENGTH_SHORT).show();
                DBHelper db = new DBHelper(Angielski.getAppContext(), NewMainActivity.databaseVersion);
                //jeśli użytkownika nie ma w bazie danych Firebase to dodajemy go.
                if (LoginActivity.mAuth.getCurrentUser() != null) {
                    User user = new User(LoginActivity.mAuth.getCurrentUser().getUid(), LoginActivity.mAuth.getCurrentUser().getEmail(), System.currentTimeMillis(), 0, 0, 0, 0);
                    mDatabase.child(mAuth.getCurrentUser().getUid()).setValue(user);
                    //Sprawdzamy czy jest już lokalny użytkownik. Jeśli jest to aktualizujemy czas i robimy update. Jeśli nie ma to dodajemy nowego.
                    User localUser = db.getUser(mAuth.getCurrentUser().getUid());
                    if (localUser == null){
                        localUser = user; //new User(LoginActivity.mAuth.getCurrentUser().getUid(), LoginActivity.mAuth.getCurrentUser().getEmail(), System.currentTimeMillis(), 0, 0, 0, 0);
                    }
                    else {
                        localUser.setLastLogin(System.currentTimeMillis());
                    }
                    db.insertOrUpdateUser(localUser);
                }
                db.close();
            }

            @Override
            public void onFailed() {
                Toast.makeText(Angielski.getAppContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void skipLoginToFirebase(){
        getUserFromFirebaseWhenSkipLogin(new OnGetDataListener() {
            @Override
            public void onStart() {
                //kiedy dzialamy na lokalnym użytkowniku i nie ma obiektu użytkownika z Firebase
                DBHelper db = new DBHelper(Angielski.getAppContext(), NewMainActivity.databaseVersion);
                User localUser = db.getUser("0000");
                //pobieramy lokalnego usera z bazy danych. Jeśli go nie ma to tworzymy nowego.
                if (localUser == null){
                    db.insertOrUpdateUser(new User("0000", null, System.currentTimeMillis(), 0, 0,0,0));
                } else {
                    //jeśli jest to aktualizujemy czas i zapisujemy go.
                    localUser.setLastLogin(System.currentTimeMillis());
                    db.insertOrUpdateUser(localUser);
                }
                db.close();
            }

            @Override
            public void onSuccess(User user) {
                //kiedy jesteśmy zalogowani i działamy na użytkowniku firebase

                //jeśli użytkownik jest już w bazie danych Firebase to aktualizujemy tylko lastLogin.
                mDatabase.child(mAuth.getCurrentUser().getUid()).child("lastLogin").setValue(System.currentTimeMillis());
                //sprawdzamy czy istnieje lokalny użytkownik
                DBHelper db = new DBHelper(Angielski.getAppContext(), NewMainActivity.databaseVersion);
                User localUser = db.getUser(mAuth.getCurrentUser().getUid());
                //jeśli nie istnieje lokalny to dodajemy go do bazy danych z danymi z firebase.
                if (localUser == null){
                    user.setLastLogin(System.currentTimeMillis());
                    db.insertOrUpdateUser(user);
                    //jeśli istnieje lokalny to:
                } else {
                    //1. Na serwerze jest nowsza wersja bazy danych
                    if (user.getAllTime() > localUser.getAllTime()){
                        /**  Pytanie o baze danych  i wtedy update punktow albo nie  **/
                        db.insertOrUpdateUser(user);
                        //2. Na serwerze jest taka sama wersja
                    } else if (user.getAllTime() == localUser.getAllTime()){
                        db.insertOrUpdateUser(user);
                        //nie robimy nic
                        //3. Aktualizujemy punkty na serwerze.
                    } else {
                        mDatabase.child(mAuth.getCurrentUser().getUid()).child("allTime").setValue(localUser.getAllTime());
                        mDatabase.child(mAuth.getCurrentUser().getUid()).child("weekly").setValue(localUser.getWeekly());
                        mDatabase.child(mAuth.getCurrentUser().getUid()).child("monthly").setValue(localUser.getMonthly());
                    }
                }
                db.close();
            }

            @Override
            public void userDoesNotExist() {
                //kiedy jesteśmy zalogowani, ale nie ma użytkownika w bazie danych Firebase
                Toast.makeText(Angielski.getAppContext(), "userDoesNotExist", Toast.LENGTH_SHORT).show();
                DBHelper db = new DBHelper(Angielski.getAppContext(), NewMainActivity.databaseVersion);
                //jeśli użytkownika nie ma w bazie danych Firebase to dodajemy go.
                if (LoginActivity.mAuth.getCurrentUser() != null) {
                    User user = new User(LoginActivity.mAuth.getCurrentUser().getUid(), LoginActivity.mAuth.getCurrentUser().getEmail(), System.currentTimeMillis(), 0, 0, 0, 0);
                    mDatabase.child(mAuth.getCurrentUser().getUid()).setValue(user);
                    //Sprawdzamy czy jest już lokalny użytkownik. Jeśli jest to aktualizujemy czas i robimy update. Jeśli nie ma to dodajemy nowego.
                    User localUser = db.getUser(mAuth.getCurrentUser().getUid());
                    if (localUser == null){
                        localUser = user; //new User(LoginActivity.mAuth.getCurrentUser().getUid(), LoginActivity.mAuth.getCurrentUser().getEmail(), System.currentTimeMillis(), 0, 0, 0, 0);
                    }
                    else {
                        localUser.setLastLogin(System.currentTimeMillis());
                    }
                    db.insertOrUpdateUser(localUser);
                }
                db.close();
            }

            @Override
            public void onFailed() {

            }
        });
    }

    private void getUserFromFirebaseWhenSkipLogin(final OnGetDataListener listener) {
        userInFirebase = null;

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mAuth == null){
                    //lokalny użytkownik
                    //sprawdzamy czy jest, jak nie to tworzymy
                    listener.onStart();
                }
                //jeśli jest użytkownik
                else if (mAuth != null && mAuth.getCurrentUser() != null) {
                    userInFirebase = dataSnapshot.child(mAuth.getCurrentUser().getUid()).getValue(User.class);
                    //zalogowany to pobieramy dane z serwera
                }
                if (userInFirebase != null){
                    //jeśli użytkownik jest już w bazie danych
                    listener.onSuccess(userInFirebase);
                }else {
                    //jeśli użytkownika nie ma w bazie danych Firebase
                    listener.userDoesNotExist();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseServer", "getUserFromFirebase + onCancelled + getUserFromFirebaseWhenSkipLogin");
                listener.onFailed();
            }
        });
    }
}
