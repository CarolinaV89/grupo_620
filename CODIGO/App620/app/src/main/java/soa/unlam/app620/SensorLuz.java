package soa.unlam.app620;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SensorLuz extends AppCompatActivity implements SensorEventListener {

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
    }
        @Override
        public final void onAccuracyChanged (Sensor sensor,int accuracy){
            // Do something here if sensor accuracy changes.
        }

        @Override
        public final void onSensorChanged (SensorEvent event){
            lx = event.values[0];
            ((TextView) findViewById(R.id.lx)).setText("LUX: " + lx);
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


    }






