package com.lab.helper;

public class ImageData {
	String id;
	String bitmap;
	String description;
	String longitude;
	String latitude;
	String date;
	
	public ImageData(String id, String bitmap){
		this.id = id;
		this.bitmap = bitmap;
		this.description = "";
		this.longitude = "";
		this.latitude = "";
		this.date = "";
	}
	public ImageData(String id, String bitmap, String description, String longitude, String latitude, String date){
		this.id = id;
		this.bitmap = bitmap;
		this.description = description;
		this.longitude = longitude;
		this.latitude = latitude;
		this.date = date;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBitmap() {
		return bitmap;
	}
	public void setBitmap(String bitmap) {
		this.bitmap = bitmap;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
}
