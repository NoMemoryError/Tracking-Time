package com.lab.helper;

import java.util.List;

import com.lab.activities.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class CollectorListViewAdapter extends ArrayAdapter<CollectorRowItem> {
	
    Context context;
    private OnClickListener addDescClickListner;
    private OnCheckedChangeListener checkChangeListner;
    private List items;
    
    public CollectorListViewAdapter(Context context, int resourceId,
            List<CollectorRowItem> items) {
        super(context, resourceId, items);
        this.items = items;
        this.context = context;
    }
     
    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtViewDesc;
        CheckBox checkbox;
    }
     
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        CollectorRowItem rowItem = getItem(position);
         
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_collector_row, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.txtViewDesc = (TextView) convertView.findViewById(R.id.txtViewDesc);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.check);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
                 
        holder.imageView.setImageBitmap(rowItem.getBitmap());
        
        holder.txtViewDesc.setOnClickListener(addDescClickListner);
        holder.txtViewDesc.setText(rowItem.getDesc());
        holder.txtViewDesc.setTag(items.get(position));
         
        holder.checkbox.setTag(items.get(position));
        holder.checkbox.setChecked(rowItem.isSelected());
        holder.checkbox.setOnCheckedChangeListener(checkChangeListner);
         
        return convertView;
    }
    public void onCheckChangeListner(OnCheckedChangeListener checkChangeListner){
    	this.checkChangeListner = checkChangeListner;
    }
    public void setAddDescClickListener(OnClickListener addDescClickListner){
    	this.addDescClickListner = addDescClickListner;
    }
}