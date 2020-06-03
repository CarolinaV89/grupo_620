package soa.unlam.app620;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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

public class SensorLuz extends AppCompatActivity implements SensorEventListener {
    private static final String URI_EVENT = "http://so-unlam.net.ar/api/api/event";
    public IntentFilter filtro;
    private float lx = 0;
    private SensorManager sensorManager;
    private Sensor light;
    public SharedPreferences sharedPreferences;
    public ListView listView;
    boolean luzPrendida=true;
    ArrayList<String> values;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_luz);
        this.sharedPreferences = getSharedPreferences("datos",MODE_PRIVATE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        listView=(ListView) findViewById(R.id.lista);
        this.values =new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);
        loadData();
        listView.invalidateViews();
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
            //Detecta si la luz pasa de estar encendida a apagada
            if (lx == 0 && luzPrendida){
                this.values.add(Calendar.getInstance().getTime()+ " se apago la luz.");
                luzPrendida=false;
                saveData();
                registerLightEvent();
                }
            //Si la luz pasa de apagada a prendida
            else if (lx!=0 && luzPrendida==false){
                this.values.add(Calendar.getInstance().getTime()+ " se prendio la luz.");
                luzPrendida=true;
                saveData();
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
    private void registerLightEvent (){
        JSONObject obj = new JSONObject();
        try {
            obj.put("env", "DEV");
            obj.put("type_events", "Sensor");
            obj.put("state", "ACTIVO");
            obj.put("description", "Luz_Apagada");
            Intent i = new Intent(SensorLuz.this, ServicesHttp.class);
            i.putExtra("uri", URI_EVENT);
            i.putExtra("datosJson", obj.toString());
            i.putExtra("type", "event");
            startService(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        SharedPreferences sharedPreferencesObject = getSharedPreferences("datos", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesObject.edit();
        editor.putString("Event", listToString(this.values));
        editor.apply();
        listView.invalidateViews();
    }

    private void loadData() {
        SharedPreferences sharedPreferencesObject = getSharedPreferences("datos", MODE_PRIVATE);
        String eventsLight = sharedPreferencesObject.getString("Event", "");

        if (eventsLight != "") {
            ArrayList<String> listaDeEventos = (ArrayList<String>) stringToList(eventsLight);
            for(String eventLight : listaDeEventos){
                this.values.add(eventLight);
            }
        }
    }
        @Override
        protected void onResume () {
            // Register a listener for the sensor.
            super.onResume();
            sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
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
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.unregisterListener(this);
        super.onDestroy();
    }

    }






