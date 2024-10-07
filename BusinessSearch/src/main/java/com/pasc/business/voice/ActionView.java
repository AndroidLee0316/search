package com.pasc.business.voice;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * @date 2019/6/6
 * @des
 * @modify
 **/
public class ActionView extends AppCompatImageView {
    private IVisibilityListener visibilityListener;
    public void setVisibilityListener(IVisibilityListener visibilityListener) {
        this.visibilityListener = visibilityListener;
    }

    public ActionView(Context context) {
        super (context);
    }

    public ActionView(Context context, @Nullable AttributeSet attrs) {
        super (context, attrs);
    }

    public ActionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener (l);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility (visibility);
        if (visibilityListener!=null){
            if (visibility==VISIBLE){
                visibilityListener.visibility (true);
            }else {
                visibilityListener.visibility (false);
            }
        }

    }



    public interface IVisibilityListener{
        void visibility(boolean show);
    }
}
