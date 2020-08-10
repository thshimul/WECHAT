package com.sadikul.winchat.Utils;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ASUS on 13-Dec-17.
 */

public class WinChat extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
