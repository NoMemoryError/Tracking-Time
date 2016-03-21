package com.lab.activities;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.lab.helper.TTClient;

import de.contextdata.ContextData;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignupActivity extends Activity implements ContextData.Listener {
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_signup);
		
		final Button signupBtn = (Button) findViewById(R.id.btnSignup);
        signupBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				onSignupClick();
			}
        });
	}
	public void onSignupClick()
	{
		 final EditText usernameField = (EditText) findViewById(R.id.username);
		 String username = usernameField.getText().toString();
	        
	     final EditText passwordField = (EditText) findViewById(R.id.password);
	     String password = passwordField.getText().toString();
	     
	     final EditText confirmPasswordField = (EditText) findViewById(R.id.confirmPassword);
	     String confirmPassword = confirmPasswordField.getText().toString();
	     
	     final EditText emailField = (EditText) findViewById(R.id.email);
	     String email = emailField.getText().toString();
	     
	     if(password.equals(confirmPassword)){
		     final JsonObject jsonObject = new JsonObject();
		     jsonObject.addProperty("name", username);
		     jsonObject.addProperty("pass", password);
		     jsonObject.addProperty("email", email);
		     
		     System.out.println(jsonObject.toString());
		     
		     new AsyncTask<String, Void, String> () {
	
				@Override
				protected String doInBackground(String... arg0) {
					String response = TTClient.Param.sendPostRequest("http://api.learning-context.de/3/user/new", jsonObject);
					return response;
				}
				
				protected void onPostExecute(String result) {
					try {
						JSONObject getResult = new JSONObject(result);
						String resultStr = getResult.getString("result");
						
						if(resultStr.equals("1"))
						{
							startActivity(new Intent(SignupActivity.this, LoginActivity.class)); 
							SignupActivity.this.finish();
						}
						else
						{
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		    	 
		     }.execute();
	     }
	     else{
	    	final TextView passwordMismatch = (TextView) findViewById(R.id.passwordMismatch);
	    	passwordMismatch.setVisibility(View.VISIBLE);
			((EditText) findViewById(R.id.password)).setText("");
			((EditText) findViewById(R.id.confirmPassword)).setText("");
	     }
	}
	@Override
	public void onGETResult(String result) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPOSTResult(String result) {
		// TODO Auto-generated method stub
		
	}
}
