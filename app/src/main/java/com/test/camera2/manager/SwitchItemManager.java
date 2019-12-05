package com.test.camera2.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.test.camera2.MainActivity;
import com.test.camera2.R;
import com.test.camera2.customeview.SwitchItemRelativeLayout;
import com.test.camera2.customeview.SwitchItemTitleView;

/**
 * Created by cmm on 2019/11/28.
 */

public class SwitchItemManager {

    private final SwitchItemRelativeLayout switch_relativeLayout;
    private final Context ctx;
    private final LayoutInflater mInflater;

    public SwitchItemManager(Context ctx, ViewGroup parentView) {
        switch_relativeLayout = parentView.findViewById(R.id.switch_RelativeLayout);
        this.ctx = ctx;
        ((MainActivity) ctx).setGestureListener(switch_relativeLayout.getGestureListener());
        mInflater = LayoutInflater.from(ctx);
        fillLayout(13);
    }

    private SwitchItemTitleView prevFill = null;

    private void fillLayout(int num) {
        if (switch_relativeLayout.getChildCount() != 0) {
            switch_relativeLayout.removeAllViews();
        } else {
            for (int i = 0; i < num; i++) {
                SwitchItemTitleView switchItemTitleView = (SwitchItemTitleView) mInflater.inflate(R.layout.item_mode_tab, null, false);
                switchItemTitleView.setTitleName("title" + i);
                switchItemTitleView.setTag(i);
                switchItemTitleView.setId(View.generateViewId());
                switchItemTitleView.setSwitchItemTitleViewClickListener(switch_relativeLayout);
                switch_relativeLayout.addView(switchItemTitleView);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) switchItemTitleView.getLayoutParams();
                if (prevFill == null) {
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                } else {
                    params.addRule(RelativeLayout.RIGHT_OF, prevFill.getId());
                    params.leftMargin = 30;
                }
                switchItemTitleView.setLayoutParams(params);

                prevFill = switchItemTitleView;
            }
        }
    }
}
