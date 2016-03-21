package com.lab.receiver;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lab.notification.PictureNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

public class PictureReceiver extends BroadcastReceiver{

    public static final String PREF_NAME = "TimeTracking";
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (intent != null) {
			
			SharedPreferences shared = context.getSharedPreferences(PREF_NAME, 0);
			String authObj = shared.getString("authObj", "");
			
		    if(!(authObj.equalsIgnoreCase(""))){
				Cursor cursor = context.getContentResolver().query(intent.getData(), null, null, null, null);
				cursor.moveToFirst();
				String imagePath = cursor.getString(cursor.getColumnIndex("_data"));
				Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
				if (bitmap != null) {
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					bitmap = shrinkImage(bitmap, 400);
					
					bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream);
					byte[] byteArr = stream.toByteArray();
					String image_str = Base64.encodeToString(byteArr, Base64.DEFAULT);
					
					Location location = getLocation(context);
					double lat;
					double lon;
					try {
				        lat = location.getLatitude();
				        lon = location.getLongitude();
				    } catch (NullPointerException e) {
				        lat = -1.0;
				        lon = -1.0;
				    }
				    
				    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
					Date date = new Date();
				    
				    JSONArray jsonArr;
				   
				    try {
					    String sharedVar = shared.getString("value", "");
					    String prevString;
					    if(!(sharedVar.equalsIgnoreCase(""))){
					    	prevString = (shared.getString("value", ""));
					    	jsonArr = new JSONArray(prevString);
					    } else {
					    	jsonArr = new JSONArray();
					    }
				    	
						JSONObject jsonObj = new JSONObject();
						jsonObj.put("id", System.currentTimeMillis() / 1000L);
						jsonObj.put("image", image_str);
						jsonObj.put("date", dateFormat.format(date));
						jsonObj.put("longitude", lon);
						jsonObj.put("latitude", lat);
						
						Log.d("longitude + latitude", lon + " : " +lat );
						jsonArr.put(jsonObj);
				   
					    SharedPreferences.Editor editor = shared.edit();
					    editor.putString("value", jsonArr.toString());
					    editor.commit();
					    
					    if(jsonArr != null && jsonArr.length() > 0){
					    	PictureNotification.notify(jsonArr.length(), context);
					    }
					    
				    } catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private Bitmap shrinkImage(Bitmap bitmap, int maxDim) {
		int widthOrig = bitmap.getWidth();
		int heightOrig = bitmap.getHeight();

		Bitmap shrinkedBitmap = null;
		int widthScaled = 0;
		int heightScaled = 0;

		if (heightOrig <= widthOrig) {
			widthScaled = maxDim;
			heightScaled = (heightOrig * maxDim) / widthOrig;
		} else {
			heightScaled = maxDim;
			widthScaled = (widthOrig * maxDim) / heightOrig;
		}
		shrinkedBitmap = Bitmap.createScaledBitmap(bitmap, widthScaled,
				heightScaled, true);

		return shrinkedBitmap;
	}
	
	private Location getLocation(Context context) {
	    // Get the location manager
	    LocationManager locationManager = (LocationManager)context.getSystemService(context.LOCATION_SERVICE);
	    Criteria criteria = new Criteria();
	    String bestProvider = locationManager.getBestProvider(criteria, false);
	    Location location = null;
	    if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
	    	location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	    } else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	    	location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    }
	    
	    return location;
	}
	
	public void turnGPSOn(Context ctx)
	{
	     Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
	     intent.putExtra("enabled", true);
	     ctx.sendBroadcast(intent);

	    String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	    if(!provider.contains("gps")){ //if gps is disabled
	        final Intent poke = new Intent();
	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        poke.setData(Uri.parse("3")); 
	        ctx.sendBroadcast(poke);
	    }
	}
	// automatic turn off the gps
	public void turnGPSOff(Context ctx)
	{
	    String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	    if(provider.contains("gps")){ //if gps is enabled
	        final Intent poke = new Intent();
	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        poke.setData(Uri.parse("3")); 
	        ctx.sendBroadcast(poke);
	    }
	}
}