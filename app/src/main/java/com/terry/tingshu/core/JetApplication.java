package com.terry.tingshu.core;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.terry.tingshu.AudioPlayService;
import com.terry.tingshu.GlobalSongManager;
import com.terry.tingshu.SystemConst;

import java.io.File;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class JetApplication extends Application {

    private GlobalSongManager globalSongManager;

    private LocalBroadcastManager localBroadcastManager;

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

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        globalSongManager = GlobalSongManager.getInstance(this);

        Init();
    }

    private void Init() {
        initImageLoader(this);

        Iconify.with(new FontAwesomeModule())
                .with(new MaterialModule());

        initService();
    }

    private void initService() {
        Intent startIntent = new Intent(this, AudioPlayService.class);
        startService(startIntent);
    }


    private void initImageLoader(Context context) {

    }

    public void sendControlBroadcast_PLAY() {
        Intent intent = new Intent(SystemConst.ACTION_PLAYER_CONTROL);
        intent.putExtra(SystemConst.EXTRA_KEY_PLAYER_CONTROL, SystemConst.PLAYER_PLAY);
        localBroadcastManager.sendBroadcastSync(intent);
    }

    public void sendControlBroadcast_PAUSE() {
        Intent intent = new Intent(SystemConst.ACTION_PLAYER_CONTROL);
        intent.putExtra(SystemConst.EXTRA_KEY_PLAYER_CONTROL, SystemConst.PLAYER_PAUSE);
        localBroadcastManager.sendBroadcastSync(intent);
    }

    public void sendControlBroadcast_NEXT() {
        Intent intent = new Intent(SystemConst.ACTION_PLAYER_CONTROL);
        intent.putExtra(SystemConst.EXTRA_KEY_PLAYER_CONTROL, SystemConst.PLAYER_NEXT);
        localBroadcastManager.sendBroadcastSync(intent);
    }

    public void sendControlBroadcast_PREVIOUS() {
        Intent intent = new Intent(SystemConst.ACTION_PLAYER_CONTROL);
        intent.putExtra(SystemConst.EXTRA_KEY_PLAYER_CONTROL, SystemConst.PLAYER_PREVIOUS);
        localBroadcastManager.sendBroadcastSync(intent);
    }

    public void sendInfoBroadcast_PAUSE(){
        //region send pause broadcast
        Intent intent = new Intent(SystemConst.ACTION_MUSIC_SERVICE_INFO);
        intent.putExtra(SystemConst.EXTRA_KEY_PLAYER_INFO, SystemConst.INFO_PLAYER_PAUSE);
        localBroadcastManager.sendBroadcast(intent);
        //endregion
    }


    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

}
