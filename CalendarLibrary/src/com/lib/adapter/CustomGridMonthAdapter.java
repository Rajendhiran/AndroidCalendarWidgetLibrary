package com.lib.adapter;

import java.util.ArrayList;

import com.lib.calendar.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author Rajendhiran. E
 */

public class CustomGridMonthAdapter extends BaseAdapter 
{
	Context ctx;
	ArrayList<String> Data;
	LayoutInflater factory;
	int row;
	Holder h;
			
	/**
	 *   @author Rajendhiran. E
	 */
	
	public CustomGridMonthAdapter(Context context,ArrayList<String> months_data, int calendarTitle) 
	{
		ctx = context;
		Data = months_data;
		row=calendarTitle;
	}

	public int getCount() 
	{
		return Data.size();
	}
		
	public Object getItem(int arg0) 
	{
		return null;
	}
	
	public long getItemId(int arg0) 
	{
		return 0;
	}
	
	public View getView(int position, View convertView, ViewGroup arg2) 
	{		
		h = new Holder();
		if(convertView==null)
		{
			factory = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = factory.inflate(row,null);
			
			h.CalTitle = (TextView) convertView.findViewById(R.id.title);				
			convertView.setTag(h);
		}
		else
		{
			h = (Holder) convertView.getTag();
		}		
		h.CalTitle.setText(Data.get(position).toString());
		h.CalTitle.setTextColor(Color.parseColor("#000000"));
	//	Typeface tface = Typeface.createFromAsset(convertView.getResources().getAssets(),"Organic_Elements.TTF");
		//h.CalTitle.setTypeface(tface);
		return convertView;		
	}	
	public class Holder
	{
		TextView CalTitle;
	}
}