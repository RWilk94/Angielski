package com.rwilk.angielski.database.firebase;


import com.rwilk.angielski.database.User;

public interface OnGetDataListener {

    void onStart();
    void onSuccess(User user);
    void userDoesNotExist();
    void onFailed();


}
