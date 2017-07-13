package com.rwilk.angielski.views.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rwilk.angielski.R;
import com.rwilk.angielski.file.FileOperations;
import com.rwilk.angielski.views.LoginActivity;
import com.rwilk.angielski.views.NewMainActivity;


/**
 * Fragment about. Profile, save and upload files.
 */
public class About extends Fragment implements View.OnClickListener {

    private FirebaseAuth auth;

    private TextView textViewPointsWeekly;
    private TextView textViewPointsMonthly;
    private TextView textViewPointsAllTime;



    public static About newInstance() {
        return new About();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        FirebaseApp.initializeApp(getContext());
        //storage = FirebaseStorage.getInstance();
        //storageReference = storage.getReference();

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
        //setPoints();
        return view;
    }

    /*private void setPoints() {
        DBHelper db = new DBHelper(getContext(), NewMainActivity.databaseVersion);
        Points points = db.getPoints();
        if (points != null) {
            //textViewPointsWeekly.setText(Integer.toString(points.getWeekly()));
            textViewPointsWeekly.setText(String.format(Locale.UK, "%d", points.getWeekly()));
            textViewPointsMonthly.setText(String.format(Locale.UK, "%d", points.getMonthly()));
            textViewPointsAllTime.setText(String.format(Locale.UK, "%d", points.getAllTime()));
        }
        db.close();
    }*/

    /**
     * Listener for onClick method.
     *
     * @param view current view
     */
    @Override
    public void onClick(View view) {
        FileOperations fileOperations = new FileOperations(auth);
        int i = view.getId();
        if (i == R.id.LogOutOrDisconnect) {
            ((NewMainActivity) getActivity()).closeActivity();
        } else if (i == R.id.restoreBackup) {
            fileOperations.verifyStoragePermissions(getActivity());
            fileOperations.downloadFile();
            //verifyStoragePermissions(getActivity());
            //downloadFile();
        } else if (i == R.id.createBackup) {
            fileOperations.verifyStoragePermissions(getActivity());
            //verifyStoragePermissions(getActivity());
            fileOperations.uploadFile(getActivity());
            //uploadFile();
        }
    }




    /**
     * Method find user in database which has email equal given by user.
     * Podobno nowa metoda na szukanie po emailu.
     *
     * @param email user email
     */
    public void findFriend(final String email) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        DatabaseReference node = databaseReference.child("Users");

        node.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Skasowane... Błąd...");
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            //setPoints();
        }
    }

}
