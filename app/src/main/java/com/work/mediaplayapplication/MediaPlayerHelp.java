package com.work.mediaplayapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;

/**
 * 作者: 胡庆岭
 * 创建时间: 2020/7/25 22:36
 * 更新时间: 2020/7/25 22:36
 * 描述:
 */
public class MediaPlayerHelp implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    @SuppressLint("StaticFieldLeak")
    private static MediaPlayerHelp mInstance = new MediaPlayerHelp();
    private MediaPlayer mMediaPlayer;
    private Context mContext;
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

    public void setOnMediaPlayerCallback(OnMediaPlayerCallback onMediaPlayerCallback) {
        this.onMediaPlayerCallback = onMediaPlayerCallback;
    }

    //
    public OnMediaPlayerCallback onMediaPlayerCallback;

    private MediaPlayerHelp() {

    }

    public synchronized static MediaPlayerHelp getInstance() {
        return mInstance;
    }

    private void init() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        } else {
            mMediaPlayer.reset();
        }
        mPlayHelpStatus = MediaPlayerHelp.ON_CREATE;
    }

    public MediaPlayerHelp setContext(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
        return this;
    }

    private void initMediaPlayEvent() {
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);

    }


    public void prepare(@NonNull Object o, boolean isPreparePlay) {
        this.mIsPreparePlay = isPreparePlay;
        prepare(o);
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
    public boolean isCanPlay(){
        return mPlayHelpStatus == MediaPlayerHelp.ON_PAUSE || mPlayHelpStatus == MediaPlayerHelp.ON_CREATED;
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
            mPlayHelpStatus = MediaPlayerHelp.ON_STOP;
        }
        Log.w("MediaPlayerHelp", "stop:" + "---" + mPlayHelpStatus);
    }

    public void destroy() {
        if (mMediaPlayer != null) {
            stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mPlayHelpStatus = MediaPlayerHelp.ON_DESTROY;
        }
        Log.w("MediaPlayerHelp", "destroy:" + "---" + mPlayHelpStatus);
    }

    public void pause() {
        mPlayHelpStatus = MediaPlayerHelp.ON_PAUSE;
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        Log.w("MediaPlayerHelp", "pause:" + "---" + mPlayHelpStatus + "--" + mMediaPlayer.isPlaying());
    }

    public void start() {
        mPlayHelpStatus = MediaPlayerHelp.ON_PLAYING;
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
        Log.w("MediaPlayerHelp", "start:" + "---" + mPlayHelpStatus + "---" + mMediaPlayer.isPlaying());
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        mPlayHelpStatus = MediaPlayerHelp.ON_CREATED;
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
        mPlayHelpStatus = MediaPlayerHelp.ON_PAUSE;
        mMediaPlayer.start();
        Log.w("MediaPlayerHelp", "onCompletion:" + mMediaPlayer.isLooping() + "---" + mPlayHelpStatus);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public interface OnMediaPlayerCallback {
        default void onPrepare(@NonNull MediaPlayer mediaPlayer) {
        }

        default void onCompletion(@NonNull MediaPlayer mediaPlayer) {
        }
    }
}
