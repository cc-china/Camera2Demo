package com.test.camera2.permissionTest;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

import com.test.camera2.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


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

        try {
            FileOutputStream fos = new FileOutputStream("a.txt");
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(new Object());
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FileInputStream fis = new FileInputStream("a.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object o = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
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
