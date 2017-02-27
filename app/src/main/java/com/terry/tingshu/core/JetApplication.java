package com.terry.tingshu.core;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.widget.Toast;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.terry.tingshu.AudioPlayService;

import java.io.File;

import static android.os.Environment.getExternalStoragePublicDirectory;

/**
 * Created by terry on 2017/1/19.
 * Copyright (C) 2017 Terry Cheng
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class JetApplication extends Application {

    private Context mContext;

    public AudioPlayService getService() {
        return mService;
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

        //Application context.
        mContext = getApplicationContext();

        //SharedReferences.
        mSharedPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        //sss
        Init();
    }

    private void Init() {
        initImageLoader(this);
        Toast.makeText(mContext, "Application Initialized...", Toast.LENGTH_SHORT).show();
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

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

}
