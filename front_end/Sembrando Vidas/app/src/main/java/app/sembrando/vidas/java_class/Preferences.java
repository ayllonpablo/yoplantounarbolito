package app.sembrando.vidas.java_class;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private Context context;

    public Preferences(Context context){
        this.context = context;
    }

    public void savePreferencesUser(String token, String user_id ){
        SharedPreferences preferences= context.getSharedPreferences("preferenceLogin", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token",token);
        editor.putString("user_id", user_id);
        editor.commit();
    }

    public void savePreferencesUserNamePoints(String name, String points ){
        SharedPreferences preferences= context.getSharedPreferences("preferenceLogin", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("name",name);
        editor.putString("points", points);
        editor.commit();
    }

    public void savePreferencesTree(String tree_id){
        SharedPreferences preferences= context.getSharedPreferences("preferenceTree", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("tree_id",tree_id);
        editor.commit();
    }

    public void deletePreferences(){
        SharedPreferences preferences= context.getSharedPreferences("preferenceLogin", context.MODE_PRIVATE);
        preferences.edit().remove("token").commit();
        preferences.edit().remove("user_id").commit();
        preferences.edit().remove("name").commit();
        preferences.edit().remove("Points").commit();


        SharedPreferences preferencesTree= context.getSharedPreferences("preferenceTree", context.MODE_PRIVATE);
        preferencesTree.edit().remove("tree_id").commit();
    }

    public String getToken(){
        SharedPreferences preference= context.getSharedPreferences("preferenceLogin", context.MODE_PRIVATE);
        return preference.getString("token","");
    }

    public String getUserId(){
        SharedPreferences preference= context.getSharedPreferences("preferenceLogin", context.MODE_PRIVATE);
        return preference.getString("user_id","");
    }

    public String getTreeId(){
        SharedPreferences preference= context.getSharedPreferences("preferenceTree", context.MODE_PRIVATE);
        return preference.getString("tree_id","");
    }

    public String getName(){
        SharedPreferences preference= context.getSharedPreferences("preferenceLogin", context.MODE_PRIVATE);
        return preference.getString("name","");
    }

    public String getPoints(){
        SharedPreferences preference= context.getSharedPreferences("preferenceLogin", context.MODE_PRIVATE);
        return preference.getString("points","");
    }

}
