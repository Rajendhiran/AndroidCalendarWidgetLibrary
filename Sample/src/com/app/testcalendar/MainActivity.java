package com.app.testcalendar;

import java.util.Date;
import com.lib.calendar.MonthView;
import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;

public class MainActivity extends Activity 
{
	public MonthView mv;
	public TextView tv;
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		process();
	}

	private void process() 
	{
		/*Date d=mv.GetDate();
		tv.setText(d.toString());*/
	}

	private void init() 
	{
		mv = (MonthView) findViewById(R.id.monthView);
	}
}