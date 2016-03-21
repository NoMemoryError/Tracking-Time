package com.lab.activities;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.lab.helper.ImageAdapter;
import com.lab.helper.ImageData;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

public class FullImageActivity extends Activity {
	TextView descView;
	TextView otherDetailsView;
	ImageData imgObject;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        
        // get intent data
        Intent i = getIntent();
        
        // Selected image position
        int position = i.getExtras().getInt("position");
        ImageAdapter imageAdapter = new ImageAdapter(this);	
        imgObject = ImageAdapter.itemList.get(position);
        
        ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
        descView = (TextView) findViewById(R.id.full_image_desc);
        otherDetailsView = (TextView) findViewById(R.id.full_image_other_details);
        imageView.setImageBitmap(imageAdapter.decodeBase64(imgObject.getBitmap()));
        
        setImageDetails();
    }
    
    private void setImageDetails(){
    	Double lat = Double.parseDouble(imgObject.getLatitude());
        Double lon = Double.parseDouble(imgObject.getLongitude());
        String address = "";	
        if(lat != -1.0 && lon != -1.0){
	        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
	        List<Address> addresses;
			try {
				addresses = geocoder.getFromLocation(lat, lon, 1);
				Address add = addresses.get(0);
				address = (add.getAddressLine(0).isEmpty() ? "" : add.getAddressLine(0) + "\n") + 
						(add.getAddressLine(1).isEmpty() ? "" : add.getAddressLine(1) + "\n") + 
								(add.getAddressLine(2).isEmpty() ? "" : add.getAddressLine(2));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
       
        descView.setText(Html.fromHtml("<b>" + imgObject.getDescription() + "</b><br>"));
        otherDetailsView.setText(Html.fromHtml("<b>Date: </b>" + imgObject.getDate() + 
        		(address.isEmpty() ? "" : "<br><b>Location: </b>" + address)));
    
    }
}
