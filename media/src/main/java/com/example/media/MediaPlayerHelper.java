package com.example.media;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;

/**
 * 作者: 胡庆岭
 * 创建时间: 2020/7/25 22:36
 * 更新时间: 2020/7/25 22:36
 * 描述:
 */
public class MediaPlayerHelper implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnVideoSizeChangedListener {
    @SuppressLint("StaticFieldLeak")
    private static MediaPlayerHelper mInstance = new MediaPlayerHelper();
    private MediaPlayer mMediaPlayer;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    //没准备（没初始化）
    public static final int ON_CREATE = 0;
    //初始化完成
    public static final int ON_CREATED = 1;
    //播放中
    public static final int ON_PLAYING = 2;
    //暂停
    public static final int ON_PAUSE = 3;
    //停止
    public static final int ON_STOP = 4;
    //销毁
    public static final int ON_DESTROY = 5;
    //默认的，Music,外音
    public static final int MODE_DEFAULT = 0;
    //感应器
    public static final int MODE_SENSE = 1;
    //听筒
    public static final int MODE_HANDSET = 2;
    private int mPlayHelpStatus;

    public int getPlayHelpStatus() {
        return mPlayHelpStatus;
    }

    private int mPlayerMode;
    private boolean mIsPreparePlay;
    private SurfaceView mSurfaceView;
    private int mVideoWidth ;
    private int mVideoHeight ;

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public void setOnMediaPlayerCallback(OnMediaPlayerCallback onMediaPlayerCallback) {
        this.onMediaPlayerCallback = onMediaPlayerCallback;
    }

    //
    public OnMediaPlayerCallback onMediaPlayerCallback;

    private MediaPlayerHelper() {

    }

    public synchronized static MediaPlayerHelper getInstance(Context context) {
        mContext = context.getApplicationContext();
        return mInstance;
    }

    private void init() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        } else {
            mMediaPlayer.reset();
        }
        mPlayHelpStatus = MediaPlayerHelper.ON_CREATE;

    }


    private void initMediaPlayEvent() {
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
    }

    public void setSurfaceView(@NonNull SurfaceView surfaceView) {
        this.mSurfaceView = surfaceView;
        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(surfaceView.getHolder());
        }
    }

    public void prepare(@NonNull Object o, boolean isPreparePlay) {
        this.mIsPreparePlay = isPreparePlay;
        prepare(o);
    }

    public void setVideoSize(int width, int height) {
        this.mVideoWidth = width;
        this.mVideoHeight = height;
    }

    public void prepare(@NonNull Object o) {
        init();
        try {
            if (o instanceof String) {
                String url = (String) o;
                mMediaPlayer.setDataSource(url);
            } else if (o instanceof File) {
                File file = (File) o;
                mMediaPlayer.setDataSource(file.getAbsolutePath());
            } else if (o instanceof Uri) {
                Uri uri = (Uri) o;
                mMediaPlayer.setDataSource(uri.toString());
            } else if (o instanceof Integer) {
                if (mContext == null) {
                    return;
                }
                int resId = (int) o;
                AssetFileDescriptor afd = mContext.getResources().openRawResourceFd(resId);
                if (afd == null) {
                    return;
                }
                final AudioAttributes aa;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    aa = new AudioAttributes.Builder().build();
                    mMediaPlayer.setAudioAttributes(aa);
                }
                mMediaPlayer.setAudioSessionId(0);
                mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
            }
            mMediaPlayer.prepareAsync();
            //   mMediaPlayer.prepare();
            initMediaPlayEvent();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public boolean isCanPlay() {
        return mPlayHelpStatus == MediaPlayerHelper.ON_PAUSE || mPlayHelpStatus == MediaPlayerHelper.ON_CREATED;
    }

    public long getDuration() {
        if (mMediaPlayer != null) {
            mMediaPlayer.getDuration();
        }
        return 0;
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mPlayHelpStatus = MediaPlayerHelper.ON_STOP;
        }
        Log.w("MediaPlayerHelp", "stop:" + "---" + mPlayHelpStatus);
    }

    public void destroy() {
        if (mMediaPlayer != null) {
            stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mPlayHelpStatus = MediaPlayerHelper.ON_DESTROY;
        }
        Log.w("MediaPlayerHelp", "destroy:" + "---" + mPlayHelpStatus);
    }

    public void pause() {
        mPlayHelpStatus = MediaPlayerHelper.ON_PAUSE;
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        Log.w("MediaPlayerHelp", "pause:" + "---" + mPlayHelpStatus + "--" + mMediaPlayer.isPlaying());
    }

    public void setSeek(int duration) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(duration);
        }
    }

    public void start() {
        mPlayHelpStatus = MediaPlayerHelper.ON_PLAYING;
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
        Log.w("MediaPlayerHelp", "start:" + "---" + mPlayHelpStatus + "---" + mMediaPlayer.isPlaying());
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        mPlayHelpStatus = MediaPlayerHelper.ON_CREATED;
        mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        if (onMediaPlayerCallback != null) {
            onMediaPlayerCallback.onPrepare(mMediaPlayer);
        }
        if (mIsPreparePlay) {
            start();
        }
        Log.w("MediaPlayerHelp", "onPrepared:" + mMediaPlayer.isLooping() + "---" + mPlayHelpStatus);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mPlayHelpStatus = MediaPlayerHelper.ON_PAUSE;
        mMediaPlayer.start();
        Log.w("MediaPlayerHelp", "onCompletion:" + mMediaPlayer.isLooping() + "---" + mPlayHelpStatus);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
        if (width > 0 && height > 0 && mSurfaceView != null) {
            if (mVideoWidth == 0){
                mVideoWidth = mContext.getResources().getDisplayMetrics().widthPixels;
                mVideoHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            float ratioW = (float) width / mVideoWidth;
            float ratioH = (float) height / mVideoHeight;
            float ratio = Math.max(ratioW, ratioH);
            width = (int) Math.ceil((float) width / ratio);
            height = (int) Math.ceil((float) height / ratio);
            ViewGroup.LayoutParams layout = mSurfaceView.getLayoutParams();
            layout.width = width;
            layout.height = height;
            mSurfaceView.setLayoutParams(layout);
        }
    }

    public interface OnMediaPlayerCallback {
        default void onPrepare(@NonNull MediaPlayer mediaPlayer) {
        }

        default void onCompletion(@NonNull MediaPlayer mediaPlayer) {
        }

        default void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
        }
    }



}
