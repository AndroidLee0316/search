package com.pasc.business.search.customview;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.pasc.business.search.R;

public class SelectDialog extends Dialog {

    private final View view;
    public TextView mTitle;
    private TextView mTvContext;
    private TextView mTvConfirm;
    private TextView mTvCancel;

    public SelectDialog(Context context) {
        super(context, R.style.AppSearchBaseDialog);
        setContentView(R.layout.search_dialog_select_delete);
        mTitle = (TextView) findViewById(R.id.temp_tv_title);
        mTvContext = (TextView) findViewById(R.id.temp_tv_context);
        mTvConfirm = (TextView) findViewById(R.id.temp_tv_confirm);
        mTvCancel = (TextView) findViewById(R.id.temp_tv_cancel);
        view  = findViewById(R.id.temp_view);
    }

    public SelectDialog setTitle(String text,boolean show){
        mTitle.setText (text);
        mTitle.setVisibility (show?View.VISIBLE:View.GONE);
        return this;
    }
    public SelectDialog setContent(String text,boolean show){
        mTvContext.setText (text);
        mTvContext.setVisibility (show?View.VISIBLE:View.GONE);
        return this;
    }

    public SelectDialog setOnSelectedListener(final OnSelectedListener onSelectedListener) {
        if (onSelectedListener==null){
            return this;
        }

        mTvConfirm.setOnClickListener(new View.OnClickListener() {//确认
            @Override
            public void onClick(View v) {
                onSelectedListener.onSelected();
                dismiss();
            }
        });

        mTvCancel.setOnClickListener(new View.OnClickListener() {//取消
            @Override
            public void onClick(View v) {
                onSelectedListener.onCancel();
                dismiss();
            }
        });
        return this;
    }




    public View getView() {
        return view;
    }


    public interface OnSelectedListener {
        void onSelected();

        void onCancel();
    }




}