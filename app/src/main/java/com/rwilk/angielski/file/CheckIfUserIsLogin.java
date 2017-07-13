package com.rwilk.angielski.file;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.rwilk.angielski.database.DBHelper;
import com.rwilk.angielski.database.User;
import com.rwilk.angielski.views.LoginActivity;
import com.rwilk.angielski.views.NewMainActivity;


public class CheckIfUserIsLogin {

    public CheckIfUserIsLogin() {  }

    public void check(Context context, final Activity activity){
        final DBHelper db = new DBHelper(context, NewMainActivity.databaseVersion);
        User localUser;
        //jeśli jest zalogowany użytkownik to szukamy jego odpowiednika w lokalnej bazie danych.
        if (LoginActivity.userInFirebase != null){
            localUser = db.getUser(LoginActivity.userInFirebase.getIdUser());
            if (localUser == null){
                //Nie ma lokalnego użytkownika - musimy stworzyć
                db.insertOrUpdateUser(LoginActivity.userInFirebase); //powinno dodać usera do bazy danych
                //^^ Dziala tylko jeśli nie ma użytkownika w bazie danych

                //jeśli już był lokalny użytkownik
            } else if (LoginActivity.userInFirebase.getBackup() > localUser.getBackup()){
                //pytamy czy chce ściągnąć nowszą bazę danych


                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Na serwerze znajduje się nowsza wersja bazy danych. Czy chcesz ją pobrać?")
                        .setTitle("Nowsza wersja bazy danych");

                builder.setPositiveButton("Tak", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //przywracanie bazy danych z firebase
                        FileOperations fileOperations = new FileOperations(LoginActivity.mAuth);
                        fileOperations.verifyStoragePermissions(activity);
                        fileOperations.downloadFile();

                        db.updateBackup(LoginActivity.userInFirebase);

                        /*System.out.println("Dupa, dupa, dupa");
                        System.out.println(LoginActivity.userInFirebase.getBackup() + " ");
                        System.out.println("Pobieramy bazę danych z serwera...");
                        System.out.println("Dupa, dupa, dupa");*/
                    }
                });
                builder.setNegativeButton("Nie", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.updateBackup(LoginActivity.userInFirebase);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                //tu trzeba będzie wyciągnąć metody z About

            }
            //niezalogowany
        } else {
            localUser = db.getUser("0000"); //0000 to będzie lokalny użytkownik

            //niezalogowany i nie ma lokalnego usera
            if (localUser==null){
                localUser = new User("0000", null, System.currentTimeMillis(), 0, 0, 0, 0);
                db.insertOrUpdateUser(localUser);
            }
            //niezalogowany, ale jest juz lokalny user
            else{
                localUser.setLastLogin(System.currentTimeMillis());
                db.insertOrUpdateUser(localUser);
            }
        }
        db.close();
    }
}
