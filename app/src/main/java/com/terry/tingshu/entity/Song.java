package com.terry.tingshu.entity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
    private int id;
    private String filePath;
    private int duration;
    private float size;
    private boolean isDownloaded;
    private boolean isCached;

    public Uri getPlayUri() {
        return playUri;
    }

    public void setPlayUri(Uri playUri) {
        this.playUri = playUri;
    }

    private Uri playUri;

    public int getLastPlayPosition() {
        return lastPlayPosition;
    }

    public void setLastPlayPosition(int lastPlayPosition) {
        this.lastPlayPosition = lastPlayPosition;
    }

    private int lastPlayPosition;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public boolean isCached() {
        return isCached;
    }

    public void setCached(boolean cached) {
        isCached = cached;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.filePath);
        dest.writeInt(this.duration);
        dest.writeFloat(this.size);
        dest.writeByte(this.isDownloaded ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCached ? (byte) 1 : (byte) 0);
        dest.writeInt(this.lastPlayPosition);
    }

    public Song() {
    }

    protected Song(Parcel in) {
        this.id = in.readInt();
        this.filePath = in.readString();
        this.duration = in.readInt();
        this.size = in.readFloat();
        this.isDownloaded = in.readByte() != 0;
        this.isCached = in.readByte() != 0;
        this.lastPlayPosition = in.readInt();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getFileName() {
        int start = this.filePath.lastIndexOf("/");
        int end = this.filePath.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return this.filePath.substring(start + 1, end);
        } else {
            return null;
        }
    }
}
