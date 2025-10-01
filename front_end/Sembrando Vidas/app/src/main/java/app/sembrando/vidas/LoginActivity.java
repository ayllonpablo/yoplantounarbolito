package app.sembrando.vidas;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import app.sembrando.vidas.dataBasesInterfaz.UserDatabase;
import app.sembrando.vidas.java_class.Preferences;
import app.sembrando.vidas.java_class.Validations;
import app.sembrando.vidas.java_class.Variables;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.yoplantounarbolito_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {//implements Response.Listener<JSONObject>,Response.ErrorListener {

    //request
    RequestQueue request;
    JsonObjectRequest JOR;
    String url;
    Validations validations = new Validations();

    //interface
    UserDatabase user_database;

    //layouts
    TextView errors;
    EditText email, password;

    //Preferences
    Preferences preferences;

    Variables variables = new Variables();

    LinearProgressIndicator linear_progres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.yoplantounarbolito_app.R.layout.activity_login);
        url = variables.getUrl();
        email = findViewById(com.example.yoplantounarbolito_app.R.id.editTextEmailLogin);
        password = findViewById(com.example.yoplantounarbolito_app.R.id.editTextPasswordLogin);
        user_database = new UserDatabase();
        preferences = new Preferences(LoginActivity.this);
        linear_progres = findViewById(R.id.linear_progres_2);
        linear_progres.setVisibility(View.GONE);
    }

    public void OnclickLogin(View view) {
        linear_progres.setVisibility(View.VISIBLE);
        loginUser();
    }

    private void loginUser(){
        request = Volley.newRequestQueue(this);
        Map<String, String> params = new HashMap<>();
        params.put(user_database.getEmail(), email.getText().toString());
        params.put(user_database.getPassword(), password.getText().toString());
        JSONObject parameters = new JSONObject(params);

        JOR = new JsonObjectRequest(Request.Method.POST, url + "/auth/login", parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String token = response.getString("accessToken");
                    JSONObject user = response.getJSONObject("user");
                    String user_id = user.getString("id");
                    preferences.savePreferencesUser(token, user_id);
                    Intent homeTreeActivity = new Intent(getApplicationContext(),HomeActivity.class);
                    startActivity(homeTreeActivity);
                    finishAffinity();

                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this,"Se produjo un error",Toast.LENGTH_SHORT).show();
                }
                linear_progres.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                linear_progres.setVisibility(View.GONE);
                validations.errors(error, LoginActivity.this);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/vnd.api+json");
                headers.put("Content-Type", "application/vnd.api+json");
                return headers;
            }
        };
        request.add(JOR);
    }

    public void OnclickGoToRegisterActivity(View view) {
        linear_progres.setVisibility(View.VISIBLE);
        Intent registerUser = new Intent(getApplicationContext(),RegisterUserActivity.class);
        startActivity(registerUser);
        linear_progres.setVisibility(View.GONE);
        //finish();
    }


}