package app.sembrando.vidas;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import app.sembrando.vidas.classes.Tree;
import app.sembrando.vidas.classes.TreeUser;
import app.sembrando.vidas.dataBasesInterfaz.TreeDatabase;
import app.sembrando.vidas.dataBasesInterfaz.TreeUserDatabase;
import app.sembrando.vidas.java_class.Preferences;
import app.sembrando.vidas.java_class.Variables;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.yoplantounarbolito_app.R;
import app.sembrando.vidas.java_class.Validations;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterTreeActivity extends AppCompatActivity implements OnMapReadyCallback {

    //Components
    com.google.android.material.textfield.TextInputEditText name;
    Validations validations = new Validations();


    //requests
    RequestQueue request;
    JsonObjectRequest JOR;
    Variables variables = new Variables();
    String url;

    //tree
    Tree tree;
    TreeDatabase tree_database;
    TreeUser tree_user;
    TreeUserDatabase tree_user_database;

    //buttons
    Button buton_register_tree;

    //Preferencias
    Preferences preferences;

    //Map
    private GoogleMap mMap;
    private Marker marcador;

    //Avatar Cards
    CardView cardAvatar1, cardAvatar2, cardAvatar3, cardAvatar4, cardAvatar5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tree);
        tree = new Tree();
        tree_database = new TreeDatabase();
        tree_user = new TreeUser();
        tree_user_database = new TreeUserDatabase();
        preferences = new Preferences(RegisterTreeActivity.this);
        buton_register_tree = findViewById(R.id.button_register_tree_resgister);

        url = variables.getUrl();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_register_tree);
        mapFragment.getMapAsync(this);

        name = findViewById(R.id.editTextNameRegisterTree);

        // Initialize avatar cards
        cardAvatar1 = findViewById(R.id.cardAvatar1);
        cardAvatar2 = findViewById(R.id.cardAvatar2);
        cardAvatar3 = findViewById(R.id.cardAvatar3);
        cardAvatar4 = findViewById(R.id.cardAvatar4);
        cardAvatar5 = findViewById(R.id.cardAvatar5);

        buton_register_tree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    private void registerTree(){
        request = Volley.newRequestQueue(this);

        Map<String, String> params = new HashMap<>();
        params.put("user_id", preferences.getUserId());
        params.put(tree_database.getName(), name.getText().toString());
        params.put(tree_database.getLat(), tree.getLat() + "");
        params.put(tree_database.getLng(), tree.getLng() + "");
        params.put(tree_database.getAvatar(), tree.getAvatar());
        params.put(tree_database.getPath_photo(), tree.getPath_photo());
        params.put(tree_database.getState(), tree.getState());
        JSONObject parameters = new JSONObject(params);

        JOR = new JsonObjectRequest(Request.Method.POST, url + "/trees", parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String id_tree = response.getString("id");
                    preferences.savePreferencesTree(id_tree);
                    Intent photoActivity = new Intent(getApplicationContext(), RegisterPhotoActivity.class);
                    Toast.makeText(RegisterTreeActivity.this,"Se le regalaron 15 puntos por plantar el arbolito",Toast.LENGTH_SHORT).show();
                    startActivity(photoActivity);
                    finishAffinity();


                } catch (JSONException e) {
                    Toast.makeText(RegisterTreeActivity.this,"Se produjo un error",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                validations.errors(error, RegisterTreeActivity.this);
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

    public void OnclickRegisterTree(View view) {
        Toast.makeText(RegisterTreeActivity.this,"Registrando",Toast.LENGTH_LONG).show();
        registerTree();
    }

    //MAP LOGIC
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        miUbicacion();
    }

    private void agregarMarket(double lat, double lng) {
        LatLng coordenadas = new LatLng(lat, lng);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
        if (marcador != null) marcador.remove();
        marcador = mMap.addMarker(new MarkerOptions().position(coordenadas));
        mMap.animateCamera(miUbicacion);
    }

    private void actualizarUbicacion(Location location) {
        tree.setLat(location.getLatitude());
        tree.setLng(location.getLongitude());
        agregarMarket(tree.getLat(), tree.getLng());
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            actualizarUbicacion(location);
        }
    };

    private void miUbicacion() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            actualizarUbicacion(location);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0 , locationListener);
    }

    private void resetAvatarBackgrounds() {
        int defaultColor = ContextCompat.getColor(this, R.color.card_green_light);
        cardAvatar1.setCardBackgroundColor(defaultColor);
        cardAvatar2.setCardBackgroundColor(defaultColor);
        cardAvatar3.setCardBackgroundColor(defaultColor);
        cardAvatar4.setCardBackgroundColor(defaultColor);
        cardAvatar5.setCardBackgroundColor(defaultColor);
    }

    public void OnclickSelectAvatar1(View view) {
        tree.setAvatar("avatar1");
        resetAvatarBackgrounds();
        cardAvatar1.setCardBackgroundColor(ContextCompat.getColor(this, R.color.dark_green));
        Toast.makeText(RegisterTreeActivity.this,tree.getAvatar()+" Seleccionado",Toast.LENGTH_SHORT).show();
    }

    public void OnclickSelectAvatar2(View view) {
        tree.setAvatar("avatar2");
        resetAvatarBackgrounds();
        cardAvatar2.setCardBackgroundColor(ContextCompat.getColor(this, R.color.dark_green));
        Toast.makeText(RegisterTreeActivity.this,tree.getAvatar()+" Seleccionado",Toast.LENGTH_SHORT).show();
    }

    public void OnclickSelectAvatar3(View view) {
        tree.setAvatar("avatar3");
        resetAvatarBackgrounds();
        cardAvatar3.setCardBackgroundColor(ContextCompat.getColor(this, R.color.dark_green));
        Toast.makeText(RegisterTreeActivity.this,tree.getAvatar()+" Seleccionado",Toast.LENGTH_SHORT).show();
    }
    public void OnclickSelectAvatar4(View view) {
        tree.setAvatar("avatar4");
        resetAvatarBackgrounds();
        cardAvatar4.setCardBackgroundColor(ContextCompat.getColor(this, R.color.dark_green));
        Toast.makeText(RegisterTreeActivity.this,tree.getAvatar()+" Seleccionado",Toast.LENGTH_SHORT).show();
    }
    public void OnclickSelectAvatar5(View view) {
        tree.setAvatar("avatar5");
        resetAvatarBackgrounds();
        cardAvatar5.setCardBackgroundColor(ContextCompat.getColor(this, R.color.dark_green));
        Toast.makeText(RegisterTreeActivity.this,tree.getAvatar()+" Seleccionado",Toast.LENGTH_SHORT).show();
    }

    public void showDialog(){
        new MaterialAlertDialogBuilder(RegisterTreeActivity.this)
                .setTitle("Atento:")
                .setMessage("Tome en cuenta que tiene que estar en la localizacion de su arbolito, despues no po" +
                        "dra cambiar la localización")
                .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("MainActivity", "Aborting mission...");
                    }
                })
                .setPositiveButton("Registrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    Log.d("MainActivity", "Aborting mission...");
                    registerTree();
                    }
                 }).show();
    }
}