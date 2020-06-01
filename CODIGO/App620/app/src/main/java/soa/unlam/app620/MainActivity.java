package soa.unlam.app620;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnLogin;
    Button btnRegistrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencias a los objetos en el xml
        btnLogin=(Button)findViewById(R.id.btnLoginMain);
        btnRegistrar=(Button)findViewById(R.id.btnRegistrarMain);

        // Event Listeners
        btnLogin.setOnClickListener(this);
        btnRegistrar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            //Si se ocurrio un evento en el boton OK
            case R.id.btnLoginMain:
                //se genera un Intent para poder lanzar la activity principal
                intent=new Intent(MainActivity.this, Login.class);

                //Se le agrega al intent los parametros que se le quieren pasar a la activyt principal
                //cuando se lanzado
                //intent.putExtra("textoOrigen",txtOrigen.getText().toString());

                //se inicia la activity principal
                startActivity(intent);
                break;
            case R.id.btnRegistrarMain:
                intent=new Intent(MainActivity.this, Registro.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(getApplicationContext(),"Error en Listener de botones",Toast.LENGTH_LONG).show();
        }

    }
}
