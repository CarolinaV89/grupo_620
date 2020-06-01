package soa.unlam.app620;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MenuSensores extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_sensores);

        Button btn1 = (Button) findViewById(R.id.btnAcel);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(v.getContext(), SensorAcelerometro.class);
                startActivityForResult(intent2, 0);
            }

        });

        Button btn2 = (Button) findViewById(R.id.btnLuz);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SensorLuz.class);
                startActivityForResult(intent, 0);
            }

        });

        Button btnLogOut = (Button) findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuSensores.this,Login.class);
                startActivity(intent);
            }
        });
    }
}