package com.lab.activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;
import com.lab.helper.ImageAdapter;
import com.lab.helper.ImageData;

public class GMapActivity extends Activity {
	public static final String PREF_NAME = "TimeTracking";
	private GoogleMap googleMap;
	private Marker customMarker;
	ImageAdapter imageAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_map);
	    
	    // get intent data
        Intent i = getIntent();	        
        // Selected image position
        int position = i.getExtras().getInt("position");
	    imageAdapter = new ImageAdapter(this);
	    
	    try {
	    	if (googleMap == null) {
	    		googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	    	}
	    	googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	       
	        setAllItems(position);
	        getRequiredItemZoomed(ImageAdapter.itemList.get(position).getLatitude(), ImageAdapter.itemList.get(position).getLongitude());
			    
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	   }
	   private void setUpMap(Bitmap imgBitmap, final LatLng location, String desc, String date, Boolean isInfoWindow) {
		   
			View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
			ImageView imgView = (ImageView) marker.findViewById(R.id.img_thumbnail);
			imgView.setImageBitmap(imgBitmap);
	 
			customMarker = googleMap.addMarker(new MarkerOptions()
			.position(location)
			.title(date.isEmpty() ? "" : "Captured on: " + date)
			.snippet(desc.isEmpty() ? "" : "Details: " + desc)
			.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker))));
			
			if(isInfoWindow){
				customMarker.showInfoWindow();
			}
		}
	   public static Bitmap createDrawableFromView(Context context, View view) {
			DisplayMetrics displayMetrics = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
			view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
			view.buildDrawingCache();
			Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
	 
			Canvas canvas = new Canvas(bitmap);
			view.draw(canvas);
	 
			return bitmap;
		}
	   private void getRequiredItemZoomed(String latitude, String longitude){
		   Double lat = Double.parseDouble(latitude);
		   Double lon = Double.parseDouble(longitude);
		   
		   if(lat != -1.0 && lon != -1.0){
			   final LatLng coordinates = new LatLng(lat, lon);
			   final View mapView = getFragmentManager().findFragmentById(R.id.map).getView();
			   
				if (mapView.getViewTreeObserver().isAlive()) {
					mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {
							CameraUpdate center = CameraUpdateFactory.newLatLng(coordinates);
							CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
				
							googleMap.moveCamera(center);
							googleMap.animateCamera(zoom);
						}
					});
				}
		   }
	   }
	   private void setAllItems(int position){
		   Double longStr;
		   Double latStr;
		   String desc;
		   String date;
		   String imgStr;
		   Boolean isInfoWindow;
		   int pos = 0;
		   for(ImageData imgData : ImageAdapter.itemList){
			   isInfoWindow = false;
			   imgStr = imgData.getBitmap();
			   longStr = Double.parseDouble(imgData.getLongitude());
			   latStr = Double.parseDouble(imgData.getLatitude());
			   date = imgData.getDate();
			   desc = imgData.getDescription();
			   if(pos == position){
				   isInfoWindow = true;
			   }
			   pos ++;
			   setUpMap(imageAdapter.decodeBase64(imgStr), new LatLng(latStr, longStr), desc, date, isInfoWindow);
			}
	   }
}
