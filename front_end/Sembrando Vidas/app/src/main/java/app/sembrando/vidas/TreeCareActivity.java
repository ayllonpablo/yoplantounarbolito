package app.sembrando.vidas;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import app.sembrando.vidas.classes.Action;
import app.sembrando.vidas.classes.User;
import app.sembrando.vidas.dataBasesInterfaz.ActionDatabase;
import app.sembrando.vidas.dataBasesInterfaz.UserDatabase;
import app.sembrando.vidas.java_class.Preferences;
import app.sembrando.vidas.java_class.Validations;
import app.sembrando.vidas.java_class.Variables;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.yoplantounarbolito_app.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TreeCareActivity extends AppCompatActivity {

    //Request
    RequestQueue request;
    JsonObjectRequest JOR;
    Variables variables;
    String url;

    //botones
    Button button_regar, button_limpiar, button_abonar, button_establecido;

    //textView
    TextView text_hola, text_points;

    //preferencias
    Preferences preferences;

    //User
    User user;
    UserDatabase user_database;

    //Action
    Action action;
    ActionDatabase action_database;

    //validaciones
    Validations validations;

    int puntos = 0;

    //Map
    private GoogleMap mMap;
    private Marker marcador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_care);
        action = new Action();
        action_database = new ActionDatabase();
        validations = new Validations();
        variables = new Variables();
        url = variables.getUrl();
        text_hola = findViewById(R.id.text_hola);
        text_points = findViewById(R.id.text_points);
        preferences = new Preferences(TreeCareActivity.this);
        text_hola.setText("hola "+preferences.getName());
        text_points.setText(preferences.getPoints() + " Puntos");
        puntos = Integer.parseInt(preferences.getPoints());

        //Toast.makeText(TreeCareActivity.this, "Id de arbol en preferencia:"+preferences.getTreeId(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(TreeCareActivity.this, "Id de user en preferencia:"+preferences.getUserId(), Toast.LENGTH_SHORT).show();

        button_regar = findViewById(R.id.button_regar);
        button_limpiar = findViewById(R.id.button_limpiar);
        button_abonar = findViewById(R.id.button_abonar);
        button_establecido = findViewById(R.id.button_establecido);
        miUbication();
        button_regar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(TreeCareActivity.this,"funciona el boton",Toast.LENGTH_LONG).show();
                waterTree();
            }
        });
        button_limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(TreeCareActivity.this,"funciona el boton",Toast.LENGTH_LONG).show();
                cleanTree();
            }
        });
        button_abonar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(TreeCareActivity.this,"funciona el boton",Toast.LENGTH_LONG).show();
                fertiliceTree();
            }
        });
        button_establecido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(TreeCareActivity.this,"funciona el boton",Toast.LENGTH_LONG).show();
                greepTree();
            }
        });
    }

    private void waterTree(){
        Toast.makeText(TreeCareActivity.this, "Latitud: " + action.getLat(), Toast.LENGTH_SHORT).show();
        Toast.makeText(TreeCareActivity.this, "Longitud: " + action.getLng(), Toast.LENGTH_SHORT).show();
        action.setName(variables.getREGAR());
        action();

    }
    private void cleanTree(){
        action.setName(variables.getLIMPIEZA());
        action();

    }
    private void fertiliceTree(){
        action.setName(variables.getABONO());
        action();
    }
    private void greepTree(){
        action.setName(variables.getAGARRE());
        action();
    }

    private void action(){

        request = Volley.newRequestQueue(this);

        Map<String, String> params = new HashMap<>();
        params.put(action_database.getName(), action.getName());
        params.put(action_database.getUser_id(), preferences.getUserId());
        params.put(action_database.getTree_id(), preferences.getTreeId());
        params.put(action_database.getLat(), action.getLat() + "");
        params.put(action_database.getLng(), action.getLng() + "");
        JSONObject parameters = new JSONObject(params);

        JOR = new JsonObjectRequest(Request.Method.POST, url  + "/actions", parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Toast.makeText(TreeCareActivity.this, "Correcto:"+preferences.getTreeId(), Toast.LENGTH_SHORT).show();
                new MaterialAlertDialogBuilder(TreeCareActivity.this)
                        .setTitle("Excelente, bien hecho!!")
                        .setMessage("Cuidado realizado, no te olvides volver la proxima vez!!")
                        .setNegativeButton("Entendido", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("MainActivity", "Aborting mission...");
                            }
                        }).show();
                try {
                    String points = response.getString("points");
                    puntos = Integer.parseInt( points) + puntos;
                    text_points.setText(puntos + " Puntos");

                    Toast.makeText(TreeCareActivity.this,"puntos: " + points, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                validations.errors(error,TreeCareActivity.this);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + preferences.getToken());
                return headers;
            }
        };
        request.add(JOR);
    }

    private void updateUbication(Location location) {
        action.setLat(location.getLatitude());
        action.setLng(location.getLongitude());
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            updateUbication(location);
        }
    };

    private void miUbication() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            updateUbication(location);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0 , locationListener);
    }
}