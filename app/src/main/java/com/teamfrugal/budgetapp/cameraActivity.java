package com.teamfrugal.budgetapp;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class:               cameraActivity
 * Desc:                This class contains code that creates a new ocr preview which is then drawn
 *                      to the screen. The OCR preview is created by calling the cameraOCR constructor
 *                      which is laid out in cameraOCR.java
 *
 * Related layout:     activity_camera.xml
 * Called from:        MainActivity.java
 * Calls:              cropImage.java, cameraOCR.java
 */

public class cameraActivity extends AppCompatActivity {
    /** Check if this device has a camera */
    private FrameLayout preview;
    private Camera ocrCamera = null;
    private cameraOCR ocrPreview = null;
    private int flashOn = 0;
    FloatingActionButton snapPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Create OCR box
        OcrBox box = new OcrBox(this);
        addContentView(box, new Toolbar.LayoutParams(Toolbar.LayoutParams.FILL_PARENT, Toolbar.LayoutParams.FILL_PARENT));

        try {
            ocrCamera = Camera.open();
        } catch (Exception e) {
            //
        }

        if (ocrCamera != null) {
            Camera.Parameters p = ocrCamera.getParameters();

            // So no exceptions are thrown
            if (p.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            ocrCamera.setParameters(p);
            ocrPreview = new cameraOCR(this, ocrCamera, getFilesDir(), getAssets(), box);
            preview = (FrameLayout) findViewById(R.id.content_camera);
            preview.addView(ocrPreview);
        }
        else {
            CharSequence error = "Camera unable to be initialized";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, error, duration);
            toast.show();
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Receipt");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        snapPhoto = (FloatingActionButton) findViewById(R.id.snap);

        snapPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                ocrCamera.takePicture(null, null, savePicture);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(cameraActivity.this, cropImage.class);
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
            try {
                f.createNewFile();
                FileOutputStream ff = new FileOutputStream(f);
                ff.write(data);
                ff.close();
            } catch (IOException e) {
                e.printStackTrace();
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
}