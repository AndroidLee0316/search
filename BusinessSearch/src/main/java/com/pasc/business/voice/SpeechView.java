package com.pasc.business.voice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * @date 2019/5/27
 * @des
 * @modify
 **/
public class SpeechView extends View {

    private int childCount = 8;
    private int midIndex = childCount + 1;
    private int maxHeight = 80;
    private int childMargin = 45;
    private float strokeWidth = 20;
    private float maxLoadingRadius = 120;
    private float currentLoadingRadius = strokeWidth;
    private int bgColor = Color.BLACK;
    private int blueColor = Color.parseColor ("#4e72b8");
    private int midBlueColor = Color.parseColor ("#585eaa");
    private int lightBlueColor = Color.parseColor ("#afb4db");
    private int bgOvalColor = Color.parseColor ("#2a5caa");
    private int grayColor = Color.parseColor ("#666666");

    private int listenStatus = 0;
    private int transformStatus = 1;
    private int transformStatusTwo = 2;
    private int loadingStatus = 3;
    private int status = listenStatus;
    private ValueAnimator transformAnimator, transformAnimatorTwo;
    private boolean isStart = false;
    private float volume = 1;
    // 1- 100

    // 加速圈
    private int mSteps = 3;
    private final float INDETERMINANT_MIN_SWEEP = 5f;
    private final int RESETTING_ANGLE = 620;
    private int mAnimDuration = 3000;
    private float mStartAngle;
    private AnimatorSet loadingAnimator;
    private float mIndeterminateSweep;
    private float mIndeterminateRotateOffset;

    private int getColor(int num) {
        if (num <= 3) {
            return blueColor;
        } else if (num <= 5) {
            return midBlueColor;
        } else {
            return lightBlueColor;
        }
    }
    public synchronized void updateVolume(float volume) {
        if (status == listenStatus) {
            if (volume <= 10) {
                this.volume = 1;
            } else {
                float data = volume / 10.f;
                this.volume = 0.8f + (float) Math.log10 (data);
            }
            SpeechView.this.notifyAll ();
        }
    }
    public synchronized void startTransform() {
        status = transformStatus;
        for (LineInfo lineInfo : lines) {
            lineInfo.setTransform (true);
            lineInfo.setChangeIndex (0);
            lineInfo.updateRate (1);
            lineInfo.setTmpIndex (lineInfo.getIndex ());
        }
        stopTransform ();
        if (transformAnimator != null) {
            transformAnimator.start ();
        }
        stopLoading ();

    }
    public synchronized void stopTransform() {
        if (transformAnimator != null) {
            transformAnimator.cancel ();
        }
    }
    private synchronized void startTransformTwo() {
        status = transformStatusTwo;
        stopTransformTwo ();
        if (transformAnimatorTwo != null) {
            transformAnimatorTwo.start ();
        }
    }
    private synchronized void stopTransformTwo() {
        if (transformAnimatorTwo != null) {
            transformAnimatorTwo.cancel ();
        }
    }
    public synchronized void startListen() {
        status = listenStatus;
        for (LineInfo lineInfo : lines) {
            lineInfo.setTransform (false);
            lineInfo.setTmpIndex (0);
            lineInfo.updateRate (1f);
        }
        start ();
        SpeechView.this.notifyAll ();
    }
    private synchronized void start() {
        if (isStart) {
            return;
        }
        isStart = true;
        new Thread (new Runnable () {
            @Override
            public void run() {
                int pos = 0;
                while (isStart) {
                    if (status == listenStatus) {
                        for (int i = 0; i < lines.size (); i++) {
                            float rate = (float) Math.abs (Math.sin (i + pos));
                            rate = rate <= 0.2f ? 0.2f : rate;
                            rate = volume * rate;
                            LineInfo lineInfo = lines.get (i);
                            lineInfo.updateRate (rate);
                        }
                        postInvalidate ();
                        pos++;
                        if (pos >= Integer.MAX_VALUE) {
                            pos = 0;
                        }
                        synchronized (SpeechView.this) {
                            try {
                                SpeechView.this.wait (200);
                            } catch (InterruptedException e) {
                                e.printStackTrace ();
                            }
                        }
                    } else {
                        synchronized (SpeechView.this) {
                            try {
                                SpeechView.this.wait ();
                            } catch (Exception e) {
                                e.printStackTrace ();
                            }
                        }
                    }


                }
            }
        }).start ();
    }
    public synchronized void destroy() {
        isStart = false;
        stopTransform ();
        stopLoading ();
    }
    public synchronized void startLoading() {
        status = loadingStatus;
        resetAnimation ();
    }

    public synchronized void stopLoading() {
        if (loadingAnimator != null) {
            loadingAnimator.cancel ();
            loadingAnimator.removeAllListeners ();
            loadingAnimator = null;
        }
    }

    private LineInfo newLineInfo() {
        LineInfo lineInfo = new LineInfo (childCount, midIndex, maxHeight, childMargin, strokeWidth);
        return lineInfo;
    }

