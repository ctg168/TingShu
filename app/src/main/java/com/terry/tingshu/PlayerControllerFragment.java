package com.terry.tingshu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.terry.tingshu.core.FragmentBase;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by terry on 2017/2/25.
 * TingShu
 */

public class PlayerControllerFragment extends FragmentBase implements View.OnClickListener {

    TextView tvCurrentPosition;
    TextView tvDuration;
    ProgressBar progressBar;

    IconTextView btnPlayerPrevious;
    IconTextView btnPlayerPlay;
    IconTextView btnPlayerNext;
    IconTextView btnPlayerList;
    IconTextView btnPlayerAutoStop;


    AudioPlayService audioPlayService;


    StringBuilder mFormatBuilder;
    Formatter mFormatter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_controller, container, false);

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

        audioPlayService = mApp.getService();

        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        init();
        return view;
    }

    private void init() {
        if (audioPlayService.isPlaying()) {
            btnPlayerPlay.setText(getString(R.string.player_pause));
        } else {
            btnPlayerPlay.setText(getString(R.string.player_play));
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (audioPlayService.isPlaying()) {
                    tvDuration.post(new Runnable() {
                        @Override
                        public void run() {
                            tvDuration.setText(stringForTime(audioPlayService.getDuration()));
                            progressBar.setMax(audioPlayService.getDuration());
                        }
                    });

                    try {
                        Thread.sleep(1000);
                        tvCurrentPosition.post(new Runnable() {
                            @Override
                            public void run() {
                                tvCurrentPosition.setText(stringForTime(audioPlayService.getCurrentPos()));
                                progressBar.setProgress(audioPlayService.getCurrentPos());
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_player_play:
                if (audioPlayService.isPlaying()) {
                    audioPlayService.playerPause();
                    btnPlayerPlay.setText(getString(R.string.player_play));
                } else {
                    audioPlayService.playerStart();
                    btnPlayerPlay.setText(getString(R.string.player_pause));
                }
                break;
            case R.id.btn_player_previous:
                audioPlayService.playerPrevious();
                break;
            case R.id.btn_player_next:
                audioPlayService.playerNext();
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


    /**
     * 接收后台Service发出的广播
     */
    class MusicBoxReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            int current = intent.getIntExtra("",1);


        }
    }


}
