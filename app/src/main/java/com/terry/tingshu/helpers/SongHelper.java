package com.terry.tingshu.helpers;

import com.terry.tingshu.SystemConst;
import com.terry.tingshu.core.JetApplication;
import com.terry.tingshu.entity.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class SongHelper {

    private int mCurrentIndex = -1;

    List<Song> songList;

    private JetApplication mApp;

    public SongHelper(JetApplication mContext) {
        this.mApp = mContext;
    }

    /**
     * 加载一个文件夹下面的所有歌曲成为当前的播放列表.
     *
     * @param folderPath 指定文件夹.
     */
    private void loadPlayList(String folderPath) {
        songList = getSongsInFolder(folderPath);
        if (songList != null && songList.size() > 0)
            mCurrentIndex = 0;
    }

    public void init(Song song) {
        File file = new File(song.getFileName());
        songList = getSongsInFolder(file.getParent());
        if (songList != null && songList.size() > 0)
            mCurrentIndex = 0;
    }

    public List<Song> getSongList() {
        return this.songList;
    }

    public Song get() {
        if (songList != null && songList.size() > mCurrentIndex) {
            Song song = songList.get(mCurrentIndex);
            mApp.getSharedPreferences().edit().putString(SystemConst.KEY_LAST_SONG_URL, song.getUri()).apply();
            return song;
        }
        return null;
    }

    public Song getLastSong() {
        String lastUrl = mApp.getSharedPreferences().getString(SystemConst.KEY_LAST_SONG_URL, "");
        if (lastUrl.length() > 0) {
            Song song = resolveSong(new File(lastUrl));
            return song;
        }
        return null;
    }

    public boolean isOnlySongInQueue() {
        return songList.size() == 1;
    }

    public boolean isFirstSongInQueue() {
        return mCurrentIndex == 0;
    }

    public boolean isLastSongInQueue() {
        return mCurrentIndex == songList.size() - 1;
    }

    public int getLastPosition() {
        return mApp.getSharedPreferences().getInt(SystemConst.KEY_LAST_SONG_POS, -1);
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

    private Song resolveSong(File file) {
        if (file.isFile() && file.getName().endsWith(".mp3")) {
            Song song = new Song();
            song.setCached(false);
            song.setUri(file.getPath());
            song.setDownloaded(true);
            song.setSize(file.length());
        }
        return null;
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

        for (File file : files) {
            songList.add(resolveSong(file));
        }
        return songList;
    }

}
