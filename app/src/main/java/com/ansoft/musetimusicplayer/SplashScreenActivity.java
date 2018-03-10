package com.ansoft.musetimusicplayer;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.os.Handler;


public class SplashScreenActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_WRITE_PERMISSION = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            closeNow();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        requestPermission();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashScreenActivity.this, SongListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        },15000);
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);

            //Toast.makeText(MainActivity.this, "Write External Storage permission allows us to do download files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            closeNow();
        }

    }

    private void closeNow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            finishAffinity();
        }

        else
        {
            finish();
        }
    }



}
