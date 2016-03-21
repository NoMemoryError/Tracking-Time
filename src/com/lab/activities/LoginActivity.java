package com.lab.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import de.contextdata.*;
import de.contextdata.ContextData.Listener;

import com.lab.helper.DataContext;

public class LoginActivity extends Activity implements ContextData.Listener {
	public static final String PREF_NAME = "TimeTracking";
	public String TAG = "LoginActivity";
	JSONObject json;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        checkForCredentials();
        
        addListenerOnSubmitBtn();
        addListnerOnSignupLink();
    }
	public void checkForCredentials(){
		SharedPreferences shared = getSharedPreferences(PREF_NAME, 0);
		String authObj = shared.getString("authObj", "");
	    if(!(authObj.equalsIgnoreCase(""))){
	    	try {
	    		JSONObject json = new JSONObject(authObj);
	    		String username = json.get("username").toString();
	    		String password = json.get("password").toString();
				if(!(username.equalsIgnoreCase("")) && !(password.equalsIgnoreCase(""))){
					if(DataContext.cd == null){
						DataContext.cd = new ContextData(
						    	"http://api.learning-context.de/", 3, username, password, 7, "jb6f4bro0ntwvba1p85gwepgbn4wuqz23bgsn2m9bjilpf4dnt");
					}
					redirectToHostActivity();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	public void addListenerOnSubmitBtn() {
        final Button signinBtn = (Button) findViewById(R.id.btnSignin);
        signinBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				 final EditText usernameField = (EditText) findViewById(R.id.username);
				 String username = usernameField.getText().toString();
			        
			     final EditText passwordField = (EditText) findViewById(R.id.password);
			     String password = passwordField.getText().toString();
			     
			     try {
			    	 json = new JSONObject();
			    	 json.put("username", username);
			    	 json.put("password", password);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			     
			    DataContext.cd = new ContextData(
			    	"http://api.learning-context.de/", 3, username, password, 7, "jb6f4bro0ntwvba1p85gwepgbn4wuqz23bgsn2m9bjilpf4dnt");
			    DataContext.cd.registerGETListener(getListener());
			    DataContext.cd.get("/user/test", "{}");
			}
		});
	}
	public void addListnerOnSignupLink() {
		 TextView signupLink = (TextView)findViewById(R.id.lnksignup);
		 signupLink.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					goToSignUp();
				}
			});
	}
	@Override
	public void onGETResult(String result) {
		try {
			JSONObject getResult = new JSONObject(result);
			String resultStr = getResult.getString("result");
			
			if(resultStr.equals("1")){
				SharedPreferences shared = getSharedPreferences(PREF_NAME, 0);
				SharedPreferences.Editor editor = shared.edit();
			    editor.putString("authObj", json.toString());
			    editor.commit();
			    
			    redirectToHostActivity();
			}
			else {
				final TextView invaliduserField = (TextView) findViewById(R.id.invaliduser);
				invaliduserField.setVisibility(View.VISIBLE);
				((EditText) findViewById(R.id.username)).setText("");
				((EditText) findViewById(R.id.password)).setText("");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void redirectToHostActivity(){
		startActivity(new Intent(this, HostActivity.class)); 
        this.finish();
	}
	@Override
	public void onPOSTResult(String result) {
		Log.d(TAG, "POST works: " + result);	
	}
	public Listener getListener(){
		return this;
	}
	public void goToSignUp(){
		startActivity(new Intent(this, SignupActivity.class)); 
        //this.finish();
	}
}
