package com.test.camera2.camera2;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cmm on 2019/12/5.
 *
 */

public abstract class BaseCameraMode {

    protected final Context mContext;
    protected List<Surface> mSurfaceList = new ArrayList<>();
    protected CameraDevice cameraDevice;

    public BaseCameraMode(Context ctx, TextureView textureView) {
        this.mContext = ctx;

    }

    protected abstract void takePicture(CameraCaptureSession session);

    /**
     * 拍照demo
     */
    //拿到拍照后照片信息
    protected CameraCaptureSession.CaptureCallback cameraCaptureSessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }

        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        @Override
        public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
        }
    };
}
