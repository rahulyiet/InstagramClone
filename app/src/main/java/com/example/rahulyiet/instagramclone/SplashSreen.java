package com.example.rahulyiet.instagramclone;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SplashSreen extends AppCompatActivity {

    Handler handler;
    TextView splashText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_sreen);
        Handler handler;
        splashText=findViewById(R.id.splashText);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashSreen.this,MainActivity.class);
                 startActivity(intent);

            }
        },2000);




    }
}
