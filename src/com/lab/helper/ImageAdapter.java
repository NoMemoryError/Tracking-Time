package com.lab.helper;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter{
	private Context mContext;
    public static ArrayList<ImageData> itemList;
    
    // Constructor
    public ImageAdapter(Context c){
    	if(itemList == null || itemList.isEmpty()){
    		itemList = new ArrayList<ImageData>();
    	}
        mContext = c;
    }
 
    public void addBitmap(String id, String bitmap){
    	itemList.add(new ImageData(id, bitmap));
    }
    public void add(String id, String bitmap, String description, String longitude, String latitude, String date){
    	itemList.add(new ImageData(id, bitmap, description, longitude, latitude, date));
    }
    @Override
    public int getCount() {
        return itemList.size();
    }

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(330, 330));//140
        
        Bitmap bmImg = decodeBase64(itemList.get(position).getBitmap());
        imageView.setImageBitmap(bmImg);
        return imageView;
    }
    public Bitmap decodeBase64(String input) 
	{
	    byte[] decodedByte = Base64.decode(input, 0);
	    return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length); 
	}

}

