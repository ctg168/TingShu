package com.terry.tingshu.core;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.terry.tingshu.AudioPlayService;
import com.terry.tingshu.GlobalSongManager;

import java.io.File;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class JetApplication extends Application {

    private GlobalSongManager globalSongManager;

    public AudioPlayService getService() {
        return mService;
    }

    public GlobalSongManager getGlobalSongManager() {
        return globalSongManager;
    }

    public void setService(AudioPlayService mService) {
        this.mService = mService;
    }

    private AudioPlayService mService;

    //SharedPreferences.
    private static SharedPreferences mSharedPreferences;

    public File getRootMusicFolder() {
        return getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //SharedReferences.
        mSharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        Init();
    }

    private void Init() {
        initImageLoader(this);

        Iconify.with(new FontAwesomeModule())
                .with(new MaterialModule());

        initSongManager();

        initService();
    }

    private void initService() {
        Intent startIntent = new Intent(this, AudioPlayService.class);
        startService(startIntent);
    }

    private void initSongManager() {
        globalSongManager = new GlobalSongManager(this);
    }

    private void initImageLoader(Context context) {

    }

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

}
