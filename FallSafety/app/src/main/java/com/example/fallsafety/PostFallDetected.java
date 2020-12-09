package com.example.fallsafety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;

import android.widget.ImageButton;
import android.widget.Toast;


public class PostFallDetected extends AppCompatActivity implements View.OnClickListener {
    private Context mCtx =this;

    private ImageButton btnNoCaida;
    private ImageButton btnSiCaida;

    private CountDownTimer count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_fall_detected);


        btnNoCaida = (ImageButton) findViewById(R.id.btnNoCaida);
        btnNoCaida.setOnClickListener(this);
        btnSiCaida= (ImageButton) findViewById(R.id.btnSiCaida);
        btnSiCaida.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnNoCaida:

                cancelarServicio();

                Toast toast = Toast.makeText(this, "AVISO CANCELADO", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();

                break;

            case R.id.btnSiCaida:

                cancelarServicio();
                Intent intent= new Intent(this, Emergencia.class);
                startActivity(intent);

                break;

        }
    }

    public void cancelarServicio(){
        stopService(new Intent(mCtx, ActiveService.class));

        count = new CountDownTimer( 30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                startService(new Intent(mCtx, ActiveService.class));
            }
        }.start();

    }
}
