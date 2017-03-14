package com.terry.tingshu.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.terry.tingshu.GlobalSongManager;
import com.terry.tingshu.AudioPlayService;

/**
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

public abstract class PlayerActivityBase extends AppCompatActivity {
    protected JetApplication mApp;
    private Context mContext;
    protected AudioPlayService playService;

    protected GlobalSongManager globalSongManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mApp = (JetApplication) getApplicationContext();

        bindService();
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }

    private void bindService() {
        Intent bindingIntent = new Intent(this, AudioPlayService.class);
        bindService(bindingIntent, conn, 0);
    }

    protected abstract void onPostServiceBind();

    protected final ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioPlayService playbackService = ((AudioPlayService.ServiceBinder) service).getService();
            if (playbackService != null) {
                mApp.setService(playbackService);
                playService = mApp.getService();
                globalSongManager =  mApp.getGlobalSongManager();
                onPostServiceBind();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
