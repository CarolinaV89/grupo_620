package soa.unlam.app620;

import androidx.appcompat.app.AppCompatActivity;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServicesHttp extends IntentService {
//variable utilizada para almacenar la descripcion de las excepciones que se generen durante la ejecucion del thread
    private Exception mException=null;

    private HttpURLConnection httpURLConnection;
    private URL mUrl;

    private ServicesHttp() { super("servicesHttp_GET");    }

    @Override
    public void onCreate() {
        super.onCreate();

       Log.i("LOGUEO_SERVICE","Service onCreate()");
    }

    //metodo que se invoca cuando se ejecuta la instruccion en la activity principal

        protected void onHandleIntent(Intent intent){
            try{
                //se obtiene la uri que envia la mainactivity a través de un intent

                String uri = intent.getExtras().getString("uri");
                JSONObject datosJson = new JSONObject(intent.getExtras().getString(("datosJson")));
                ejecutarPost(uri,datosJson);
            }catch(Exception e){
        }
}

    protected void ejecutarPost(String uri, JSONObject datosJson) {

        String result = POST(uri,datosJson);

        if(result==null){
            Log.e("LOGUEO_SERVICE","Error en GET:\n" + mException.toString());
            return;
        }
        if(result== "NO_OK"){
            Log.e("LOGUEO_SERVICE","se recibio response NO_OK");
            return;
        }
    }

    private StringBuilder convertInputStreamToString(InputStreamReader inputStream) throws IOException{
        BufferedReader br = new BufferedReader(inputStream);
        StringBuilder result = new StringBuilder();
        String line;
        while((line = br.readLine()) != null){
            result.append(line + "\n");

        }
        br.close();
        return result;
    }
    // si se ejecuto el Request correctamente, se llama el metodo de la activity principal encargado
    //de actualizar el valor de texto mostrado en textview



    private String POST(String uri, JSONObject datosJson) {
        HttpURLConnection urlConnection = null;
        String result ="";

        try{
            URL mUrl = new URL(uri);

            urlConnection = (HttpURLConnection) mUrl.openConnection();
            urlConnection.setRequestProperty("Content-Type","application/json: charset=UTF-8");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("POST");

            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

            wr.write(datosJson.toString().getBytes("UTF-8"));

            Log.i("LOGUEO_SERVICE","Se envia al servidor" + datosJson.toString());

            wr.flush();
            wr.close();

            //se envia el request al servidor
            urlConnection.connect();

            //se obtiene la respuesta que envió el servidor antes del request

            int responseCode = urlConnection.getResponseCode();

            //se analiza si la respuesta fue correcta
            if((responseCode == HttpURLConnection.HTTP_OK) || (responseCode == httpURLConnection.HTTP_CREATED))
                result= convertInputStreamToString (new InputStreamReader(urlConnection.getInputStream())).toString();
            else
                result = "NO_OK";
            mException=null;
            urlConnection.disconnect();
            return result;

        }catch(Exception e){
            mException =e;
            return null;
        }
    }
}
