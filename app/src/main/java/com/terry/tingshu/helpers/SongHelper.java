package com.terry.tingshu.helpers;

import android.content.Context;

import com.terry.tingshu.entity.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2017 Terry Cheng
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

public class SongHelper {

    private int mCurrentIndex = -1;

    List<Song> songList;

    private Context mContext;

    public SongHelper(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 加载一个文件夹下面的所有歌曲成为当前的播放列表.
     *
     * @param folderPath 指定文件夹.
     */
    public void loadPlayList(String folderPath) {
        songList = getSongsInFolder(folderPath);
        if (songList != null && songList.size() > 0)
            mCurrentIndex = 0;
    }

    public List<Song> getSongList() {
        return this.songList;
    }

    public Song get() {
        if (songList != null)
            return songList.get(mCurrentIndex);
        return null;
    }

    public boolean moveFirst() {
        if (songList != null && songList.size() > 0) {
            mCurrentIndex = 0;
            return true;
        }
        return false;
    }

    public boolean moveNext() {
        if (songList != null && songList.size() > 0) {
            if (songList.size() > mCurrentIndex + 1) {
                mCurrentIndex++;
                return true;
            }
        }
        return false;
    }

    public boolean movePrevious() {
        if (songList != null && songList.size() > 0) {
            if (mCurrentIndex > 0) {
                mCurrentIndex--;
                return true;
            }
        }
        return false;
    }

    public boolean moveLast() {
        if (songList != null && songList.size() > 0) {
            mCurrentIndex = songList.size() - 1;
            return true;
        }
        return false;
    }


    /**
     * 获得一个路径下所有的歌曲.
     *
     * @param path 指定的路径
     * @return 歌曲列表
     */
    private List<Song> getSongsInFolder(String path) {
        List<Song> songList = new ArrayList<>();
        File folder = new File(path);
        File[] files = folder.listFiles();

        int i = 0;
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".mp3")) {
                Song song = new Song();
                song.setId(i++);
                song.setCached(false);
                song.setUri(file.getPath());
                song.setDownloaded(true);
                song.setSize(file.length());
                songList.add(song);
            }
        }
        return songList;
    }

}
