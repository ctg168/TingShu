package com.terry.tingshu.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by terry on 2017/1/11.
 *
 */

public abstract class ServiceBase extends Service {

    //region life cycle
    @Override
    public void onCreate() {
        System.out.println("ServiceBase.onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        System.out.println("ServiceBase.onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("ServiceBase.onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("ServiceBase.onBind");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("ServiceBase.onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        System.out.println("ServiceBase.onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }
    //endregion


}
