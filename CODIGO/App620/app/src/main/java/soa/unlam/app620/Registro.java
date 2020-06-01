package soa.unlam.app620;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Registro extends AppCompatActivity {
    private EditText nombre;
    private EditText apellido;
    private EditText dni;
    private EditText email;
    private EditText password;
    private EditText comision;
    private EditText grupo;
    private Button btnRegistrar;

    public IntentFilter filtro;
    //private Registro.ReceptorOperation receiver = new Registro.ReceptorOperation(); ///ver para que es

    //private static final String URI_LOGIN = "http://so-unlam.net.ar/api/api/login";
    private static final String URI_REGISTER_USER= "http://so-unlam.net.ar/api/api/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        ///rescato la informacion de la interfaz grafica
        nombre = (EditText)findViewById(R.id.nombreRegistro);
        apellido = (EditText)findViewById(R.id.apellidoReigistro);
        dni = (EditText)findViewById(R.id.dniRegistro);
        email = (EditText)findViewById(R.id.emailRegistro);
        password = (EditText)findViewById(R.id.claveRegistro);
        comision = (EditText)findViewById(R.id.comisionRegistro);
        grupo = (EditText)findViewById(R.id.grupoRegistro);
       // resultado = (EditText)findViewById(R.id.resultado);
        btnRegistrar = (Button)findViewById(R.id.btnRegistro);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validaCampos()) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("env", "DEV");
                        obj.put("name", nombre.getText().toString());
                        obj.put("lastname", apellido.getText().toString());
                        obj.put("dni", Integer.parseInt(dni.getText().toString()));
                        obj.put("email", email.getText().toString());
                        obj.put("password", password.getText().toString());
                        obj.put("commission", Integer.parseInt(comision.getText().toString()));
                        obj.put("group", Integer.parseInt(grupo.getText().toString()));

                        //se asocia el intent al servicio

                        Intent i = new Intent(Registro.this, ServicesHttp.class); ///ver min 31:10
                        //se agrega el parametro uri
                        i.putExtra("uri", URI_REGISTER_USER);
                        i.putExtra("datosJson", obj.toString());
                        i.putExtra("type", "registrar");
                        startService(i);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        configurarBroadcastReceiver();

    }



    //METODO QUE CREA Y CONFIGURA UN BROADCAST RECEIVER PARA COMUNICAR AL SERVICIO QUE RECIBE LOS MENSAJES DEL SERVICIO
    // CON LA ACTIVITY PRINCIPAL

    private void configurarBroadcastReceiver() {
        //SE ASOCIA (REGISTRA) LA ACCION RESPUESTA_OPERACION, PARA QUE CUANDO EL SERVICIO DE RECEPCION LA EJECUTE
        //SE INVOQUE AUTOMATICAMENTE AL ONRECEIVE DEL OBJETO RECEIVER
        filtro = new IntentFilter("intent.action.Registro");

        filtro.addCategory("Intent.category.LAUNCHER");

        registerReceiver(receiver, filtro);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String datosJsonString = intent.getStringExtra("datosJson");
                if (datosJsonString.equals("Error")) {
                    Toast.makeText(context.getApplicationContext(), "Error de conexión o el usuario ya existe", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Registro.this, MainActivity.class));
                }else {
                    JSONObject JSONData = new JSONObject(datosJsonString);
                    Log.i("[DEBUG] Main", "Datos:" + datosJsonString);
                    Log.i("[DEBUG] Main", "Tengo: " + JSONData.get("state"));
                    if (JSONData.get("state").equals("success")) {
                        Toast.makeText(context.getApplicationContext(), "Registro exitoso:", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Registro.this, Login.class));
                    } else {
                        Toast.makeText(context.getApplicationContext(), "Error:" + JSONData.get("msg"), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Registro.this, MainActivity.class));
                    }
                }
                
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
    public boolean validaCampos() {

        boolean esValido = true;
        String auxEmail = email.getText().toString();
        String auxPwd = password.getText().toString();
        String auxComision = comision.getText().toString();
        String auxGrupo = grupo.getText().toString();
        String auxNombre = nombre.getText().toString();
        String auxApellido = apellido.getText().toString();
        String auxDni = dni.getText().toString();

        if(auxNombre.isEmpty()){
            nombre.setError("Nombre invalido");
            esValido = false;
        }
        if(auxApellido.isEmpty()){
            apellido.setError("Apellido invalido");
            esValido = false;
        }

        if(auxDni.isEmpty() || auxDni.length()>8){
            dni.setError("DNI invalido");
            esValido = false;
        }

        if(auxEmail.isEmpty() || !auxEmail.contains("@")){
            email.setError("Email invalido");
            esValido = false;
        }
        if(auxPwd.isEmpty() || auxPwd.length()<8){
            password.setError("Password invalida");
            esValido = false;
        }

        if(auxComision.isEmpty()){
            comision.setError("Comision invalida");
            esValido = false;
        }

        if(auxGrupo.isEmpty()){
            grupo.setError("Grupo invalido");
            esValido = false;
        }
        return esValido;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
