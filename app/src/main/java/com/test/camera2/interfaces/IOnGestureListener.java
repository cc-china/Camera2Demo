package com.test.camera2.interfaces;

import android.view.MotionEvent;

/**
 * Created by cmm on 2019/11/28.
 */

public interface IOnGestureListener {
    void onDown(MotionEvent event);
    void onUp(MotionEvent event);
    void onMove(MotionEvent event);
    void onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
    void onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy);
}
