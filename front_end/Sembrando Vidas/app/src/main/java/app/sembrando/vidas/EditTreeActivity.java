package app.sembrando.vidas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import app.sembrando.vidas.classes.Tree;
import app.sembrando.vidas.dataBasesInterfaz.TreeDatabase;
import app.sembrando.vidas.java_class.Preferences;
import app.sembrando.vidas.java_class.Validations;
import app.sembrando.vidas.java_class.Variables;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.yoplantounarbolito_app.R;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditTreeActivity extends AppCompatActivity {

    RequestQueue request;
    JsonObjectRequest JOR;
    Variables variables = new Variables();
    String url;

    SharedPreferences preference;
    String token, id;

    EditText edit_name_tree;

    Button button_edit_name, button_camera;

    Validations validations = new Validations();

    Preferences preferences;
    Map<String, String> params;

    TreeDatabase tree_database;
    Tree tree;
    boolean avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tree);

        url = variables.getUrl();
        preferences = new Preferences(EditTreeActivity.this);
        tree_database = new TreeDatabase();
        tree = new Tree();
        avatar = false;

        edit_name_tree = findViewById(R.id.edit_name_tree);
        button_edit_name = findViewById(R.id.button_name_edit_tree);
        button_camera = findViewById(R.id.button_camera);

        preference = getSharedPreferences("preferenceLogin", Context.MODE_PRIVATE);
        token = preference.getString("token","");
        id = preference.getString("id","");

        params = new HashMap<>();

        button_edit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!edit_name_tree.getText().toString().equals("")) {
                    //Toast.makeText(EditTreeActivity.this, "Cambia", Toast.LENGTH_SHORT).show();
                    params.put(tree_database.getName(), edit_name_tree.getText().toString());
                }
                if(avatar == true){
                    params.put(tree_database.getAvatar(), tree.getAvatar());
                    //Toast.makeText(EditTreeActivity.this, "Cambia avatar", Toast.LENGTH_SHORT).show();
                }
                editTree();
            }
        });
        button_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoActivity = new Intent(getApplicationContext(), RegisterPhotoActivity.class);
                startActivity(photoActivity);
                finish();
            }
        });
    }

    private void editTree(){
        request = Volley.newRequestQueue(this);
        JSONObject parameters = new JSONObject(params);

        JOR = new JsonObjectRequest(Request.Method.PATCH, url + "/trees/" + preferences.getTreeId(), parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Intent mainTree = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainTree);
                Toast.makeText(EditTreeActivity.this,"Cambio exitoso",Toast.LENGTH_SHORT).show();
                finishAffinity();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                validations.errors(error, EditTreeActivity.this);
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

    public void OnclickSelectAvatar1(View view) {
        tree.setAvatar("avatar1");
        avatar = true;
        Toast.makeText(EditTreeActivity.this,"Hoja feliz seleccionada",Toast.LENGTH_SHORT).show();
    }

    public void OnclickSelectAvatar2(View view) {
        tree.setAvatar("avatar2");
        avatar = true;
        Toast.makeText(EditTreeActivity.this,"Brote feliz seleccionado",Toast.LENGTH_SHORT).show();
    }

    public void OnclickSelectAvatar3(View view) {
        tree.setAvatar("avatar3");
        avatar = true;
        Toast.makeText(EditTreeActivity.this,"Árbol feliz Seleccionado",Toast.LENGTH_SHORT).show();
    }
    public void OnclickSelectAvatar4(View view) {
        tree.setAvatar("avatar4");
        avatar = true;
        Toast.makeText(EditTreeActivity.this,"Maceta femenina Seleccionada",Toast.LENGTH_SHORT).show();
    }
    public void OnclickSelectAvatar5(View view) {
        tree.setAvatar("avatar5");
        avatar = true;
        Toast.makeText(EditTreeActivity.this,"Maceta masculina Seleccionado",Toast.LENGTH_SHORT).show();
    }
}