    private List<LineInfo> lines = new ArrayList<> ();
    {
        // 0 - > 8
        for (int i = 0; i <= childCount; i++) {
            LineInfo lineInfo = newLineInfo ();
            lineInfo.setColor (getColor (i));
            lineInfo.setIndex (i);
            lineInfo.setLeft (true);
            lines.add (lineInfo);
        }
        //9
        LineInfo mid = newLineInfo ();
        mid.setIndex (midIndex);
        mid.setColor (getColor (midIndex));
        lines.add (mid);

        // 8 - >0
        for (int i = childCount; i >= 0; i--) {
            LineInfo lineInfo = newLineInfo ();
            lineInfo.setIndex (i);
            lineInfo.setColor (getColor (i));
            lineInfo.setLeft (false);
            lines.add (lineInfo);

        }
    }

    private void regressIndex(int index) {
        for (LineInfo lineInfo : lines) {
            if (lineInfo.getTmpIndex () == index) {
                lineInfo.setTmpIndex (lineInfo.getTmpIndex () + 1);
            }
            lineInfo.setChangeIndex (index);
        }
    }


    private Paint listenPaint, progressPaint, bgPaint;
    RectF oval = new RectF ();

    public SpeechView(Context context) {
        this (context, null);
    }

    public SpeechView(Context context, @Nullable AttributeSet attrs) {
        super (context, attrs);
        init (context);
    }

    void init(Context context) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        listenPaint = new Paint (Paint.ANTI_ALIAS_FLAG);
        listenPaint.setStyle (Paint.Style.FILL);
        listenPaint.setStrokeCap (Paint.Cap.ROUND);
        listenPaint.setShadowLayer (strokeWidth/2, 0, 0, bgOvalColor);

        bgPaint = new Paint (Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle (Paint.Style.STROKE);
        bgPaint.setStrokeCap (Paint.Cap.ROUND);
        bgPaint.setStrokeWidth (strokeWidth * 1.2f);

        progressPaint = new Paint (Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle (Paint.Style.STROKE);
        progressPaint.setStrokeCap (Paint.Cap.ROUND);
        progressPaint.setStrokeWidth (strokeWidth/2);
        progressPaint.setShadowLayer (strokeWidth/2, 0, 0, bgOvalColor);

        transformAnimator = new ValueAnimator ();
        transformAnimator = ValueAnimator.ofInt (0, childCount);
        transformAnimator.setDuration (200);
        transformAnimator.setInterpolator (new AccelerateInterpolator ());
        transformAnimator.addUpdateListener (new ValueAnimator.AnimatorUpdateListener () {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int index = (int) animation.getAnimatedValue ();
                regressIndex (index);
                invalidate ();
            }
        });
        transformAnimator.addListener (new AnimatorListenerAdapter () {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                startTransformTwo ();
            }
        });

