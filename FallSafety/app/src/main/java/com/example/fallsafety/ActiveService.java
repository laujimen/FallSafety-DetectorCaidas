package com.example.fallsafety;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;



public class ActiveService extends Service implements SensorEventListener, TextToSpeech.OnInitListener {

    /**
     * Sensores
     */
    private SensorManager mSensorManager;
    private Sensor mSensorAcc;
    double x, y, z;
    double medX, medY, medZ;

    int i = 0;

    boolean min = false;
    boolean max = false;
    boolean vertical = false;


    /*
    Variables TTS
     */
    private TextToSpeech tts = null;



    double totalX = 0;
    double totalY = 0;
    double totalZ = 0;

    /*
     * Notificaciones
     */

    private final static String CHANNEL_ID = "NOTIFICACION";
    public final static int NOTIFICACION_ID = 0;


    static CountDownTimer countDownTimer;
    Emergencia emergencia;


    public ActiveService() {

    }

    @Override
    public void onCreate() {


        //Se inicializa el sensor acelerometro
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensorAcc, SensorManager.SENSOR_DELAY_NORMAL);
        System.out.println("activa servicio");
        Toast.makeText(this, "SERVICIO ACTIVADO", Toast.LENGTH_SHORT).show();

        emergencia = new Emergencia();
        emergencia.getLocation();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            System.out.println("no hay permsisos de lectura del telefono");
        }

        tts = new TextToSpeech(this, this);


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        /*
        Al llamar al método el servicio se detiene, se destruye
         */
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        System.out.println("Desactiva servicio");
        Toast.makeText(this, "SERVICIO DESACTIVADO", Toast.LENGTH_SHORT).show();


        countDownTimer.cancel();

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

    }


    @Override
    public void onInit(int status) {
        //Check for successful instantiation
        if (status == TextToSpeech.SUCCESS) {
            tts.setPitch(0.8f);
            tts.setSpeechRate(1.1f);
        } else if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "Text To Speech failed... ", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {


               /*
               Cuando los sensores cambian se recogen los valores y se guardan en x,y,z.
               */

                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                double sum = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
                double acel = sum / SensorManager.GRAVITY_EARTH;
                
                /*
                INCLUIR ALGORITMO DE DETECCIÓN
                */


            }
        }
    }


    public void fallDetected() {

        /*
        Este método implementa las acciones a realizar en caso de detectar una caída
        - SUENE UNA VOZ INDICANDO QUE SE HA PRODUCIDO UNA CAÍDA
        - 20 SEGUNDOS DE ESPERA PARA PODER CANCELAR
        - LLAMAR A CADA CONTACTO DE LA BASE DE DATOS
        - MANDAR SMS EN CASO DE NO RESPUESTA
         */


        Toast.makeText(this, "CAIDA", Toast.LENGTH_LONG).show();
        String textCaida = getResources().getString(R.string.textCaida);
        textSpoken(textCaida);


    }




    private void crearNotif() {
        //Creacion Intents y pending intents de los botones de la app
        Intent intentCancelar = new Intent(this, PostFallDetected.class);
        PendingIntent pendingIntentCancelar = PendingIntent.getActivity(this, 0, intentCancelar, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher_foreground);
        builder.setContentTitle("CAIDA DETECTADA");
        builder.setContentText("Se llamará a su contacto de emergencia");
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setVibrate(new long[]{1000, 1000, 2000, 1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setContentIntent(pendingIntentCancelar);
        builder.setAutoCancel(true);


        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(NOTIFICACION_ID, builder.build());


    }

    private void crearCanalNotificacion() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }

    private void textSpoken(String text) {

        tts.speak(text, TextToSpeech.QUEUE_ADD, null, "speak");
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
