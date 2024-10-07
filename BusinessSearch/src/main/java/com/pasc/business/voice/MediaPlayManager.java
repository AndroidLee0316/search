package com.pasc.business.voice;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

/**
 * @date 2019/6/13
 * @des
 * @modify
 **/
public class MediaPlayManager {

    public static MediaPlayManager instance() {
        return Single.instance;
    }

    private static final class Single {
        private final static MediaPlayManager instance = new MediaPlayManager ();
    }

    private MediaPlayer mediaPlayer;

    public interface MediaPlayListener {
        void onStart();

        void onError();

        void onComplete();
    }

    private MediaPlayListener mediaPlayListener;

    public void setMediaPlayListener(MediaPlayListener mediaPlayListener) {
        this.mediaPlayListener = mediaPlayListener;
    }

    public void start(Context context, String fileName) {
        try {
            destroy ();
            mediaPlayer = new MediaPlayer ();
            AssetFileDescriptor fileDescriptor = context.getAssets ().openFd (fileName);
            mediaPlayer.setDataSource (fileDescriptor.getFileDescriptor (), fileDescriptor.getStartOffset (), fileDescriptor.getLength ());
            mediaPlayer.setAudioStreamType (AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping (false);
            mediaPlayer.setOnCompletionListener (new MediaPlayer.OnCompletionListener () {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mediaPlayListener != null) {
                        mediaPlayListener.onComplete ();
                    }
                }
            });
            mediaPlayer.setOnPreparedListener (new MediaPlayer.OnPreparedListener () {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (mediaPlayListener != null) {
                        mediaPlayListener.onStart ();
                    }
                    try {
                        mp.start ();

                    } catch (Exception e) {
                        e.printStackTrace ();
                        if (mediaPlayListener != null) {
                            mediaPlayListener.onError ();
                        }
                        return;
                    }

                }
            });
            mediaPlayer.setOnInfoListener (new MediaPlayer.OnInfoListener () {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    return false;
                }
            });

            mediaPlayer.setOnErrorListener (new MediaPlayer.OnErrorListener () {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    if (mediaPlayListener != null) {
                        mediaPlayListener.onError ();
                    }
                    return false;
                }
            });

            mediaPlayer.prepareAsync ();

        } catch (Exception e) {
            if (mediaPlayListener != null) {
                mediaPlayListener.onError ();
            }
            e.printStackTrace ();
        }

    }

    public void stop() {

        if (mediaPlayer != null  /****&& mediaPlayer.isPlaying ()*****/ ) {
            try {
                mediaPlayer.stop ();
                mediaPlayer.release ();
            } catch (Exception e) {

            }
        }
        mediaPlayer = null;

    }

    public void destroy() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release ();
            } catch (Exception e) {

            }
        }
        mediaPlayer = null;

    }


}
