package com.ajdi.xyzreader;

import android.app.Application;

import timber.log.Timber;

/**
 * @author Yassin Ajdi
 * @since 4/7/2019.
 */
public class XYZApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
