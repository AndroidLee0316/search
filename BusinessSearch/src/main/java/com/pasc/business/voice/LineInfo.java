package com.pasc.business.voice;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * @date 2019/5/27
 * @des
 * @modify
 **/
public class LineInfo {

    private int childCount = 8;
    private int midIndex = childCount + 1;
    private int maxHeight = 80;
    private int childMargin = 45;
    private float strokeWidth = 20;


    public LineInfo() {
    }

    public LineInfo(int childCount, int midIndex, int maxHeight, int childMargin, float strokeWidth) {
        this.childCount = childCount;
        this.midIndex = midIndex;
        this.maxHeight = maxHeight;
        this.childMargin = childMargin;
        this.strokeWidth = strokeWidth;
    }

    private int circleNum = 4;
    private int color = Color.parseColor ("#0000ff");
    private int index = 0;
    private int tmpIndex = 0;
    private int changeIndex = 0;
    private boolean left = true;
    private float changeRate = 1;
    private boolean transform = false;

    public void setTransform(boolean transform) {
        this.transform = transform;
    }


    public void updateRate(float changeRate) {
        this.changeRate = changeRate;
    }

    public void randomRate() {
        float rate = (float) Math.random ();
        rate = rate < 0.3f ? 0.3f : rate;
        float rr = (float) (rate * Math.sin (index));
        rr = rr + 0.5f;
        updateRate (rr);

//        float rr = (float) (Math.random () * Math.sin (index));
//        updateRate (rr);
    }


    public void setColor(int color) {
        this.color = color;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setChangeIndex(int changeIndex) {
        this.changeIndex = changeIndex;
    }

    public int getIndex() {
        return index;
    }
    // 直线偏移左边距离 方程
    // y=k*x + b
    // (midIndex,0) , (midIndex-1,childMargin)
    // 0 = k*midIndex + b
    // childMargin = k* (midIndex - 1) + b
    // => k=-childMargin
    // => b=childMargin*midIndex
    // => y = -childMargin * x + childMargin*midIndex

    private int getTmp() {
        return transform ? tmpIndex : index;
    }

    public void setTmpIndex(int tmpIndex) {
        this.tmpIndex = tmpIndex;
    }

    public boolean hasChange() {
        return tmpIndex != index;
    }

    public int getTmpIndex() {
        return tmpIndex;
    }

    private float getW() {
        return childMargin * getTmp () - childMargin * midIndex;
    }

    // 高度方程
    private float getH() {
//         float base = 0.7f;
//        float h = (float) (maxHeight * (Math.pow (base, midIndex - getTmp (true))));
//        if (h <= strokeWidth) {
//            h = strokeWidth;
//        }
        float h = 0;
        if (transform) {
//            if (speechView.preHashChange (index)) {
//                h = 0.1f;
//            } else {
//                h = (float) (maxHeight * (Math.log10 (tmpIndex)));
//            }
            int diff = index - changeIndex;
            if (diff <= 1) {
                diff = 1;
            }
            h = (float) (maxHeight * (Math.log10 (diff)));
        } else {
            h = (float) (maxHeight * (Math.log10 (index)));
        }
        h = Math.max (0.1f, h);
        return h;
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor (color);
        paint.setStrokeWidth (strokeWidth);
        int mHeight = canvas.getHeight ();
        int mWidth = canvas.getWidth ();
        float lineHeight = getH ();
        float tmpMargin = (left ? (getW ()) : (-getW ()));
        float startX = mWidth / 2 + tmpMargin;
        float startY = mHeight / 2 - (lineHeight / 2) * changeRate;
        float stopX = mWidth / 2 + tmpMargin;
        float stopY = mHeight / 2 + (lineHeight / 2) * changeRate;

        if (index <= circleNum - 1) {
//        if (false){
            canvas.drawCircle (startX, mHeight / 2, strokeWidth / 2, paint);
        } else {
            canvas.drawLine (startX, startY, stopX, stopY, paint);
//            canvas.drawLine (startX, startY, stopX, stopY, paint);

        }
//        if (lineHeight == strokeWidth) {
//            canvas.drawCircle (startX, mHeight / 2, strokeWidth / 2, paint);
//        } else {
//            if (transform){
//                Log.e ("yzj", "index: "+index+" ,startY: "+startY +" ,stopY: "+stopY);
//            }
//            canvas.drawLine (startX, startY, stopX, stopY, paint);
//        }

    }

}
