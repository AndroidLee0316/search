package com.pasc.business.voice;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;


/**
 * @date 2019/5/31
 * @des
 * @modify
 **/
public class WaveView extends View {
    private int lineHeight = 6;
    private int _waveNumber = 5;
    private final int MODE_ONE = 0, MODE_TWO = 1;
    private int _mainWaveWidth = 2;
    private int mode = MODE_TWO;
    private Paint paint1, paint2;
    private Path pArr[] = new Path[_waveNumber];
    private Paint paintArr[] = new Paint[_waveNumber];

    float _waveHeight;
    float _waveWidth;
    float _waveMid;
    float _maxAmplitude;//最大振幅

    float _idleAmplitude;//最小振幅
    float _amplitude;//归一化振幅系数，与音量正相关
    float _density;//x轴粒度

    float _phase1;//firstLine相位
    float _phase2;//secondLine相位

    float _phaseShift1;
    float _phaseShift2;

    float _frequency1;
    float _frequency2;

    float _stopAnimationRatio;


    float _currentVolume;
    float _lastVolume;
    float _middleVolume;
    float tmpVolume = 0.1f;

    float _maxWidth;//波纹显示最大宽度
    float _beginX;//波纹开始坐标


    Path path1 = new Path ();
    Path path2 = new Path ();

    private ValueAnimator transformAnimator;

    public WaveView(Context context) {
        this (context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this (context, attrs, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
        setLayerType (View.LAYER_TYPE_HARDWARE, null);
        paint1 = new Paint (Paint.ANTI_ALIAS_FLAG);
        paint1.setStrokeWidth (lineHeight);
        paint1.setStrokeCap (Paint.Cap.ROUND);
        paint1.setStyle (Paint.Style.STROKE);
        path1.reset ();


        paint2 = new Paint (Paint.ANTI_ALIAS_FLAG);
        paint2.setStrokeWidth (lineHeight);
        paint2.setStrokeCap (Paint.Cap.ROUND);
        paint2.setStyle (Paint.Style.STROKE);
        path2.reset ();
        if (mode == MODE_TWO) {
            for (int i = 0; i < pArr.length; i++) {
                Path path = new Path ();
                pArr[i] = path;
                Paint paint = new Paint (Paint.ANTI_ALIAS_FLAG);
                paint.setStrokeCap (Paint.Cap.ROUND);
                paint.setStyle (Paint.Style.STROKE);
                if (i == 0) {
                    paint.setStrokeWidth (lineHeight);
                } else {
                    paint.setStrokeWidth (lineHeight/1.5f);
                }
                paintArr[i] = paint;
            }
        }

        init ();
        transformAnimator = new ValueAnimator ();
        transformAnimator = new ValueAnimator ();
        transformAnimator = ValueAnimator.ofFloat (0.1f, 0.2f);
        transformAnimator.setDuration (200);
        transformAnimator.setRepeatCount (ValueAnimator.INFINITE);
        transformAnimator.setInterpolator (new LinearInterpolator ());
        transformAnimator.addUpdateListener (new ValueAnimator.AnimatorUpdateListener () {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                updateVolume ((Float) animation.getAnimatedValue ());
                if (syncVolumeListener != null) {
                    //同步
                    updateVolume (syncVolumeListener.volume ());
                } else {
                    updateVolume (tmpVolume);
                }
            }
        });
        transformAnimator.start ();
        updateVolume (tmpVolume);
    }

    public void startAnimation() {
        if (!transformAnimator.isRunning ()) {
            transformAnimator.start ();
        }
    }

    public void cancelAnimation() {
        if (transformAnimator.isRunning ()) {
            transformAnimator.cancel ();
        }
    }

    void init() {
        _frequency1 = 2.0f;
        _frequency2 = 1.6f;

        _amplitude = 1.0f;
        _idleAmplitude = 0.05f;

        _phase1 = 0.0f;
        _phase2 = 0.0f;
        _phaseShift1 = -0.22f;
        _phaseShift2 = -0.2194f;

        _density = 4f;
        _beginX = 0.0f;
        _lastVolume = 0.0f;
        _currentVolume = 0.0f;
        _middleVolume = 0.05f;

        _stopAnimationRatio = 1.0f;
    }

    private void updateVolume(float volume) {
        if (volume > 0) {
            _middleVolume = volume;
        }
        _phase1 += _phaseShift1; // Move the wave
        _phase2 += _phaseShift2;

        _amplitude = Math.max (_middleVolume, _idleAmplitude);
        invalidate ();
    }

    public static int argb(float alpha, float red, float green, float blue) {
        return ((int) (alpha * 255.0f + 0.5f) << 24) |
                ((int) (red * 255.0f + 0.5f) << 16) |
                ((int) (green * 255.0f + 0.5f) << 8) |
                (int) (blue * 255.0f + 0.5f);
    }
    int offset = 0;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged (w, h, oldw, oldh);
        _waveHeight = h;
        _waveWidth = w;
        _waveMid = _waveWidth / 2.0f;
        _maxAmplitude = _waveHeight * 0.5f;
        _maxWidth = _waveWidth + _density;

        if (mode == MODE_ONE) {
            int c11 = Color.parseColor ("#8D67E6");
            int c12 = Color.parseColor ("#62C4F8");
            Shader bs1 = new LinearGradient (0, 0, _waveWidth, 0, new int[]{c11, c12}, null, Shader.TileMode.CLAMP);
            paint1.setShader (bs1);
            int c21 = Color.parseColor ("#62C5F8");
            int c22 = Color.parseColor ("#C589F8");
            Shader bs2 = new LinearGradient (0, 0, _waveWidth, 0, new int[]{c21, c22}, null, Shader.TileMode.CLAMP);
            paint2.setShader (bs2);
        } else {
            updateShader ();
        }


    }

