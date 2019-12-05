package com.test.camera2.customeview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.test.camera2.interfaces.IOnGestureListener;

/**
 * Created by cmm on 2019/11/28.
 */

public class SwitchItemRelativeLayout extends RelativeLayout implements SwitchItemTitleView.SwitchItemTitleViewClickListener {

    private final Scroller mScroller;
    private int mCurrentIndex = 0;

    public SwitchItemRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context, new DecelerateInterpolator());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //switchItemModeTitle(getChildCount() / 2, 0);
    }

    @Override
    public void switchItemTitleViewClick(View view) {
        switchItemModeTitle((int) view.getTag(), 1000);
    }

    private boolean isMovedBack = false;

    private void switchItemModeTitle(int index, int animationDuration) {
        if (index != mCurrentIndex) {
            if (index > mCurrentIndex) {
                isMovedBack = true;
            } else {
                isMovedBack = false;
            }
            performModeTitleAnimation(index, animationDuration);
        }
    }

    private void performModeTitleAnimation(int index, int animationDuration) {
        int dx = 0;
        int moveNum = Math.abs(index - mCurrentIndex);
        if (index < getChildCount()) {
            if (index == 0) {
                //直接跳到index为0
                dx = -getScrollX();
            } else if (isMovedBack) {
                //向后移动
                if (moveNum > 1) {
                    //向后移动大于一个mode
                    for (int i = 0; i < moveNum; i++) {
                        RelativeLayout.LayoutParams params = (LayoutParams) getChildAt(mCurrentIndex + i + 1).getLayoutParams();
                        dx += getChildAt(mCurrentIndex + i + 1).getMeasuredWidth() + params.leftMargin;
                    }
                } else {
                    //向后移动一个mode
                    RelativeLayout.LayoutParams params = (LayoutParams) getChildAt(mCurrentIndex + 1).getLayoutParams();
                    dx = getChildAt(mCurrentIndex + 1).getMeasuredWidth() + params.leftMargin;
                }
            } else if (moveNum > 1) {
                //向前移动大于一个mode
                for (int i = 0; i < moveNum; i++) {
                    RelativeLayout.LayoutParams params = (LayoutParams) getChildAt(mCurrentIndex - i - 1).getLayoutParams();
                    dx += -(getChildAt(mCurrentIndex - i - 1).getMeasuredWidth() + params.leftMargin);
                }
            } else {
                //向前移动一个mode
                RelativeLayout.LayoutParams params = (LayoutParams) getChildAt(mCurrentIndex - 1).getLayoutParams();
                dx = -(getChildAt(mCurrentIndex - 1).getMeasuredWidth() + params.leftMargin);
            }
        } else {
            throw new IndexOutOfBoundsException();
        }
        mCurrentIndex = index;
        mScroller.startScroll(getScrollX(), 0, dx, 0, animationDuration);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    public IOnGestureListener getGestureListener() {
        return new GestureListenerImpl();
    }

    private class GestureListenerImpl implements IOnGestureListener {
        @Override
        public void onDown(MotionEvent event) {

        }

        @Override
        public void onUp(MotionEvent event) {

        }

        @Override
        public void onMove(MotionEvent event) {

        }

        @Override
        public void onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        }

        @Override
        public void onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
            switchItemModeTitle(mCurrentIndex+1,1000);
        }
    }
}
