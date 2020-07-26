package com.work.mediaplayapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.IllegalFormatCodePointException;

public class MainActivity extends AppCompatActivity {

    private Button mBtnPlayPause;
    private Button mBtnStop;
    private MediaPlayerHelp mMediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mBtnPlayPause = findViewById(R.id.btn_play_pause);
        mBtnStop = findViewById(R.id.btn_stop);
    }

    private void initData() {
        mMediaPlayer = MediaPlayerHelp.getInstance().setContext(this);
        mMediaPlayer.prepare(R.raw.xiangqinxiangai);
    }

    private void initEvent() {
        mBtnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = mMediaPlayer.getPlayHelpStatus();
                if (status == MediaPlayerHelp.ON_PLAYING) {
                    mMediaPlayer.pause();
                    mBtnPlayPause.setText("播放");
                } else if (status == MediaPlayerHelp.ON_STOP) {
                    mBtnPlayPause.setText("暂停");
                    mMediaPlayer.prepare(R.raw.xiangqinxiangai, true);
                } else if (mMediaPlayer.isCanPlay()) {
                    mMediaPlayer.start();
                    mBtnPlayPause.setText("暂停");
                }
            }
        });
        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.stop();
                mBtnPlayPause.setText("播放");
            }
        });
        mMediaPlayer.setOnMediaPlayerCallback(new MediaPlayerHelp.OnMediaPlayerCallback() {
            @Override
            public void onPrepare(@NonNull MediaPlayer mediaPlayer) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.destroy();
    }
}