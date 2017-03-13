package com.terry.tingshu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;

import com.terry.tingshu.core.JetApplication;
import com.terry.tingshu.core.ServiceBase;
import com.terry.tingshu.entity.Song;
import com.terry.tingshu.helpers.SongHelper;

import java.io.File;
import java.io.IOException;

public class AudioPlayService extends ServiceBase {
    private JetApplication mApp;

    private SongHelper songHelper;

    private MusicPlayer mPlayer;

    private MyBinder mBinder = new MyBinder();

    private LocalBroadcastManager localBroadcastManager;

    public AudioPlayService() {
        //region heart beat
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
        //endregion

    }

    @Override
    public void onCreate() {
        super.onCreate();
        initMsgBroadCast();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mApp = (JetApplication) getApplicationContext();
        mApp.setService(this);
        this.songHelper = new SongHelper(this.mApp);

        initMediaPlayer();


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("AudioPlayService.onBind");
        return mBinder;
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
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mPlayer.start();

                Intent intent = new Intent(SystemConst.ACTION_MUSIC_SERVICE_INFO);
                intent.putExtra(SystemConst.EXTRA_KEY_PLAYER_INFO, SystemConst.INFO_PLAYER_PLAYING);
                localBroadcastManager.sendBroadcast(intent);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (mPlayer.isPlaying()) {
                            try {
                                Thread.sleep(1000);
                                Intent intent = new Intent(SystemConst.ACTION_MUSIC_SERVICE_INFO);
                                intent.putExtra(SystemConst.EXTRA_KEY_CURRENT_POSITION, mPlayer.getCurrentPosition());
                                localBroadcastManager.sendBroadcast(intent);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }).start();

            }
        });

    }

    private void initMsgBroadCast() {
        localBroadcastManager = LocalBroadcastManager.getInstance(AudioPlayService.this);
        localBroadcastManager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null && intent.getAction().equals(SystemConst.ACTION_PLAYER_CONTROL)) {
                    int control = intent.getIntExtra(SystemConst.EXTRA_KEY_PLAYER_CONTROL, -1);
                    // ============== 广播订阅 ===============
                    //播放服务接收的广播信息有：
                    //当前的播放状态：播放，暂停，上一首，下一首
                    switch (control) {
                        case SystemConst.PLAYER_PLAY:
                            playerStart();
                            System.out.println("AudioPlayService.onReceive:PLAYER_PLAY");
                            break;
                        case SystemConst.PLAYER_PAUSE:
                            playerPause();
                            System.out.println("AudioPlayService.onReceive:PLAYER_PAUSE");
                            break;
                        case SystemConst.PLAYER_PREVIOUS:
                            playerPrevious();
                            System.out.println("AudioPlayService.onReceive:PLAYER_PREVIOUS");
                            break;
                        case SystemConst.PLAYER_NEXT:
                            playerNext();
                            System.out.println("AudioPlayService.onReceive:PLAYER_NEXT");
                            break;
                    }
                }
            }
        }, new IntentFilter(SystemConst.ACTION_PLAYER_CONTROL));

    }

    private void initSongHelper(String songUrl) {
        File songfile = new File(songUrl);
        this.songHelper.loadPlayList(songfile.getParent());
    }

    /**
     * 播放某一首歌.
     *
     * @param song 指定播放的歌曲.
     */
    public void playSong(Song song) {
        initSongHelper(song.getUri());

        Uri songUri = Uri.parse("file://" + song.getUri());

        playerPlay(songUri);
    }

    private void playerPlay(Uri songURI) {
        if (mPlayer != null) {
//            mPlayer.stop();
            mPlayer.reset();
        }

        try {
            mPlayer.setDataSource(this, songURI);
            mPlayer.prepareAsync();
            mApp.getSharedPreferences().edit().putString(SystemConst.KEY_LAST_SONG_URL, songURI.getPath()).apply();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playerPause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            Intent intent = new Intent(SystemConst.ACTION_MUSIC_SERVICE_INFO);
            intent.putExtra(SystemConst.EXTRA_KEY_PLAYER_INFO, SystemConst.INFO_PLAYER_PAUSE);
            localBroadcastManager.sendBroadcast(intent);

            String lastUrl = songHelper.get().getUri();
            int lastPos = mPlayer.getCurrentPosition();

            mApp.getSharedPreferences().edit().putString(SystemConst.KEY_LAST_SONG_URL, lastUrl).apply();
            mApp.getSharedPreferences().edit().putInt(SystemConst.KEY_LAST_SONG_POS, lastPos).apply();
        }
    }

    private void playerStart() {
        resumePlay();
    }

    private void playerPrevious() {
        if (mPlayer != null) {
            if (songHelper.movePrevious()) {
                playSong(songHelper.get());
            }
        }
    }

    private void playerNext() {
        if (mPlayer != null) {
            if (songHelper.moveNext()) {
                playSong(songHelper.get());
            }
        }
    }

    private void resumePlay() {
        String lastUrl = mApp.getSharedPreferences().getString(SystemConst.KEY_LAST_SONG_URL, "");
        int lastPos = mApp.getSharedPreferences().getInt(SystemConst.KEY_LAST_SONG_POS, 0);

        if (lastUrl.length() > 0) {
            initSongHelper(lastUrl);
            playerPlay(Uri.parse(lastUrl));
            if (lastPos > 0)
                mPlayer.seekTo(lastPos);
        }
    }

    public SongHelper getSongHelper() {
        return songHelper;
    }

    public MusicPlayer getMusicPlayer() {
        return mPlayer;
    }

    public class MyBinder extends Binder {
        public AudioPlayService getService() {
            return AudioPlayService.this;
        }
    }
}
