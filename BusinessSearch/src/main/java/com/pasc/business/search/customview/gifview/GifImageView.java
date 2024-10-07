package com.pasc.business.search.customview.gifview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

public class GifImageView extends AppCompatImageView{
    private static final String TAG = "GifDecoderView";
    private GifDecoder gifDecoder;
    private Bitmap tmpBitmap;
    private final Handler handler = new Handler (Looper.getMainLooper ());
    private boolean animating;
    private boolean renderFrame;
    private boolean shouldClear;
    private AnimThread animationThread;
    private OnFrameAvailable frameCallback = null;
    private long framesDisplayDuration = -1L;
    private OnAnimationStop animationStopCallback = null;
    private OnAnimationStart animationStartCallback = null;

    private final Runnable updateResults = new Runnable () {
        @Override
        public void run() {
            if (tmpBitmap != null && !tmpBitmap.isRecycled ()) {
                setImageBitmap (tmpBitmap);
            }
        }
    };

    private final Runnable cleanupRunnable = new Runnable () {
        @Override
        public void run() {
//      tmpBitmap = null;
//      gifDecoder = null;
//      animationThread = null;
//      shouldClear = false;
        }
    };

    public GifImageView(final Context context, final AttributeSet attrs) {
        super (context, attrs);
    }

    public GifImageView(final Context context) {
        super (context);
    }

    public synchronized void setBytes(final byte[] bytes) {
        gifDecoder = new GifDecoder ();
        try {
            gifDecoder.read (bytes);
        } catch (final Exception e) {
            gifDecoder = null;
            Log.e (TAG, e.getMessage (), e);
            return;
        }
//        if (animating) {
//            startAnimationThread ();
//        } else {
//            gotoFrame (0);
//        }
    }

    public synchronized void start(final byte[] bytes) {
        clear ();
        setBytes (bytes);
        startAnimation ();
    }

    public long getFramesDisplayDuration() {
        return framesDisplayDuration;
    }

    /**
     * Sets custom display duration in milliseconds for the all frames. Should be called before {@link
     * #startAnimation()}
     *
     * @param framesDisplayDuration Duration in milliseconds. Default value = -1, this property will
     *                              be ignored and default delay from gif file will be used.
     */
    public void setFramesDisplayDuration(long framesDisplayDuration) {
        this.framesDisplayDuration = framesDisplayDuration;
    }

    public synchronized void startAnimation() {
        animating = true;
        startAnimationThread ();
    }

    public boolean isAnimating() {
        return animating;
    }

    public synchronized void stopAnimation() {
        animating = false;

        if (animationThread != null) {
            animationThread.interrupt ();
            animationThread.stop=true;
            animationThread = null;
        }
    }

    public void gotoFrame(int frame) {
        if (gifDecoder.getCurrentFrameIndex () == frame) return;
        if (gifDecoder.setFrameIndex (frame - 1) && !animating) {
            renderFrame = true;
            startAnimationThread ();
        }
    }

    public void resetAnimation() {
        gifDecoder.resetLoopIndex ();
        gotoFrame (0);
    }

    public synchronized void clear() {
        animating = false;
        renderFrame = false;
        shouldClear = true;
        stopAnimation ();
        handler.post (cleanupRunnable);
    }

    private boolean canStart() {
        return (animating || renderFrame) && gifDecoder != null && animationThread == null;
    }

    public int getGifWidth() {
        return gifDecoder.getWidth ();
    }

    /**
     * Gets the number of frames read from file.
     *
     * @return frame count.
     */
    public int getFrameCount() {
        return gifDecoder.getFrameCount ();
    }

    public int getGifHeight() {
        return gifDecoder.getHeight ();
    }

    public OnFrameAvailable getOnFrameAvailable() {
        return frameCallback;
    }

    public void setOnFrameAvailable(OnFrameAvailable frameProcessor) {
        this.frameCallback = frameProcessor;
    }

    public interface OnFrameAvailable {
        Bitmap onFrameAvailable(Bitmap bitmap);
    }

    public OnAnimationStop getOnAnimationStop() {
        return animationStopCallback;
    }

    public void setOnAnimationStop(OnAnimationStop animationStop) {
        this.animationStopCallback = animationStop;
    }

    public void setOnAnimationStart(OnAnimationStart animationStart) {
        this.animationStartCallback = animationStart;
    }

    public interface OnAnimationStop {
        void onAnimationStop();
    }

    public interface OnAnimationStart {
        void onAnimationStart();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow ();
        clear ();
    }

    private synchronized void startAnimationThread() {
        if (canStart ()) {
            animationThread = new AnimThread ();
            animationThread.start ();
        }
    }

    private class AnimThread extends Thread {
        public boolean stop=false;
        @Override
        public void run() {
            super.run ();
            if (animationStartCallback != null) {
                animationStartCallback.onAnimationStart ();
            }

            do {
                if (!animating && !renderFrame) {
                    break;
                }
                if (gifDecoder == null) {
                    break;
                }
//                Log.e (TAG, "run: "+getName () );
                boolean advance = gifDecoder.advance ();

                //milliseconds spent on frame decode
                long frameDecodeTime = 0;
                try {
                    long before = System.nanoTime ();
                    tmpBitmap = gifDecoder.getNextFrame ();
                    if (frameCallback != null) {
                        tmpBitmap = frameCallback.onFrameAvailable (tmpBitmap);
                    }
                    frameDecodeTime = (System.nanoTime () - before) / 1000000;
                    handler.post (updateResults);
                } catch (final ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                    Log.w (TAG, e);
                } catch (Exception e) {

                }

                renderFrame = false;
                if (!animating || !advance) {
                    animating = false;
                    break;
                }
                try {
                    int delay = gifDecoder.getNextDelay ();
                    // Sleep for frame duration minus time already spent on frame decode
                    // Actually we need next frame decode duration here,
                    // but I use previous frame time to make code more readable
                    delay -= frameDecodeTime;
                    if (delay > 0) {
                        Thread.sleep (framesDisplayDuration > 0 ? framesDisplayDuration : delay);
                    }
                } catch (final InterruptedException e) {
                    // suppress exception
                } catch (Exception e) {

                }
            } while (animating && !stop);

            if (shouldClear) {
                handler.post (cleanupRunnable);
            }
            if (animationStopCallback != null) {
                animationStopCallback.onAnimationStop ();
            }
        }
    }
}
