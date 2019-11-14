package com.test.camera2.permissionTest;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

import java.io.File;

import com.test.com.myapplication.R;

/**
 * Created by cmm on 2019/11/13.
 */

public class TestActivity extends Activity {

    String[] test = {Manifest.permission.CAMERA
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final String DCIM_CAMERA_FOLDER_ABSOLUTE_PATH =
            Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + "/Camera";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv_context = findViewById(R.id.tv_context);

        ActivityCompat.requestPermissions(
                this, test, 1);
        File dir = new File(DCIM_CAMERA_FOLDER_ABSOLUTE_PATH);
        dir.mkdirs();
        if ( !dir.canWrite()) {
            tv_context.setText("错误");
        } else {
            tv_context.setText("正确" + dir.canWrite());
        }
    }

    /*@Override
    protected void onCreateTasks(Bundle savedInstanceState) {
        super.onCreateTasks(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv_context = findViewById(R.id.tv_context);

        ActivityCompat.requestPermissions(
                this, test, 1);
        File dir = new File(DCIM_CAMERA_FOLDER_ABSOLUTE_PATH);
        dir.mkdirs();
        if ( !dir.canWrite()) {
            tv_context.setText("错误");
        } else {
            tv_context.setText("正确" + dir.canWrite());
        }
    }*/
}
