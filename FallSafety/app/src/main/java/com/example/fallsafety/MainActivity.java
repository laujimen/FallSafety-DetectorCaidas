package com.example.fallsafety;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;




public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mCtx =this;

    /*
 Variables TTS
  */
    private static int TTS_DATA_CHECK=1;

    private TextView textEstado;
    private Switch aSwitch;

    static ContactsDbAdapter dbAdapter;


    private ImageButton btnAdvice;

    public MainActivity(){

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        textEstado= (TextView) findViewById(R.id.textEstado);
        aSwitch= (Switch) findViewById(R.id.btnswitch);

        //creamos el adaptador de la BD y la abrimos
        dbAdapter = new ContactsDbAdapter(this);
        dbAdapter.open();

        // obtiene id de fila de la tabla si se le ha pasado (hemos pulsado un contacto para editarl0)
        Long mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(ContactsDbAdapter.KEY_ROWID);


        btnAdvice= (ImageButton) findViewById(R.id.btnadvice);
        btnAdvice.setOnClickListener(this);

        Emergencia emergencia = new Emergencia();
        emergencia.getLocation();

        /*
        Verificación TTS
         */

        Intent checkTTs= new Intent();
        checkTTs.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTs, TTS_DATA_CHECK);
    }

    @Override
    public void onClick(View v){

        Cursor contactsCursor = dbAdapter.fetchAllContacts();

        if(contactsCursor.getCount()==0) {

            Toast.makeText(this, getResources().getString(R.string.noContactos), Toast.LENGTH_SHORT).show();
        } else {

            if (v.getId() == btnAdvice.getId()) {
                Toast.makeText(this, "BOTÓN DE EMERGENCIA PULSADO", Toast.LENGTH_LONG).show();

                Intent intent= new Intent(this, Emergencia.class);
                startActivity(intent);

            }
        }
    }

    public void clickSwitch(View view){

        Cursor contactsCursor = dbAdapter.fetchAllContacts();

        if(contactsCursor.getCount()==0) {
            aSwitch.setChecked(false);
            Toast.makeText(this, getResources().getString(R.string.noContactos), Toast.LENGTH_SHORT).show();
        } else{
            if(view.getId() == R .id.btnswitch){

                if(aSwitch.isChecked()){
                    textEstado.setText(getResources().getString(R.string.estadoOn));
                    startService(new Intent(mCtx, ActiveService.class));
                } else {
                    textEstado.setText(getResources().getString(R.string.estadoOff));
                    stopService(new Intent(mCtx, ActiveService.class));
                }
            }

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TTS_DATA_CHECK) {

            if (resultCode != TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }





    public void Contact(View view){
        Intent contact= new Intent(this, ContactsActivity.class);
        startActivity(contact);
    }

    public void checkPermissions(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            int permsRequestCode = 100;
            String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS};
            int accessFinePermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int accessCoarsePermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            int callPermission = checkSelfPermission(Manifest.permission.CALL_PHONE);
            int smsPermission= checkSelfPermission(Manifest.permission.SEND_SMS);
            int readPhonePermission= checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            int readContacts = checkSelfPermission(Manifest.permission.READ_CONTACTS);


            if (callPermission == PackageManager.PERMISSION_GRANTED && accessFinePermission == PackageManager.PERMISSION_GRANTED && accessCoarsePermission == PackageManager.PERMISSION_GRANTED && smsPermission==PackageManager.PERMISSION_GRANTED && readPhonePermission==PackageManager.PERMISSION_GRANTED && readContacts==PackageManager.PERMISSION_GRANTED) {

            } else {
                requestPermissions(perms, permsRequestCode);

            }
        }
    }
}
