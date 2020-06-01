package soa.unlam.app620;


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

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class Registro extends AppCompatActivity {
    private EditText nombre;
    private EditText apellido;
    private EditText dni;
    private EditText email;
    private EditText contraseña;
    private EditText comision;
    private EditText grupo;
    private EditText resultado;
    private Button btnLogin;
    private Button btnRegistrar;

    public IntentFilter filtro;
    private ReceptorOperation receiver = new ReceptorOperation(); ///ver para que es

    private static final String URI_LOGIN = "http://so-unlam.net.ar/api/api/login";
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
          contraseña = (EditText)findViewById(R.id.claveRegistro);
          comision = (EditText)findViewById(R.id.comisionRegistro);
          grupo = (EditText)findViewById(R.id.grupoRegistro);

          resultado = (EditText)findViewById(R.id.resultado);
        btnRegistrar = (Button)findViewById(R.id.btnRegistro);
        btnLogin = (Button)findViewById(R.id.btnLogin);

       btnLogin.setOnClickListener(HandlerBtnLogin);
       btnRegistrar.setOnClickListener(HandlerBtnRegistrar);

       configurarBroadcastReciever();

    }



    ///metodo que actua como Listener de eventos que ocurren en los componentes gráficos
    private View.OnClickListener HandlerBtnLogin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //ver que va aca
            JSONObject obj = new JSONObject();
            try{
                obj.put("email", email.getText().toString());
                obj.put("password",contraseña.getText().toString());
                Intent i = new Intent(Registro.this, ServicesHttp.class);
                i.putExtra("uri",URI_LOGIN);
                i.putExtra("datosJson",obj.toString());
                startService(i);

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    };


    private View.OnClickListener HandlerBtnRegistrar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("env", "TEST");
                obj.put("name", nombre.getText().toString());
                obj.put("lastname", apellido.getText().toString());
                obj.put("dni", Integer.parseInt(dni.getText().toString()));
                obj.put("email", email.getText().toString());
                obj.put("password", contraseña.getText().toString());
                obj.put("commission", Integer.parseInt(comision.getText().toString()));
                obj.put("group", Integer.parseInt(grupo.getText().toString()));

                //se asocia el intent al servicio

                Intent i = new Intent(Registro.this, ServicesHttp.class); ///ver min 31:10
                //se agrega el parametro uri
                i.putExtra("uri", URI_REGISTER_USER);
                i.putExtra("datosJson", obj.toString());


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };




    //METODO QUE CREA Y CONFIGURA UN BROADCAST RECEIVER PARA COMUNICAR AL SERVICIO QUE RECIBE LOS MENSAJES DEL SERVICIO
    // CON LA ACTIVITY PRINCIPAL

private void configurarBroadcastReciever() {
        //SE ASOCIA (REGISTRA) LA ACCION RESPUESTA_OPERACION, PARA QUE CUANDO EL SERVICIO DE RECEPCION LA EJECUTE
        //SE INVOQUE AUTOMATICAMENTE AL ONRECEIVE DEL OBJETO RECEIVER
        filtro = new IntentFilter("com.example.intentservice.intent.action.RESPUESTA_OPERACION");

        filtro.addCategory(Intent.CATEGORY_DEFAULT);

        registerReceiver(receiver, filtro);
        }

    public class ReceptorOperation extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent){
            try{
                String datosJsonString = intent.getStringExtra("datosJson");
                JSONObject datosJson = new JSONObject(datosJsonString);

                Log.i("LOGUEO_MAIN","Datos Json Main Thread: "+ datosJsonString);
                resultado.setText(datosJsonString);
                Toast.makeText(getApplicationContext(),"Se recibió respuesta del server",Toast.LENGTH_LONG).show();
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

}
