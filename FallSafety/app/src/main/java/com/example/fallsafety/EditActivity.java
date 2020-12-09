package com.example.fallsafety;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EditActivity extends AppCompatActivity {


    private EditText mNombreText, mTelefonoText, mPrioridadText;
    private Long mRowId;
    private ContactsDbAdapter dbAdapter;
    private static final int PICK_CONTACT_REQUEST=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // infla el layout
        setContentView(R.layout.activity_edit);

        // obtiene referencia a los tres views que componen el layout
        mNombreText = (EditText) findViewById(R.id.nombreContacto);
        mTelefonoText = (EditText) findViewById(R.id.numeroContacto);
        mPrioridadText= (EditText) findViewById(R.id.prioridadContacto);
        Button confirmButton = (Button) findViewById(R.id.guardar);

        //creamos el adaptador de la BD y la abrimos
        dbAdapter = new ContactsDbAdapter(this);
        dbAdapter.open();

        // obtiene id de fila de la tabla si se le ha pasado (hemos pulsado un contacto para editarl0)
        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(ContactsDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(ContactsDbAdapter.KEY_ROWID) : null;
        }

        // Si se le ha pasado un id (no era null) rellena  con los campos guardados en la BD
        // en caso contrario se dejan en blanco
        if (mRowId != null) {
            Cursor contact = dbAdapter.fetchContact(mRowId);
            mNombreText.setText(contact.getString(
                    contact.getColumnIndexOrThrow(ContactsDbAdapter.KEY_NOMBRE)));
            mTelefonoText.setText(contact.getString(
                    contact.getColumnIndexOrThrow(ContactsDbAdapter.KEY_TELEFONO)));
            mPrioridadText.setText(contact.getString(
                    contact.getColumnIndexOrThrow(ContactsDbAdapter.KEY_PRIORIDAD)));
        }

        FloatingActionButton fab= (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarContacto();
            }
        });

    }

    private void seleccionarContacto() {
        Intent selectContacto = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        selectContacto.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(selectContacto, PICK_CONTACT_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();

                Cursor cursor = getContentResolver().query(uri, null, null, null, null);

                if(cursor.moveToFirst()){
                    int columnaNombre=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int columnaNumero=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                    String nombre= cursor.getString(columnaNombre);
                    String numero= cursor.getString(columnaNumero);

                    mNombreText.setText(nombre);
                    mTelefonoText.setText(numero);

                }

            }
        }
    }

    public void deleteContact(View view){
        if (mRowId != null) {
            dbAdapter.deleteContact(mRowId);
        }
        setResult(RESULT_OK);
        dbAdapter.close();
        finish();
    }



    public void saveNote(View view) {
        String nombre = mNombreText.getText().toString();
        String telefono = mTelefonoText.getText().toString();
        String prioridad= mPrioridadText.getText().toString();






        if (telefono.length()!=9 && telefono.length()!=12 &&  telefono.length()!=16 && telefono.length()==0){
            Toast.makeText(this,"Número Incorrecto", Toast.LENGTH_LONG).show();
        } else if(nombre.length() == 0){
            Toast.makeText(this, "Introduce un nombre", Toast.LENGTH_LONG).show();
        } else if(prioridad.length() == 0 ){
            Toast.makeText(this, "Prioridad incorrecta", Toast.LENGTH_LONG).show();
        } else {
            int priority= Integer.parseInt(prioridad);

            if (mRowId == null) {
                long id = dbAdapter.createContact(nombre, telefono,priority);
                if (id > 0) {
                    mRowId = id;
                }
            } else {
                dbAdapter.updateContact(mRowId, nombre, telefono,priority);
            }
            Toast.makeText(this,"Contacto añadido correctamente", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            dbAdapter.close();
            finish();



        }
    }
}
