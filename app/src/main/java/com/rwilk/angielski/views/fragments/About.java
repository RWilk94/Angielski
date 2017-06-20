package com.rwilk.angielski.views.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rwilk.angielski.database.DBHelper;
import com.rwilk.angielski.R;
import com.rwilk.angielski.database.Points;
import com.rwilk.angielski.database.User;
import com.rwilk.angielski.views.LoginActivity;
import com.rwilk.angielski.views.NewMainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Fragment about. Profile, save and upload files.
 */
public class About extends Fragment implements View.OnClickListener {

    private FirebaseAuth auth;
    private String uniqueID;

    private FirebaseStorage storage;// = FirebaseStorage.getInstance();
    private StorageReference storageReference;// = storage.getReference();

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private TextView textViewPointsWeekly;
    private TextView textViewPointsMonthly;
    private TextView textViewPointsAllTime;

    private ProgressDialog progressDialog;

    public static About newInstance() {
        return new About();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        FirebaseApp.initializeApp(getContext());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        TextView email = (TextView) view.findViewById(R.id.email);
        ImageView photo = (ImageView) view.findViewById(R.id.photo);
        textViewPointsWeekly = (TextView) view.findViewById(R.id.about_points_weekly);
        textViewPointsMonthly = (TextView) view.findViewById(R.id.about_points_monthly);
        textViewPointsAllTime = (TextView) view.findViewById(R.id.about_points_all_time);
        Button logOutOrDisconnect = (Button) view.findViewById(R.id.LogOutOrDisconnect);
        logOutOrDisconnect.setOnClickListener(this);
        view.findViewById(R.id.createBackup).setOnClickListener(this);
        view.findViewById(R.id.restoreBackup).setOnClickListener(this);
        auth = LoginActivity.mAuth;

        if (auth != null) {
            if (auth.getCurrentUser() != null) {
                if (auth.getCurrentUser().getPhotoUrl() != null) {
                    String personPhotoUrl = auth.getCurrentUser().getPhotoUrl().toString();

                    Glide.with(getContext()).load(personPhotoUrl)
                            .thumbnail(1.0f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(photo);
                }
                if (auth.getCurrentUser().getEmail() != null) {
                    email.setText(auth.getCurrentUser().getEmail());
                }
            } else {
                view.findViewById(R.id.createBackup).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.restoreBackup).setVisibility(View.INVISIBLE);
                ((Button) view.findViewById(R.id.LogOutOrDisconnect)).setText(R.string.sign_in);
            }
        }
        setPoints();
        //view.findViewById(R.id.findFriend).setOnClickListener(this);

        return view;
    }

    private void setPoints() {
        DBHelper db = new DBHelper(getContext(), NewMainActivity.databaseVersion);
        Points points = db.getPoints();
        if (points != null) {
            //textViewPointsWeekly.setText(Integer.toString(points.getWeekly()));
            textViewPointsWeekly.setText(String.format(Locale.UK, "%d", points.getWeekly()));
            textViewPointsMonthly.setText(String.format(Locale.UK, "%d", points.getMonthly()));
            textViewPointsAllTime.setText(String.format(Locale.UK, "%d", points.getAllTime()));
        }
        db.close();
    }

    /**
     * Listener for onClick method.
     *
     * @param view current view
     */
    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.LogOutOrDisconnect) {
            ((NewMainActivity) getActivity()).closeActivity();
        } else if (i == R.id.restoreBackup) {
            verifyStoragePermissions(getActivity());
            downloadFile();
        } else if (i == R.id.createBackup) {
            verifyStoragePermissions(getActivity());
            uploadFile();
        } //else if (i == R.id.findFriend) {
        //findFriend("wilkrafal1mizst@gmail.com");
        //displayPopupWindow(view);
        //}

    }

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
                Toast.makeText(getContext(), "File has been created", Toast.LENGTH_SHORT).show();
                File oldDb = new File("/data/data/" + "com.rwilk.angielski" + "/databases/" + "my_db");
                try {
                    copyFile(new FileInputStream(myFile), new FileOutputStream(oldDb));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
    private void uploadFile() {
        exportDB();
        File sdcard = Environment.getExternalStorageDirectory();
        File database = new File(sdcard, "my_db.db");
        Uri filePath = Uri.fromFile(database);
        System.out.println(filePath.getPath());

        progressDialog = new ProgressDialog(getContext());
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
                        Toast.makeText(getContext(), "Database uploaded", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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
    public static void verifyStoragePermissions(Activity activity) {
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


    /**
     * Method find user in database which has email equal given by user.
     *
     * @param email user email
     * @return ArrayList<User> if find or null if user does not exist.
     */
    public void findFriend(final String email) {
        final List<User> selectedUsers = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child("Users").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child : children) {
                    User user = child.getValue(User.class);
                    if (user.getEmail().equalsIgnoreCase(email)) {
                        selectedUsers.add(user);
                        Toast.makeText(getContext(), "Find user:" + selectedUsers.get(0).getName(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        /*if (selectedUsers.isEmpty()){
            Toast.makeText(getContext(), "Can't find friends", Toast.LENGTH_SHORT).show();
            //return null;
        }
        else {
            Toast.makeText(getContext(), "Find user:" + selectedUsers.get(0).getName(), Toast.LENGTH_SHORT).show();
            //return selectedUsers;
        }*/
    }
/*
    private void displayPopupWindow(View anchorView) {
        PopupWindow popup = new PopupWindow(getActivity());
        View layout = getLayoutInflater(getArguments()).inflate(R.layout.popup_content, null);
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Show anchored to button
        popup.setBackgroundDrawable(new BitmapDrawable());
        //popup.showAsDropDown((anchorView);
        popup.showAsDropDown(anchorView);
        //popup.
    }*/

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            setPoints();
        }
    }

}
