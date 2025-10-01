package app.sembrando.vidas.java_class;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class Validations {

    public static Bitmap convert(String base64Str) throws IllegalArgumentException
    {
        byte[] decodedBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",")  + 1),
                Base64.DEFAULT
        );

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String convert(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    public void showDialog(String errors, Context context){
        new MaterialAlertDialogBuilder(context)
                .setTitle("Error")
                .setMessage(errors)
                .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("MainActivity", "Aborting mission...");
                    }
                }).show();
    }

    public void errors(VolleyError error, Context context){
        String res = "";
        NetworkResponse networkResponse = error.networkResponse;

        // Check if networkResponse is null (connection error)
        if (networkResponse == null) {
            showDialog("Error de conexión. No se pudo conectar al servidor. Verifica tu conexión a internet.", context);
            return;
        }

        String json_error = new String(networkResponse.data);
        try {
            JSONObject jsonError = new JSONObject(json_error);
            json_error = jsonError.toString();
        } catch (JSONException e) {
            Log.d("MainActivity", "Aborting mission...");
        }
        Log.e("Error;", json_error);
        int code_error = networkResponse.statusCode;

        switch (code_error){
            case 422 :
                res = json_error.
                        replaceAll("\\]", "").
                        replaceAll("\\}\\}", " ").
                        replaceAll("\\[", "").
                        replaceAll("\\{", "\n").
                        replaceAll("\\}", "\n").
                        replaceAll(",", "\n").
                        replaceAll("\"", "").
                        replaceAll("errors:", "");;
                break;
            case 401:
                res = "No autenticado";
                break;
            case 400:
                res= "Mala solicitud";
                break;
            case 403:
                res = "No autorizado";
                break;
            case 404:
                res = "No se encuentra la pagina";
                break;
            default:
                res= "Error en el servidor, vuelva a intentar despues" + code_error +json_error;
                break;
        }
        showDialog( res, context);
    }
}