        transformAnimatorTwo = new ValueAnimator ();
        transformAnimatorTwo = ValueAnimator.ofFloat (strokeWidth, maxLoadingRadius);
        transformAnimatorTwo.setDuration (500);
        transformAnimatorTwo.setInterpolator (new LinearInterpolator ());
        transformAnimatorTwo.addUpdateListener (new ValueAnimator.AnimatorUpdateListener () {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentLoadingRadius = (float) animation.getAnimatedValue ();
                invalidate ();
            }
        });
        transformAnimatorTwo.addListener (new AnimatorListenerAdapter () {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                startLoading ();
            }
        });

        startListen ();
    }



    private AnimatorSet createIndeterminateAnimator(float step) {

        final float maxSweep = 360f * (mSteps - 1) / mSteps + INDETERMINANT_MIN_SWEEP;
        final float start = -90f + step * (maxSweep - INDETERMINANT_MIN_SWEEP);

        // Extending the front of the arc
        final ValueAnimator frontEndExtend = ValueAnimator.ofFloat (INDETERMINANT_MIN_SWEEP,
                maxSweep);
        frontEndExtend.setDuration (mAnimDuration / mSteps / 2);
        frontEndExtend.setInterpolator (new DecelerateInterpolator (1));
        frontEndExtend.addUpdateListener (new ValueAnimator.AnimatorUpdateListener () {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mIndeterminateSweep = (Float) animation.getAnimatedValue ();
                invalidate ();
            }
        });
        frontEndExtend.addListener (new AnimatorListenerAdapter () {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd (animation);
                frontEndExtend.removeAllListeners ();
                frontEndExtend.cancel ();
            }
        });

        // Overall rotation
        final ValueAnimator rotateAnimator1 = ValueAnimator.ofFloat (step * 720f / mSteps,
                (step + .5f) * 720f / mSteps);
        rotateAnimator1.setDuration (mAnimDuration / mSteps / 2);
        rotateAnimator1.setInterpolator (new LinearInterpolator ());
        rotateAnimator1.addUpdateListener (new ValueAnimator.AnimatorUpdateListener () {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mIndeterminateRotateOffset = (Float) animation.getAnimatedValue ();
            }
        });

        rotateAnimator1.addListener (new AnimatorListenerAdapter () {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd (animation);
                rotateAnimator1.removeAllListeners ();
                rotateAnimator1.cancel ();
            }
        });

        // Followed by...

        // Retracting the back end of the arc
        final ValueAnimator backEndRetract = ValueAnimator.ofFloat (start,
                start + maxSweep - INDETERMINANT_MIN_SWEEP);
        backEndRetract.setDuration (mAnimDuration / mSteps / 2);
        backEndRetract.setInterpolator (new DecelerateInterpolator (1));
        backEndRetract.addUpdateListener (new ValueAnimator.AnimatorUpdateListener () {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStartAngle = (Float) animation.getAnimatedValue ();
                mIndeterminateSweep = maxSweep - mStartAngle + start;
                invalidate ();
                if (mStartAngle > RESETTING_ANGLE) {
                    resetAnimation ();
                }
            }
        });

        backEndRetract.addListener (new AnimatorListenerAdapter () {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd (animation);
                backEndRetract.cancel ();
                backEndRetract.removeAllListeners ();
            }
        });

        // More overall rotation
        final ValueAnimator rotateAnimator2 = ValueAnimator.ofFloat ((step + .5f) * 720f / mSteps,
                (step + 1) * 720f / mSteps);
        rotateAnimator2.setDuration (mAnimDuration / mSteps / 2);
        rotateAnimator2.setInterpolator (new LinearInterpolator ());
        rotateAnimator2.addUpdateListener (new ValueAnimator.AnimatorUpdateListener () {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mIndeterminateRotateOffset = (Float) animation.getAnimatedValue ();
            }
        });

        rotateAnimator2.addListener (new AnimatorListenerAdapter () {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd (animation);
                rotateAnimator2.removeAllListeners ();
                rotateAnimator2.cancel ();
            }
        });

        AnimatorSet set = new AnimatorSet ();
        set.play (frontEndExtend).with (rotateAnimator1);
        set.play (backEndRetract).with (rotateAnimator2).after (rotateAnimator1);
        return set;
    }

    private void resetAnimation() {
        if (loadingAnimator != null && loadingAnimator.isRunning ()) {
            loadingAnimator.cancel ();
        }
        mIndeterminateSweep = INDETERMINANT_MIN_SWEEP;
        loadingAnimator = new AnimatorSet ();
        AnimatorSet prevSet = null;
        AnimatorSet nextSet;
        for (int k = 0; k < mSteps; k++) {
            nextSet = createIndeterminateAnimator (k);
            AnimatorSet.Builder builder = loadingAnimator.play (nextSet);
            if (prevSet != null) {
                builder.after (prevSet);
            }
            prevSet = nextSet;

        }
        loadingAnimator.start ();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawColor (bgColor);
        int width = canvas.getWidth ();
        int height = canvas.getHeight ();
        if (status == listenStatus || status == transformStatus) {
            //left
            for (int i = childCount; i >= 0; i--) {
                lines.get (i).draw (canvas, listenPaint);
            }
            // right
            for (int i = childCount + 2; i < lines.size (); i++) {
                lines.get (i).draw (canvas, listenPaint);
            }
            //mid
            lines.get (childCount + 1).draw (canvas, listenPaint);
        } else if (status == transformStatusTwo) {
            // 椭圆
//            progressPaint.setColor (bgOvalColor);
//            updateOvalRectF (oval, width, height, currentLoadingRadius,0.1f);
//            canvas.drawOval (oval,progressPaint);
            // transformTwo
            updateRectF (oval, width, height, currentLoadingRadius);
            progressPaint.setColor (lightBlueColor);
            canvas.drawArc (oval, 0, 360, false, progressPaint);
        } else {
            // 椭圆
//            progressPaint.setColor (bgOvalColor);
//            updateOvalRectF (oval, width, height, maxLoadingRadius,0.1f);
//            canvas.drawOval (oval,progressPaint);
            //loading
            updateRectF (oval, width, height, maxLoadingRadius);
            progressPaint.setColor (grayColor);
            canvas.drawArc (oval, 0, 360, false, progressPaint);
            progressPaint.setColor (lightBlueColor);
            canvas.drawArc (oval, mStartAngle + mIndeterminateRotateOffset, mIndeterminateSweep, false, progressPaint);

        }


    }

    void updateRectF(RectF oval, int width, int height, float radius) {
        float innerRadius = radius;
        float left = (width - innerRadius) / 2;
        float top = (height - innerRadius) / 2;
        float right = left + innerRadius;
        float bottom = top + innerRadius;
        oval.left = left;
        oval.top = top;
        oval.right = right;
        oval.bottom = bottom;
    }


    void updateOvalRectF(RectF oval, int width, int height, float radius, float marginRate) {
        float innerRadius = radius;
        float padding = innerRadius * marginRate;
        float left = (width - innerRadius) / 2;
        float top = (height - innerRadius) / 2 - padding;
        float right = left + innerRadius;
        float bottom = top + innerRadius + 2 * padding;
        oval.left = left;
        oval.top = top;
        oval.right = right;
        oval.bottom = bottom;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow ();
        startListen ();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow ();
        destroy ();
    }
}
