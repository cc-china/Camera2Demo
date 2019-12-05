package com.test.camera2.customeview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.camera2.R;

/**
 * Created by cmm on 2019/11/28.
 */

public class SwitchItemTitleView extends RelativeLayout{

    private SwitchItemTitleViewClickListener mListener;

    public interface SwitchItemTitleViewClickListener{
        void switchItemTitleViewClick(View view);
    }

    public void setSwitchItemTitleViewClickListener(SwitchItemTitleViewClickListener listener){
        this.mListener = listener;
    }

    private TextView tv_title;

    public SwitchItemTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
    boolean mInLayout = false;



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tv_title = findViewById(R.id.tv_title);
        tv_title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.switchItemTitleViewClick(SwitchItemTitleView.this);
            }
        });
    }

    public void setTitleName(String s) {
        tv_title.setText(s);
    }
}
