package com.lab.helper;

import org.json.JSONObject;

import android.graphics.Bitmap;


public class CollectorRowItem {
	private int id;
	private Bitmap bitmap;
	private String desc;
	private JSONObject jsonObj;
	private boolean selected;
     
    public CollectorRowItem(int id, Bitmap bitmap, JSONObject jsonObj) {
    	this.id = id;
        this.bitmap = bitmap;
        this.jsonObj = jsonObj;
        this.desc = "Add Description";
        this.selected = false;
    }
    public long getId() {
        return id;
    }
    public Bitmap getBitmap() {
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public JSONObject getJsonObj() {
        return jsonObj;
    }
    public void setJsonObj(JSONObject jsonObj) {
        this.jsonObj = jsonObj;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
