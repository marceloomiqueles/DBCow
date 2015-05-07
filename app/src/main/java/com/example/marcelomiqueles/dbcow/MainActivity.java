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
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;


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
            case R.id.exportar:
                exportarDB();
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

    public static void exportarDB() {

        CSVWriter writer = null;

        // Abrimos la base de datos 'DBUsuarios' en modo escritura
        PersonasSQLiteHelper usdbh = new PersonasSQLiteHelper(context, "DBPersonas", null, 1);

        db = usdbh.getWritableDatabase();

        //Alternativa 1: método rawQuery()
        Cursor c = db.rawQuery("SELECT rut, nombre, apellido, mail, telefono FROM Personas", null);

        //Recorremos los resultados para mostrarlos en pantalla
        try
        {
            writer = new CSVWriter(new FileWriter("/sdcard/myfile.csv"), ',');
            if (c != null) {
                if (c.moveToFirst()) {
                    //Recorremos el cursor hasta que no haya más registros
                    do {
                        String entrie = c.getString(0) + "#" + c.getString(1) + "#" + c.getString(2) + "#" + c.getString(3) + "#" + c.getString(4);
                        String[] entries = entrie.split("#");
                        writer.writeNext(entries);
                    } while (c.moveToNext());
                }
            }
            writer.close();
            Toast.makeText(context, "Guardo!!!", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            //error
            Toast.makeText(context, "Fallo!!!", Toast.LENGTH_SHORT).show();
        }
//        String outFileName = "db.csv";
//
//        //Abrimos la base de datos 'DBUsuarios' en modo escritura
//        PersonasSQLiteHelper usdbh = new PersonasSQLiteHelper(context, "DBPersonas", null, 1);
//
//        db = usdbh.getWritableDatabase();
//
//        //Alternativa 1: método rawQuery()
//        Cursor c = db.rawQuery("SELECT rut, nombre, apellido, mail FROM Personas", null);
//
//        //Recorremos los resultados para mostrarlos en pantalla
//        try {
//            File outFile = new File(outFileName);
//            FileWriter fileWriter = new FileWriter(outFile);
//            BufferedWriter out = new BufferedWriter(fileWriter);
//            if (c != null) {
//                if (c.moveToFirst()) {
//                    //Recorremos el cursor hasta que no haya más registros
//                    do {
//                        csvValues = c.getString(0) + "#";
//                        csvValues += c.getString(1) + "#";
//                        csvValues += c.getString(2) + "#";
//                        csvValues += c.getString(3);
//
//                        out.write(csvValues);
//                    } while (c.moveToNext());
//                }
//            }
//            out.close();
//        } catch (IOException e) {
//            Toast.makeText(context, "Fallo!!!", Toast.LENGTH_SHORT).show();
//        }
    }

    private void createTodo() {
        Intent i = new Intent(this, IngresoForm.class);
        startActivity(i);
    }
}
