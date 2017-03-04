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

public class AudioPlayService extends ServiceBase implements MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener {

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

        String lastUrl = mApp.getSharedPreferences().getString("last_song", "");
        if (lastUrl.length() > 0)
            playerPlay(Uri.parse(lastUrl));

        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("AudioPlayService.onUnbind");
        return super.onUnbind(intent);
        //这里如果返回true,在本服务被重新绑定时，会调用onRebind()方法.
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
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
        Uri songUri = Uri.parse("file://" + song.getUri());
        mApp.getSharedPreferences().edit().putString("last_song", songUri.toString()).apply();
        playerPlay(songUri);
    }

    private void playerPlay(Uri songURI) {
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

            mPlayer.setDataSource(this, songURI);
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
        if (mPlayer != null && !mPlayer.isPlaying()) {
            mPlayer.start();
        }
    }

    public void playerPrevious() {
        if (mPlayer != null) {
            if (songHelper.movePrevious()) {
                playSong(songHelper.get());
            }
        }
    }

    public void playerNext() {
        if (mPlayer != null) {
            if (songHelper.moveNext()) {
                playSong(songHelper.get());
            }
        }
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public int getCurrentPos() {
        return mPlayer.getCurrentPosition();
    }

    public int getDuration() {
        if (mPlayer != null)
            return mPlayer.getDuration();
        return 0;
    }


    public class MyBinder extends Binder {
        public AudioPlayService getService() {
            return AudioPlayService.this;
        }
    }


}
