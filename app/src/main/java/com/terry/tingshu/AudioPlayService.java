package com.terry.tingshu;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import com.terry.tingshu.core.JetApplication;
import com.terry.tingshu.core.ServiceBase;
import com.terry.tingshu.entity.Song;
import com.terry.tingshu.helpers.SongHelper;

import java.io.IOException;

public class AudioPlayService extends ServiceBase {

    private SongHelper songHelper;

    private MusicPlayer mPlayer;

    private JetApplication mApp;

    private MyBinder mBinder = new MyBinder();

    public AudioPlayService() {
        init();
        System.out.println("AudioPlayService.AudioPlayService");

        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    try {
                        Thread.sleep(5000);
                        System.out.println("SERVICE RUNNING:..........................[" + Thread.currentThread().getId() + "][" + i++ + ']');
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("AudioPlayService.onBind");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("AudioPlayService.onStartCommand");

        mApp = (JetApplication) getApplicationContext();
        mApp.setService(this);

        initMediaPlayer();
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("AudioPlayService.onUnbind");
        return super.onUnbind(intent);
        //这里如果返回true,在本服务被重新绑定时，会调用onRebind()方法.
    }

    private void initMediaPlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        mPlayer = new MusicPlayer();
        mPlayer.reset();

        //PARTIAL_WAKE_LOCK:保持CPU 运转，屏幕和键盘灯有可能是关闭的。
        mPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void init() {
        this.songHelper = new SongHelper(this.mApp);

    }

    public SongHelper getSongHelper() {
        return this.songHelper;
    }

    /**
     * 播放某一首歌.
     *
     * @param song 指定播放的歌曲.
     */
    public void playSong(Song song) {

        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
        }

        mPlayer = new MusicPlayer();
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mPlayer.start();
            }
        });

        try {
            //playingFile = filePath.toString();
            mPlayer.setDataSource(this, Uri.parse("file://" + song.getUri()));
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playerPause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
    }

    public void playerStart() {

    }

    public void playerPrevious() {

    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public int getCurrentPos() {
        return mPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mPlayer.getDuration();
    }

    public class MyBinder extends Binder {
        public AudioPlayService getService() {
            return AudioPlayService.this;
        }
    }
}