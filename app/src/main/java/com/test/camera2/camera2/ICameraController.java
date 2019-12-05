package com.test.camera2.camera2;

import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.view.Surface;

import java.util.List;

/**
 * Created by cmm on 2019/12/5.
 */

public interface ICameraController {
    void setCameraID(String cameraID);
    void setCameraManager(CameraManager cameraManager);
    void updateCameraSurfaceList(List<Surface> surfaceList);
    void configurationRepeatingBuild(CaptureRequest.Builder builder);

}
