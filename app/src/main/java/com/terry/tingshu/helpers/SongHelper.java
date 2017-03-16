package com.terry.tingshu.helpers;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.terry.tingshu.entity.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SongHelper {

    /**
     * Resolve a local song from a mp3 file.
     *
     * @param file
     * @return
     */
    @Nullable
    public static Song resolveLocalSongFile(File file) {
        if (file.isFile() && file.getName().endsWith(".mp3")) {
            Song song = new Song();
            song.setCached(false);
            song.setFilePath(file.getPath());
            song.setPlayUri(Uri.parse("file://" + file.getPath()));
            song.setDownloaded(true);
            song.setSize(file.length());
            return song;
        }
        return null;
    }

    /**
     * Get all songs from a folder.
     *
     * @param path 指定的路径
     * @return 歌曲列表
     */
    public static List<Song> getSongsInFolder(String path) {
        List<Song> songList = new ArrayList<>();
        File folder = new File(path);
        File[] files = folder.listFiles();

        for (File file : files) {
            Song song = resolveLocalSongFile(file);
            if (song != null)
                songList.add(song);
        }
        return songList;
    }
}
