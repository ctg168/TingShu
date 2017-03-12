package com.terry.tingshu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.terry.tingshu.core.FragmentBase;

import java.util.Formatter;
import java.util.Locale;

public class PlayerControllerFragment extends FragmentBase implements View.OnClickListener {

    //region controls define
    TextView tvCurrentPosition;
    TextView tvDuration;
    ProgressBar progressBar;

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

    LocalBroadcastManager localBroadcastManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_controller, container, false);

        //region controls init
        tvCurrentPosition = (TextView) view.findViewById(R.id.tv_current_position);
        tvDuration = (TextView) view.findViewById(R.id.tv_duration);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

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


        //endregion

        audioPlayService = mApp.getService();
        musicPlayer = audioPlayService.getMusicPlayer();

        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

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
                if (intent.getAction() != null && intent.getAction().equals(SystemConst.ACTION_MUSIC_SEVICE_INFO)) {
                    int control = intent.getIntExtra(SystemConst.EXTRA_KEY_PLAYER_INFO, -1);
                    if (control >= 0) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               // showPlayInfo();
                            }
                        });

                        // ============== 广播订阅 ===============
                        //播放服务发布的信息有：
                        //当前播放的歌曲，位置.
                        //当前的播放状态：播放，停止，暂停
//                        switch (control) {
//                            case SystemConst.INFO_PLAYER_PLAYING:
//
//                                break;
//                            case SystemConst.INFO_PLAYER_PAUSE:
//                                showPlayInfo();
//                                break;
//                        }
                    }
                }

            }
        }, new IntentFilter(SystemConst.ACTION_MUSIC_SEVICE_INFO));
    }

    private void showPlayInfo() {
        int duration = audioPlayService.getMusicPlayer().getDuration();
        tvDuration.setText(stringForTime(duration));
        progressBar.setMax(audioPlayService.getMusicPlayer().getDuration());
        if (audioPlayService.getMusicPlayer().isPlaying()) {
            btnPlayerPlay.setText(getString(R.string.player_pause));
            while (audioPlayService.getMusicPlayer().isPlaying()) {
                try {
                    int currentPos = audioPlayService.getMusicPlayer().getCurrentPosition();
                    tvCurrentPosition.setText(stringForTime(currentPos));
                    progressBar.setProgress(currentPos);

                    Thread.sleep(1000);

                    System.out.println("PlayerControllerFragment.showPlayInfo: " + currentPos + "/" + duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            btnPlayerPlay.setText(getString(R.string.player_play));
        }
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(SystemConst.ACTION_PLAYER_CONTROLL);
        switch (v.getId()) {
            case R.id.btn_player_play:
                if (audioPlayService.getMusicPlayer().isPlaying()) {
                    intent.putExtra(SystemConst.EXTRA_KEY_PLAYER_CONTROLL, SystemConst.PLAYER_PAUSE);
                } else {
                    intent.putExtra(SystemConst.EXTRA_KEY_PLAYER_CONTROLL, SystemConst.PLAYER_PLAY);
                }
                break;
            case R.id.btn_player_previous:
                intent.putExtra(SystemConst.EXTRA_KEY_PLAYER_CONTROLL, SystemConst.PLAYER_PREVIOUS);
                break;
            case R.id.btn_player_next:
                intent.putExtra(SystemConst.EXTRA_KEY_PLAYER_CONTROLL, SystemConst.PLAYER_NEXT);
                break;
            case R.id.btn_player_list:
                showPlayList();
                break;
            case R.id.btn_player_auto_stop:
                showAutoStopDialog();
                break;
        }
        localBroadcastManager.sendBroadcastSync(intent);
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


}
