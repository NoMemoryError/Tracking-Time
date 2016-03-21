package com.lab.activities;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.lab.helper.CollectorListViewAdapter;
import com.lab.helper.CollectorRowItem;

import de.contextdata.ContextData;
import de.contextdata.Entity;
import de.contextdata.Event;
import de.contextdata.ContextData.Listener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class CollectorActivity extends Activity implements ContextData.Listener {
	
	public static final String PREF_NAME = "TimeTracking";
	ListView listView;
	List<CollectorRowItem> rowItems;
	CollectorListViewAdapter adapter;
	SharedPreferences shared;
	Button upload;
	Button discard;
	CheckBox checkAll;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collector);
		
		discard = (Button) findViewById(R.id.btnDiscard);
    	upload = (Button) findViewById(R.id.btnUpload);
	    
    	discard.setOnClickListener(discardClickListener);
    	upload.setOnClickListener(uploadClickListener);
    	
		shared = getSharedPreferences(PREF_NAME, 0);
	    String prevString = (shared.getString("value", ""));
	    try
	    {
		    JSONArray jsonArr = new JSONArray(prevString);
			
			rowItems = new ArrayList<CollectorRowItem>();
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject obj = jsonArr.getJSONObject(i);
				
				String encodedImage = (String) obj.get("image");
				int id = (Integer) obj.get("id");
				
				byte[] imageAsBytes = Base64.decode(encodedImage, 0);
				Bitmap bp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
				
				CollectorRowItem item = new CollectorRowItem(id, bp, obj);
			    rowItems.add(item);
			}
	    }catch (JSONException e) {
			e.printStackTrace();
		}

		listView = (ListView) findViewById(R.id.list);
		adapter = new CollectorListViewAdapter(this, R.layout.activity_collector_row, rowItems);
		adapter.setAddDescClickListener(addDescClickListner);
		adapter.onCheckChangeListner(checkChangeListner);
		
		listView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.activity_collector_header, null));
		checkAll = (CheckBox) findViewById(R.id.checkAll);
		checkAll.setOnCheckedChangeListener(checkAllClickListener);
		
		listView.setAdapter(adapter);
	}
	
	private OnClickListener uploadClickListener= new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			boolean isChecked = false;
			for(int pos=0; pos<rowItems.size();){
	            if (pos != ListView.INVALID_POSITION) {
	                CollectorRowItem item = rowItems.get(pos);         
	                isChecked = item.isSelected();
	                if(isChecked ){
	                	
						Log.d("jsonObj", item.getJsonObj().toString());
						
						int ts = (int) (new Timestamp(System.currentTimeMillis()).getTime() / 1000);
						Event e1 = new Event("START", "PICTURE", ts);
						try {
							JSONObject jObj = item.getJsonObj();
							Iterator<?> keys = jObj.keys();
			
							while( keys.hasNext() ){
								String key = (String)keys.next();
								Entity<String> event = new Entity<String>(key, jObj.get(key).toString());
								e1.addEntity(event);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						SharedPreferences shared = getSharedPreferences(PREF_NAME, 0);
						String authObj = shared.getString("authObj", "");
						
						String username = "";
						String password = "";
						
					    if(!(authObj.equalsIgnoreCase(""))){
					    	try {
					    		JSONObject json = new JSONObject(authObj);
								username = json.get("username").toString();
								password = json.get("password").toString();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					    	
					    	ContextData cd = new ContextData(
							    	"http://api.learning-context.de/", 3, username, password, 7, "jb6f4bro0ntwvba1p85gwepgbn4wuqz23bgsn2m9bjilpf4dnt");
							if(cd != null) {
								cd.registerPOSTListener(getListener());
								Gson gson = new Gson();
								String data = gson.toJson(e1);
								data = "[" + data + "]";
								cd.post("/events/update", data);
								rowItems.remove(item);
								listView.setAdapter(adapter);
								
								int id = (int) item.getId();
								
								String jsonStr = (shared.getString("value", ""));
								JSONArray jsonArr;
								try {
									int loc = -1;
									jsonArr = new JSONArray(jsonStr);
									
									for (int i = 0; i < jsonArr.length(); i++) {
										JSONObject obj = jsonArr.getJSONObject(i);
										if(id == (Integer)obj.get("id")){
											loc = i;
										}
									}
									JSONArray jsonarr = new JSONArray();
									if (jsonArr != null) { 
										for (int j=0;j<jsonArr.length();j++){ 
											//Excluding the item at position
											if (j != loc) {
												jsonarr.put(jsonArr.get(j));
											}
										}
										SharedPreferences.Editor editor = shared.edit();
										editor.putString("value", jsonarr.toString());
										editor.commit();
									}	
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
					    }
					    if((username.equalsIgnoreCase("") || (password.equalsIgnoreCase("")))){
					    	System.err.println("Unabe to find user credentials.");
					    }
					    if(rowItems.size() == 0){
							CollectorActivity.this.finish();
						}
					    }
	                else{
	                	pos++;
	                }
	                }
	            }
			showUploadDiscardButtons();
            }
		};
	
	private OnClickListener discardClickListener= new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			boolean isChecked = false;
            for(int pos=0; pos<rowItems.size();){
	            if (pos != ListView.INVALID_POSITION) {
	                CollectorRowItem item = rowItems.get(pos);         
	                isChecked = item.isSelected();
	                if(isChecked ){
						//CollectorRowItem item = (CollectorRowItem) v.getTag();
						rowItems.remove(item);
						listView.setAdapter(adapter);
						
						int id = (int) item.getId();
						
						String jsonStr = (shared.getString("value", ""));
						JSONArray jsonArr;
						try {
							int loc = -1;
							jsonArr = new JSONArray(jsonStr);
							
							for (int i = 0; i < jsonArr.length(); i++) {
								JSONObject obj = jsonArr.getJSONObject(i);
								if(id == (Integer)obj.get("id")){
									loc = i;
								}
							}
							JSONArray jsonarr = new JSONArray();
							if (jsonArr != null) { 
								for (int j=0;j<jsonArr.length();j++){ 
									//Excluding the item at position
									if (j != loc) {
										jsonarr.put(jsonArr.get(j));
									}
								}
								SharedPreferences.Editor editor = shared.edit();
								editor.putString("value", jsonarr.toString());
								editor.commit();
							}	
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if(rowItems.size() == 0){
							CollectorActivity.this.finish();
						}
	                }
	                else{
	                	pos++;
	                }
	            }
	        }
            showUploadDiscardButtons();
        }
	};

	private OnClickListener addDescClickListner = new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			final CollectorRowItem item = (CollectorRowItem) v.getTag();
			LayoutInflater li = LayoutInflater.from(CollectorActivity.this);
			View promptsView = li.inflate(R.layout.add_description_alertbox, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CollectorActivity.this);
			alertDialogBuilder.setView(promptsView);

			final EditText userInput = (EditText) promptsView.findViewById(R.id.addDescAlertBox);

			if(item.getDesc().equals("Add Description")) {
				userInput.setHint(item.getDesc());
			}
			else {
				userInput.setText(item.getDesc());
				userInput.setSelection(userInput.getText().length());
			}
			// set dialog message
			alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Add",
				  new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
				    	item.setDesc(userInput.getText().toString());
				    	adapter.notifyDataSetChanged();
				    	
				    	int itemId = (int) item.getId();
						String jsonStr = (shared.getString("value", ""));
						JSONArray jsonArr;
						try {
							JSONObject updateJson = new JSONObject();
							int pos = -1;
							jsonArr = new JSONArray(jsonStr);
							JSONObject obj = new JSONObject();
							for (int i = 0; i < jsonArr.length(); i++) {
								obj = jsonArr.getJSONObject(i);
								if(itemId == (Integer)obj.get("id")){
									obj.put("desc", userInput.getText().toString());
									updateJson = obj;
									pos = i;
								}
							}
							if(pos != -1){
								jsonArr.put(pos, updateJson);
								item.setJsonObj(updateJson);
							}
							
							SharedPreferences.Editor editor = shared.edit();
							editor.putString("value", jsonArr.toString());
							editor.commit();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				  })
				.setNegativeButton("Discard",
				  new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				    }
				  });

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
			
			listView.setAdapter(adapter);
		}
	};
	
	private OnCheckedChangeListener checkChangeListner = new OnCheckedChangeListener() {
		@Override
        public void onCheckedChanged(CompoundButton view, boolean isChecked) {
			final CollectorRowItem element = (CollectorRowItem) view.getTag();
            element.setSelected(view.isChecked());
            showUploadDiscardButtons();
		}
	};
	
	private OnCheckedChangeListener checkAllClickListener = new OnCheckedChangeListener() {
		@Override
        public void onCheckedChanged(CompoundButton view, boolean isChecked) {
			for(int i=0;i< rowItems.size();i++){
				if (i != ListView.INVALID_POSITION) {
	                CollectorRowItem row = rowItems.get(i);  
	                row.setSelected(isChecked);
	            }
            }
            adapter.notifyDataSetChanged();
		}
	};
	
	@Override
	public void onGETResult(String result) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPOSTResult(String result) {
		Log.d("Uploading Response", result);
	}
	
	public Listener getListener() {
		return this;
	}
	public void showUploadDiscardButtons(){
		boolean isChecked;
		int count = 0;
        for(int pos=0; pos<rowItems.size(); pos++){
            if (pos != ListView.INVALID_POSITION) {
                CollectorRowItem p = rowItems.get(pos);         
                isChecked = p.isSelected();
                if(isChecked ){
                	count++;
                }
            }
        }

        if(count > 0) {
        	upload.setVisibility(View.VISIBLE);
        	discard.setVisibility(View.VISIBLE);
        }
        else {
        	if (checkAll.isChecked()) {
        		checkAll.setChecked(false);
            }
        	upload.setVisibility(View.INVISIBLE);
        	discard.setVisibility(View.INVISIBLE);
        }
	}
}