    void updateShader(){
        if (_waveWidth<=0){
            return;
        }
        float colorOffset = (float) Math.sin (Math.PI * (offset++ % 255.0f) / 255.0f);
        for (int i = 0; i < paintArr.length; i++) {
            float  progress = 1.0f - (float) i / paintArr.length;
            float multiplier = Math.min (1.0f, (progress / 3.0f * 2.0f) + (1.0f / 3.0f));
            float colorFactor = i == 0 ? 1.0f : 1.0f * multiplier * 0.6f;
            int startColor = argb (colorFactor, colorOffset, 255.0f / 255.0f, 255.0f / 255.0f);
            int endColor = argb (colorFactor, 255.0f / 255.0f, 1 - colorOffset, 255.0f / 255.0f);
            Shader sd = new LinearGradient (0, 0, _waveWidth, 0, new int[]{startColor, endColor}, null, Shader.TileMode.CLAMP);
            paintArr[i].setShader (sd);

        }
    }

    private void updatePath(Path path, float maxAmplitude, float phase, float frequency) {
        float normedAmplitude = Math.min (_amplitude, 1.0f);
        path.reset ();
        for (float x = _beginX; x < _maxWidth; x += _density) {
            float scaling = (float) -Math.pow (x / _waveMid - 1, 2) + 1.0f; // make center bigger
            float rate = (float) Math.sin (2 * Math.PI * (x / _waveWidth) * frequency + phase);
            float y = scaling * maxAmplitude * normedAmplitude * _stopAnimationRatio * rate + (_waveHeight * 0.5f);
            if (_beginX == x) {
                path.moveTo (x, y);
            } else {
                path.lineTo (x, y);
            }
        }
    }

    private void updatePath(Path path, float maxAmplitude, float phase, float frequency, int yOffset) {
        // (-(2x-1)^2+1)sin (2pi*f*x)
        // (-(2x-1)^2+1)sin (2pi*f*x + 3.0)
        path.reset ();
        float normedAmplitude = Math.min (_amplitude, 1.0f);

        float x = _beginX;
        float y = 0;
        for (; x < _maxWidth; x += _density) {
            float scaling = (float) (-Math.pow (x / _waveMid - 1, 2) + 1); // make center bigger

            y = (float) (scaling * maxAmplitude * normedAmplitude * _stopAnimationRatio * Math.sin (2 * Math.PI * (x / _waveWidth) * frequency + phase) + (_waveHeight * 0.5 - yOffset));

            if (_beginX == x) {
                path.moveTo (x, y);
            } else {
                path.lineTo (x, y);

            }
        }
        x = x - _density;
        path.lineTo (x, y + 2 * yOffset);

        for (; x >= _beginX; x -= _density) {
            float scaling = (float) (-Math.pow (x / _waveMid - 1, 2) + 1); // make center bigger
            y = (float) (scaling * maxAmplitude * normedAmplitude * _stopAnimationRatio * Math.sin (2 * Math.PI * (x / _waveWidth) * frequency + phase) + (_waveHeight * 0.5 + yOffset));
            path.lineTo (x, y);

        }
        x = x + _density;
        path.lineTo (x, y - 2 * yOffset);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!(getVisibility () == VISIBLE)) {
            return;
        }
//        long time = SystemClock.currentThreadTimeMillis ();
        if (mode == MODE_ONE) {
            updatePath (path1, _maxAmplitude, _phase1, _frequency1);
            updatePath (path2, _maxAmplitude * 0.8f, _phase2 + 3, _frequency2);
//            path3.reset ();
//            boolean flag=path3.op (path1,path2, Path.Op.UNION);
//            canvas.drawPath (path3, paint3);
            canvas.drawPath (path1, paint1);
            canvas.drawPath (path2, paint2);

        } else {
            updateShader ();
            for (int i = 0; i < pArr.length; i++) {
                int waveWidth = i == 0 ? _mainWaveWidth : _mainWaveWidth / 2;
                float progress = 1.0f - (float) i / pArr.length;
                float amplitudeFactor = 1.5f * progress - 0.5f;
                updatePath (pArr[i], _maxAmplitude * amplitudeFactor, _phase1, _frequency1, waveWidth / 2);
                canvas.drawPath (pArr[i], paintArr[i]);
            }
        }
//         Log.e ("yzjTime", "onDraw: " + (SystemClock.currentThreadTimeMillis () - time));

    }

    public interface SyncVolumeListener {
        float volume();
    }

    private SyncVolumeListener syncVolumeListener;

    //同步音量
    public void setSyncVolumeListener(SyncVolumeListener syncVolumeListener) {
        this.syncVolumeListener = syncVolumeListener;
    }

    // 异步音量
    public void setVolume(float volume) {
        this.tmpVolume = volume;
    }
}
