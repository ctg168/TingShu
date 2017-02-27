package com.terry.tingshu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.terry.tingshu.R;
import com.terry.tingshu.core.JetApplication;
import com.terry.tingshu.helpers.jetdict.JetDict;

import java.io.File;

/**
 * Created by terry on 2017/1/11.
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

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private JetDict<String, File> mFileDict;
    private Context mContext;
    private JetApplication mApp;

    public FolderAdapter(Context context, JetDict<String, File> fileDict) {
        this.mContext = context;
        this.mApp = (JetApplication) context.getApplicationContext();
        this.mFileDict = fileDict;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_folder_item, null);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.tvFolderPath.setText(mFileDict.keys().get(position).replace(mApp.getRootMusicFolder().getAbsolutePath(), ""));
        holder.tvFileCount.setText(String.valueOf(mFileDict.getValues(mFileDict.keys().get(position)).size()));

        if (mOnItemClickListener != null) {
            //点击
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            });

            //长按
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mFileDict.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvFolderPath;
        public TextView tvFileCount;

        public ViewHolder(View itemView) {
            super(itemView);
            tvFolderPath = (TextView) itemView.findViewById(R.id.folderPath);
            tvFileCount = (TextView) itemView.findViewById(R.id.tv_file_count);
        }
    }

}

