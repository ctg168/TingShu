package com.terry.tingshu.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by terry on 2017/1/11.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class Song implements Parcelable {
    private int id;
    private String uri;
    private int duration;
    private float size;
    private boolean isDownloaded;
    private boolean isCached;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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
        dest.writeString(this.uri);
        dest.writeInt(this.duration);
        dest.writeFloat(this.size);
        dest.writeByte(this.isDownloaded ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCached ? (byte) 1 : (byte) 0);
    }

    public Song() {
    }

    protected Song(Parcel in) {
        this.id = in.readInt();
        this.uri = in.readString();
        this.duration = in.readInt();
        this.size = in.readFloat();
        this.isDownloaded = in.readByte() != 0;
        this.isCached = in.readByte() != 0;
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
        int start = this.uri.lastIndexOf("/");
        int end = this.uri.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return this.uri.substring(start + 1, end);
        } else {
            return null;
        }
    }
}
