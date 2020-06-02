package soa.unlam.app620;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SensorLuz extends AppCompatActivity implements SensorEventListener {

    private static final String URI_EVENT = "http://so-unlam.net.ar/api/api/event";
    public IntentFilter filtro;
    private float lx = 0;
    private float curllx = 0;
    private SensorManager sensorManager;
    private Sensor light;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_luz);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        Button btn4 = (Button) findViewById(R.id.btnVolv);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(v.getContext(), MenuSensores.class);
                startActivityForResult(intent4, 0);
            }

        });
        configurarBroadcastReceiver();
    }
    private void configurarBroadcastReceiver() {
        filtro = new IntentFilter("intent.action.Event");
        filtro.addCategory("Intent.category.LAUNCHER");
        registerReceiver(receiver, filtro);
    }
        @Override
        public final void onAccuracyChanged (Sensor sensor,int accuracy){
            // Do something here if sensor accuracy changes.
        }

        @Override
        public final void onSensorChanged (SensorEvent event){
            lx = event.values[0];
            ((TextView) findViewById(R.id.lx)).setText("LUX: " + lx);
            if (lx == 0){
                JSONObject obj = new JSONObject();
                try {
                    obj.put("env", "DEV");
                    obj.put("type_events", "Sensor");
                    obj.put("state", "ACTIVO");
                    obj.put("description", "Luz_Apagada");
                    //obj.put("token",Login.TOKEN);
                    //se asocia el intent al servicio
                    Intent i = new Intent(SensorLuz.this, ServicesHttp.class);
                    //se agrega el parametro uri
                    i.putExtra("uri", URI_EVENT);
                    i.putExtra("datosJson", obj.toString());
                    i.putExtra("type", "event");
                    startService(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onResume () {
            // Register a listener for the sensor.
            super.onResume();
            sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
            configurarBroadcastReceiver();
        }

        @Override
        protected void onPause () {
            // Be sure to unregister the sensor when the activity pauses.
            super.onPause();
            sensorManager.unregisterListener(this);
        }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String datosJsonString = intent.getStringExtra("datosJson");
                Log.i("[DEBUG] RegEvent", "Json" + datosJsonString);
                if (datosJsonString.equals("Error")) {
                    Toast.makeText(context.getApplicationContext(), "No se pudo registrar evento.", Toast.LENGTH_LONG).show();
                }else{
                    JSONObject JSONData = new JSONObject(datosJsonString);
                    Log.i("[DEBUG] RegEvent", "Datos:" + datosJsonString);
                    Log.i("[DEBUG] RegEvent", "Estado: " + JSONData.get("state"));
                    if (JSONData.get("state").equals("success")) {
                        Toast.makeText(context.getApplicationContext(), "Evento registrado: Luz apagada.", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    }






