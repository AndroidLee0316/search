package com.pasc.business.voice;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pasc.business.search.R;
import com.pasc.business.search.SearchManager;
import com.pasc.business.search.customview.gifview.GifImageView;
import com.pasc.business.search.util.IoUtil;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * @date 2019/5/30
 * @des
 * @modify
 **/
public class VoiceView extends FrameLayout implements View.OnClickListener {
    public VoiceView(@NonNull Context context) {
        this (context, null);
    }

    public VoiceView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this (context, attrs, 0);
    }

    public VoiceView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from (context).inflate (R.layout.search_speech_view, this);
        initView ();
    }

    private Context context;
    private LinearLayout llVoiceHeader;
    private GifImageView ivRobotIcon;
    private TextView tvRobotTip;
    private TextView tvRobotSkill;
    private TextView tvRobotClose;
    private RelativeLayout rvVoiceAnim;
    private WaveView waveView;
    private TextView tvTip;
    private TextView tvMsg;
    private TextView tvTmpMsg;
    private View llVoiceFooter;
    private View animContainer;
    Disposable gifDisposable;

    public void setVolume(float volume) {
        waveView.setVolume (volume);

    }

    public boolean isOpen() {
        return animContainer.getVisibility () == View.VISIBLE;
    }

    private void initView() {
        llVoiceHeader = (LinearLayout) findViewById (R.id.ll_voice_header);
        ivRobotIcon = (GifImageView) findViewById (R.id.iv_robot_icon);
        tvRobotTip = (TextView) findViewById (R.id.tv_robot_tip);
        tvRobotSkill = (TextView) findViewById (R.id.tv_robot_skill);
        tvRobotClose = (TextView) findViewById (R.id.tv_robot_close);
        rvVoiceAnim = (RelativeLayout) findViewById (R.id.rv_voice_anim);
        waveView = (WaveView) findViewById (R.id.waveView);
        tvTip = (TextView) findViewById (R.id.tv_tip);
        tvMsg = (TextView) findViewById (R.id.tv_msg);
        llVoiceFooter = findViewById (R.id.ll_voice_footer);
        animContainer = findViewById (R.id.ll_voice_container);
        tvTmpMsg=findViewById (R.id.tv_tmp_msg);
        tvRobotSkill.setOnClickListener (this);
        tvRobotClose.setOnClickListener (this);
        setVisibility (GONE);
//        speechStatus ();
    }

    public void sayStatus(){
        updateGif (R.raw.search_voice_say);
        tvRobotTip.setText ("深小i正在说");
    }

    public void speechStatus(){
        updateGif (R.raw.search_voice_speech);
        tvRobotTip.setText ("深小i正在听");
    }

    void disposePre(){
        if (gifDisposable!=null && !gifDisposable.isDisposed ()){
            gifDisposable.dispose ();
        }
        gifDisposable=null;
    }
    void updateGif(final int raw){
        disposePre ();
        gifDisposable= Single.create (new SingleOnSubscribe<byte[]> () {
            @Override
            public void subscribe(SingleEmitter<byte[]> emitter) throws Exception {
                byte[] bytes= IoUtil.raw2Bytes (SearchManager.instance ().getApp (),raw);
                if (bytes!=null && bytes.length>0){
                    emitter.onSuccess (bytes);
                }
            }
        }).subscribeOn (Schedulers.io ()).observeOn (AndroidSchedulers.mainThread ()).subscribe (new Consumer<byte[]> () {
            @Override
            public void accept(byte[] bytes) throws Exception {
                ivRobotIcon.start (bytes);
            }
        }, new Consumer<Throwable> () {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId ();
        if (id == R.id.tv_robot_skill) {
        } else if (id == R.id.tv_robot_close) {
            if (voiceListener!=null){
                voiceListener.close ();
            }
        }
    }

    public void openVoice() {
        if (!isOpen ()) {
            voiceAnim (false);
        }
    }

    public void closeVoice(boolean hideParent) {
        if (isOpen ()) {
            voiceAnim (hideParent);
        }
    }
    public void onlyCloseVoice(){
        waveView.cancelAnimation ();
        waveView.setVisibility (GONE);
    }

    private void voiceAnim(final boolean hideParent) {
        waveView.startAnimation ();
        final boolean isOpen = isOpen ();
        Animation animation = null;
        if (isOpen) {
            animation = AnimationUtils.loadAnimation (context, R.anim.search_voice_top_out);
        } else {
            animContainer.setVisibility (VISIBLE);
            animation = AnimationUtils.loadAnimation (context, R.anim.search_voice_top_in);
        }

        animation.setInterpolator (new LinearInterpolator ());
        animContainer.startAnimation (animation);
        animation.setAnimationListener (new Animation.AnimationListener () {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isOpen) {
                    if (hideParent){
                        setVisibility (GONE);
                    }
                    animContainer.setVisibility (GONE);
                } else {
                    animContainer.setVisibility (VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    public void setTvTmpMsg(String text){
        tvTmpMsg.setText (text);
    }
    public void showStatus(boolean showMsg,String tip,String text){
        tvMsg.setVisibility (showMsg?View.VISIBLE:View.GONE);
        waveView.setVisibility (!showMsg?View.VISIBLE:View.GONE);
        tvTmpMsg.setVisibility (!showMsg?View.VISIBLE:View.GONE);
        if (showMsg){
            sayStatus ();
            tvMsg.setText (text);
            tvTip.setText (tip);
            tvTip.setVisibility (!TextUtils.isEmpty (tip)?View.VISIBLE:View.GONE);

        }else {
            speechStatus ();
            tvTip.setVisibility (View.GONE);
        }

    }

    public void show(){
        setVisibility (VISIBLE);
//        openVoice ();
        waveView.startAnimation ();

    }
    public void close(){
//        closeVoice (true);
        setVisibility (GONE);
        ivRobotIcon.clear ();
        waveView.cancelAnimation ();

    }


    public interface IVoiceListener{
        void close();
    }
    private IVoiceListener  voiceListener;

    public void setVoiceListener(IVoiceListener voiceListener) {
        this.voiceListener = voiceListener;
    }
}
