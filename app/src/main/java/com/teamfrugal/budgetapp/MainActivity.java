package com.teamfrugal.budgetapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * Class:           MainActivity
 * Desc:            Main Activity view when the app opens. Contains one button which opens the cameraActivity view.
 * Related layout:  activity_main.xml
 * Called from:     N/A
 * Calls:           cameraActivity.java
 */


public class MainActivity extends AppCompatActivity {
    FloatingActionButton button;

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
}
