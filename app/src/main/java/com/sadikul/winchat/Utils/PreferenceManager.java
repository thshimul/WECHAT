package com.sadikul.winchat.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ASUS on 27-Nov-17.
 */

public class PreferenceManager {
    static PreferenceManager mPreferenceManager;
    SharedPreferences mSharedPreference;

    Context context;
    public PreferenceManager(Context context){
        this.context=context;
        mSharedPreference=context.getSharedPreferences(Constants.NaptelPreference, Context.MODE_PRIVATE);
    }

    public static synchronized PreferenceManager getInstance(Context context){
        if(mPreferenceManager==null){
            mPreferenceManager=new PreferenceManager(context);
        }
        return mPreferenceManager;
    }


    public void setLoginStatus(boolean login){
        SharedPreferences.Editor editor=mSharedPreference.edit();
        editor.putBoolean(Constants.isLoginCheck,login);
        editor.commit();
        editor.apply();
    }

    public boolean getLoginStatus() {
        return mSharedPreference.getBoolean(Constants.isLoginCheck,false);
    }

    public void logout(){
        SharedPreferences.Editor editor=mSharedPreference.edit();
        editor.clear();
        editor.commit();
        editor.apply();
    }
}
