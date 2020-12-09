package com.example.fallsafety;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;



public class Emergencia extends AppCompatActivity{
    /*
    SMS Variables
     */

    SmsManager smsManager;
    String phoneNumber;
    String textMsg;

    /*
    Variables de ubicación
     */

    double latitude, longitude;


    /*
   Variables base de datos
    */
    private ContactsDbAdapter dbAdapter;

    public Emergencia(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergencia_activity);

        getLocation();

        Toast.makeText(this, "clase emergencia", Toast.LENGTH_LONG).show();
        String smsEmergencia=getResources().getString(R.string.smsEmergencia);
        sendSMS(this, smsEmergencia);
        makeCall(this);



    }


    public void sendSMS(Context context, String txt){

        getLocation();

        smsManager= SmsManager.getDefault();
        //textMsg=txt + "https://maps.google.com/?q="+String.valueOf(latitude)+","+String.valueOf(longitude) ;
        textMsg=txt + "https://maps.google.com/?q=40.332853,-3.765089" ;

        Toast.makeText(context,"ENVIADO SMS", Toast.LENGTH_LONG).show();

        /*
         RECORRER LA BASE DE DATOS
         */

        //creamos el adaptador de la BD y la abrimos
        dbAdapter = new ContactsDbAdapter(context);
        dbAdapter.open();

        Cursor contactsCursor= dbAdapter.fetchAllContacts();
        if (contactsCursor.getCount()!=0){
            contactsCursor.moveToFirst();
            do{
                System.out.println("NOMBRE: " + contactsCursor.getString(1));
                System.out.println("TELEFONO: " + contactsCursor.getString(2));
                phoneNumber= contactsCursor.getString(2);
                System.out.println("PHONE NUMBER A MANDAR SMS: " + phoneNumber);
                System.out.println(textMsg);
                smsManager.sendTextMessage(phoneNumber, null, textMsg, null, null);

            } while (contactsCursor.moveToNext());
        }

    }

    public void makeCall(Context context){


        //creamos el adaptador de la BD y la abrimos
        dbAdapter = new ContactsDbAdapter(context);
        dbAdapter.open();

        Cursor contactsCursor= dbAdapter.fetchAllContacts();
        if (contactsCursor.getCount()!=0){
            contactsCursor.moveToFirst();

            System.out.println("NOMBRE: " + contactsCursor.getString(1));
            System.out.println("TELEFONO: " + contactsCursor.getString(2));
            phoneNumber=contactsCursor.getString(2);

            try{

                System.out.println("ey");

                if(ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED){

                    System.out.println("No hay permiso de llamada");

                }

                Intent intentLlamada = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phoneNumber));
                intentLlamada.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentLlamada);

            } catch (Exception e){
                System.out.println("err");
                e.printStackTrace();
            }

        }



    }


     public void getLocation(){
        try{

            String serviceString= Context.LOCATION_SERVICE;
            LocationManager locationManager = (LocationManager) getSystemService(serviceString);

            LocationListener locationListener= new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    latitude= location.getLatitude();
                    longitude= location.getLongitude();

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            // Comprobacion permiso para acceder a la ubicación, si no se solicita
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                //ActivityCompat.requestPermissions(MainActivity.this.getApplicationContext(), new String[]{Manifest.permission.SEND_SMS}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);

                Toast.makeText(this.getApplicationContext(), "No hay permisos de ubicacion", Toast.LENGTH_SHORT).show();

            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);

            Criteria criteria= new Criteria();


            String provider= String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

            System.out.println("provider: " + provider);

            Location location= locationManager.getLastKnownLocation(provider);
            if(location!=null){
                Log.e("TAG", "GPS ON");
                latitude=location.getLatitude();
                longitude=location.getLongitude();
            } else{
                locationManager.requestLocationUpdates(provider, 1000,0,  locationListener );
            }

            locationManager.requestLocationUpdates(provider, 5000, 0, locationListener);

            System.out.println("lat" + latitude + "lon " + longitude);

        } catch (Exception e){
            e.printStackTrace();
        }




    }


}
