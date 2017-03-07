package com.terry.tingshu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;

import com.terry.tingshu.core.JetApplication;
import com.terry.tingshu.core.ServiceBase;
import com.terry.tingshu.entity.Song;
import com.terry.tingshu.helpers.SongHelper;

import java.io.IOException;

import static com.terry.tingshu.Const.KEY_LAST_SONG_POS;
import static com.terry.tingshu.Const.KEY_LAST_SONG_URL;

public class AudioPlayService extends ServiceBase implements MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener {


    //LocalBroadCastManager


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

        resumePlayer();

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
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });
    }

    private void init() {
        this.songHelper = new SongHelper(this.mApp);
    }

    private void resumePlayer() {
        String lastUrl = mApp.getSharedPreferences().getString(KEY_LAST_SONG_URL, "");
        int lastPos = mApp.getSharedPreferences().getInt( KEY_LAST_SONG_POS, 0);
        if (lastUrl.length() > 0)
            playerPlay(Uri.parse(lastUrl));
        if (lastPos > 0)
            mPlayer.seekTo(lastPos);
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
        mApp.getSharedPreferences().edit().putString(KEY_LAST_SONG_URL, songUri.toString()).apply();
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
            mApp.getSharedPreferences().edit().putInt(KEY_LAST_SONG_POS, mPlayer.getCurrentPosition()).apply();
        }
    }

    public void playerStart() {
        if (mPlayer != null && !mPlayer.isPlaying()) {
            mPlayer.start();
        }
    }

    public void playerStop() {
        if (mPlayer != null) {
            mPlayer.stop();
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

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1 && mPlayer != null) {
                Intent intent = new Intent();
                intent.setAction(Const.ACTION_MUSIC_CURRENT);
                intent.putExtra("currentPosition", mPlayer.getCurrentPosition());
                sendBroadcast(intent);
                handler.sendEmptyMessageDelayed(1, 1000);
            }
        }
    };


    class PlayerServiceReceiver extends  BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int control = intent.getIntExtra("control", -1);
            switch (control) {
                case Const.PLAYER_PLAY:
                    playerStart();
                    break;
                case Const.PLAYER_PAUSE:
                    playerPause();
                    break;
                case Const.PLAYER_STOP:
                    playerStop();
                    break;
                case Const.PLAYER_PREVIOUS:
                    playerPrevious();
                    break;
                case Const.PLAYER_NEXT:
                    playerNext();
                    break;
            }
        }
    }
}
