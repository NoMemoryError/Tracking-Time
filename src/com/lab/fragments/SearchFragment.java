/**
 * 
 */
package com.lab.fragments;


import java.util.Calendar;


import com.lab.activities.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

/**
 * @author Memoona Mughal
 *
 */
public class SearchFragment extends Fragment{
	DatePicker datePicker;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		//Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_search, container, false);
		
		datePicker = (DatePicker) view.findViewById(R.id.datepicker);
		setCurrentDateOnDatePicker();
		return view;
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
	}
}
