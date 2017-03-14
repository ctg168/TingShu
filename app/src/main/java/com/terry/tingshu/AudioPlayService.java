package com.terry.tingshu;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.RemoteViews;

import com.terry.tingshu.core.JetApplication;
import com.terry.tingshu.core.ServiceBase;
import com.terry.tingshu.entity.Song;
import com.terry.tingshu.helpers.SongHelper;

import java.io.IOException;

public class AudioPlayService extends ServiceBase {
    private JetApplication mApp;
    private SongHelper songHelper;
    private MusicPlayer mPlayer;
    private MyBinder mBinder = new MyBinder();

    private LocalBroadcastManager localBroadcastManager;
    private NotificationCompat.Builder mNotificationBuilder;

    private MediaPlayer.OnPreparedListener mediaPlayerPrepared;
    private MediaPlayer.OnCompletionListener mediaPlayerCompleted;

    private boolean isFirstRun = true;

    public static final int mNotificationId = 1080; //NOTE: Using 0 as a notification ID causes Android to ignore the notification call.

    public static final String LAUNCH_NOW_PLAYING_ACTION = "com.terry.music.player.LAUNCH_NOW_PLAYING_ACTION";
    public static final String PREVIOUS_ACTION = "com.terry.music.player.PREVIOUS_ACTION";
    public static final String PLAY_PAUSE_ACTION = "com.terry.music.player.PLAY_PAUSE_ACTION";
    public static final String NEXT_ACTION = "com.terry.music.player.NEXT_ACTION";
    public static final String STOP_SERVICE = "com.terry.music.player.STOP_SERVICE";

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        mApp = (JetApplication) getApplicationContext();
        mApp.setService(this);
        songHelper = mApp.getSongHelper();

        initMsgBroadCast();
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
        initPlayerListener();

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        mPlayer = new MusicPlayer();
        mPlayer.reset();

