package com.lab.helper;

import java.util.Date;


public class SearchFields {
	String day;
	String month;
	String year;
	Date lowerDate;
	boolean isDateSearch;
	boolean isRangeSearch;
	
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		switch(day){
		case "1":
			day = "01";
		case "2":
			day = "02";
			break;
		case "3":
			day = "03";
			break;
		case "4":
			day = "04";
			break;
		case "5":
			day = "05";
			break;
		case "6":
			day = "06";
			break;
		case "7":
			day = "07";
			break;
		case "8":
			day = "08";
			break;
		case "9":
			day = "09";
			break;
		}
		this.day = day;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		switch(month){
		case "1":
			month = "01";
		case "2":
			month = "02";
			break;
		case "3":
			month = "03";
			break;
		case "4":
			month = "04";
			break;
		case "5":
			month = "05";
			break;
		case "6":
			month = "06";
			break;
		case "7":
			month = "07";
			break;
		case "8":
			month = "08";
			break;
		case "9":
			month = "09";
			break;
		}
		this.month = month;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public Date getLowerDate() {
		return lowerDate;
	}
	public void setLowerDate(Date lowerDate) {
		this.lowerDate = lowerDate;
	}
	public boolean isDateSearch() {
		return isDateSearch;
	}
	public void setDateSearch(boolean isDateSearch) {
		this.isDateSearch = isDateSearch;
	}
	public boolean isRangeSearch() {
		return isRangeSearch;
	}
	public void setRangeSearch(boolean isRangeSearch) {
		this.isRangeSearch = isRangeSearch;
	}
}
