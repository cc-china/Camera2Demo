package com.test.camera2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.camera2.interfaces.IOnGestureListener;
import com.test.camera2.manager.GestureManager;
import com.test.camera2.manager.SwitchItemManager;
import com.test.camera2.module.BackCameraMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String[] test = {Manifest.permission.CAMERA
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final String DCIM_CAMERA_FOLDER_ABSOLUTE_PATH =
            Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + "/Camera";
    private TextureView textureView;
    private SurfaceTexture surfaceTexture;
    private List<Surface> previewSurface = new ArrayList<>();
    private Surface imageReaderSurface;
    private ImageView iv_show;
    private CameraCharacteristics cameraCharacteristics;
    private SwitchItemManager switchItemManager;
    private GestureManager gestureManager;
    private BackCameraMode mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(
                this, test, 1);
        setContentView(R.layout.activity_main);
        gestureManager = new GestureManager(this);

        textureView = findViewById(R.id.camera_preview);
        textureView.setOnTouchListener(new mSurfaceTouchListener());

        //setTextureViewListener();
        mode = new BackCameraMode(this,textureView);

        TextView tv_context = findViewById(R.id.tv_context);
        iv_show = findViewById(R.id.iv_show);
        tv_context.setOnClickListener(this);
        switchItemManager = new SwitchItemManager(this, (ViewGroup) findViewById(R.id.relativeLayout));
        File dir = new File(DCIM_CAMERA_FOLDER_ABSOLUTE_PATH);
        dir.mkdirs();
        if (!dir.canWrite()) {
            tv_context.setText("错误");
        } else {
            tv_context.setText("拍照" + dir.canWrite());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       //mode.onResume();
    }

    public void setGestureListener(IOnGestureListener listener) {
        gestureManager.registerGestureListener(listener);
    }

    private void setTextureViewListener() {
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                MainActivity.this.surfaceTexture = surfaceTexture;
                MainActivity.this.surfaceTexture.setDefaultBufferSize(500, 500);
                MainActivity.this.previewSurface.add(new Surface(MainActivity.this.surfaceTexture));
                MainActivity.this.previewSurface.add(createImageReader().getSurface());

                getCamera2Manager();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });
    }

    private void getCamera2Manager() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        getCamera2IDList(cameraManager);
    }

    private void getCamera2IDList(CameraManager cameraManager) {
        try {
            String[] cameraIdList = cameraManager.getCameraIdList();
            //获取相机信息，这个信息是只读的
            cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraIdList[0]);
            //获取该设备支持的所有预览尺寸
            StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert streamConfigurationMap != null;
            Size[] outputSizes = streamConfigurationMap.getOutputSizes(SurfaceTexture.class);
            //开启相机
            openCamera2(cameraManager, cameraIdList);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera2(CameraManager cameraManager, String[] cameraIdList) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            cameraManager.openCamera(cameraIdList[0], CameraDeviceStateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraDevice cameraDevice;
    private CameraDevice.StateCallback CameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            //创建一个Session会话，用来获取相机预览数据
            try {
                MainActivity.this.cameraDevice = cameraDevice;
                cameraDevice.createCaptureSession(MainActivity.this.previewSurface, CameraCaptureSessionStateCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {

        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    private CameraCaptureSession cameraCaptureSession;
    CameraCaptureSession.StateCallback CameraCaptureSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
            //cameraCaptureSession 上开启预览
            try {
                MainActivity.this.cameraCaptureSession = cameraCaptureSession;
                CaptureRequest.Builder request = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                request.addTarget(previewSurface.get(0));
                CaptureRequest build = request.build();
                cameraCaptureSession.setRepeatingRequest(build, CameraCaptureSessionSaptureCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

        }
    };


    private CameraCaptureSession.CaptureCallback CameraCaptureSessionSaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
//            try {
//                CaptureRequest.Builder requests = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//                requests.addTarget(previewSurface.get(0));
//                CaptureRequest build = requests.build();
//                session.capture(build, CameraCaptureSessionSaptureCallback, null);
//            } catch (CameraAccessException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
        }
    };


    /**
     * 拍照demo
     */
    //拿到拍照后照片信息
    private CameraCaptureSession.CaptureCallback cameraCaptureSessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {
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
    //拿到拍照后照片元数据
    private ImageReader.OnImageAvailableListener imageReaderOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader imageReader) {
            Image image = imageReader.acquireNextImage();
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();

            byte[] data68 = ImageUtil.getBytesFromImageAsType(image, 2);
            int[] rgb = ImageUtil.decodeYUV420SP(data68, imageWidth, imageHeight);
            Bitmap bitmap2 = Bitmap.createBitmap(rgb, 0, imageWidth,
                    imageWidth, imageHeight,
                    android.graphics.Bitmap.Config.ARGB_8888);
            iv_show.setImageBitmap(bitmap2);

        }
    };

    //选择合适尺寸
    private Size getOptionSize(CameraCharacteristics characteristics, Class clazz, int maxWidth, int maxHeight) {
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        Size[] size = map.getOutputSizes(clazz);
        float aspectRatio = ((float) maxWidth) / ((float) maxHeight);
        for (int i = 0; i < size.length; i++) {
            if (((float) size[i].getWidth()) / ((float) size[i].getHeight()) == aspectRatio && size[i].getWidth() <= maxWidth && size[i].getHeight() <= maxHeight) {
                return size[i];
            }
        }
        return null;
    }

    //创建ImageReader
    private ImageReader createImageReader() {
        Size optionSize = getOptionSize(cameraCharacteristics, ImageReader.class, 1920, 1080);
        ImageReader imageReader = ImageReader.newInstance(
                optionSize.getWidth(), optionSize.getHeight(), ImageFormat.JPEG, 2);
        imageReader.setOnImageAvailableListener(imageReaderOnImageAvailableListener, null);
        return imageReader;
    }

    //创建captureRequest
    private CaptureRequest createCaptureImageRequest() {
        CaptureRequest.Builder takePictureCaptureRequest = null;
        try {
            takePictureCaptureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
//            takePictureCaptureRequest.addTarget(previewSurface.get(0));
            takePictureCaptureRequest.addTarget(MainActivity.this.previewSurface.get(1));
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return takePictureCaptureRequest.build();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_context:
                takePicture(cameraCaptureSession);
                break;
        }
    }

    private void takePicture(CameraCaptureSession cameraCaptureSession) {

        try {
            cameraCaptureSession.capture(createCaptureImageRequest(), cameraCaptureSessionCaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private class mSurfaceTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return gestureManager.getOnTouchListener().onTouch(view,motionEvent);
        }
    }
}
