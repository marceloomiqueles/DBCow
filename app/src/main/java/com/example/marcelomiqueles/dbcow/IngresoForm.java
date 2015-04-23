package com.example.marcelomiqueles.dbcow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by marcelomiqueles on 15-04-15.
 */
public class IngresoForm extends Activity {

    private EditText rut, nombre, apellido, mail, telefono, searchTo, searchToMail;
    private Button guardar;
    private Context context;

    private String rut_s, nombre_s, apellido_s, mail_s, telefono_s;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_form);

        context = this.getApplicationContext();

        rut = (EditText) findViewById(R.id.editRut);
        nombre = (EditText) findViewById(R.id.editNombre);
        apellido = (EditText) findViewById(R.id.editApellido);
        mail = (EditText) findViewById(R.id.editMail);
        telefono = (EditText) findViewById(R.id.editTelefono);

        guardar = (Button) findViewById(R.id.saveBtn);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            guardaDatos();
            MainActivity.cargarDatos();
            }
        });

        searchTo = (EditText) findViewById(R.id.editRut);
        searchTo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    ValidarCampoRut();
            }
        });

        searchToMail = (EditText) findViewById(R.id.editMail);
        searchToMail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    validarCampoMail();
            }
        });

    }

    private void guardaDatos() {
        PersonasSQLiteHelper usdbh = new PersonasSQLiteHelper(this, "DBPersonas", null, 1);
        SQLiteDatabase db = usdbh.getWritableDatabase();

        //Si hemos abierto correctamente la base de datos
        if (db != null) {
            if (!ValidaSiExisteRut()) {
                //Creamos el registro a insertar como objeto ContentValues

                rut_s = reemplazaEditRut(rut.getText().toString());
                nombre_s = nombre.getText().toString().trim();
                apellido_s = apellido.getText().toString().trim();
                mail_s = mail.getText().toString().trim();
                telefono_s = telefono.getText().toString().trim();

                if (rut_s.length() > 7 && validaRut(rut.getText().toString()) && nombre_s.length() > 0 && apellido_s.length() > 0 && mail_s.length() > 0 && telefono_s.length() > 0) {
                    ContentValues nuevoRegistro = new ContentValues();
                    nuevoRegistro.put("rut", rut_s);
                    nuevoRegistro.put("nombre", nombre_s);
                    nuevoRegistro.put("apellido", apellido_s);
                    nuevoRegistro.put("mail", mail_s);
                    nuevoRegistro.put("telefono", telefono_s);

                    //Insertamos el registro en la base de datos
                    db.insert("Personas", null, nuevoRegistro);

                    //Insertamos los datos en la tabla Usuarios
                    //db.execSQL("INSERT INTO Personas (rut, nombre, apellido, mail, telefono) " + "VALUES ('" + rut.getText().toString() + "', '" + nombre.getText().toString() + "', '" + apellido.getText().toString() + "', '" + mail.getText().toString() + "', '" + telefono.getText().toString() + "')");

                    //Cerramos la base de datos
                    db.close();

                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                    dialogo1.setTitle("Importante");
                    dialogo1.setMessage("Registro insertado!");
                    //dialogo1.setMessage("Rut Valido!");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                        }
                    });
                    dialogo1.show();
//
                    limpiarForm();
                } else {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                    dialogo1.setTitle("Importante");
                    dialogo1.setMessage("Faltan Datos!");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            rut.requestFocus();
                        }
                    });
                    dialogo1.show();
                }
            } else {
                limpiarForm();
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                dialogo1.setTitle("Importante");
                dialogo1.setMessage("Este Rut ya existe!");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        rut.requestFocus();
                    }
                });
                dialogo1.show();
            }
        }
    }

    private String reemplazaEditRut(String rut_final) {
        rut_final = rut_final.trim().replace(".", "");
        rut_final = rut_final.replace("-","");
        return rut_final;
    }

    public boolean ValidaSiExisteRut() {
        PersonasSQLiteHelper usdbh = new PersonasSQLiteHelper(this, "DBPersonas", null, 1);
        SQLiteDatabase db = usdbh.getWritableDatabase();

        String[] args = new String[]{reemplazaEditRut(rut.getText().toString())};
        Cursor c = db.rawQuery(" SELECT rut FROM Personas WHERE rut=? ", args);
        if (c.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    private void limpiarForm() {
        rut.setText("");
        nombre.setText("");
        apellido.setText("");
        mail.setText("");
        telefono.setText("");
    }

    private boolean validaRut(String rut) {
        boolean validacion = false;
        try {
            rut =  rut.toUpperCase();
            rut = rut.replace(".", "");
            rut = rut.replace("-", "");

            Toast.makeText(getApplicationContext(), rut, Toast.LENGTH_SHORT).show();

            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

            char dv = rut.charAt(rut.length() - 1);

            int m = 0, s = 1;
            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if (dv == (char) (s != 0 ? s + 47 : 75)) {
                validacion = true;
            }

        } catch (Exception e) { }
        return validacion;
    }

    private void ValidarCampoRut() {
        if (validaRut(rut.getText().toString())) {
            if (reemplazaEditRut(rut.getText().toString()).trim().length() > 7) {
                if (ValidaSiExisteRut()) {
                    limpiarForm();
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                    dialogo1.setTitle("Importante");
                    dialogo1.setMessage("Este Rut ya existe!");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            rut.requestFocus();
                        }
                    });
                    dialogo1.show();
                } else {
                    Toast.makeText(getApplicationContext(), "no existe!", Toast.LENGTH_SHORT).show();
                }
            } else {
                rut.setText("");
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                dialogo1.setTitle("Importante");
                dialogo1.setMessage("Este Rut no es valido!");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        rut.requestFocus();
                    }
                });
                dialogo1.show();
            }
        } else {
            rut.setText("");
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Importante");
            dialogo1.setMessage("Este Rut no es valido!");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    rut.requestFocus();
                }
            });
            dialogo1.show();
        }
    }

    public void validarCampoMail() {
        if (!isEmailValid(mail.getText().toString().trim())) {
            mail.setText("");
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Importante");
            dialogo1.setMessage("Este Correo no es valido!");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    mail.requestFocus();
                }
            });
            dialogo1.show();
        }
    }

    public boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }

}
