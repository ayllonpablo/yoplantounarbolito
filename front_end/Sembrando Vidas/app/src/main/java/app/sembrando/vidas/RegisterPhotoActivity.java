package app.sembrando.vidas;

import android.util.Base64;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
//import android.support.annotation.NonNull;
//import android.support.v4.content.FileProvider;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import androidx.core.content.FileProvider;
import app.sembrando.vidas.java_class.Preferences;
import app.sembrando.vidas.java_class.Validations;
import app.sembrando.vidas.java_class.Variables;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.yoplantounarbolito_app.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
//import static android.support.v4.content.FileProvider.getUriForFile;

public class RegisterPhotoActivity extends AppCompatActivity {
    private final String CARPETA_RAIZ="misImagenesPrueba/";
    private final String RUTA_IMAGEN=CARPETA_RAIZ+"misFotos";

    final int COD_SELECCIONA=100;
    final int COD_FOTO=200;

    Variables variables = new Variables();
    String url;

    Button buttonLoad;
    Button buttonSavePhoto;
    ImageView imagen;
    Validations validations;
    String path;
    String rutaImagen;
    Bitmap bitmap;
    ProgressBar loadPhoto;
    TextView textViewLoadPhoto;
    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.yoplantounarbolito_app.R.layout.activity_register_photo);

        //id = getDate.getString("tree_id");
        url = variables.getUrl();
        imagen= findViewById(com.example.yoplantounarbolito_app.R.id.photo_tree);
        validations = new Validations();
        buttonLoad= findViewById(com.example.yoplantounarbolito_app.R.id.button_load_photo);
        buttonSavePhoto = findViewById(com.example.yoplantounarbolito_app.R.id.button_save_photo);
        buttonSavePhoto.setVisibility(View.GONE);
        loadPhoto = findViewById(com.example.yoplantounarbolito_app.R.id.loadPhoto);
        loadPhoto.setVisibility(View.GONE);
        textViewLoadPhoto = findViewById(R.id.textViewLoadPhoto);
        textViewLoadPhoto.setVisibility(View.GONE);
        preferences = new Preferences(RegisterPhotoActivity.this);

    }

    public void onclick(View view) {
        tomarFotografia();
    }

    private void dialogImagen() {

        /*final CharSequence[] opciones={"Tomar Foto"*//*"Cargar Imagen"*//*,"Cancelar"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(RegisterPhotoActivity.this);
        alertOpciones.setTitle("Seleccione una Opción");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Tomar Foto")){
                    tomarFotografia();
                }else{
                    if (opciones[i].equals("Cargar Imagen")){
                        Intent intent=new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicación"),COD_SELECCIONA);
                    }else{
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        alertOpciones.show();*/
        new MaterialAlertDialogBuilder(RegisterPhotoActivity.this)
            .setTitle("Error")
            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("MainActivity", "Aborting mission...");
                }
            })
            .setPositiveButton("Tomar una foto", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tomarFotografia();
                }
            }).show();
    }

    private void tomarFotografia() {
        /*File fileImagen=new File(getFilesDir(),RUTA_IMAGEN);
        boolean isCreada=fileImagen.exists();
        String nombreImagen="";
        if(isCreada==false){
            isCreada=fileImagen.mkdirs();
        }*/
        if(true){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager()) != null){
                File imagenArchivo = null;
                try {
                    imagenArchivo = crearImagen();
                }catch (IOException e){
                    Log.e("error", e.toString());
                }
                if(imagenArchivo != null){
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
                    {
                        Uri fotoUri = FileProvider.getUriForFile(this, "com.example.yoplantounarbolito_app.fileprovider", imagenArchivo );
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                        startActivityForResult(intent,COD_FOTO);
                    }else
                    {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagenArchivo ));
                    }
                }
            }
        }
    }

    private File crearImagen() throws IOException{
        String nombreImagen = (System.currentTimeMillis()/1000) + ".jpg";
        path=Environment.getExternalStorageDirectory()+File.separator+RUTA_IMAGEN+File.separator+nombreImagen;
        File directorio = getExternalFilesDir(path);
        File imagen = File.createTempFile(nombreImagen, ".jpg", directorio);
        rutaImagen = imagen.getAbsolutePath();
        return imagen;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            switch (requestCode){
                case COD_SELECCIONA:
                    Uri miPath=data.getData();
                    imagen.setImageURI(miPath);

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(RegisterPhotoActivity.this.getContentResolver(), miPath);
                        if (imagen.getDrawable() != null){
                            buttonSavePhoto.setVisibility(View.VISIBLE);
                        }else{
                            buttonSavePhoto.setVisibility(View.GONE);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case COD_FOTO:
                    MediaScannerConnection.scanFile(RegisterPhotoActivity.this, new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("Ruta de almacenamiento","Path: "+path);
                                }
                            });
                    bitmap= BitmapFactory.decodeFile(rutaImagen);
                    imagen.setImageBitmap(bitmap);
                    if (imagen.getDrawable() != null){
                        buttonSavePhoto.setVisibility(View.VISIBLE);
                    }else{
                        buttonSavePhoto.setVisibility(View.GONE);
                    }

                    break;
            }
        }
    }

    private void savePhoto(){
        RequestQueue request;
        JsonObjectRequest JOR;
        request = Volley.newRequestQueue(this);
        String img = imgToString(bitmap);
        Map<String, String> params = new HashMap<>();
        params.put("photo",img);
        JSONObject parameters = new JSONObject(params);
        JOR = new JsonObjectRequest(Request.Method.PUT, url + "/savephoto/" + preferences.getTreeId(), parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadPhoto.setVisibility(View.GONE);
                textViewLoadPhoto.setVisibility(View.GONE);
                finishAffinity ();
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                Toast.makeText(RegisterPhotoActivity.this,"Cambio exitoso",Toast.LENGTH_SHORT).show();
                startActivity(mainActivity);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadPhoto.setVisibility(View.GONE);
                textViewLoadPhoto.setVisibility(View.GONE);
                buttonSavePhoto.setEnabled(true);
                buttonLoad.setEnabled(true);
                validations.showDialog("No se pudo registrar la fotografía, tome otra fotografía o intente despues.",
                                            RegisterPhotoActivity.this);
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

    private String imgToString(Bitmap bitmap){
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, array);
        byte[] imagenByte = array.toByteArray();
        String imagenString = Base64.encodeToString(imagenByte, Base64.DEFAULT);
        Log.i("imagen:", imagenString);
        return imagenString;
    }
    public void OnclickSavePhoto(View view) {
        loadPhoto.setVisibility(View.VISIBLE);
        textViewLoadPhoto.setVisibility(View.VISIBLE);
        buttonSavePhoto.setEnabled(false);
        buttonLoad.setEnabled(false);
        savePhoto();
    }
}