package edu.uw.covidsafe;

import android.app.Application;


public class CovidApplication extends Application {

    ApplicationComponent applicationComponent = DaggerApplicationComponent.create();

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
