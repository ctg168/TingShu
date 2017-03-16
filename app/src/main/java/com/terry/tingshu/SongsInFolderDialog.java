package com.terry.tingshu;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.terry.tingshu.adapter.SongAdapter;
import com.terry.tingshu.core.DialogFragmentBase;
import com.terry.tingshu.entity.Song;
import com.terry.tingshu.helpers.SongHelper;

import java.util.List;

public class SongsInFolderDialog extends DialogFragmentBase {
    TextView tvFolderPath;
    RecyclerView songsView;
    SongAdapter adapter;

    String folderPath;

    List<Song> mSongList;

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_songs_in_folder;
    }

    @Override
    protected void init() {
        songsView = (RecyclerView) layoutView.findViewById(R.id.songListRecyclerView);
        tvFolderPath = (TextView) layoutView.findViewById(R.id.tv_folder_path);

        tvFolderPath.setText(folderPath);

        mSongList = SongHelper.getSongsInFolder(folderPath);

        fillPlayList();
    }

    private void fillPlayList() {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
        songsView.setLayoutManager(layoutManager);
        songsView.setNestedScrollingEnabled(false);

        adapter = new SongAdapter(mSongList);
        adapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dismiss();
                mApp.getGlobalSongManager().initByList(mSongList, position);
                mApp.sendControlBroadcast_PLAY();
            }
        });
        songsView.setAdapter(adapter);
    }
}
