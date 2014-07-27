package net.louage.bijoux.constants;

import com.google.gson.Gson;

import net.louage.bijoux.model.User;
import android.content.Context;
import android.content.SharedPreferences.Editor;



public class SharedPreferences {
	Context context;

	private SharedPreferences() {
		super();
	}
	
    public static boolean checkConnectionData(Context ctx) {
		android.content.SharedPreferences preferences = ctx.getSharedPreferences("ConnectionData", 0);
		boolean a = false;
		boolean b = false;
		if ((preferences).contains("spUsername")) {
			String usrnm = ((android.content.SharedPreferences) preferences).getString("spUsername", "");
			if (usrnm.trim().length() > 0) {
				a = true;
			} else {
				a = false;
			}
		}
		if ((preferences).contains("spPassword")) {
			String psswrd = ((android.content.SharedPreferences) preferences).getString("spPassword", "");
			if (psswrd.trim().length() > 0) {
				b = true;
			} else {
				b = false;
			}
		}
		if (a == false || b == false) {
			return false;
		} else {
			return true;
		}
	}
    
    public static int getUserId(Context ctx){
    	android.content.SharedPreferences preferences = ctx.getSharedPreferences("ConnectionData", 0);
		return ((android.content.SharedPreferences) preferences).getInt("user_id", 0);	
    }
    
    public static String getroleName(Context ctx){
    	android.content.SharedPreferences preferences = ctx.getSharedPreferences("ConnectionData", 0);
    	String role = ((android.content.SharedPreferences) preferences).getString("role", "");
    	switch (role) {
		case "admin":
			return "admin";
		case "teamleader":
			return "teamleader";
		case "member":
			return "member";
		default:
			return "unknown";
		}  	
    	
    }
    
    public static String getUsername(Context ctx){
    	android.content.SharedPreferences preferences = ctx.getSharedPreferences("ConnectionData", 0);
    	String username = ((android.content.SharedPreferences) preferences).getString("spUsername", "");
    	return username;
    }
    
    public static String getPassword(Context ctx){
    	android.content.SharedPreferences preferences = ctx.getSharedPreferences("ConnectionData", 0);
    	String password = ((android.content.SharedPreferences) preferences).getString("spPassword", "");
    	return password;
    }
    
    public static void setUserId(Context ctx, int id){
    	android.content.SharedPreferences preferences = ctx.getSharedPreferences("ConnectionData", 0);
		Editor editor = preferences.edit();
		editor.putInt("user_id", id);
		editor.commit();	
    }
    
    public static void setUserName(Context ctx, String name){
    	android.content.SharedPreferences preferences = ctx.getSharedPreferences("ConnectionData", 0);
		Editor editor = preferences.edit();
		editor.putString("spUsername", name);
		editor.commit();	
    }
    
    public static void setPassword(Context ctx, String password){
    	android.content.SharedPreferences preferences = ctx.getSharedPreferences("ConnectionData", 0);
		Editor editor = preferences.edit();
		editor.putString("spPassword", password);
		editor.commit();	
    }
    
    public static void setRole(Context ctx, String role){
    	android.content.SharedPreferences preferences = ctx.getSharedPreferences("ConnectionData", 0);
		Editor editor = preferences.edit();
		editor.putString("role", role);
		editor.commit();	
    }
    
    public static void setUser(Context ctx, User user){
    	android.content.SharedPreferences preferences = ctx.getSharedPreferences("ConnectionData", 0);
		Editor editor = preferences.edit();
		Gson gson = new Gson();
		String usr = gson.toJson(user);
		editor.putString("user", usr);
		editor.commit();	
    }
    
    public static User getUser(Context ctx){
    	android.content.SharedPreferences preferences = ctx.getSharedPreferences("ConnectionData", 0);
		Gson gson = new Gson();
		String jsonUser = preferences.getString("user", "");
		User usr = gson.fromJson(jsonUser, User.class);
		return usr;	
    }
    
    public static void deleteSharedPreferences(Context ctx){
    	android.content.SharedPreferences preferences = ctx.getSharedPreferences("ConnectionData", 0);
		preferences.edit().clear().commit();
    }
}
