package com.test.camera2.camera2;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.util.Size;

import com.test.camera2.ImageUtil;


/**
 * Created by cmm on 2019/12/5.
 *
 */

public class ImageReaderUtils {
    //创建ImageReader
    public static android.media.ImageReader createImageReader(CameraCharacteristics cameraCharacteristics) {
        Size optionSize = getOptionSize(cameraCharacteristics, android.media.ImageReader.class, 1920, 1080);
        android.media.ImageReader imageReader = android.media.ImageReader.newInstance(
                optionSize.getWidth(), optionSize.getHeight(), ImageFormat.JPEG, 2);
        imageReader.setOnImageAvailableListener(imageReaderOnImageAvailableListener, null);
        return imageReader;
    }

    //选择合适尺寸
    private static Size getOptionSize(CameraCharacteristics characteristics, Class clazz, int maxWidth, int maxHeight) {
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

    //拿到拍照后照片元数据
    private static ImageReader.OnImageAvailableListener imageReaderOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
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
            //iv_show.setImageBitmap(bitmap2);

        }
    };

}

