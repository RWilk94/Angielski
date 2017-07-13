package com.rwilk.angielski.file;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rwilk.angielski.application.Angielski;
import com.rwilk.angielski.database.DBHelper;
import com.rwilk.angielski.views.LoginActivity;
import com.rwilk.angielski.views.NewMainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Klasa odpowiedzialna za pobieranie bazy danych z serwera i wysylanie bazy danych na serwer.
 */
public class FileOperations {

    private FirebaseAuth auth;
    private String uniqueID;
    private StorageReference storageReference;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    public FileOperations(FirebaseAuth auth) {
        this.auth = auth;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    private ProgressDialog progressDialog;

    /**
     * Method download database file from server. Next call the copyFile method.
     */
    public void downloadFile() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        if (auth.getCurrentUser() != null)
            uniqueID = auth.getCurrentUser().getUid();
        StorageReference islandRef = storageRef.child("database/" + uniqueID + ".db");

        File storagePath = new File(Environment.getExternalStorageDirectory(), "angielski");
        if (!storagePath.exists()) { // Create direcorty if not exists
            storagePath.mkdirs();
        }
        final File myFile = new File(storagePath, "my_db");

        islandRef.getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Angielski.getAppContext(), "File has been created", Toast.LENGTH_SHORT).show();
                File oldDb = new File("/data/data/" + "com.rwilk.angielski" + "/databases/" + "my_db");
                try {
                    copyFile(new FileInputStream(myFile), new FileOutputStream(oldDb));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //DBHelper db = new DBHelper(Angielski.getAppContext(), NewMainActivity.databaseVersion);
                //db.
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Jeśli nie ma pliku bazy danych na serwerze.
                        Toast.makeText(Angielski.getAppContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        //uploadFile();
                    }
                });
    }

    /**
     * Method copied database file from data/package/databases/name to sdcard.
     * Method is important, because the App upload database file from sdcard.
     */
    private void exportDB() {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source; //null
        FileChannel destination; //null
        String currentDBPath = "/data/" + "com.rwilk.angielski" + "/databases/" + "my_db";
        String backupDBPath = "my_db.db";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method gets the database file from sdcard and upload to database.
     */
    @SuppressWarnings("VisibleForTests")
    public void uploadFile(Activity activity) {
        exportDB();
        File sdcard = Environment.getExternalStorageDirectory();
        File database = new File(sdcard, "my_db.db");
        Uri filePath = Uri.fromFile(database);
        System.out.println(filePath.getPath());

        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Uploading");
        progressDialog.show();

        if (auth.getCurrentUser() != null)
            uniqueID = auth.getCurrentUser().getUid();

        StorageReference riversRef = storageReference.child("database/" + uniqueID + ".db");
        riversRef.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(Angielski.getAppContext(), "Database uploaded", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Angielski.getAppContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    }
                });
    }

    /**
     * Method copied database file from sdcard to internal storage.
     *
     * @param fromFile path to file which will be copied.
     * @param toFile   path to directory/file where we want to copied file.
     * @throws IOException
     */
    private void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);

            DBHelper db = new DBHelper(Angielski.getAppContext(), NewMainActivity.databaseVersion);
            db.updateBackup(LoginActivity.userInFirebase);
            db.close();

        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }

    /**
     * Method verifies storage permissions to download file and upload.
     *
     * @param activity current activity
     */
    public void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


}
