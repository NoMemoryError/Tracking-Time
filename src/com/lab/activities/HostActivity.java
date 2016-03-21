package com.lab.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lab.helper.DataContext;
import com.lab.helper.ImageAdapter;
import com.lab.helper.ImageData;
import com.lab.helper.SearchFields;

import de.contextdata.ContextData;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.Spinner;

public class HostActivity extends FragmentActivity implements ContextData.Listener{
	public static final String PREF_NAME = "TimeTracking";
	public String TAG = "HostActivity";
	public static boolean isGridLoading;
	
	ImageAdapter imgAdapter;
	Button searchBtn;
	Spinner spinner;
	DatePicker datePicker;
	GridView gridView;
	AlertDialog.Builder alertBox;
	JSONArray jsonarr;
	SearchFields searchFields;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host);
		isGridLoading = true;
		searchFields = new SearchFields();
		imgAdapter = new ImageAdapter(this);
		gridView = (GridView) findViewById(R.id.gridview);
		
//		FragmentManager fragManager = getSupportFragmentManager();
//        Fragment fragment = fragManager.findFragmentById(R.id.fragment_place);
//        
//        datePicker = (DatePicker) fragment.getView().findViewById(R.id.datepicker);
//        
//		  searchBtn = (Button) fragment.getView().findViewById(R.id.search_btn);
//        searchBtn.setOnClickListener(searchClickListner);
//        
//        datePicker = (DatePicker) fragment.getView().findViewById(R.id.datepicker);
        
		
		datePicker = (DatePicker) findViewById(R.id.datepicker);
		searchBtn = (Button) findViewById(R.id.search_btn);
		searchBtn.setOnClickListener(searchClickListner);
		
		spinner = (Spinner) findViewById(R.id.dateSpinner);
		spinner.setOnItemSelectedListener(itemSelectedListner);
		
		if(DataContext.cd != null) {
			DataContext.cd.registerGETListener(this);
			DataContext.cd.get(("/events/show"), "{\"model\":\"COMPLETE\", \"type\":\"PICTURE\"}");
		} else {
			System.err.println("The context is null please initialize it");
		}
		setCurrentDateOnDatePicker();
		addListnerToGridElements();
	}
	public void setCurrentDateOnDatePicker() {

		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// set current date into date picker
		datePicker.init(year, month, day, null);
		datePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
		datePicker.setMaxDate(c.getTimeInMillis());
		
		searchFields.setRangeSearch(false);
		searchFields.setDateSearch(true);
	}
	public void addListnerToGridElements(){
		gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
			   
            	alertBox = new AlertDialog.Builder(HostActivity.this);
            	alertBox.setTitle("View");
            	addListnerToAlertDialogOptions(position);
            	alertBox.show();
            }
		});
	}
	public void addListnerToAlertDialogOptions(final int position){
		final String[] option = new String[] { "Picture Details", "Details on Map"};
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(HostActivity.this, android.R.layout.select_dialog_item, option);

    	alertBox.setAdapter(adapter, new DialogInterface.OnClickListener() {
	          public void onClick(DialogInterface dialog, int which) {
	        	  Intent intent;
	        	  switch (which) {
	              	case 0: 
	            	  	// Sending image pos to FullImageActivity
		                intent = new Intent(HostActivity.this, FullImageActivity.class);
		                
		                // passing array index
		                intent.putExtra("position", position);
		                startActivity(intent);
	                break;
	              	case 1:
						// Sending image pos to MapsActivity
		                intent = new Intent(HostActivity.this, GMapActivity.class);
		                
		                // passing array index
		                intent.putExtra("position", position);
		                startActivity(intent);
	                break;
	        	  }
	          }
    	});
	}
	private OnClickListener searchClickListner = new OnClickListener() {
		@Override
		public void onClick(View v) {
			searchFields.setDay(Integer.toString(datePicker.getDayOfMonth()));
			searchFields.setMonth(Integer.toString(datePicker.getMonth() + 1));
			searchFields.setYear(Integer.toString(datePicker.getYear()));

        	isGridLoading = false;
        	if(DataContext.cd != null) {
    			DataContext.cd.registerGETListener(HostActivity.this);
    			DataContext.cd.get(("/events/show"), "{\"model\":\"COMPLETE\", \"type\":\"PICTURE\"}");
    		} else {
    			System.err.println("The context is null please initialize it");
    		}
		}
	};
	private OnItemSelectedListener itemSelectedListner = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View arg1, int position,
				long id) {
			if(position > 0){
				Date today = new Date();
				Date upperDate;
				
				searchFields.setRangeSearch(true);
				searchFields.setDateSearch(false);
				
				switch(position){
					case 1:
						upperDate = new Date(today.getYear(), today.getMonth(), today.getDate() - 1);
						searchFields.setLowerDate(upperDate);
						break;
					case 2:
						upperDate = new Date(today.getYear(), today.getMonth(), today.getDate()- 7);
						searchFields.setLowerDate(upperDate);
						Log.d("date set: ", searchFields.getLowerDate().toString());
						break;
					case 3:
						upperDate = new Date(today.getYear(), today.getMonth() - 1, today.getDate());
						searchFields.setLowerDate(upperDate);
						break;
					case 4:
						upperDate = new Date(today.getYear() - 1, today.getMonth(), today.getDate());
						searchFields.setLowerDate(upperDate);
						break;
				}
			}
			else{
				searchFields.setRangeSearch(false);
				searchFields.setDateSearch(true);
			}
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}
	};
	@Override
	public void onGETResult(String result) {
		handleResultingJson(result);
		if(isGridLoading){	
			gridView.setAdapter(imgAdapter);
//			isGridLoading = false;
		}
		else{
			if(searchFields.isDateSearch()){
				String date = searchFields.getDay() + "." + searchFields.getMonth() + "." + searchFields.getYear();
				Log.d("date is selected", date);
				for (Iterator<ImageData> iter = ImageAdapter.itemList.iterator(); iter.hasNext(); ) {
					ImageData imgData = iter.next();
					if(!imgData.getDate().equals(date)){
				        iter.remove();
				    }
				}
				gridView.setAdapter(imgAdapter);
			}
			else
			if(searchFields.isRangeSearch()){
				Log.d("here", "here");
				Log.d("lower date", searchFields.getLowerDate().toString());
				Log.d("today", new Date().toString());
				SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
				for (Iterator<ImageData> iter = ImageAdapter.itemList.iterator(); iter.hasNext(); ) {
					ImageData imgData = iter.next();
					try {
						Log.d("imgData get date", imgData.getDate());
						if((formatter.parse(imgData.getDate()).before(searchFields.getLowerDate()))){
							Log.d("before the lower and after ", imgData.getDate());
						    iter.remove();
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				gridView.setAdapter(imgAdapter);
			}
		}
	}
	@Override
	public void onPOSTResult(String result) {
		Log.d(TAG, "POST works: " + result);
	}
	public void handleResultingJson(String result){
		try {
			ImageAdapter.itemList = new ArrayList<ImageData>();
			String id = "";
			String imgStr = "";
			String desc = "";
			String longitude = "";
			String latitude = "";
			String date = "";
			
			jsonarr = new JSONObject(result).getJSONArray("events");
			for(int i = 0; i<jsonarr.length(); i++){
				JSONObject eventsarr = jsonarr.getJSONObject(i);
				JSONArray entitiesarr = eventsarr.getJSONArray("entities");
				
				for(int j = 0; j<entitiesarr.length(); j++){
					JSONObject entityObj =  entitiesarr.getJSONObject(j);
					String key = entityObj.getString("key");
					String value = entityObj.getString("value");
					if(key.equals("id")){
						id = value;
					}
					else 
						if(key.equals("image")){
						imgStr = value;
					}
					else 
						if(key.equals("date")){
						date = value;
					}
					else 
						if(key.equals("desc")){
						desc = value;
					}
					else 
						if(key.equals("latitude")){
						latitude = value;
					}
					else 
						if(key.equals("longitude")){
						longitude = value;
					}
				}
				if(!id.isEmpty() && !imgStr.isEmpty()){
					imgAdapter.add(id, imgStr, desc, longitude, latitude, date);
					imgStr = "";
					desc = "";
					longitude = "";
					latitude = "";
					date = "";
					id = "";
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	@Override  
	public void onBackPressed(){
		if(!isGridLoading){
			Log.d("!backbtn", ""+isGridLoading);
			this.startActivity(new Intent(HostActivity.this, HostActivity.class));
			this.finish();
		    //return; 
		}
		else if(isGridLoading){
			Log.d("backbtn", ""+isGridLoading);
			super.onBackPressed();
		} 
	}  
}
