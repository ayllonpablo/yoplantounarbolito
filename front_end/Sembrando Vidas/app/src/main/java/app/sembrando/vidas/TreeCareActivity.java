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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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
    TextView text_hola, text_points, text_tree_name;

    //ImageView
    ImageView image_tree_avatar;

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

    // Fechas de disponibilidad
    private String regarAvailableDate = "";
    private String limpiarAvailableDate = "";
    private String abonarAvailableDate = "";
    private String establecidoAvailableDate = "";

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
        text_tree_name = findViewById(R.id.textView16);
        image_tree_avatar = findViewById(R.id.image_tree_avatar);
        preferences = new Preferences(TreeCareActivity.this);
        text_hola.setText("Hola "+preferences.getName());
        text_points.setText(preferences.getPoints() + " Puntos");
        puntos = Integer.parseInt(preferences.getPoints());

        // Cargar información del árbol
        loadTreeInfo();

        //Toast.makeText(TreeCareActivity.this, "Id de arbol en preferencia:"+preferences.getTreeId(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(TreeCareActivity.this, "Id de user en preferencia:"+preferences.getUserId(), Toast.LENGTH_SHORT).show();

        button_regar = findViewById(R.id.button_regar);
        button_limpiar = findViewById(R.id.button_limpiar);
        button_abonar = findViewById(R.id.button_abonar);
        button_establecido = findViewById(R.id.button_establecido);

        // Verificar disponibilidad de acciones
        checkActionsAvailability();

        // Botón home
        CardView buttonHome = findViewById(R.id.button_home_tree_care);
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TreeCareActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

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
        action("Regar", regarAvailableDate);

    }
    private void cleanTree(){
        action.setName(variables.getLIMPIEZA());
        action("Limpiar", limpiarAvailableDate);

    }
    private void fertiliceTree(){
        action.setName(variables.getABONO());
        action("Abonar", abonarAvailableDate);
    }
    private void greepTree(){
        action.setName(variables.getAGARRE());
        action("Establecido", establecidoAvailableDate);
    }

    private void action(final String actionName, final String availableDate){

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

                    // Actualizar disponibilidad de acciones después de realizar una acción
                    checkActionsAvailability();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Verificar si es error 422 (validación fallida por frecuencia)
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == 422) {
                    try {
                        String json_error = new String(networkResponse.data);
                        Log.e("TreeCareActivity", "Error 422 response: " + json_error);
                        JSONObject jsonError = new JSONObject(json_error);

                        String errorMessage = "";

                        // Intentar extraer el mensaje del formato {"errors": [...]}
                        if (jsonError.has("errors")) {
                            Object errorsObj = jsonError.get("errors");

                            // Si es un array directo
                            if (errorsObj instanceof org.json.JSONArray) {
                                org.json.JSONArray errorsArray = (org.json.JSONArray) errorsObj;
                                if (errorsArray.length() > 0) {
                                    errorMessage = errorsArray.getString(0);
                                }
                            }
                            // Si es un objeto {"name": [...]}
                            else if (errorsObj instanceof JSONObject) {
                                JSONObject errors = (JSONObject) errorsObj;
                                if (errors.has("name")) {
                                    errorMessage = errors.getJSONArray("name").getString(0);
                                }
                            }
                        }
                        // Si el mensaje está directamente en "message"
                        else if (jsonError.has("message")) {
                            errorMessage = jsonError.getString("message");
                        }

                        Log.e("TreeCareActivity", "Extracted error message: " + errorMessage);

                        // Verificar si el error es por frecuencia de acción
                        if (errorMessage.contains("estará disponible el") ||
                            errorMessage.contains("Todavía no es posible")) {

                            new MaterialAlertDialogBuilder(TreeCareActivity.this)
                                .setTitle("Acción no disponible")
                                .setMessage(errorMessage)
                                .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                            return;
                        }
                    } catch (JSONException e) {
                        Log.e("TreeCareActivity", "Error parsing error response: " + e.getMessage());
                    }
                }

                // Si no es error de frecuencia, mostrar error normal
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

    private void loadTreeInfo() {
        String treeId = preferences.getTreeId();
        if (treeId == null || treeId.isEmpty()) {
            text_tree_name.setText("Sin árbol");
            return;
        }

        request = Volley.newRequestQueue(this);

        JOR = new JsonObjectRequest(Request.Method.GET, url + "/trees/" + treeId, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String treeName = response.getString("name");
                    text_tree_name.setText(treeName);

                    // Cargar avatar del árbol
                    String avatar = response.getString("avatar");
                    setTreeAvatar(avatar);
                } catch (JSONException e) {
                    text_tree_name.setText("Mi árbol");
                    Log.e("TreeCareActivity", "Error parsing tree data: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                text_tree_name.setText("Mi árbol");
                Log.e("TreeCareActivity", "Error loading tree info: " + error.toString());
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

    private void setTreeAvatar(String avatarName) {
        // Mapear el nombre del avatar al recurso mipmap
        int avatarResource = R.mipmap.hoja; // Default (avatar1)

        if (avatarName != null) {
            switch (avatarName.toLowerCase()) {
                case "avatar1":
                case "hoja":
                    avatarResource = R.mipmap.hoja;
                    break;
                case "avatar2":
                case "brote_feliz":
                case "brote":
                    avatarResource = R.mipmap.brote_feliz;
                    break;
                case "avatar3":
                case "arbolito_feliz":
                case "arbolito":
                    avatarResource = R.mipmap.arbolito_feliz;
                    break;
                case "avatar4":
                case "maceta_femenina":
                    avatarResource = R.mipmap.maceta_femenina;
                    break;
                case "avatar5":
                case "maseta_masculino":
                case "maceta_masculino":
                    avatarResource = R.mipmap.maseta_masculino;
                    break;
                default:
                    Log.w("TreeCareActivity", "Avatar desconocido: " + avatarName + ", usando hoja por defecto");
                    break;
            }
        }

        image_tree_avatar.setImageResource(avatarResource);
    }

    private void checkActionsAvailability() {
        request = Volley.newRequestQueue(this);

        String checkUrl = url + "/actions/check-availability?userId=" + preferences.getUserId() + "&treeId=" + preferences.getTreeId();

        JOR = new JsonObjectRequest(Request.Method.GET, checkUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Regar
                    if (response.has("REGAR")) {
                        JSONObject regarData = response.getJSONObject("REGAR");
                        boolean available = regarData.getBoolean("available");
                        regarAvailableDate = regarData.getString("availableDate");
                        updateButtonState(button_regar, available);
                    }

                    // Limpiar
                    if (response.has("LIMPIEZA")) {
                        JSONObject limpiarData = response.getJSONObject("LIMPIEZA");
                        boolean available = limpiarData.getBoolean("available");
                        limpiarAvailableDate = limpiarData.getString("availableDate");
                        updateButtonState(button_limpiar, available);
                    }

                    // Abonar
                    if (response.has("ABONO")) {
                        JSONObject abonarData = response.getJSONObject("ABONO");
                        boolean available = abonarData.getBoolean("available");
                        abonarAvailableDate = abonarData.getString("availableDate");
                        updateButtonState(button_abonar, available);
                    }

                    // Establecido
                    if (response.has("AGARRE")) {
                        JSONObject establecidoData = response.getJSONObject("AGARRE");
                        boolean available = establecidoData.getBoolean("available");
                        establecidoAvailableDate = establecidoData.getString("availableDate");
                        updateButtonState(button_establecido, available);
                    }

                } catch (JSONException e) {
                    Log.e("TreeCareActivity", "Error parsing availability: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TreeCareActivity", "Error checking availability: " + error.toString());
                // En caso de error, habilitar todos los botones
                enableAllButtons();
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

    private void updateButtonState(Button button, boolean available) {
        if (available) {
            button.setEnabled(true);
            button.setAlpha(1.0f);
        } else {
            button.setEnabled(false);
            button.setAlpha(0.5f);
        }
    }

    private void enableAllButtons() {
        button_regar.setEnabled(true);
        button_regar.setAlpha(1.0f);

        button_limpiar.setEnabled(true);
        button_limpiar.setAlpha(1.0f);

        button_abonar.setEnabled(true);
        button_abonar.setAlpha(1.0f);

        button_establecido.setEnabled(true);
        button_establecido.setAlpha(1.0f);
    }

    private void showUnavailableDialog(String actionName, String availableDate) {
        String formattedDate = formatDate(availableDate);
        new MaterialAlertDialogBuilder(TreeCareActivity.this)
                .setTitle("Acción no disponible")
                .setMessage("La acción '" + actionName + "' estará disponible el " + formattedDate)
                .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateStr;
        }
    }
}