package com.example.marcelomiqueles.dbcow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {

    static Context context;
    private Button Insertar;

    static TextView txtResultado;

    static SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this.getApplicationContext();//Obtenemos las referencias a los controles
        txtResultado = (TextView)findViewById(R.id.txtResultado);

        cargarDatos();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.listmenu, menu);
        return true;
    }

    // Reaction to the menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert:
                Intent i = new Intent(this, IngresoForm.class);
                startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    public static void cargarDatos() {

        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        PersonasSQLiteHelper usdbh = new PersonasSQLiteHelper(context, "DBPersonas", null, 1);

        db = usdbh.getWritableDatabase();

        //Alternativa 1: método rawQuery()
        Cursor c = db.rawQuery("SELECT rut, nombre, apellido, mail FROM Personas", null);

        //Alternativa 2: método delete()
        //String[] campos = new String[] {"codigo", "nombre"};
        //Cursor c = db.query("Usuarios", campos, null, null, null, null, null);

        //Recorremos los resultados para mostrarlos en pantalla
        txtResultado.setText("");
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                String rut = c.getString(0);
                String nom = c.getString(1);
                String ape = c.getString(2);
                String mail = c.getString(3);

                txtResultado.append(" " + rut + " - " + nom + " " + ape + " - " + mail + "\n");
            } while(c.moveToNext());
        }
    }

    private void createTodo() {
        Intent i = new Intent(this, IngresoForm.class);
        startActivity(i);
    }
}
