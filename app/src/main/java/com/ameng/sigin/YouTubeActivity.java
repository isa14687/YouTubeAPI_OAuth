package com.ameng.sigin;

import android.os.Bundle;
import android.util.Log;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

/**
 * Created by user on 2016/7/17.
 */
public class YouTubeActivity extends YouTubeBaseActivity {
    YouTubePlayerView playerView;
    final static String API_KEY = "AIzaSyD4vu1GtzhOm60LdOMqtX1wkhzdWD1e-B0";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        playerView = new YouTubePlayerView(this);
        setContentView(playerView);
        //  2016/7/18 新版API android 5.0 fix
        playerView.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            String id =getIntent().getStringExtra("id");
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.e("youtubeActivity", "init Successful");
                youTubePlayer.cuePlaylist(id);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.e("youtubeActivity", "init fail");
            }
        });
    }

}
