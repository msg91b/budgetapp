package com.teamfrugal.budgetapp;

/**
 * Created by Wanderlast on 3/6/2017.
 */

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

public class Camera {
    private static MainActivity main;
    private static Uri outputFileUri;
    private Image pic;

    // MainActivity class must be the activity that launches camera/crop
    Camera(MainActivity main) {
        this.main = main;
    }

    public static void performCrop() {
        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(outputFileUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 2);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);//image output resolution
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            //cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFile);
            // start the activity - we handle returning in onActivityResult
            main.startActivityForResult(cropIntent, 2);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Log.d("error", "This device doesn't support the crop action!");
        }
    }


    /* 	starts the default camera activity.
    * - depending on the phone's manufacturer the image can be saved both in the
    * 	SD card or in the gallery. Need to handle cases were the image is sent
    * 	to the gallery
    * - This code assumes external storage is available
    */
    public void takePic() {
        // creating path to store image, SD card
        String path = Environment.getExternalStorageDirectory().toString() + "/budgetapp/imgs/";
        System.out.println(path);
        // creating directory for path
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.d("error", "Error with creating image dir/path!");
                return;
            }
        } else {
            //Toast.makeText(getApplicationContext(), "Image ready to go!", Toast.LENGTH_LONG).show();
        }

        // create image file name "pic.jpg" and tie to output URI
        outputFileUri = Uri.fromFile(new File(path + "/pic.jpg"));

        // create camera activity
        Intent camActivity = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // camera activity stores result in URI output
        camActivity.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        if (camActivity.resolveActivity(main.getPackageManager()) != null) {
            main.startActivityForResult(camActivity, 1);
        } else
            Log.d("error", "phone cam support is not available");
    }
}
