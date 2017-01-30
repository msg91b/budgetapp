package com.teamfrugal.budgetapp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

/**
 * Class:           cropImage
 * Desc:            This class sets up the view for cropping an image after it's taken in the
 *                  cameraActivity view. When it's finished, two boxes from the OcrBox class will be
 *                  visible and able to be moved/scaled to fit the name of the store as well as the
 *                  total of the reciept. Those values will then be passed to wherever we need them
 *                  to go.
 * Related layout:  crop_layout.xml
 * Called from:     cameraActivity.java
 * Calls:           N/A
 */

public class cropImage extends AppCompatActivity{
    private ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mImageView = (ImageView) findViewById(R.id.imageview);
        mImageView.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/image.jpeg"));

        setSupportActionBar(toolbar);

    }
}
