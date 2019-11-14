/*
 *   Copyright Statement:
 *
 *     This software/firmware and related documentation ("MediaTek Software") are
 *     protected under relevant copyright laws. The information contained herein is
 *     confidential and proprietary to MediaTek Inc. and/or its licensors. Without
 *     the prior written permission of MediaTek inc. and/or its licensors, any
 *     reproduction, modification, use or disclosure of MediaTek Software, and
 *     information contained herein, in whole or in part, shall be strictly
 *     prohibited.
 *
 *     MediaTek Inc. (C) 2016. All rights reserved.
 *
 *     BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 *    THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 *     RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER
 *     ON AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL
 *     WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 *     WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 *     NONINFRINGEMENT. NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH
 *     RESPECT TO THE SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY,
 *     INCORPORATED IN, OR SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES
 *     TO LOOK ONLY TO SUCH THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO.
 *     RECEIVER EXPRESSLY ACKNOWLEDGES THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO
 *     OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES CONTAINED IN MEDIATEK
 *     SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK SOFTWARE
 *     RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 *     STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S
 *     ENTIRE AND CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE
 *     RELEASED HEREUNDER WILL BE, AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE
 *     MEDIATEK SOFTWARE AT ISSUE, OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE
 *     CHARGE PAID BY RECEIVER TO MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 *     The following software/firmware and/or related documentation ("MediaTek
 *     Software") have been modified by MediaTek Inc. All revisions are subject to
 *     any receiver's applicable license agreements with MediaTek Inc.
 */

package com.test.camera2.permissionTest;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;


/**
 * The KeyguardManager service can be queried to determine which state we are in.
 * If started from the lock screen, the activity may be quickly started,
 * resumed, paused, stopped, and then started and resumed again. This is
 * problematic for launch time from the lock screen because we typically open the
 * camera in onResume() and close it in onPause(). These camera operations take
 * a long time to complete. To workaround it, this class filters out
 * high-frequency onResume()->onPause() sequences if the KeyguardManager
 * indicates that we have started from the lock screen.
 *
 * Subclasses should override the appropriate onPermission[Create|Start...]Tasks()
 * method in place of the original.
 *
 * Sequences of onResume() followed quickly by onPause(), when the activity is
 * started from a lockscreen will result in a quick no-op.
 */
public abstract class QuickActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    /** onResume tasks delay from secure lockscreen. */
    private static final long ON_RESUME_DELAY_SECURE_MILLIS = 30;
    /** onResume tasks delay from non-secure lockscreen. */
    private static final long ON_RESUME_DELAY_NON_SECURE_MILLIS = 15;

    /** A reference to the main handler on which to run lifecycle methods. */
    private Handler mMainHandler;

    /**
     * True if onResume tasks have been skipped, and made false again once they
     * are executed within the onResume() method or from a delayed Runnable.
     */
    private boolean mSkippedFirstOnResume = false;

    /** Was this session started with onCreate(). */
    protected boolean mStartupOnCreate = false;

    /** Handle to Keyguard service. */
    private KeyguardManager mKeyguardManager = null;
    /**
     * A runnable for deferring tasks to be performed in onResume() if starting
     * from the lockscreen.
     */
    private final Runnable mOnResumeTasks = new Runnable() {
        @Override
        public void run() {
            if (mSkippedFirstOnResume) {
                // Doing the tasks, can set to false.
                mSkippedFirstOnResume = false;
                onPermissionResumeTasks();
            }
        }
    };

    @Override
    protected final void onNewIntent(Intent intent) {
        setIntent(intent);
        super.onNewIntent(intent);
        onNewIntentTasks(intent);
    }
    @Override
    protected final void onCreate(Bundle bundle) {
        mStartupOnCreate = true;
        super.onCreate(bundle);
        mMainHandler = new Handler(getMainLooper());
        onPermissionCreateTasks(bundle);
    }

    @Override
    protected final void onStart() {
        onPermissionStartTasks();
        super.onStart();
    }

    @Override
    protected final void onResume() {
        startPreWarmService();
        // For lockscreen launch, there are two possible flows:
        // 1. onPause() does not occur before mOnResumeTasks is executed:
        //      Runnable mOnResumeTasks sets mSkippedFirstOnResume to false
        // 2. onPause() occurs within ON_RESUME_DELAY_*_MILLIS:
        //     a. Runnable mOnResumeTasks is removed
        //     b. onPermissionPauseTasks() is skipped, mSkippedFirstOnResume remains true
        //     c. next onResume() will immediately execute onPermissionResumeTasks()
        //        and set mSkippedFirstOnResume to false

        mMainHandler.removeCallbacks(mOnResumeTasks);
        if (isKeyguardLocked() && mSkippedFirstOnResume == false) {
            // Skipping onPermissionResumeTasks; set to true.
            mSkippedFirstOnResume = true;
            long delay = isKeyguardSecure() ? ON_RESUME_DELAY_SECURE_MILLIS :
                    ON_RESUME_DELAY_NON_SECURE_MILLIS;
            mMainHandler.postDelayed(mOnResumeTasks, delay);
        } else {
            // Doing the tasks, can set to false.
            mSkippedFirstOnResume = false;
            onPermissionResumeTasks();
        }
        super.onResume();
    }

    @Override
    protected final void onPause() {
        mMainHandler.removeCallbacks(mOnResumeTasks);
        // Only run onPermissionPauseTasks if we have not skipped onPermissionResumeTasks in a
        // first call to onResume.  If we did skip onPermissionResumeTasks (note: we
        // just killed any delayed Runnable), we also skip onPermissionPauseTasks to
        // adhere to lifecycle state machine.
        if (mSkippedFirstOnResume == false) {
            onPermissionPauseTasks();
        }
        super.onPause();
        mStartupOnCreate = false;
    }

    @Override
    protected final void onStop() {
        onPermissionStopTasks();
        super.onStop();
    }

    @Override
    protected final void onRestart() {
        super.onRestart();
    }

    @Override
    protected final void onDestroy() {
        onPermissionDestroyTasks();
        super.onDestroy();
    }

    protected boolean isKeyguardLocked() {
        boolean isLocked = false;
        KeyguardManager kgm = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (kgm != null) {
            isLocked = kgm.isKeyguardLocked();
        }
        return isLocked;
    }

    protected boolean isKeyguardSecure() {
        boolean isSecure = false;
        KeyguardManager kgm = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (kgm != null) {
            isSecure = kgm.isKeyguardSecure();
        }
        return isSecure;
    }

    /**
     * Subclasses should override this.
     */
    protected void onNewIntentTasks(Intent newIntent) {
    }

    /**
     * These are for permission check flow.
     * @param savedInstanceState create bundle to sub class.
     */
    protected void onPermissionCreateTasks(Bundle savedInstanceState) {
    }

    protected void onPermissionStartTasks() {
    }

    protected void onPermissionResumeTasks() {
    }

    protected void onPermissionPauseTasks() {
    }

    protected void onPermissionStopTasks() {
    }

    protected void onPermissionDestroyTasks() {
    }

    private void startPreWarmService() {
//        if (!CameraUtil.isServiceRun(getApplicationContext(),
//                "com.mediatek.camera.CameraAppService")) {
//            Intent appServiceIntent = new Intent(getApplicationContext(),
//                    CameraAppService.class);
//            CameraUtil.startService(getApplicationContext(), appServiceIntent);
//        }
    }
}
