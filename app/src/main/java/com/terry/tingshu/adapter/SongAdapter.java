package com.terry.tingshu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.terry.tingshu.R;
import com.terry.tingshu.entity.Song;

import java.util.List;

/**
 * Created by terry on 2017/1/16.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private List<Song> songList;

    public SongAdapter(List<Song> songList) {
        this.songList = songList;
    }

    private String playingSong;

    public String getPlayingSong() {
        return playingSong;
    }

    public void setPlayingSong(String playingSong) {
        this.playingSong = playingSong;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View songLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_play_list_item, null);
        return new ViewHolder(songLayoutView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.tvSongName.setText(song.getFileName());
        if (song.getUri().equals(playingSong)) {
            holder.isCurrentPlaying.setVisibility(View.VISIBLE);
        } else {
            holder.isCurrentPlaying.setVisibility(View.INVISIBLE);
        }

        if (mOnItemClickListener != null) {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSongName;
        TextView tvDuration;
        TextView isCurrentPlaying;

        ViewHolder(View itemView) {
            super(itemView);
            tvSongName = (TextView) itemView.findViewById(R.id.tv_song_name);
            tvDuration = (TextView) itemView.findViewById(R.id.tv_duration);
            isCurrentPlaying = (TextView) itemView.findViewById(R.id.tv_current_position);
            isCurrentPlaying.setVisibility(View.INVISIBLE);
        }
    }
}
