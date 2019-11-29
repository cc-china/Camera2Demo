package com.test.camera2.manager;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.test.camera2.interfaces.IOnGestureListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cmm on 2019/11/28.
 */

public class GestureManager {

    private final Context ctx;
    private List<IOnGestureListener> mListeners = new ArrayList<>();
    private final GestureDetector gestureDetector;

    public GestureManager(Context ctx) {
        this.ctx = ctx;
        gestureDetector = new GestureDetector(ctx, new CustomerGestureDetector());
    }

    public void registerGestureListener(IOnGestureListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public View.OnTouchListener getOnTouchListener() {
        return onTouchListener;
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            gestureDetector.onTouchEvent(motionEvent);
            return true;
        }
    };

    private class CustomerGestureDetector implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            for (IOnGestureListener item : mListeners) {
                 item.onDown(motionEvent);
            }
            return true;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            for (IOnGestureListener item : mListeners) {
                item.onScroll(motionEvent, motionEvent1, v, v1);
            }
            return false;
        }
    }
}
