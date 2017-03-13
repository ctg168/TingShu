package com.terry.tingshu;

class SystemConst {

    static final String ACTION_PLAYER_CONTROL = "action_player_control";
    static final String ACTION_MUSIC_SERVICE_INFO = "action_music_service_info";

    static final String EXTRA_KEY_PLAYER_CONTROL = "extra_key_player_control";
    static final String EXTRA_KEY_PLAYER_INFO = "extra_key_player_info";

    static final String EXTRA_KEY_CURRENT_POSITION = "extra_key_current_position";

    //UI TO SERVICE
    static final int PLAYER_PLAY = 122;
    static final int PLAYER_PAUSE = 123;
    static final int PLAYER_PREVIOUS = 125;
    static final int PLAYER_NEXT = 124;

    //SERVICE TO UI
    static final int INFO_PLAYER_PLAYING = 222;
    static final int INFO_PLAYER_PAUSE = 223;

    static final String KEY_CURRENT_POSITION = "key_current_position";
    static final String KEY_LAST_SONG_URL = "last_song";
    static final String KEY_LAST_SONG_POS = "last_song_position";

}
