package com.example.newgame_1;

import android.app.Application;

/**
 * <p>Created by shixin on 3/10/21.
 */
public class GameApplication extends Application {
    public static GameApplication INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }
}
