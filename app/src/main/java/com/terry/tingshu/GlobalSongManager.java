package com.terry.tingshu;

import com.terry.tingshu.core.JetApplication;
import com.terry.tingshu.entity.Song;
import com.terry.tingshu.helpers.SongHelper;

import java.util.List;



public class GlobalSongManager {

    private int mCurrentIndex = -1;

    List<Song> songList;

    private JetApplication mApp;

    public GlobalSongManager(JetApplication mContext) {
        this.mApp = mContext;
    }


//    public void init(String filePath) {
//        File file = new File(filePath);
//        songList = getSongsInFolder(file.getParent());
//        if (songList != null && songList.size() > 0)
//            mCurrentIndex = 0;
//    }

    public List<Song> getSongList() {
        return this.songList;
    }

    public Song get() {
        if (songList != null && songList.size() > mCurrentIndex) {
            Song song = songList.get(mCurrentIndex);
            mApp.getSharedPreferences().edit().putString(SystemConst.KEY_LAST_SONG_URL, song.getFilePath()).apply();
            return song;
        }
        return null;
    }

    public Song getLastSong() {

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

    /**
     * 加载一个文件夹下面的所有歌曲成为当前的播放列表.
     *
     * @param folderPath 指定文件夹.
     */
    public void loadSongListByPath(String folderPath) {
        songList = SongHelper.getSongsInFolder(folderPath);
        if (songList != null && songList.size() > 0)
            mCurrentIndex = 0;
    }


}
