package com.terry.tingshu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.terry.tingshu.core.FragmentBase;

/**
 * Created by terry on 2017/2/25.
 */

public class PlayerControllerFragment extends FragmentBase {

    TextView tvCurrentPosition;
    TextView tvDuration;
    ProgressBar progressBar;

    IconTextView btnPlayerPrevious;
    IconTextView btnPlayerPlay;
    IconTextView btnPlayerNext;
    IconTextView btnPlayerList;
    IconTextView btnPlayerAutostop;


    AudioPlayService audioPlayService;


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
        btnPlayerAutostop = (IconTextView) view.findViewById(R.id.btn_player_auto_stop);

        audioPlayService = mApp.getService();

        init();
        return view;
    }

    private void init() {
        if (audioPlayService.isPlaying()) {
            btnPlayerPlay.setText("{fa-pause-circle-o 40sp}");
        } else {
            btnPlayerPlay.setText("{fa-play-circle-o 40sp}");
        }

    }
}
