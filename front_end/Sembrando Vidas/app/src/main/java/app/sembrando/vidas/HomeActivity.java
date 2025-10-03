package app.sembrando.vidas;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import app.sembrando.vidas.classes.Tree;
import app.sembrando.vidas.classes.User;
import app.sembrando.vidas.dataBasesInterfaz.TreeDatabase;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    //Request
    RequestQueue request;
    JsonObjectRequest JOR;
    Variables variables = new Variables();
    String url;

    //preferencias
    Preferences preferences;

    //buttons
    Button button_log_out, button_your_tree, button_register_tree;
    androidx.cardview.widget.CardView card_ranking, card_edit_tree, card_edit_user, card_profile;
    ImageView image_home_avatar;

    //datos de Usuario
    User user;
    UserDatabase user_database;

    //datos arbol
    Tree tree;
    TreeDatabase tree_database;

    //textview
    TextView textView_home_name_user;

    //layout
    LinearLayout linear_layout_create_tree;
    LinearLayout linear_layout_care_tree;
    androidx.cardview.widget.CardView card_crea_arbol;
    androidx.cardview.widget.CardView card_cuida_arbolito;

    //validaciones
    Validations validations;

    LinearProgressIndicator linear_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        user = new User();
        user_database = new UserDatabase();
        tree = new Tree();
        tree_database = new TreeDatabase();
        validations = new Validations();
        linear_progress = findViewById(R.id.linear_progres_4);
        linear_progress.setVisibility(View.GONE);

        //preference = getSharedPreferences("preferenceLogin", Context.MODE_PRIVATE);
        preferences = new Preferences(HomeActivity.this);
        url  = variables.getUrl();

        textView_home_name_user = findViewById(R.id.text_hello);
        image_home_avatar = findViewById(R.id.image_home_avatar);

        //metodos
        getUser();

        //buttons
        button_your_tree = findViewById(R.id.button_home_your_tree);
        button_register_tree = findViewById(R.id.button_home_register_tree);
        button_log_out = findViewById(R.id.button_home_log_out);

        //cards
        card_ranking = findViewById(R.id.card_ranking);
        card_edit_tree = findViewById(R.id.card_edit_tree);
        card_edit_user = findViewById(R.id.card_edit_user);
        card_profile = findViewById(R.id.card_profile);

        //avatar o boton
        linear_layout_care_tree = findViewById(R.id.linear_layout_cuida_tu_arbol);
        linear_layout_create_tree = findViewById(R.id.linear_layout_crea_tu_arbol);
        card_crea_arbol = findViewById(R.id.card_crea_arbol);
        card_cuida_arbolito = findViewById(R.id.card_cuida_arbolito);

        linear_layout_care_tree.setVisibility(View.GONE);
        linear_layout_create_tree.setVisibility(View.GONE);
        card_crea_arbol.setVisibility(View.GONE);
        card_cuida_arbolito.setVisibility(View.GONE);


        button_your_tree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear_progress.setVisibility(View.VISIBLE);
                Intent careTree = new Intent(getApplicationContext(), TreeCareActivity.class);
                startActivity(careTree);
                linear_progress.setVisibility(View.GONE);
            }
        });

        card_ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear_progress.setVisibility(View.VISIBLE);
                Intent rankingUser = new Intent(getApplicationContext(), RankingActivity.class);
                startActivity(rankingUser);
                linear_progress.setVisibility(View.GONE);
            }
        });
        card_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent profileUser = new Intent(getApplicationContext(), UserProfileActivity.class);
                startActivity(profileUser);*/
                linear_progress.setVisibility(View.VISIBLE);
                showUser();
                linear_progress.setVisibility(View.GONE);
            }
        });
        button_register_tree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear_progress.setVisibility(View.VISIBLE);
                Intent registerTree = new Intent(getApplicationContext(), RegisterTreeActivity.class);
                startActivity(registerTree);
                linear_progress.setVisibility(View.GONE);
            }
        });

        button_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear_progress.setVisibility(View.VISIBLE);
                logOut();
            }
        });
        card_edit_tree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear_progress.setVisibility(View.VISIBLE);
                Intent editTree = new Intent(HomeActivity.this, EditTreeActivity.class);
                startActivity(editTree);
                linear_progress.setVisibility(View.GONE);
            }
        });

        card_edit_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear_progress.setVisibility(View.VISIBLE);
                Intent editUser = new Intent(HomeActivity.this, EditUserActivity.class);
                startActivity(editUser);
                linear_progress.setVisibility(View.GONE);
            }
        });
    }


    //cerrar sesion
    private void logOut(){
        request = Volley.newRequestQueue(this);

        JOR = new JsonObjectRequest(Request.Method.POST, url + "/auth/logout", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                /*preferences.deletePreferences();
                Toast.makeText(HomeActivity.this,"Gracias",Toast.LENGTH_LONG).show();
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
                finish();*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse == null) {
                    preferences.deletePreferences();
                    //Toast.makeText(HomeActivity.this,"Gracias",Toast.LENGTH_LONG).show();
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(login);
                    finish();
                }
                else{
                    String jsonError = new String(networkResponse.data);
                    validations.errors(error, HomeActivity.this);
                    Log.i("ErrorVolley",jsonError);
                }
                linear_progress.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + preferences.getToken());
                return headers;
            }
        };
        request.add(JOR);
    }

    //si tiene un arbol registrado
    private boolean ifHaveTree(){
        return true;
    }

   //metodos para obtener usuario

    //mostar dialog de informacion de usuario
    private void showUser() {
        String message = "\n" + "Nombre: "+ user.getFirstname() + "\n" + "\n"
                + "Correo: " + user.getEmail() + "\n" + "\n"
                + "Telefono: " + user.getPhone() + "\n" + "\n"
                + "Edad: " + user.getAge() +" años." + "\n" + "\n"
                + "Nombre de Arbol: " + tree.getName();
        new MaterialAlertDialogBuilder(this)
                .setTitle("Mis Datos")
                .setMessage(message)
                .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("MainActivity", "Aborting mission...");
                    }
                }).show();
    }
    // request para obtener usuario y sus arboles, en este primer caso solo recupera 1
    private void getUser() {

        request = Volley.newRequestQueue(this);

        JOR = new JsonObjectRequest(Request.Method.GET, url +"/users/" + preferences.getUserId() + "?include=trees", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                linear_progress.setVisibility(View.GONE);
                try {
                    user.setFirstname(response.getString(user_database.getFirstname()));
                    user.setEmail(response.getString(user_database.getEmail()));
                    user.setPhone(response.getString(user_database.getPhone()));
                    user.setPoints(response.getString(user_database.getPoints()));
                    user.setAge(response.getString(user_database.getAge()));
                    textView_home_name_user.setText("Hola " + user.getFirstname());
                    preferences.savePreferencesUserNamePoints(user.getFirstname(), user.getPoints() );
                    //Toast.makeText(HomeActivity.this, preferences.getPoints()+""+preferences.getName(), Toast.LENGTH_SHORT).show();
                    JSONArray trees_array = response.getJSONArray("trees");
                    //Toast.makeText(HomeActivity.this, "array: " + trees_array, Toast.LENGTH_SHORT).show();
                    if (!trees_array.isNull(0)) {
                        JSONObject tree_object = trees_array.getJSONObject(0);
                        preferences.savePreferencesTree(tree_object.getString(tree_database.getId()));

                        tree.setName(tree_object.getString(tree_database.getName()));
                        image_home_avatar.setImageResource(showAvatar(tree_object.getString(tree_database.getAvatar())));
                        linear_layout_care_tree.setVisibility(View.VISIBLE);
                        card_cuida_arbolito.setVisibility(View.VISIBLE);
                        linear_layout_create_tree.setVisibility(View.GONE);
                        card_crea_arbol.setVisibility(View.GONE);
                    } else{
                        linear_layout_care_tree.setVisibility(View.GONE);
                        card_cuida_arbolito.setVisibility(View.GONE);
                        linear_layout_create_tree.setVisibility(View.VISIBLE);
                        card_crea_arbol.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    Toast.makeText(HomeActivity.this, "Se produjo un error:", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                validations.errors(error, HomeActivity.this);
                linear_progress.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + preferences.getToken());
                return headers;
            }
        };
        request.add(JOR);
    }
    public int showAvatar(String avatar){
        int res = 0;
        switch (avatar){
            case "avatar1":
                res = R.mipmap.hoja;
                break;
            case "avatar2":
                res = R.mipmap.brote_feliz;
                break;
            case "avatar3":
                res = R.mipmap.arbolito_feliz;
                break;
            case "avatar4":
                res = R.mipmap.maceta_femenina;
                break;
            case "avatar5":
                res = R.mipmap.maseta_masculino;
                break;
            default:
                res = R.mipmap.hoja;
                break;
        }
        return res;
    }


}