package cat.paucasesnovescifp.proyectofinalfinalandroidv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private ReceptorXarxa receptor;
    Preferencies shared;
    EditText email, contra;
    Button login;
    static boolean internet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Creamos el shared preferene
        shared = new Preferencies(getApplicationContext());
        dbQuePasaAux db = new dbQuePasaAux(getApplicationContext());

        if(!shared.getToken().equals("")){
            Intent intent = new Intent(getApplicationContext(),Prueba.class);
            startActivity(intent);
            finish();
        }

        email = (EditText) findViewById(R.id.etEmailLogin);
        email.setText("miguelrubio@paucasesnovescifp.cat");
        contra = (EditText) findViewById(R.id.etContraLogin);
        contra.setText("s2p");
        login = findViewById(R.id.btnEnviarLogin);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        // Comprobar la conexión
        receptor = new ReceptorXarxa();
        // Broadcast
        this.registerReceiver(receptor,filter);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    JSONObject conexion = new JSONObject(Auxiliar.interacionPost(email.getText().toString(),contra.getText().toString(),true));
                    Toast.makeText(getApplicationContext(),conexion + " ",Toast.LENGTH_SHORT).show();

                    Log.d("Prueba",conexion + "");
                    if(conexion.getBoolean("correcta")){
                        JSONObject dades = conexion.getJSONObject("dades");
                        // Shared tiene el token y el codigo usuario
                        shared.setToken(dades.getString("token"));
                        shared.setCodiusuari(dades.getString("codiusuari"));
                        shared.setUser(dades.getString("email"));
                        shared.setPassword(contra.getText().toString());
                        Toast.makeText(getApplicationContext(),"Info: " + shared.getToken(),Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),Prueba.class);
                        startActivity(intent);
                        finish();

                    }else{
                        Toast.makeText(getApplicationContext(),"No se ha podido realizar el login",Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Donam de baixa el receptor de broadcast quan es destrueix l’aplicació
        if (receptor != null) {
            this.unregisterReceiver(receptor);
        }
    }

    public void comprovaConnectivitat(Context context) {

        //Obtenim un gestor de les connexions de xarxa
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); //Obtenim l’estat de la xarxa
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        //Si està connectat
        if (networkInfo != null && networkInfo.isConnected()) {
            //Xarxa OK

            Toast.makeText(this, "Conexión ok", Toast.LENGTH_LONG).show();
        } else {
            //Xarxa no disponible

            Toast.makeText(this, "Conexión no disponible", Toast.LENGTH_LONG).show();

        }

    }

    public void offline(){
        Intent in = new Intent(getApplicationContext(),Offline.class);
        startActivity(in);
    }

    public class ReceptorXarxa extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Actualitza l'estat de la xarxa
            comprovaConnectivitat(context);
        }
    }
}
