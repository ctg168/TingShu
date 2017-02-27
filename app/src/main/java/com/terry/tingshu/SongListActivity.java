package com.terry.tingshu;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.terry.tingshu.core.PlayerActivityBase;
import com.terry.tingshu.adapter.SongAdapter;

public class SongListActivity extends PlayerActivityBase {
    TextView tvFolderPath;

    RecyclerView playListView;
    String mCurrentPlayFolder;


    SongAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        mCurrentPlayFolder = getIntent().getStringExtra("path");
        init();
    }

    @Override
    protected void onPostServiceBind() {
        mSongHelper.loadPlayList(mCurrentPlayFolder);
        fillPlayList();
    }

    private void init() {
        playListView = (RecyclerView) findViewById(R.id.songListRecyclerView);
        tvFolderPath = (TextView) findViewById(R.id.tv_folder_path);

        tvFolderPath.setText(mCurrentPlayFolder);

    }

    private void fillPlayList(){

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getBaseContext(), LinearLayoutManager.VERTICAL, false);
        playListView.setLayoutManager(layoutManager);
        playListView.setHasFixedSize(false);

        adapter = new SongAdapter(mSongHelper.getSongList());
        adapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                playService.playSong(mSongHelper.getSongList().get(position));
            }
        });
        playListView.setAdapter(adapter);
    }
}
