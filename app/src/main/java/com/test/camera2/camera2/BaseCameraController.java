package com.test.camera2.camera2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.Surface;

import java.util.List;

/**
 * Created by cmm on 2019/12/5.
 *
 */

public abstract class BaseCameraController {
    protected final Context mContext;
    private final Handler mHandler;
    protected String mCameraID;
    protected CameraManager mCameraManager;
    protected List<Surface> mSurfaceList;
    protected CaptureRequest.Builder repeatingBuild;

    public BaseCameraController(Context ctx, Handler handler) {
        this.mContext = ctx;
        this.mHandler = handler;
    }

    protected abstract void onCreate();

    protected abstract void onResume();

    protected abstract void onPause();

    protected abstract void onDestroy();

    protected void openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mCameraManager.openCamera(mCameraID, new CameraDeviceStatusCallBack(), null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private CameraDevice camera;
    private class CameraDeviceStatusCallBack extends CameraDevice.StateCallback {


        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            BaseCameraController.this.camera = camera;
            Message message = mHandler.obtainMessage(1, camera);
            mHandler.sendMessage(message);
            try {
                camera.createCaptureSession(mSurfaceList, new CameraCaptureSessionStateCallback(), null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

        }
    }
    private class CameraCaptureSessionStateCallback extends CameraCaptureSession.StateCallback {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            try {
                session.setRepeatingRequest(configurationBuild(camera).build(), new CameraCaptureSessionCaptureCallback(), null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

        }
    }

    private class CameraCaptureSessionCaptureCallback extends CameraCaptureSession.CaptureCallback {
        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
        }
    }

    private CaptureRequest.Builder configurationBuild(CameraDevice cameraDevice) {
        CaptureRequest.Builder request = null;
        try {
            request = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        request.addTarget(mSurfaceList.get(0));
        return request;
    }
}
