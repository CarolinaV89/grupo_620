package soa.unlam.app620;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Button btnLogin;
    public static String TOKEN = "";
    public IntentFilter filtro;
    private static final String URI_LOGIN = "http://so-unlam.net.ar/api/api/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Registro el network receiver para detectar si hay conexion de internet
        registerReceiver(NetworkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        ///rescato la informacion de la interfaz grafica
        email = (EditText)findViewById(R.id.emailLogin);
        password = (EditText)findViewById(R.id.pwdLogin);
        btnLogin = (Button)findViewById(R.id.btnLoginLogin);
        //configuro el onClickListener del boton de Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validaCampos()) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("env", "DEV");
                        obj.put("name", "nombre");
                        obj.put("lastname", "apellido");
                        obj.put("dni", 99111555);
                        obj.put("email", email.getText().toString());
                        obj.put("password", password.getText().toString());
                        obj.put("commission", 03);
                        obj.put("group", 620);
                        //se asocia el intent al servicio
                        Intent i = new Intent(Login.this, ServicesHttp.class); ///ver min 31:10
                        //se agrega el parametro uri
                        i.putExtra("uri", URI_LOGIN);
                        i.putExtra("datosJson", obj.toString());
                        i.putExtra("type", "login");
                        startService(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
//Configuro el broadcast Receiver para recibir la respuesta de la solicitud http
        configurarBroadcastReceiver();
    }

//METODO QUE CREA Y CONFIGURA UN BROADCAST RECEIVER PARA COMUNICAR AL SERVICIO QUE RECIBE LOS MENSAJES DEL SERVICIO
// CON LA ACTIVITY PRINCIPAL

    private void configurarBroadcastReceiver() {
        //SE ASOCIA (REGISTRA) LA ACCION RESPUESTA_OPERACION, PARA QUE CUANDO EL SERVICIO DE RECEPCION LA EJECUTE
        //SE INVOQUE AUTOMATICAMENTE AL ONRECEIVE DEL OBJETO RECEIVER
        filtro = new IntentFilter("intent.action.Login");
        filtro.addCategory("Intent.category.LAUNCHER");
        registerReceiver(receiver, filtro);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String datosJsonString = intent.getStringExtra("datosJson");
                Log.i("[DEBUG] Login", "Json" + datosJsonString);
                if (datosJsonString.equals("Error")) {
                    Toast.makeText(context.getApplicationContext(), "Credenciales incorrectas.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Login.this, MainActivity.class));
                }else{
                    JSONObject JSONData = new JSONObject(datosJsonString);
                    Log.i("[DEBUG] Main", "Datos:" + datosJsonString);
                    Log.i("[DEBUG] Main", "Estado: " + JSONData.get("state"));
                    if (JSONData.get("state").equals("success")) {
                        Toast.makeText(context.getApplicationContext(), "Login exitoso.", Toast.LENGTH_LONG).show();
                        TOKEN= (String) JSONData.get("token");
                        startActivity(new Intent(Login.this, MenuSensores.class));
                    } else {
                        Toast.makeText(context.getApplicationContext(), "Error:" + JSONData.get("msg"), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Login.this, MainActivity.class));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
//Configuracion del Receiver para detectar cambios en el estado de la conexion de internet
    private BroadcastReceiver NetworkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean estaConectado = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (estaConectado){
                Log.d("[DEBUG] NetConnection", "Network Up");
                Toast.makeText(context.getApplicationContext(), "Conectado a Internet", Toast.LENGTH_LONG).show();
            }
            else{
                Log.d("[DEBUG] NetConnection", "Network Down");
                Toast.makeText(context.getApplicationContext(), "No hay conexion a internet", Toast.LENGTH_LONG).show();
            }
        }
    };
//Validacion de campos del formulario de login
    public boolean validaCampos() {
        boolean esValido = true;
        String mail = email.getText().toString();
        String pwd = password.getText().toString();

        if(mail.isEmpty() || !mail.contains("@")){
            email.setError("Ingrese un email valdido");
            esValido = false;
        }
        if(pwd.isEmpty() || pwd.length()<8){
            password.setError("Ingrese una constraseña");
            esValido = false;
        }
        return esValido;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        unregisterReceiver(NetworkStateReceiver);
        super.onDestroy();
    }


}
