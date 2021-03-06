package com.terry.tingshu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.terry.tingshu.core.PlayerActivityBase;

import java.io.IOException;

public class MainActivity extends PlayerActivityBase {

    private HttpServer httpServer= null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.my_toolbar);
        mToolBar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolBar);
        mToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        httpServer = new HttpServer(8009);
        try {
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(httpServer!=null)
            httpServer.stop();
    }

    @Override
    protected void onPostServiceBind() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.menu_play).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_circle_o_notch)
                        .color(Color.WHITE)
                        .actionBarSize());
        return true;
    }

}
