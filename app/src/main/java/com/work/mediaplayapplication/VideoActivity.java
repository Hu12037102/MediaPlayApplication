package com.work.mediaplayapplication;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.media.MediaPlayerHelper;

/**
 * 作者: 胡庆岭
 * 创建时间: 2020/8/14 10:14
 * 更新时间: 2020/8/14 10:14
 * 描述:视频播放器
 */
public class VideoActivity extends AppCompatActivity {
    private SurfaceView mSvVideo;
    private Button mBtnVideoPlay;
    private Button mBtnVideoStop;
    private MediaPlayerHelper mMediaPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mSvVideo = findViewById(R.id.sv_video);
        mBtnVideoPlay = findViewById(R.id.btn_video_play);
        mBtnVideoStop = findViewById(R.id.btn_video_stop);
    }

    private void initData() {
        mMediaPlayer = MediaPlayerHelper.getInstance(this);
        mMediaPlayer.setVideoSize(getResources().getDisplayMetrics().widthPixels, dp2px(this, 250));
        mMediaPlayer.prepare("http://cdn.sbnh.cn/VID_20200714_190812.mp4");

    }

    private void initEvent() {
        mSvVideo.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mMediaPlayer.setSurfaceView(mSvVideo);
                Log.w("VideoActivity--", "surfaceCreated:");
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.w("VideoActivity--", "surfaceChanged:" + width + "--" + height);
                //holder.setFixedSize( mMediaPlayer.getMediaPlayer().getVideoWidth(), mMediaPlayer.getMediaPlayer().getVideoHeight());
                // mSvVideo.getHolder().setFixedSize(50,height);
                //  mSvVideo.requestLayout();

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.w("VideoActivity--", "surfaceDestroyed:");
            }
        });


        mBtnVideoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = mMediaPlayer.getPlayHelpStatus();
                if (status == MediaPlayerHelper.ON_PLAYING) {
                    mMediaPlayer.pause();
                    mBtnVideoPlay.setText("播放");
                } else if (status == MediaPlayerHelper.ON_STOP) {
                    mBtnVideoPlay.setText("暂停");
                    mMediaPlayer.prepare(R.raw.demo, true);
                } else if (mMediaPlayer.isCanPlay()) {
                    mMediaPlayer.start();
                    mBtnVideoPlay.setText("暂停");
                }
            }
        });
        mBtnVideoStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.stop();
                mBtnVideoPlay.setText("播放");
            }
        });
     /*   mMediaPlayer.getMediaPlayer().setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                Log.w("VideoActivity--", "onVideoSizeChanged:" + width + "--" + height);
                //  mSvVideo.getHolder().setFixedSize(250,100);
             *//*   if (width != 0 && height != 0) {
                    float ratioW = (float) width / (float) dp2px(VideoActivity.this, 50);
                    float ratioH = (float) height / (float) dp2px(VideoActivity.this, 250);
                    float ratio = Math.max(ratioW, ratioH);
                    width = (int) Math.ceil((float) width / ratio);
                    height = (int) Math.ceil((float) height / ratio);
                    ConstraintLayout.LayoutParams layout = (ConstraintLayout.LayoutParams) mSvVideo.getLayoutParams();
                    layout.width = width;
                    layout.height = height;
                    mSvVideo.setLayoutParams(layout);
                }*//*
            }
        });*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.destroy();
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
