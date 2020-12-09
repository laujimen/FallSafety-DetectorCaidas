package com.example.fallsafety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContactsActivity extends AppCompatActivity {

    private ContactsDbAdapter dbAdapter;
    private ListView m_listview;

    // para indicar en un Intent si se quiere crear una nueva nota o editar una existente
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        //creamos el adaptador de la BD y la abrimos
        dbAdapter = new ContactsDbAdapter(this);
        dbAdapter.open();

        // Creamos un listview que va a contener el título de todas las notas y
        // en el que cuando pulsemos sobre un título lancemos una actividad de editar
        // la nota con el id correspondiente
        m_listview = (ListView) findViewById(R.id.id_list_view);
        m_listview.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
                    {
                        Intent i = new Intent(view.getContext(), EditActivity.class);
                        i.putExtra(ContactsDbAdapter.KEY_ROWID, id);
                        startActivityForResult(i, ACTIVITY_EDIT);
                    }
                }
        );

        // rellenamos el listview con los títulos de todas las notas en la BD
        fillData();
    }
    private void fillData() {
        Cursor contactsCursor = dbAdapter.fetchAllContacts();

        // Creamos un array con los campos que queremos mostrar en el listview (sólo el título de la nota)
        String[] from = new String[]{ContactsDbAdapter.KEY_NOMBRE, ContactsDbAdapter.KEY_TELEFONO};

        // array con los campos que queremos ligar a los campos del array de la línea anterior
        int[] to = new int[]{R.id.text1, R.id.text2};

        // Creamos un SimpleCursorAdapter y lo asignamos al listview para mostrarlo
        SimpleCursorAdapter contacts =
                new SimpleCursorAdapter(this, R.layout.contacts_row, contactsCursor, from, to, 0);
        m_listview.setAdapter(contacts);
    }



    public void createContact(View view) {
        Intent i = new Intent(this, EditActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

}
