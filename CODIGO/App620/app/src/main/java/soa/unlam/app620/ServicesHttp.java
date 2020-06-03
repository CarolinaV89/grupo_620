package soa.unlam.app620;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ServicesHttp extends IntentService {

    private HttpURLConnection conexionHttp;
    private URL mURL;

    public ServicesHttp() {
        super("HTTPPostRegister");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    protected void onHandleIntent(Intent intent){
        try {
            String uri = intent.getExtras().getString("uri");
            JSONObject datosJson = new JSONObject(intent.getExtras().getString("datosJson"));
            String type = intent.getExtras().getString("type");
            connectionHandler(uri,datosJson,type);
        } catch (JSONException e) {
            Log.e("[DEBUG] HTTP Post","Error: "+ e.toString());
        }

    }

    private void connectionHandler(String uri, JSONObject datosJson, String type) {
        String result;
        if(type.equals("event") || type.equals("eventAccel")){
           result = postEvent (uri, datosJson);
        }
        else {
            result = post(uri, datosJson);
        }
        Log.i("[DEBUG] HTTP Post", "type " + type);
        switch (type){
            case "login":
                Intent l =new Intent("intent.action.Login");
                l.putExtra("datosJson", result);
                sendBroadcast(l);
                break;
            case "registrar":
                Intent r =new Intent("intent.action.Registro");
                r.putExtra("datosJson", result);
                sendBroadcast(r);
                break;
            case "event":
                Intent e =new Intent("intent.action.Event");
                e.putExtra("datosJson",result);
                sendBroadcast(e);
                break;
            case "eventAccel":
                Intent eA =new Intent("intent.action.EventAccel");
                eA.putExtra("datosJson",result);
                sendBroadcast(eA);
                break;
            default:
                Log.i("[DEBUG] HTTP Post", "Tipo invalido");


        }

    }

    private String post(String uri, JSONObject datosJson) {
        HttpURLConnection conexionHttp=null;
        String result="";

        try {
            URL mUrl=new URL(uri);
            conexionHttp = (HttpURLConnection) mUrl.openConnection();
            conexionHttp.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            conexionHttp.setDoOutput(true);
            conexionHttp.setDoInput(true);
            conexionHttp.setConnectTimeout(5000);
            conexionHttp.setRequestMethod("POST");
            DataOutputStream wr =new DataOutputStream(conexionHttp.getOutputStream());
            wr.write(datosJson.toString().getBytes("UTF-8"));
            Log.i("[DEBUG] HTTP Post", "Datos"+datosJson.toString() + "uri: "+uri);
            wr.flush();
            wr.close();

            conexionHttp.connect();
            int responseCode= conexionHttp.getResponseCode();
            Log.i("[DEBUG] HTTP Post","Server response: "+ conexionHttp.getResponseMessage());
            Log.i("[DEBUG] HTTP Post","Return code: "+ responseCode);
            if((responseCode == conexionHttp.HTTP_OK) || (responseCode == conexionHttp.HTTP_CREATED)) {
                Log.i("[DEBUG] HTTP Post","Connection OK"+ conexionHttp.toString());
                result = streamReader(new InputStreamReader(conexionHttp.getInputStream()));
            }else {
                result = streamReader(new InputStreamReader(conexionHttp.getInputStream()));
                Log.e("[ERROR] Mensaje:", "Connection Error:"+result);
                result = "Error";
            }

            conexionHttp.disconnect();

        }catch (Exception e) {
            return "Error";
        }
        return result;
    }


    private String postEvent(String uri, JSONObject datosJson) {
        HttpURLConnection conexionHttp=null;
        String result="";

        try {
            URL mUrl=new URL(uri);
            conexionHttp = (HttpURLConnection) mUrl.openConnection();
            conexionHttp.addRequestProperty("token",Login.TOKEN);
            conexionHttp.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            conexionHttp.setDoOutput(true);
            conexionHttp.setDoInput(true);
            conexionHttp.setConnectTimeout(5000);
            conexionHttp.setRequestMethod("POST");
            DataOutputStream wr =new DataOutputStream(conexionHttp.getOutputStream());
            wr.write(datosJson.toString().getBytes("UTF-8"));
            Log.i("[DEBUG] HTTP Post", "Datos"+datosJson.toString() + "uri: "+uri);
            wr.flush();
            wr.close();

            conexionHttp.connect();
            int responseCode= conexionHttp.getResponseCode();
            Log.i("[DEBUG] HTTP Post","Server response: "+ conexionHttp.getResponseMessage());
            Log.i("[DEBUG] HTTP Post","Return code: "+ responseCode);
            if((responseCode == conexionHttp.HTTP_OK) || (responseCode == conexionHttp.HTTP_CREATED)) {
                Log.i("[DEBUG] HTTP Post","Connection OK"+ conexionHttp.toString());
                result = streamReader(new InputStreamReader(conexionHttp.getInputStream()));
            }else {
                result = streamReader(new InputStreamReader(conexionHttp.getInputStream()));
                Log.e("[ERROR] Mensaje:", "Connection Error:"+result);
                result = "Error";
            }

            conexionHttp.disconnect();

        }catch (Exception e) {
            return "Error";
        }
        return result;
    }

    private String streamReader(InputStreamReader input) throws IOException {
        BufferedReader streamReader = new BufferedReader(input);
        StringBuilder respondStreamBuild = new StringBuilder();
        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            respondStreamBuild.append(inputStr);
        return respondStreamBuild.toString();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}