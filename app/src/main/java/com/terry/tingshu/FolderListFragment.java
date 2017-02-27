package com.terry.tingshu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.terry.tingshu.adapter.FolderAdapter;
import com.terry.tingshu.core.FragmentBase;
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

public class FolderListFragment extends FragmentBase {
    JetDict<String, File> fileDict = new JetDict<>();
    RecyclerView recyclerView;
    FolderAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        init();
        return view;
    }

    private void init() {
        getFileList(mApp.getRootMusicFolder().getAbsolutePath());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FolderAdapter(this.getContext(), fileDict);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new FolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Intent intent = new Intent(getContext(), SongListActivity.class);
//                intent.putExtra("path", fileDict.keys().get(position));
//                startActivity(intent);
                SongsInFolderDialog dialog = new SongsInFolderDialog();
                dialog.setFolderPath(fileDict.keys().get(position));
                dialog.show(getActivity().getFragmentManager(), "songs_in_folder");
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    public void getFileList(String strPath) {
        File file = new File(strPath);
        if (file.isDirectory()) {
            File[] subFile = file.listFiles();
            for (File path : subFile) {
                getFileList(path.getAbsolutePath());
            }
        } else {
            if (file.getName().endsWith(".mp3")) {
                fileDict.add(file.getParent(), file);
                //System.out.println("MainActivity.getFileList: " + file.getAbsolutePath() + "\\" + file.getParent());
            }
        }
    }
}
