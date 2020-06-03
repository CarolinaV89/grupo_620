package soa.unlam.app620;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SensorAcelerometro extends AppCompatActivity implements SensorEventListener {
    private long last_update = 0, last_movement = 0;
    private float prevX = 0, prevY = 0, prevZ = 0;
    private float curX = 0, curY = 0, curZ = 0;
    private static final String URI_EVENT = "http://so-unlam.net.ar/api/api/event";
    boolean faceDown=false;
    public SharedPreferences sharedPreferences;
    public ListView listView;
    ArrayList<String> values;
    public IntentFilter filtro;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acelerometro);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.sharedPreferences = getSharedPreferences("datosAccel",MODE_PRIVATE);
        listView=(ListView) findViewById(R.id.listAccel);
        this.values =new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);
        loadData();
        listView.invalidateViews();
        Button btn3 = (Button) findViewById(R.id.btnVolver);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(v.getContext(), MenuSensores.class);
                startActivityForResult(intent3, 0);
            }

        });
        configurarBroadcastReceiver();
    }
// Configuracion del broadcast receiver para verificar si el registro e eventos fue satisfactorio
    private void configurarBroadcastReceiver() {
        filtro = new IntentFilter("intent.action.EventAccel");
        filtro.addCategory("Intent.category.LAUNCHER");
        registerReceiver(receiver, filtro);
    }
    @Override
    protected void onResume() {
        super.onResume();
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onStop() {
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.unregisterListener(this);
        super.onStop();
    }

    protected void onPause() {
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            //long current_time = event.timestamp;
            curX = event.values[0];
            curY = event.values[1];
            curZ = event.values[2];
            ((TextView) findViewById(R.id.txtAccX)).setText("Acelerometro X: " + curX);
            ((TextView) findViewById(R.id.txtAccY)).setText("Acelerometro Y: " + curY);
            ((TextView) findViewById(R.id.txtAccZ)).setText("Acelerometro Z: " + curZ);
            //Voy a detectar si el telefono pasa a estar boca abajo
            if(curZ<0 && !faceDown){
                this.values.add(Calendar.getInstance().getTime()+ " telefono se puso boca abajo.");
                faceDown=true;
                saveData();
                registerAccelEvent();
            }
            //Si el telefono pasa de boca abajo a boca arriba
            else if (curZ>0 && faceDown){
                this.values.add(Calendar.getInstance().getTime()+ " telefono se puso boca arriba.");
                faceDown=false;
                saveData();
            }
        }

    }

    private String listToString(List<String> list) {
        StringBuilder csvList = new StringBuilder();
        for(String s : list){
            csvList.append(s);
            csvList.append(",");
        }
        return csvList.toString();
    }

    private List<String> stringToList(String csvList) {
        String[] items = csvList.split(",");
        List<String> list = new ArrayList<String>();
        for(int i=0; i < items.length; i++){
            list.add(items[i]);
        }
        return list;
    }
    private void registerAccelEvent (){
        JSONObject obj = new JSONObject();
        try {
            obj.put("env", "DEV");
            obj.put("type_events", "Sensor");
            obj.put("state", "ACTIVO");
            obj.put("description", "Telefono_boca_abajo");
            Intent i = new Intent(SensorAcelerometro.this, ServicesHttp.class);
            i.putExtra("uri", URI_EVENT);
            i.putExtra("datosJson", obj.toString());
            i.putExtra("type", "eventAccel");
            startService(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void saveData() {
        SharedPreferences sharedPreferencesObject = getSharedPreferences("datosAccel", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesObject.edit();
        editor.putString("Event", listToString(this.values));
        editor.apply();
        listView.invalidateViews();
    }

    private void loadData() {
        SharedPreferences sharedPreferencesObject = getSharedPreferences("datosAccel", MODE_PRIVATE);
        String eventsLight = sharedPreferencesObject.getString("Event", "");

        if (eventsLight != "") {
            ArrayList<String> listaDeEventos = (ArrayList<String>) stringToList(eventsLight);
            for(String eventLight : listaDeEventos){
                this.values.add(eventLight);
            }
        }
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
                        Toast.makeText(context.getApplicationContext(), "Evento registrado: Telefono boca abajo.", Toast.LENGTH_LONG).show();
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
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.unregisterListener(this);
        super.onDestroy();
    }

}