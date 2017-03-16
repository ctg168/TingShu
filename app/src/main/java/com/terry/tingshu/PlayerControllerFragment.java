package com.terry.tingshu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.terry.tingshu.core.FragmentBase;

import java.util.Formatter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerControllerFragment extends FragmentBase implements View.OnClickListener {

    //region controls define
    TextView tvCurrentPosition;
    TextView tvDuration;
    SeekBar progressBar;

    IconTextView btnPlayerPrevious;
    IconTextView btnPlayerPlay;
    IconTextView btnPlayerNext;
    IconTextView btnPlayerList;
    IconTextView btnPlayerAutoStop;
    //endregion

    AudioPlayService audioPlayService;
    MusicPlayer musicPlayer;

    StringBuilder mFormatBuilder;
    Formatter mFormatter;

    Timer timer;
    MyHandler myHandler;

    private LocalBroadcastManager localBroadcastManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_controller, container, false);

        //region controls init
        tvCurrentPosition = (TextView) view.findViewById(R.id.tv_current_position);
        tvDuration = (TextView) view.findViewById(R.id.tv_duration);
        progressBar = (SeekBar) view.findViewById(R.id.progress);

        btnPlayerPrevious = (IconTextView) view.findViewById(R.id.btn_player_previous);
        btnPlayerPlay = (IconTextView) view.findViewById(R.id.btn_player_play);
        btnPlayerNext = (IconTextView) view.findViewById(R.id.btn_player_next);
        btnPlayerList = (IconTextView) view.findViewById(R.id.btn_player_list);
        btnPlayerAutoStop = (IconTextView) view.findViewById(R.id.btn_player_auto_stop);

        btnPlayerPrevious.setOnClickListener(this);
        btnPlayerPlay.setOnClickListener(this);
        btnPlayerNext.setOnClickListener(this);
        btnPlayerList.setOnClickListener(this);
        btnPlayerAutoStop.setOnClickListener(this);

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    System.out.println("PlayerControllerFragment.onProgressChanged:" + progress + "/" + seekBar.getMax());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                System.out.println("PlayerControllerFragment.onStartTrackingTouch");
                if (audioPlayService.getMusicPlayer().isPlaying()) {
                    audioPlayService.getMusicPlayer().pause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                System.out.println("PlayerControllerFragment.onStopTrackingTouch");
                if (!audioPlayService.getMusicPlayer().isPlaying()) {
                    audioPlayService.getMusicPlayer().start();
                    audioPlayService.getMusicPlayer().seekTo(progressBar.getProgress());

                }
            }
        });


        //endregion

        audioPlayService = mApp.getService();
        musicPlayer = audioPlayService.getMusicPlayer();

        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        myHandler = new MyHandler();
        init();
        return view;
    }

    private void init() {
        initMsgBroadCast();
    }

    private void initMsgBroadCast() {
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        localBroadcastManager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null && intent.getAction().equals(SystemConst.ACTION_MUSIC_SERVICE_INFO)) {
                    int control = intent.getIntExtra(SystemConst.EXTRA_KEY_PLAYER_INFO, -1);
                    if (control == SystemConst.INFO_PLAYER_PLAYING || control == SystemConst.INFO_PLAYER_PAUSE) { //如果是播放或暂停的消息.
                        int duration = audioPlayService.getMusicPlayer().getDuration();
                        tvDuration.setText(stringForTime(duration));
                        progressBar.setMax(audioPlayService.getMusicPlayer().getDuration());

                        if (audioPlayService.getMusicPlayer().isPlaying()) {
                            btnPlayerPlay.setText(getString(R.string.player_pause));

                            timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    myHandler.sendEmptyMessage(0);
                                }
                            }, 0, 500);

                        } else {
                            btnPlayerPlay.setText(getString(R.string.player_play));
                            if (timer != null)
                                timer.purge();
                        }
                    }
                }

            }
        }, new IntentFilter(SystemConst.ACTION_MUSIC_SERVICE_INFO));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_player_play:
                if (audioPlayService.getMusicPlayer().isPlaying()) {
                    mApp.sendControlBroadcast_PAUSE();
                } else {
                    mApp.sendControlBroadcast_PLAY();
                }
                break;
            case R.id.btn_player_previous:
                mApp.sendControlBroadcast_PREVIOUS();
                break;
            case R.id.btn_player_next:
                mApp.sendControlBroadcast_NEXT();
                break;
            case R.id.btn_player_list:
                showPlayList();
                break;
            case R.id.btn_player_auto_stop:
                showAutoStopDialog();
                break;
        }
    }


    private void showAutoStopDialog() {
        AutoStopDialog dialog = new AutoStopDialog();
        dialog.show(getActivity().getFragmentManager(), "auto_stop_dialog");
    }

    private void showPlayList() {

    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);

        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int currentPosition = audioPlayService.getMusicPlayer().getCurrentPosition();
            progressBar.setProgress(currentPosition);
            tvCurrentPosition.setText(stringForTime(currentPosition));
        }
    }

}
