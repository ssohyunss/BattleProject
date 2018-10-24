package com.example.sohyun.battleproject.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AgmPrefer {

    public SharedPreferences mSharedPref;
    public String mem_email = "mem_email";
    public String mem_nickname = "mem_nickname";
    public String mem_image = "mem_image";

    public AgmPrefer(Context ctx){
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public void setNickname(String name){
        SharedPreferences.Editor sharedEditor = mSharedPref.edit();
        sharedEditor.putString(mem_nickname,name);
        sharedEditor.commit();
    }

    public String getNickname(){
        return mSharedPref.getString(mem_nickname,"");
    }

    public void setEmail(String email){
        SharedPreferences.Editor sharedEditor = mSharedPref.edit();
        sharedEditor.putString(mem_email,email);
        sharedEditor.commit();
    }

    public String getEmail(){
        return mSharedPref.getString(mem_email,"");
    }


    public void setProfileImage(String image_url){
        SharedPreferences.Editor sharedEditor = mSharedPref.edit();
        sharedEditor.putString(mem_image,image_url);
        sharedEditor.commit();
    }

    public String getProfilelmage(){
        return mSharedPref.getString(mem_image,"");
    }


    public void clear(){
        this.setNickname("");
        this.setEmail("");
    }
}
