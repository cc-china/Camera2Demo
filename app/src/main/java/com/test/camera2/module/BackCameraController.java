package com.test.camera2.module;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Handler;
import android.view.Surface;

import com.test.camera2.camera2.BaseCameraController;
import com.test.camera2.camera2.ICameraController;
import com.test.camera2.camera2.ImageReaderUtils;

import java.util.List;

/**
 * Created by cmm on 2019/12/5.
 */

public class BackCameraController extends BaseCameraController implements ICameraController {


    BackCameraController(Context ctx, Handler mHandler) {
        super(ctx,mHandler);
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onResume() {
        //开启相机
        super.openCamera();
    }

    @Override
    protected void onPause() {
        //关闭相机
    }

    @Override
    protected void onDestroy() {

    }

    @Override
    public void setCameraID(String cameraID) {
        super.mCameraID = cameraID;
    }

    @Override
    public void setCameraManager(CameraManager cameraManager) {
        super.mCameraManager = cameraManager;
    }

    @Override
    public void updateCameraSurfaceList(List<Surface> surfaceList) {
        try {
            ImageReader imageReader = ImageReaderUtils.createImageReader(mCameraManager.getCameraCharacteristics(mCameraManager.getCameraIdList()[0]));
            surfaceList.add(imageReader.getSurface());
            super.mSurfaceList = surfaceList;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void configurationRepeatingBuild(CaptureRequest.Builder builder) {
        super.repeatingBuild = builder;
    }
}
