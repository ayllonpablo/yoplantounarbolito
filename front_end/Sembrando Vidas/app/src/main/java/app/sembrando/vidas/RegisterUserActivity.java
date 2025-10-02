package app.sembrando.vidas;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import app.sembrando.vidas.classes.User;
import app.sembrando.vidas.dataBasesInterfaz.UserDatabase;
import app.sembrando.vidas.java_class.Preferences;
import app.sembrando.vidas.java_class.Variables;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.yoplantounarbolito_app.R;
import app.sembrando.vidas.java_class.Validations;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterUserActivity extends AppCompatActivity {

    EditText email, phone, password, password_confirmation, age, organization, firstname, lastname;

    //request
    RequestQueue request;
    JsonObjectRequest JOR;
    Variables variables = new Variables();
    String url;

    //validaciones
    Validations validations = new Validations();

    //interfaces
    User user;
    UserDatabase user_database;

    //preferencias
    Preferences preferences;

    LinearProgressIndicator linear_progres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.yoplantounarbolito_app.R.layout.activity_register_user);
        url = variables.getUrl();
        user = new User();
        user_database = new UserDatabase();
        preferences = new Preferences(RegisterUserActivity.this);
        linear_progres = findViewById(R.id.linear_progres_3);
        linear_progres.setVisibility(View.GONE);

        firstname = findViewById(R.id.editTextFirstNameRegisterUser);
        lastname = findViewById(R.id.editTextLastNameRegisterUser);
        email = findViewById(R.id.editTextEmailRegisterUser);
        age = findViewById(R.id.editTextAgeRegisterUser);
        organization = findViewById(R.id.editTextOrganizationRegisterUser);
        phone = findViewById(R.id.editTextPhoneRegisterUser);
        password = findViewById(R.id.editTextPasswordRegisterUser);
        password_confirmation = findViewById(R.id.editTextConfirmationPasswordRegisterUser);
    }

    private void registerUser(){

        request = Volley.newRequestQueue(this);

        Map<String, String> params = new HashMap<>();
        params.put(user_database.getFirstname(), firstname.getText().toString());
        params.put(user_database.getLastname(), lastname.getText().toString());
        params.put(user_database.getEmail(), email.getText().toString());
        params.put(user_database.getAge(), age.getText().toString());
        params.put(user_database.getOrganization(), organization.getText().toString());
        params.put(user_database.getPhone(), phone.getText().toString());
        params.put(user_database.getPoints(), "10");
        params.put(user_database.getPassword(), password.getText().toString());
        params.put(user_database.getPassword_confirmation(), password_confirmation.getText().toString());
        JSONObject parameters = new JSONObject(params);

        JOR = new JsonObjectRequest(Request.Method.POST, url + "/auth/register", parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String token = response.getString("accessToken");
                    JSONObject user = response.getJSONObject("user");
                    String user_id = user.getString("id");
                    preferences.savePreferencesUser(token, user_id);
                    //Toast.makeText(RegisterUserActivity.this,"id:" + user_id,Toast.LENGTH_SHORT).show();
                    //Toast.makeText(RegisterUserActivity.this,"token:" + token,Toast.LENGTH_SHORT).show();
                    Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(mainActivity);
                    token();
                    finishAffinity();

                } catch (JSONException e) {
                    Toast.makeText(RegisterUserActivity.this,"Se produjo un error",Toast.LENGTH_SHORT).show();
                }
                linear_progres.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                validations.errors(error,RegisterUserActivity.this);
                linear_progres.setVisibility(View.GONE);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        request.add(JOR);
    }

    public void OnclickRegister(View view) {
        linear_progres.setVisibility(View.VISIBLE);
        registerUser();
    }

    private void token(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.d("Token:", token);
                        devices(token);
                    }
                });
    }




    private void devices(String fcmToken){
        request = Volley.newRequestQueue(this);
        Map<String, String> params = new HashMap<>();
        params.put("fcmToken", fcmToken);
        params.put("userId", preferences.getUserId());
        JSONObject parameters = new JSONObject(params);

        JOR = new JsonObjectRequest(Request.Method.POST, url + "/devices", parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                linear_progres.setVisibility(View.GONE);
                validations.errors(error, RegisterUserActivity.this);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        request.add(JOR);
    }
}