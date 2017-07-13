package com.rwilk.angielski.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.rwilk.angielski.R;
import com.rwilk.angielski.database.DBHelper;
import com.rwilk.angielski.database.firebase.FirebaseServer;
import com.rwilk.angielski.database.User;
import com.rwilk.angielski.database.WordSQL;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * LoginActivity jest to klasa odpowiedzialna za logowanie do aplikacji za pomocą konta Google.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private final String TAG = "GoogleActivity";
    public final int RC_SIGN_IN = 9001;
    public static FirebaseAuth mAuth;
    public GoogleApiClient mGoogleApiClient;
    public GoogleSignInOptions gso;
    private DatabaseReference mDatabase;
    public static User userInFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("LoginActivity", "OnCreateMethod + start");
        setContentView(R.layout.activity_login);

        findViewById(R.id.skipLogin).setOnClickListener(this);
        findViewById(R.id.signInButton).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.disconnectButton).setOnClickListener(this);

        createDatabase();

        FirebaseApp.initializeApp(this);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();
        //addUserToDatabase(mAuth.getCurrentUser());
        FirebaseUser currentUser = mAuth.getCurrentUser();
        openMainActivity(currentUser);
        Log.i("LoginActivity", "OnCreateMethod + end");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("LoginActivity", "onActivityResult + start");
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }
        Log.i("LoginActivity", "onActivityResult + end");
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.i("LoginActivity", "firebaseAuthWithGoogle + start");
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            openMainActivity(user);
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        addUserToDatabase();
                    }
                });
        Log.i("LoginActivity", "firebaseAuthWithGoogle + end");
    }

    private void signIn() {
        Log.i("LoginActivity", "signIn + start");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.i("LoginActivity", "signIn + end");
    }

    public void signOut() {
        Log.i("LoginActivity", "signOut + start");
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
        Log.i("LoginActivity", "signOut + end");
    }

    public void revokeAccess() {
        Log.i("LoginActivity", "revokeAccess + start");
        mAuth.signOut();
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
        Log.i("LoginActivity", "revokeAccess + end");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("LoginActivity", "onConnectionFailed + start");
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
        Log.i("LoginActivity", "onConnectionFailed + end");
    }

    private void openMainActivity(FirebaseUser user) {
        Log.i("LoginActivity", "openMainActivity + start");
        if (user != null) {
            updateUI(user);
            FirebaseServer firebaseServer = new FirebaseServer(getApplicationContext());
            firebaseServer.skipLoginToFirebase();
            Intent intent = new Intent(this, NewMainActivity.class);
            startActivity(intent);
        }
        Log.i("LoginActivity", "openMainActivity + end");
    }

    private void skipLogin() {
        Log.i("LoginActivity", "skipLogin + start");
        //checkIfUserIsLogin();

        FirebaseServer firebaseServer = new FirebaseServer(getApplicationContext());
        firebaseServer.skipLoginToFirebase();

        //get
        Intent intent = new Intent(this, NewMainActivity.class);
        startActivity(intent);
        Log.i("LoginActivity", "skipLogin + end");
    }

    private void updateUI(FirebaseUser user) {
        Log.i("LoginActivity", "updateUI + start");
        if (user != null) {
            findViewById(R.id.signInButton).setVisibility(View.GONE);
            findViewById(R.id.signOutAndDisconnect).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.signInButton).setVisibility(View.VISIBLE);
            findViewById(R.id.signOutAndDisconnect).setVisibility(View.GONE);
        }
        Log.i("LoginActivity", "updateUI + end");
    }

    @Override
    public void onClick(View view) {
        Log.i("LoginActivity", "onClick + start");
        int i = view.getId();
        if (i == R.id.signInButton) {
            signIn();
        } else if (i == R.id.skipLogin) {
            skipLogin();
        } else if (i == R.id.disconnectButton) {
            revokeAccess();
        } else if (i == R.id.signOutButton) {
            signOut();
        }
        Log.i("LoginActivity", "onClick + end");
    }


    //public boolean zmienna = false;

    /**
     * Metoda zostaje wywołana w momencie, kiedy wybieramy konto do logowania.
     */
    public void addUserToDatabase() {
        //Sprawdzamy czy użytkownik jest w bazie danych Firebase
        FirebaseServer firebaseServer = new FirebaseServer(getApplicationContext());
        firebaseServer.firstLoginToFirebase();
    }

    /** Lista słówek pobranych z pliku, używana do tworzenia bazy danych. */
    public static ArrayList<WordSQL> listOfWordsToDatabase;

    /**
     * Metoda tworzy bazę danych SQLite.
     */
    private void createDatabase() {
        Log.i("addUserToDatabase", "createDatabase + start");
        DBHelper db;
        System.out.println("DB " + DBHelper.databaseExists);
        if (!DBHelper.databaseExists) {
            listOfWordsToDatabase = odczytZPliku();
        }
        db = new DBHelper(getApplicationContext(), null, NewMainActivity.databaseVersion);
        System.out.println("DB " + DBHelper.databaseExists);
        //db.updatePoints(0);
        db.close();
        Log.i("addUserToDatabase", "createDatabase + end");
    }

    /**
     * Metoda wczytuje plik, przetwarza go i zwraca listę słówke, z których zbudujemy bazę danych.
     * @return lista słówek na podstawie której jest budowana baza danych.
     */
    public ArrayList<WordSQL> odczytZPliku() {
        Log.i("addUserToDatabase", "odczytZPliku + start");
        BufferedReader reader = null;
        String polishWord = "", englishWord = "", partOfSpeech = "", section = "";
        ArrayList<WordSQL> listaSlowZPliku = new ArrayList<>();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("words.txt"), "UTF-8"));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                if (mLine.isEmpty()) continue;
                else if (mLine.substring(0, 3).equalsIgnoreCase("//*")) {
                    section = "'" + mLine.substring(3) + "'";
                } else if (mLine.substring(0, 2).equalsIgnoreCase("//")) {
                    partOfSpeech = mLine.substring(2);
                } else {
                    int tabulator = mLine.indexOf("\t"); //znajdujemy tabulator
                    if (tabulator != -1) {
                        englishWord = mLine.substring(0, tabulator); //wycinamy polskie słowo
                        polishWord = mLine.substring(tabulator + 1, mLine.length());
                    }
                    String sql = "'" + polishWord + "', '" + englishWord + "', '"  + partOfSpeech + "'";
                    WordSQL wordSQL = new WordSQL();
                    wordSQL.setSql(sql);
                    wordSQL.setSection(section);
                    listaSlowZPliku.add(wordSQL);
                }
            }
        } catch (IOException e) {
            System.out.println("Error ---------------------------- Error");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Error ---------------------------- Error2");
                }
            }
        }
        Log.i("addUserToDatabase", "odczytZPliku + end");
        return listaSlowZPliku;
    }

    /*public void checkIfUserIsLogin(){
        Log.i("addUserToDatabase", "checkIfUserIsLogin + start");
        DBHelper db = new DBHelper(getApplicationContext(), NewMainActivity.databaseVersion);
        User localUser;
        if (LoginActivity.mAuth != null && LoginActivity.mAuth.getCurrentUser()!= null){
            localUser = db.getUser(LoginActivity.mAuth.getCurrentUser().getUid());
            //long currentTimeMillis = System.currentTimeMillis();
            if (localUser == null){
                //Tworzymy lokalnego uzytkownika z emailem pobranym z konta Google
                db.insertOrUpdateUser(new User(LoginActivity.mAuth.getCurrentUser().getUid(), null, System.currentTimeMillis(), 0, 0, 0, 0));
                //aktualizujemy czas w Firebase
                mDatabase.child(mAuth.getCurrentUser().getUid()).child("lastLogin").setValue(System.currentTimeMillis());
            } else {
                localUser.setLastLogin(System.currentTimeMillis());
                db.insertOrUpdateUser(localUser);
                mDatabase.child(mAuth.getCurrentUser().getUid()).child("lastLogin").setValue(System.currentTimeMillis());
            }
        //Teraz przypadek, kiedy sie nie logujemy
        } else {
            localUser = db.getUser("0000");
            if (localUser == null){
                db.insertOrUpdateUser(new User("0000", null, System.currentTimeMillis(), 0, 0, 0, 0));
            }
            else {
                localUser.setLastLogin(System.currentTimeMillis());
                db.insertOrUpdateUser(localUser);
            }
        }
        db.close();
        Log.i("addUserToDatabase", "checkIfUserIsLogin + end");
    }*/



}