package com.teamfrugal.budgetapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Class:           cameraOCR
 * Desc:            Sets up all of the required Methods that handle the camera and drawing of the
 *                  camera on a surface.
 * Related Layout:  N/A
 * Called from:     cameraActivity.java
 * Calls:           OCR.java <---- Used to interface with the OCR Library. Need to refactor because
 *                                 it will no longer be called within this file.
 */

public class cameraOCR extends SurfaceView implements SurfaceHolder.Callback {
    private Camera ocrCamera;
    private SurfaceHolder ocrHolder;
    File directory;
    AssetManager assets;
    Context context;
    private int num = 0;
    private int i = 0;
    OCR ocrObject;
    boolean previewOpen;
    OcrBox box;

    public cameraOCR(Context context, Camera camera, File dir, AssetManager assets, OcrBox box) {
        super(context);
        this.context = context;
        ocrCamera = camera;
        ocrCamera.setDisplayOrientation(90);
        // Install a SurfaceHolder.Callback so we get notifed when the
        // underlying surface is created and destroyed.
        ocrHolder = getHolder();
        ocrHolder.addCallback(this);
        ocrHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        directory = dir;
        this.assets = assets;
        ocrObject = new OCR(directory, assets);
        this.box = box;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (previewOpen) {
            ocrCamera.stopPreview();
        }

        try {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
            ocrCamera.setPreviewDisplay(ocrHolder);
            ocrCamera.startPreview();
            Camera.Parameters p = ocrCamera.getParameters();
            p.setRotation((info.orientation + 360) % 360);
            p.setJpegQuality(100);
            p.setPictureSize(1920, 1080);
            ocrCamera.setParameters(p);
            previewOpen = true;
        } catch (IOException e) {
            CharSequence error = "Surface unable to be created";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this.getContext(), error, duration);
            toast.show();
            //e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (ocrHolder.getSurface() == null)
            return;

        try {
            ocrCamera.stopPreview();
        } catch (Exception ex) {
            CharSequence error = "Camera not running";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this.getContext(), error, duration);
            toast.show();
        }

        try {
            ocrCamera.setPreviewDisplay(ocrHolder);
            ocrCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        ocrCamera.setPreviewCallback(new Camera.PreviewCallback() {
//            @Override
//            public void onPreviewFrame(byte[] data, Camera camera) {
//                Camera.Parameters p = camera.getParameters();
//                int width = p.getPreviewSize().width;
//                int height = p.getPreviewSize().height;
//
//                TextView text = (TextView) ((Activity) context).findViewById(R.id.OCRview);
//
//                //if (i++ % 120 == 0) {
//                //if (i++ != 30) {
//                int corners[] = new int[4];
//                box.getCorners(corners);
//                int leftx = corners[0];
//                int rightx = corners[1];
//                int topy = corners[2];
//                int bottomy = corners[3];
//
//             //   System.out.println("left: " + leftx + " -- right: " + rightx);
//           //     System.out.println("top: " + topy + " -- bottom: " + bottomy);
//
//
//                //byte[] newdata = new byte[data.length];
//                //rotate(data, newdata, height, width);
//                //int temp = height;
//                //height = width;
//                //width = temp;
//
//                //YuvImage yuv = new YuvImage(data, ImageFormat.NV21, width, height, null);
//                //ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                //yuv.compressToJpeg(new Rect(bottomy, leftx, topy, rightx), 60, bos);
//                //yuv.compressToJpeg(new Rect(0, 0, width, height), 100, bos);
//                //byte[] d = bos.toByteArray();
//                //Bitmap b = BitmapFactory.decodeByteArray(d, 0, d.length);
////              Matrix m = new Matrix();
////              m.postRotate(90);
//
////              b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
////              saveImage(b);
//
////              ocrObject.setBitmap(b);
////              int c = ocrObject.getOCRConfidence();
////              if (c > 70) {
////                  String result = ocrObject.getOCRResult() + "";
////                  text.setText(result);
////              }
//            }
//        });
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (ocrCamera != null) {
            ocrCamera.stopPreview();
            ocrCamera.setPreviewCallback(null);
            ocrHolder = null;
            ocrCamera.release();
            ocrObject.onDestroy();
            previewOpen = false;
        }
    }
}