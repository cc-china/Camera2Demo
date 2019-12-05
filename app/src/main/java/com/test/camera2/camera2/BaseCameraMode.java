package com.test.camera2.camera2;

import android.content.Context;
import android.graphics.SurfaceTexture;
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
    public BaseCameraMode(Context ctx,TextureView textureView) {
        this.mContext = ctx;

    }
}
