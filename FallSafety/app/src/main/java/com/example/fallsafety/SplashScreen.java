package com.example.fallsafety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        int time=2000;



        //Introducimos un tiempo de delay para que la actividad se ejecute durante pocos segundos
        new Handler().postDelayed(new Runnable() {
            @Override
            //Ejecuta lo que hay a continuación despues de cierto tiempo
            public void run() {
                Intent intent = new Intent (SplashScreen.this, MainActivity.class);
                startActivity(intent);
            }
        },time); //4 segundos

    }



}
