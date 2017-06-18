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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rwilk.angielski.R;
import com.rwilk.angielski.database.User;

import java.util.ArrayList;

/**
 * Created by Rafał Wilk.
 * LoginActivity jest to klasa odpowiedzialna za logowanie do aplikacji za pomocą konta Google.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private final String TAG = "GoogleActivity";
    public final int RC_SIGN_IN = 9001;
    public static FirebaseAuth mAuth;
    public GoogleApiClient mGoogleApiClient;
    public GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.skipLogin).setOnClickListener(this);
        findViewById(R.id.signInButton).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.disconnectButton).setOnClickListener(this);

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

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
            updateUserInDatabase();
        openMainActivity(currentUser);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
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
                        addUserToDatabase(mAuth.getCurrentUser());
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void signOut() {
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
    }

    public void revokeAccess() {
        mAuth.signOut();

        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void openMainActivity(FirebaseUser user) {
        if (user != null) {
            updateUI(user);
            Intent intent = new Intent(this, NewMainActivity.class);
            startActivity(intent);
        }
    }

    private void skipLogin() {
        Intent intent = new Intent(this, NewMainActivity.class);
        startActivity(intent);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            findViewById(R.id.signInButton).setVisibility(View.GONE);
            findViewById(R.id.signOutAndDisconnect).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.signInButton).setVisibility(View.VISIBLE);
            findViewById(R.id.signOutAndDisconnect).setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View view) {
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
    }

    private DatabaseReference mDatabase;

    /**
     * Method add user to Firebase database.
     */
    public void addUserToDatabase(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference("Users/" + firebaseUser.getUid());

            String userId = firebaseUser.getUid();
            User user = new User(userId, firebaseUser.getEmail(), firebaseUser.getDisplayName());
            mDatabase.setValue(user);
        }
    }

    /**
     * Method update time of lastLogin when user run the app.
     */
    public void updateUserInDatabase() {
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mDatabase.child(mAuth.getCurrentUser().getUid()).child("lastLogin").setValue(System.currentTimeMillis());
    }


    public void readDataFromDatabase() {
        mDatabase.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Log.w(TAG, "User name: " + user.getName() + ", email " + user.getEmail());
                Log.w(TAG, "User name: " + user.getName() + ", email " + user.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    /**
     * Method find user in database which has email equal given by user.
     * @param email user email
     * @return  ArrayList<User> if find or null if user does not exist.
     */
    public ArrayList<User> findFriend(final String email) {
        final ArrayList<User> selectedUsers = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child("Users").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child : children) {
                    User user = child.getValue(User.class);
                    if (user.getEmail().equalsIgnoreCase(email))
                        selectedUsers.add(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        if (selectedUsers.isEmpty()){
            Toast.makeText(getApplicationContext(), "Can't find friends", Toast.LENGTH_SHORT).show();
            return null;
        }
        else return selectedUsers;
    }
}
