package com.test.camera2;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String[] test = {Manifest.permission.CAMERA
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final String DCIM_CAMERA_FOLDER_ABSOLUTE_PATH =
            Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + "/Camera";
    private TextureView textureView;
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

        mode = new BackCameraMode(this,textureView);

        TextView tv_context = findViewById(R.id.tv_context);
        ImageView iv_show = findViewById(R.id.iv_show);
        tv_context.setOnClickListener(this);
        SwitchItemManager switchItemManager = new SwitchItemManager(this, (ViewGroup) findViewById(R.id.relativeLayout));
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
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_context:
                break;
        }
    }

    private class mSurfaceTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return gestureManager.getOnTouchListener().onTouch(view,motionEvent);
        }
    }
}