        //PARTIAL_WAKE_LOCK:保持CPU 运转，屏幕和键盘灯有可能是关闭的。
        mPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(mediaPlayerPrepared);
        mPlayer.setOnCompletionListener(mediaPlayerCompleted);
    }

    private void initPlayerListener() {

        mediaPlayerPrepared = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                if (songHelper.getLastPosition() >= 0) {
                    mPlayer.seekTo(songHelper.getLastPosition());
                }

                Intent intent = new Intent(SystemConst.ACTION_MUSIC_SERVICE_INFO);
                intent.putExtra(SystemConst.EXTRA_KEY_PLAYER_INFO, SystemConst.INFO_PLAYER_PLAYING);
                localBroadcastManager.sendBroadcast(intent);

                if (isFirstRun) {
                    mPlayer.start();
                    isFirstRun = false;
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
            }
        };

        mediaPlayerCompleted = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        };

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

    public MusicPlayer getMusicPlayer() {
        return mPlayer;
    }

    /**
     * 播放某一首歌.
     *
     * @param song 指定播放的歌曲.
     */
    public void playSong(Song song) {
        this.songHelper.init(song);
        preparePlayer(this.songHelper.get());
    }

    private void preparePlayer(Song song) {
        if (mPlayer != null) {
            mPlayer.reset();
        }
        try {
            Uri songUri = Uri.parse("file://" + song.getUri());
            mPlayer.setDataSource(this, songUri);
            mPlayer.prepareAsync();

            if (isFirstRun) {
                //Set this service as a foreground service.
                startForeground(mNotificationId, buildNotification());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playerPause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();

            //region send pause broadcast
            Intent intent = new Intent(SystemConst.ACTION_MUSIC_SERVICE_INFO);
            intent.putExtra(SystemConst.EXTRA_KEY_PLAYER_INFO, SystemConst.INFO_PLAYER_PAUSE);
            localBroadcastManager.sendBroadcast(intent);
            //endregion

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
        preparePlayer(songHelper.getLastSong());
    }

    private boolean isOnlySongInQueue() {
        return false;
    }

    private boolean isFirstSongInQueue() {
        return false;
    }

    private boolean isLastSongInQueue() {
        return false;
    }

    private Notification buildNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return buildJBNotification();
        } else {
            return buildICSNotification();
        }
    }

    /**
     * version code >= 16
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    //@SuppressLint("NewApi")
    private Notification buildJBNotification() {
        mNotificationBuilder = new NotificationCompat.Builder(mApp);
        mNotificationBuilder.setOngoing(true); //设置为一个正在进行的通知。通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
        mNotificationBuilder.setAutoCancel(false);
        mNotificationBuilder.setSmallIcon(R.mipmap.ic_launcher);

        //Open up the player screen when the user taps on the notification.
        Intent launchNowPlayingIntent = new Intent();
        launchNowPlayingIntent.setAction(AudioPlayService.LAUNCH_NOW_PLAYING_ACTION);
        PendingIntent launchNowPlayingPendingIntent = PendingIntent.getBroadcast(mApp, 0, launchNowPlayingIntent, 0);
        mNotificationBuilder.setContentIntent(launchNowPlayingPendingIntent);

        //Grab the notification layouts.
        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.notification_custom_layout);
        RemoteViews expNotificationView = new RemoteViews(getPackageName(), R.layout.notification_custom_expanded_layout);

        //Initialize the notification layout buttons.
        Intent previousTrackIntent = new Intent();
        previousTrackIntent.setAction(AudioPlayService.PREVIOUS_ACTION);
        PendingIntent previousTrackPendingIntent = PendingIntent.getBroadcast(mApp, 0, previousTrackIntent, 0);

        Intent playPauseTrackIntent = new Intent();
        playPauseTrackIntent.setAction(AudioPlayService.PLAY_PAUSE_ACTION);
        PendingIntent playPauseTrackPendingIntent = PendingIntent.getBroadcast(mApp, 0, playPauseTrackIntent, 0);

        Intent nextTrackIntent = new Intent();
        nextTrackIntent.setAction(AudioPlayService.NEXT_ACTION);
        PendingIntent nextTrackPendingIntent = PendingIntent.getBroadcast(mApp, 0, nextTrackIntent, 0);

        Intent stopServiceIntent = new Intent();
        stopServiceIntent.setAction(AudioPlayService.STOP_SERVICE);
        PendingIntent stopServicePendingIntent = PendingIntent.getBroadcast(mApp, 0, stopServiceIntent, 0);

        //Check if audio is playing and set the appropriate play/pause button.
        if (mApp.getService().getMusicPlayer().isPlaying()) {
            notificationView.setImageViewResource(R.id.notification_base_play, R.mipmap.ic_pause);
            expNotificationView.setImageViewResource(R.id.notification_expanded_base_play, R.mipmap.ic_pause);
        } else {
            notificationView.setImageViewResource(R.id.notification_base_play, R.mipmap.ic_play);
            expNotificationView.setImageViewResource(R.id.notification_expanded_base_play, R.mipmap.ic_play);
        }

        //Set the notification content. (设置新标题栏的文字)
        expNotificationView.setTextViewText(R.id.notification_expanded_base_line_one, songHelper.get().getFileName());
        expNotificationView.setTextViewText(R.id.notification_expanded_base_line_two, songHelper.get().getFileName());
        expNotificationView.setTextViewText(R.id.notification_expanded_base_line_three, songHelper.get().getFileName());

        //Set the notification content. (设置标题栏的第一行和第二行文字)
        notificationView.setTextViewText(R.id.notification_base_line_one, songHelper.get().getFileName());
        notificationView.setTextViewText(R.id.notification_base_line_two, songHelper.get().getUri());

        //根据不同情况设置不同按钮的图标、可见度、点击事件.
        if (isOnlySongInQueue()) {

            expNotificationView.setViewVisibility(R.id.notification_expanded_base_next, View.INVISIBLE);
            expNotificationView.setViewVisibility(R.id.notification_expanded_base_previous, View.INVISIBLE);
            expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_play, playPauseTrackPendingIntent);

            notificationView.setViewVisibility(R.id.notification_base_next, View.INVISIBLE);
            notificationView.setViewVisibility(R.id.notification_base_previous, View.INVISIBLE);
            notificationView.setOnClickPendingIntent(R.id.notification_base_play, playPauseTrackPendingIntent);
        } else if (isFirstSongInQueue()) {

            expNotificationView.setViewVisibility(R.id.notification_expanded_base_previous, View.INVISIBLE);
            expNotificationView.setViewVisibility(R.id.notification_expanded_base_next, View.VISIBLE);
            expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_play, playPauseTrackPendingIntent);
            expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_next, nextTrackPendingIntent);

            notificationView.setViewVisibility(R.id.notification_base_previous, View.INVISIBLE);
            notificationView.setViewVisibility(R.id.notification_base_next, View.VISIBLE);
            notificationView.setOnClickPendingIntent(R.id.notification_base_play, playPauseTrackPendingIntent);
            notificationView.setOnClickPendingIntent(R.id.notification_base_next, nextTrackPendingIntent);
        } else if (isLastSongInQueue()) {

            expNotificationView.setViewVisibility(R.id.notification_expanded_base_previous, View.VISIBLE);
            expNotificationView.setViewVisibility(R.id.notification_expanded_base_next, View.INVISIBLE);
            expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_play, playPauseTrackPendingIntent);
            expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_previous, previousTrackPendingIntent);

            notificationView.setViewVisibility(R.id.notification_base_previous, View.VISIBLE);
            notificationView.setViewVisibility(R.id.notification_base_next, View.INVISIBLE);
            notificationView.setOnClickPendingIntent(R.id.notification_base_play, playPauseTrackPendingIntent);
            notificationView.setOnClickPendingIntent(R.id.notification_base_previous, previousTrackPendingIntent);
        } else {

            expNotificationView.setViewVisibility(R.id.notification_expanded_base_previous, View.VISIBLE);
            expNotificationView.setViewVisibility(R.id.notification_expanded_base_next, View.VISIBLE);
            expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_play, playPauseTrackPendingIntent);
            expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_next, nextTrackPendingIntent);
            expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_previous, previousTrackPendingIntent);

            notificationView.setViewVisibility(R.id.notification_base_previous, View.VISIBLE);
            notificationView.setViewVisibility(R.id.notification_base_next, View.VISIBLE);
            notificationView.setOnClickPendingIntent(R.id.notification_base_play, playPauseTrackPendingIntent);
            notificationView.setOnClickPendingIntent(R.id.notification_base_next, nextTrackPendingIntent);
            notificationView.setOnClickPendingIntent(R.id.notification_base_previous, previousTrackPendingIntent);
        }

        //Set the "Stop service" pending intents.
        expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_collapse, stopServicePendingIntent);
        notificationView.setOnClickPendingIntent(R.id.notification_base_collapse, stopServicePendingIntent);


        //Attach the shrunken layout to the notification
        mNotificationBuilder.setContent(notificationView);

        //Build the notification object.
        Notification notification = mNotificationBuilder.build();

        //Attach the expanded layout th the notification and set its flags.
        notification.bigContentView = expNotificationView;
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE | Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        return notification;
    }

    /**
     * Builds and returns a fully constructed Notification for devices
     * on Ice Cream Sandwich (APIs 14 & 15).
     */
    private Notification buildICSNotification() {
        mNotificationBuilder = new NotificationCompat.Builder(mApp);
        mNotificationBuilder.setOngoing(true); //设置为一个正在进行的通知。通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
        mNotificationBuilder.setAutoCancel(false);
        mNotificationBuilder.setSmallIcon(R.mipmap.ic_launcher);

        //Open up the player screen when the user taps on the notification.
        Intent launchNowPlayingIntent = new Intent();
        launchNowPlayingIntent.setAction(AudioPlayService.LAUNCH_NOW_PLAYING_ACTION);
        PendingIntent launchNowPlayingPendingIntent = PendingIntent.getBroadcast(mApp, 0, launchNowPlayingIntent, 0);
        mNotificationBuilder.setContentIntent(launchNowPlayingPendingIntent);

        //Grab the notification layout
        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.notification_custom_layout);

        //Initialize the notification layout buttons.
        Intent previousTrackIntent = new Intent();
        previousTrackIntent.setAction(AudioPlayService.PREVIOUS_ACTION);
        PendingIntent previousTrackPendingIntent = PendingIntent.getBroadcast(mApp, 0, previousTrackIntent, 0);

        Intent playPauseTrackIntent = new Intent();
        playPauseTrackIntent.setAction(AudioPlayService.PLAY_PAUSE_ACTION);
        PendingIntent playPauseTrackPendingIntent = PendingIntent.getBroadcast(mApp, 0, playPauseTrackIntent, 0);

        Intent nextTrackIntent = new Intent();
        nextTrackIntent.setAction(AudioPlayService.NEXT_ACTION);
        PendingIntent nextTrackPendingIntent = PendingIntent.getBroadcast(mApp, 0, nextTrackIntent, 0);

        Intent stopServiceIntent = new Intent();
        stopServiceIntent.setAction(AudioPlayService.STOP_SERVICE);
        PendingIntent stopServicePendingIntent = PendingIntent.getBroadcast(mApp, 0, stopServiceIntent, 0);

        //Check if audio is playing and set the appropriate play/pause button.
        if (mApp.getService().getMusicPlayer().isPlaying()) {
            notificationView.setImageViewResource(R.id.notification_base_play, R.mipmap.ic_pause);
        } else {
            notificationView.setImageViewResource(R.id.notification_base_play, R.mipmap.ic_play);
        }

        //Set the notification content. (设置标题栏的第一行和第二行文字)
        notificationView.setTextViewText(R.id.notification_base_line_one, songHelper.get().getFileName());
        notificationView.setTextViewText(R.id.notification_base_line_two, songHelper.get().getUri());

        //根据不同情况设置不同按钮的图标、可见度、点击事件.
        if (isOnlySongInQueue()) {
            notificationView.setViewVisibility(R.id.notification_base_next, View.INVISIBLE);
            notificationView.setViewVisibility(R.id.notification_base_previous, View.INVISIBLE);
            notificationView.setOnClickPendingIntent(R.id.notification_base_play, playPauseTrackPendingIntent);
        } else if (isFirstSongInQueue()) {
            notificationView.setViewVisibility(R.id.notification_base_previous, View.INVISIBLE);
            notificationView.setViewVisibility(R.id.notification_base_next, View.VISIBLE);
            notificationView.setOnClickPendingIntent(R.id.notification_base_play, playPauseTrackPendingIntent);
            notificationView.setOnClickPendingIntent(R.id.notification_base_next, nextTrackPendingIntent);
        } else if (isLastSongInQueue()) {
            notificationView.setViewVisibility(R.id.notification_base_previous, View.VISIBLE);
            notificationView.setViewVisibility(R.id.notification_base_next, View.INVISIBLE);
            notificationView.setOnClickPendingIntent(R.id.notification_base_play, playPauseTrackPendingIntent);
            notificationView.setOnClickPendingIntent(R.id.notification_base_previous, previousTrackPendingIntent);
        } else {
            notificationView.setViewVisibility(R.id.notification_base_previous, View.VISIBLE);
            notificationView.setViewVisibility(R.id.notification_base_next, View.VISIBLE);
            notificationView.setOnClickPendingIntent(R.id.notification_base_play, playPauseTrackPendingIntent);
            notificationView.setOnClickPendingIntent(R.id.notification_base_next, nextTrackPendingIntent);
            notificationView.setOnClickPendingIntent(R.id.notification_base_previous, previousTrackPendingIntent);
        }

        //Set the "Stop Service" pending intent.
        notificationView.setOnClickPendingIntent(R.id.notification_base_collapse, stopServicePendingIntent);

        //Attach the shrunken layout to the notification.
        mNotificationBuilder.setContent(notificationView);

        //Build the notification object and set its flags
        Notification notification = mNotificationBuilder.build();
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE | Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        return notification;
    }

    public class MyBinder extends Binder {
        public AudioPlayService getService() {
            return AudioPlayService.this;
        }
    }

}
