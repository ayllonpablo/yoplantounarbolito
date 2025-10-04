package app.sembrando.vidas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import app.sembrando.vidas.java_class.Variables;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.yoplantounarbolito_app.R;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    RequestQueue request;
    JsonObjectRequest JOR;
    Variables variables = new Variables();
    String url;

    String token;
    SharedPreferences preference;
    private static final int SPLASH_DELAY = 3000; // 3 segundos

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url = variables.getUrl();
        validaPermisos();
        authenticated();
    }

    private void validaPermisos() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return;
        }

        // Solicitar permisos si no están otorgados, pero no bloquear la app
        if((checkSelfPermission(CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
            requestPermissions(new String[]{CAMERA, ACCESS_FINE_LOCATION}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // No bloquear la aplicación si se rechazan los permisos
        // Los permisos se solicitarán cuando sean necesarios (cámara, ubicación)
    }
    private void authenticated(){
        request = Volley.newRequestQueue(this);

        JOR = new JsonObjectRequest(Request.Method.GET, url + "/auth/me", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Esperar 3 segundos antes de ir a HomeActivity
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(homeActivity);
                        finish();
                    }
                }, SPLASH_DELAY);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Esperar 3 segundos antes de ir a LoginActivity
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(login);
                        finish();
                    }
                }, SPLASH_DELAY);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                preference = getSharedPreferences("preferenceLogin", Context.MODE_PRIVATE);
                token = preference.getString("token","");
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        request.add(JOR);
    }
}