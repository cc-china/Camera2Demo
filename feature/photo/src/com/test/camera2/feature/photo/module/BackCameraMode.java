package com.test.camera2.module;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.test.camera2.camera2.BaseCameraMode;
import com.test.camera2.camera2.ICameraMode;


/**
 * Created by cmm on 2019/12/5.
 *
 */

public class BackCameraMode extends BaseCameraMode implements ICameraMode {

    private final BackCameraController backCameraController;
    private final CameraManager cameraManager;
    private String[] cameraIdList;


    public BackCameraMode(Context ctx, TextureView textureView) {
        super(ctx,textureView);
        setSurfaceView(textureView);
        Log.e("1111111111122222",System.currentTimeMillis()+"");
        backCameraController = new BackCameraController(mContext,mHandler);
        cameraManager = (CameraManager) ctx.getSystemService(Context.CAMERA_SERVICE);
        getCameraIdList();
        Log.e("1111111111133333",System.currentTimeMillis()+"");
    }

    @Override
    public void onCreate() {
        backCameraController.onCreate();
        backCameraController.setCameraID(cameraIdList[0]);
        backCameraController.setCameraManager(cameraManager);
        Log.e("111111111111144444",System.currentTimeMillis()+"");
        backCameraController.updateCameraSurfaceList(mSurfaceList);

        onResume();
    }

    @Override
    public void onResume() {
        backCameraController.onResume();
    }

    @Override
    public void onPause() {
        backCameraController.onPause();
    }

    @Override
    public void onDestroy() {
        backCameraController.onDestroy();
    }

    @Override
    protected void takePicture(CameraCaptureSession session) {
        try {
            session.capture(createCaptureImageRequest(), cameraCaptureSessionCaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //创建captureRequest
    private CaptureRequest createCaptureImageRequest() {
        CaptureRequest.Builder takePictureCaptureRequest = null;
        try {
            takePictureCaptureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            takePictureCaptureRequest.addTarget(mSurfaceList.get(1));
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return takePictureCaptureRequest.build();
    }

    private void getCameraIdList() {
        try {
            cameraIdList = cameraManager.getCameraIdList();
        } catch (CameraAccessException e) {
            e.printStackTrace();
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

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    BackCameraMode.super.cameraDevice = (CameraDevice) msg.obj;
                    backCameraController.configurationRepeatingBuild(configurationBuild(cameraDevice));
                    break;
            }
            return false;
        }
    });



    private void setSurfaceView(TextureView textureView) {
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                surface.setDefaultBufferSize(width,height);
                mSurfaceList.add(new Surface(surface));
                Log.e("11111111111111111111",System.currentTimeMillis()+"");
                onCreate();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

}
