package com.teamfrugal.budgetapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Class:           MainActivity
 * Desc:            Main Activity view when the app opens. Contains one button which opens the cameraActivity view.
 * Related layout:  activity_main.xml
 * Called from:     N/A
 * Calls:           cameraActivity.java
 */


public class MainActivity extends AppCompatActivity {
    FloatingActionButton button;

    //Matthew stuff
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (FloatingActionButton) findViewById(R.id.camera);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this,
                        cameraActivity.class);
                startActivity(intent);
            }
        });
    }

    //Pedro stuff

    ///....
    private byte[] imgByteArray;// cropped image as a array of bytes

    // Need to create thread to handle socket handling
    // AsyncTask is slower compared to using threads???
    private class ClientThread extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... x) {
            String result = "error";
            try {
                Client user = new Client();
                user.connect("136.168.201.100", 43281);
                user.sendImage(imgByteArray);
                user.close();
                return user.getResult();
            } catch (Exception e) {
                StringWriter s = new StringWriter();
                PrintWriter p = new PrintWriter(s);
                e.printStackTrace(p);
                Log.d("error:", s.toString());
                return result;
            }
        }

        protected void onProgressUpdate() {
        }

        protected void onPostExecute(String result) {
            //THIS IS MISSING A TEXTVIEW DECLARATION, COMMENTED OUT FOR NOW =LL
            //textview.setText(result);
        }
    }

    // This code handles the default android activities for cropping and taking a picture
    @Override
    public void onActivityResult(int code, int result, Intent act) {
        // camera activity returned, start crop activity
        if (code == 1 && result == Activity.RESULT_OK) {
            //Toast.makeText(getApplicationContext(), "crop started", Toast.LENGTH_SHORT).show();
            Log.d("test", "crop!");
            //there's a reference to "cam" here. I made Camera static to fix this, but it
            //may have horribly broken other stuff - LL
            Camera.performCrop();
        }
        // crop completed, store result in sd card and send to server
        // creates thead and listens for server's reply
        else if (code == 2 && result == Activity.RESULT_OK) {
            Bundle extra = act.getExtras();
            Bitmap cropImg = extra.getParcelable("data");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            cropImg.compress(Bitmap.CompressFormat.PNG, 100, out);
            this.imgByteArray = out.toByteArray();

            String path = Environment.getExternalStorageDirectory().toString() + "/budgetapp/imgs/";
            File img = new File(path + "crop.jpg");
            try {
                FileOutputStream w = new FileOutputStream(img);
                w.write(imgByteArray);
                w.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ClientThread().execute();
        }
        // error!
        else if (code == 1 || code == 2)
            Log.d("error", "image not saved!");
    }

}
