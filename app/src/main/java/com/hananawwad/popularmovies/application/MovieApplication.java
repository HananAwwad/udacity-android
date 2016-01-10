package com.hananawwad.popularmovies.application;

import android.app.Application;

import com.facebook.stetho.Stetho;


/**
 * Created by user on 11/22/2015.
 */
public class MovieApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
