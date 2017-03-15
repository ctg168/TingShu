package com.terry.tingshu;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import com.terry.tingshu.core.JetApplication;
import com.terry.tingshu.entity.Song;
import com.terry.tingshu.helpers.SongHelper;

import java.io.File;
import java.util.List;

public class GlobalSongManager {

    private int mCurrentIndex = -1;
    List<Song> songList;
    private JetApplication mApp;

    //region single tone
    private static final Object mLock = new Object();

    private GlobalSongManager(Context mContext) {
        this.mApp = (JetApplication) mContext;
    }

    private static GlobalSongManager mInstance;

    public static GlobalSongManager getInstance(Context context) {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new GlobalSongManager(context.getApplicationContext());
            }
            return mInstance;
        }
    }
    //endregion

    public Song getLastSavedSong() {

        return null;
    }

    public Song getCurrent() {
        if (songList != null && songList.size() > mCurrentIndex) {
            Song song = songList.get(mCurrentIndex);
            mApp.getSharedPreferences().edit().putString(SystemConst.KEY_LAST_SONG_URL, song.getFilePath()).apply();
            return song;
        }
        return null;
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

    public boolean isOnlySongInQueue() {
        return songList.size() == 1;
    }

    public boolean isFirstSongInQueue() {
        return mCurrentIndex == 0;
    }

    public boolean isLastSongInQueue() {
        return mCurrentIndex == songList.size() - 1;
    }

    public void initBySong(Song song) {
        File songPath = new File(song.getFilePath());
        songList = SongHelper.getSongsInFolder(songPath.getParent());
        if (songList != null && songList.size() > 0) {
            int i = 0;
            for (Song songItem : songList) {
                if (songItem.getFilePath().equals(song.getFilePath())) {
                    mCurrentIndex = i;
                    break;
                }
                i++;
            }
        }
    }

    public void initByList(List<Song> songList, int currentSongIndex) {
        this.songList = songList;
        mCurrentIndex = currentSongIndex;
    }
}
