package com.teamfrugal.budgetapp.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.teamfrugal.budgetapp.R;
import com.teamfrugal.budgetapp.camera.CameraPreview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.FileHandler;

/**
 * Class:               cameraActivity
 * Desc:                This class contains code that creates a new ocr preview which is then drawn
 *                      to the screen. The OCR preview is created by calling the cameraOCR constructor
 *                      which is laid out in cameraOCR.java
 *
 * Related layout:     activity_camera.xml
 * Called from:        ShowShoppingListActivity.java
 * Calls:              cropImage.java, cameraOCR.java
 */

public class CameraActivity extends AppCompatActivity {
    /** Check if this device has a camera */
    private FrameLayout preview;
    private Camera ocrCamera = null;
    private CameraPreview ocrPreview = null;
    private int flashOn = 0;
    FloatingActionButton snapPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        onResume();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Receipt");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        snapPhoto = (FloatingActionButton) findViewById(R.id.snap);

        snapPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                ocrCamera.takePicture(null, null, savePicture);
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //ocrCamera.stopPreview();
                Intent intent = new Intent(CameraActivity.this, CropActivity.class);
                startActivity(intent);
            }
        });
    }


    Camera.PictureCallback savePicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            System.out.println("picture taken");
            int width = ocrPreview.getWidth();
            int height = ocrPreview.getHeight();


            File f = new File(Environment.getExternalStorageDirectory() + "/image.jpeg");
            BitmapFactory.Options op = new BitmapFactory.Options();
            Bitmap bm = BitmapFactory.decodeByteArray(data,0, data.length, op);


            if (bm.getHeight() == 1080) {
                try {
                    System.out.println("rotating");


                    Matrix matrix = new Matrix();

                    matrix.setRotate(90, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);

                    Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, 1920, 1080, matrix, true);
                    FileOutputStream ff = new FileOutputStream(f);

                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, ff);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    FileOutputStream ff = new FileOutputStream(f);
                    ff.write(data);
                    ff.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Camera.Parameters p = ocrCamera.getParameters();

        if (id == R.id.torch) {
            if (flashOn == 1) {
                if (p.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_OFF)) {
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    ocrCamera.setParameters(p);
                }
                flashOn = 0;
            } else {
                if (p.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    ocrCamera.setParameters(p);
                }
                flashOn = 1;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        ocrCamera = getCamera();

        ocrPreview = new CameraPreview(this, ocrCamera, getFilesDir(), getAssets());
        preview = (FrameLayout) findViewById(R.id.content_camera);
        preview.addView(ocrPreview);

        super.onResume();
    }

    public static Camera getCamera() {
        Camera camera = null;

        try {
            camera = Camera.open();

            Camera.Parameters p = camera.getParameters();

            // So no exceptions are thrown
            if (p.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            camera.setParameters(p);
        } catch (Exception e) {
            System.out.println("Unable to get instance of camera");
        }

        return camera;
    }
}