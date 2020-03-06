package cat.paucasesnovescifp.proyectofinalfinalandroidv2;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Prueba extends AppCompatActivity {

    Preferencies shared;
    EditText msg;
    Button enviarMsg;
    ListView listamensaje;
    AdapterQuePasa adapter;
    ArrayList<Missatge> mensajes = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg);

        shared = new Preferencies(getApplicationContext());
        msg = findViewById(R.id.msg);
        enviarMsg = findViewById(R.id.ok);
        listamensaje = findViewById(R.id.list);



        enviarMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject enviarMensaje = new JSONObject(Auxiliar.interacionPostToken(msg.getText().toString(),shared.getCodiusuari(),false,shared.getToken()));
                    Log.d("PruebaEnviarMensaje", enviarMensaje + "");
                    msg.setText("");
                    recibir();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        recibir();




    }

    public void recibir(){
        try {
            //tenemos los mensajes
            JSONObject mensajesJson = new JSONObject(Auxiliar.interacioGetToken(shared.getCodiusuari(),false,shared.getToken()));
            // comprobamos si nos ha devuelto un true en que es correcto el mensaje
            if(mensajesJson.getBoolean("correcta")){
                // Creamos un json con la array de mensajes
                JSONArray mensajesArray = mensajesJson.getJSONArray("dades");
                for (int i = 0; i < mensajesArray.length();i++){
                    JSONObject mensajeIndividual = mensajesArray.getJSONObject(i);
                    Missatge missatge = new Missatge();
                    missatge.setId(mensajeIndividual.getString("codimissatge"));
                    missatge.setMsg(mensajeIndividual.getString("msg"));
                    missatge.setDate(mensajeIndividual.getString("datahora"));
                    missatge.setUserId(mensajeIndividual.getString("codiusuari"));
                    missatge.setUserName(mensajeIndividual.getString("nom"));
                    mensajes.add(missatge);
                    adapter = new AdapterQuePasa(getApplicationContext(),0,mensajes,shared.getCodiusuari());
                    listamensaje.setAdapter(adapter);

                }
            }else{
                Toast.makeText(getApplicationContext(),"No se ha podido recibir los mensajes",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cerrarSesion:
                Log.d("Cerrar sesion", "cucu");
                borrarShared();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void borrarShared() {
        shared.setCodiusuari("-1");
        shared.setUser("");
        shared.setPassword("");
        shared.setToken("");
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }

}
