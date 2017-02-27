package com.terry.tingshu;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.terry.tingshu.adapter.SongAdapter;
import com.terry.tingshu.core.DialogFragmentBase;
import com.terry.tingshu.helpers.SongHelper;

/**
 * Created by terry on 2017/2/23.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class SongsInFolderDialog extends DialogFragmentBase {
    TextView tvFolderPath;
    RecyclerView songsView;
    SongAdapter adapter;

    SongHelper mSongHelper;
    String folderPath;

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
        mSongHelper = new SongHelper(getActivity());
        mSongHelper.loadPlayList(folderPath);

        fillPlayList();
    }

    private void fillPlayList(){

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
        songsView.setLayoutManager(layoutManager);
        //songsView.setHasFixedSize(false);
        songsView.setNestedScrollingEnabled(false);

        adapter = new SongAdapter(mSongHelper.getSongList());
        adapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mApp.getService().playSong(mSongHelper.getSongList().get(position));
            }
        });
        songsView.setAdapter(adapter);
    }
}
