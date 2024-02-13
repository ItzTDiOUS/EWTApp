package com.example.ewtapp;


import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.Objects;

public class VideoPlayer extends AppCompatActivity {
    YouTubePlayerView youTubePlayerView1;
    YouTubePlayerView youTubePlayerView2;
    YouTubePlayerView youTubePlayerView3;
    YouTubePlayerView youTubePlayerView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.videoplayer);

        youTubePlayerView1 = findViewById(R.id.video1);
        youTubePlayerView2 = findViewById(R.id.video2);
        youTubePlayerView3 = findViewById(R.id.video3);
        youTubePlayerView4 = findViewById(R.id.video4);


        initializePlayer(youTubePlayerView1, "5J3cw4biWWo", false);
        initializePlayer(youTubePlayerView2, "izlrVGhr-UQ", false);
        initializePlayer(youTubePlayerView3, "hI4kbjl4je0", false);
        initializePlayer(youTubePlayerView4, "_t6sg2C-jqw", false);

    }

    private void initializePlayer(YouTubePlayerView playerView, String videoId, boolean autoPlay) {
        playerView.setEnableAutomaticInitialization(false);
        getLifecycle().addObserver(playerView);

        View customPlayerUi = playerView.inflateCustomPlayerUi(R.layout.custom_player_ui);

        YouTubePlayerListener listener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                CustomPlayerUiController customPlayerUiController = new CustomPlayerUiController(customPlayerUi, youTubePlayer, playerView);
                youTubePlayer.addListener(customPlayerUiController);

                YouTubePlayerUtils.loadOrCueVideo(youTubePlayer, getLifecycle(), videoId, 0F);
            }
        };

        IFramePlayerOptions options = new IFramePlayerOptions.Builder().controls(0).build();
        playerView.initialize(listener, options);

    }}