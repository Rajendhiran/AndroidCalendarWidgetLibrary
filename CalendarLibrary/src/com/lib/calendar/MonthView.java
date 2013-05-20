package com.lib.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import com.lib.adapter.CustomGridMonthAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class MonthView extends TableLayout 
{	
	int day=0,month=0,year=0;
	int SelectedDay=0,SelectedMonth=0,SelectedYear=0;
	boolean Arrow=false;
	boolean ApptDialog=true;
	int value=-1;
	//TextView AppointmentDateTitle,NoAppointment,AppointmentCount;	
	//ClinixAppointment Apt= new ClinixAppointment();	
	public int firstDay=Calendar.SUNDAY;
	public GestureDetector mGestureDetector;
	private TextView btn;
	public ImageView btn1;
	public ViewSwitcher calendarSwitcher;
	private TranslateAnimation animSet1,animSet2;
	private Context context;
	private TableRow tr;	
	private Boolean[] isEvent = new Boolean[32]; 
	private int[] resDaysSun = {R.string.sunday,R.string.monday,R.string.tuesday,R.string.wednesday,
			R.string.thursday,R.string.friday,R.string.saturday};
	private int[] resDaysMon = {R.string.monday,R.string.tuesday,R.string.wednesday,
			R.string.thursday,R.string.friday,R.string.saturday,R.string.sunday};
	private String[] days;

	private int[] monthIds = {R.string.january,R.string.february,R.string.march,R.string.april,R.string.may,R.string.june,
			R.string.july,R.string.august,R.string.september,R.string.october,R.string.november,R.string.december};
	//Typeface tface =Typeface.createFromAsset(getResources().getAssets(), "Organic_Elements.TTF");
	private String[] months = new String[12];

	Calendar cal,prevCal,today;	//today will be used for setting a box around today's date
	//prevCal will be used to display last few dates of previous month in the calendar	
	public MonthView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		init(context);		
	}
	/*public MonthView(Context context)
	{
		super(context);
		init(context);		
	}*/

	private void init(Context contxt)
	{		
		context = contxt; //initializing the context variable

		mGestureDetector = new GestureDetector(context, new MySimpleOnGestureListener(context));		
		Resources res = getResources();
		for(int i=0;i<12;i++)
			months[i] = res.getString(monthIds[i]);

		days = new String[7];
		setStretchAllColumns(true); //stretch all columns so that calendar's width fits the screen
		today = Calendar.getInstance();//get current date and time's instance 
		today.clear(Calendar.HOUR);//remove the hour,minute,second and millisecond from the today variable
		today.clear(Calendar.MINUTE);
		today.clear(Calendar.SECOND);
		today.clear(Calendar.MILLISECOND);
		if(firstDay==Calendar.MONDAY)
			today.setFirstDayOfWeek(Calendar.MONDAY);
		cal = (Calendar) today.clone();//create exact copy as today for dates display purpose.

		//setBackgroundColor(Color.parseColor("#CC9966"));
		//setBackgroundColor(Color.parseColor("#CDAF95"));
		//setBackgroundResource(R.drawable.background);
		// setPadding(2, 2, 2,2);
		DisplayMonth(true);//uses cal and prevCal to display the month
		//DisplayMonthsGrid();
	}

	public Date GetDate()
	{
		Calendar Calc = Calendar.getInstance();		
		Calc.set(SelectedYear, SelectedMonth, SelectedDay);		
		return Calc.getTime();		
	}

	Calendar CalcDia= Calendar.getInstance();
	public Date GetDateDialog()
	{
		//CalcDia = Calendar.getInstance();		
		//CalcDia.set(SelectedYear, SelectedMonth, SelectedDay);		
		return CalcDia.getTime();		
	}	

	public void SetDateDialog(Date date)
	{
		CalcDia.setTime(date);		
		//Calc.set(SelectedYear, SelectedMonth, SelectedDay);						
	}

	public void GoToDate(Date date)
	{
		today.setTime(date);
		today.clear(Calendar.HOUR);//remove the hour,minute,second and millisecond from the today variable
		today.clear(Calendar.MINUTE);
		today.clear(Calendar.SECOND);
		today.clear(Calendar.MILLISECOND);
		if(firstDay==Calendar.MONDAY)
			today.setFirstDayOfWeek(Calendar.MONDAY);
		cal = (Calendar) today.clone();
		DisplayMonth(true);
	}
	private boolean animFlag=false;
	//Change month listener called when the user clicks to show next or prev month.
	private OnClickListener ChangeMonthListener = new OnClickListener(){

		@Override
		public void onClick(View v) 
		{
			Arrow=true;
			ChangeMonth(v,Arrow);			
		}};
		//Main function for displaying the current selected month

		int selected_day=0;
		@SuppressWarnings("deprecation")
		void DisplayMonth(boolean animationEnabled)
		{

			if(animationEnabled)
			{
				animSet1 = new TranslateAnimation(0,getWidth(),1,1);
				animSet1.setDuration(300);

				animSet2 = new TranslateAnimation(0,-getWidth(),1,1);
				animSet2.setDuration(300);
			}
			Resources r = getResources();
			String tempDay;
			for(int i=0;i<7;i++)
			{
				if(firstDay == Calendar.MONDAY)
					tempDay = r.getString(resDaysMon[i]);
				else
					tempDay = r.getString(resDaysSun[i]);
				days[i] = tempDay.substring(0,3);
				//Toast.makeText(getContext(), ""+days[i],Toast.LENGTH_LONG).show();
			}		 

			removeAllViews();//Clears the calendar so that a new month can be displayed, removes all child elements (days,week numbers, day labels)

			int firstDayOfWeek,prevMonthDay,nextMonthDay,week;
			cal.set(Calendar.DAY_OF_MONTH, 1); //Set date = 1st of current month so that we can know in next step which day is the first day of the week. 
			firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)-1; //get which day is on the first date of the month
			if(firstDay==Calendar.MONDAY)
			{
				firstDayOfWeek--;
				if(firstDayOfWeek==-1)
					firstDayOfWeek=6;
			}
			week = cal.get(Calendar.WEEK_OF_YEAR)-1; //get which week is the current week.
			if(firstDayOfWeek==0 && cal.get(Calendar.MONTH)==Calendar.JANUARY) //adjustment for week number when january starts with first day of month as sunday
				week = 1;
			if(week==0)
				week = 52;

			prevCal = (Calendar) cal.clone();	//create a calendar item for the previous month by subtracting 
			prevCal.add(Calendar.MONTH, -1);	//1 from the current month

			//get the number of days in the previous month to display last few days of previous month
			prevMonthDay = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH)-firstDayOfWeek+1;
			nextMonthDay = 1;	//set the next month counter to date 1
			android.widget.TableRow.LayoutParams lp;

			RelativeLayout rl = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.calendar_top, null);

			//create the left arrow button for displaying the previous month
			btn1 = (ImageView) rl.findViewById(R.id.imgLeft);
			btn1.setTag("<");
			btn1.setOnClickListener(ChangeMonthListener);

			btn = (TextView) rl.findViewById(R.id.txtDay);
			btn.setText(months[cal.get(Calendar.MONTH)]);
			//btn.setTypeface(tface);
			//String mon = btn.getText().toString();
			month = cal.get(Calendar.MONTH)+1;
			year = cal.get(Calendar.YEAR);
			//Toast.makeText(getContext(), ""+month+"/"+year,Toast.LENGTH_LONG).show();

			//month =Integer.parseInt(months[cal.get(Calendar.MONTH)].toString());
			//year =Integer.parseInt(months[cal.get(Calendar.YEAR)].toString());

			TextView tx =(TextView) rl.findViewById(R.id.txtYear);
			tx.setText(" - "+cal.get(Calendar.YEAR));
		//	tx.setTypeface(tface);
			//((TextView)rl.findViewById(R.id.txtYear)).setText();

			//create the right arrow button for displaying the next month
			btn1 = (ImageView) rl.findViewById(R.id.imgRight);
			btn1.setTag(">");
			btn1.setOnClickListener(ChangeMonthListener);
			//add the tablerow containing the next and prev views to the calendar
			addView(rl);		

			tr = new TableRow(context); //create a new row to add to the tablelayout
			tr.setWeightSum(0.7f);
			//tr.setBackgroundColor(Color.parseColor("#E9C2A6"));
			lp = new TableRow.LayoutParams();
			lp.weight = 0.1f;
			//Create the day labels on top of the calendar 
			for(int i=0;i<7;i++)
			{
				btn = new TextView(context);
				//btn.setBackgroundResource(R.drawable.calheader);
				//tr.setBackgroundColor(Color.parseColor("#E9C2A6"));
				//tr.setBackgroundColor(Color.parseColor("#CC9966"));
				//tr.setBackgroundColor(Color.parseColor("#CDAF95"));
				tr.setBackgroundResource(R.drawable.rectgrad);
				btn.setPadding(10, 3,10, 3);
				btn.setLayoutParams(lp);
				btn.setTextColor(Color.BLACK);
				btn.setText(days[i]);
				btn.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
				//btn.setTypeface(tface);			
				btn.setGravity(Gravity.CENTER);
				tr.addView(btn);	//add the day label to the tablerow
			}
			if(animationEnabled)
			{
				if(animFlag)
					tr.startAnimation(animSet1);
				else
					tr.startAnimation(animSet2);					
			}
			addView(tr); //add the tablerow to the tablelayout (first row of the calendar)

			tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));		
			//tr.setBackgroundColor(Color.parseColor("#f2f2f2"));
			/*initialize the day counter to 1, it will be used to display the dates of the month*/
			int day=1;
			lp = new TableRow.LayoutParams();

			lp.weight = 0.1f;
			for(int i=0;i<6;i++)
			{
				if(day>cal.getActualMaximum(Calendar.DAY_OF_MONTH))
					break;
				tr = new TableRow(context);
				tr.setWeightSum(0.7f);
				//tr.setBackgroundColor(Color.parseColor("#f2f2f2"));
				//this loop is used to fill out the days in the i-th row in the calendar
				for(int j=0;j<7;j++)
				{
					btn = new TextView(context);
					btn.setLayoutParams(lp);
					btn.setBackgroundResource(R.drawable.rectgrad);
					btn.setGravity(Gravity.CENTER);
					btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
					btn.setTextColor(Color.GRAY);
					if(j<firstDayOfWeek && day==1)  //checks if the first day of the week has arrived or previous month's date should be printed
						//btn.setText(Html.fromHtml(String.valueOf("<b>"+ prevMonthDay++ +"</b>")));
						btn.setText(""+(prevMonthDay++));
					else if(day>cal.getActualMaximum(Calendar.DAY_OF_MONTH)) //checks to see whether to print next month's date
					{
						//btn.setText(Html.fromHtml("<b>"+nextMonthDay+++"</b>"));
						btn.setText(""+(nextMonthDay++));
					}
					else	//day counter is in the current month
					{
						try{
							if(isEvent[day])
								btn.setBackgroundResource(R.drawable.dayinmonth);
							else
								btn.setBackgroundResource(R.drawable.rectgrad);
						}catch(Exception ex)
						{
							btn.setBackgroundResource(R.drawable.rectgrad);
						}
						cal.set(Calendar.DAY_OF_MONTH, day);
						btn.setTag(day); //tag to be used when closing the calendar view
						btn.setOnClickListener(dayClickedListener);	
						if(cal.equals(today))//if the day is today then set different background and text color
						{
							tv = btn;
							btn.setBackgroundResource(R.drawable.current_day);
							btn.setTextColor(Color.BLACK);
							if(SelectedDay==0)
							{
								SelectedDay=day;
								SelectedMonth=month-1;
								SelectedYear=year;
							}
							//DisplayListContent();
						}
						else if(selected_day==day)
						{
							tv = btn;
							btn.setBackgroundResource(R.drawable.selectedgrad);
							btn.setTextColor(Color.WHITE);
							SelectedDay=day;
							SelectedMonth=month-1;
							SelectedYear=year;	
							//DisplayListContent();
						}
						else
							btn.setTextColor(Color.BLACK);						


						//set the text of the day
						///btn.setText(Html.fromHtml("<b>"+String.valueOf(day++)+"</b>"));
						btn.setText(""+(day++));
						if(j==0)
							btn.setTextColor(Color.parseColor("#D73C10"));
						else if(j==6)
							btn.setTextColor(Color.parseColor("#000099"));

						if((day==this.day+1)&&(this.month==cal.get(Calendar.MONTH)+1)&&(this.year==cal.get(Calendar.YEAR)))
							btn.setBackgroundColor(Color.GRAY);
					}
					btn.setPadding(8,8,8,8);	//maintains proper distance between two adjacent days
					btn.setOnTouchListener(new View.OnTouchListener()
					{
						public boolean onTouch(View paramView, MotionEvent paramMotionEvent) 
						{
							boolean x=false;
						   if(mGestureDetector.onTouchEvent(paramMotionEvent))
							x= mGestureDetector.onTouchEvent(paramMotionEvent);						   
						   return x;
						}
					});
					tr.addView(btn);

				}
				if(animationEnabled) 
				{
					if(animFlag)
						tr.startAnimation(animSet1);
					else
						tr.startAnimation(animSet2);
				}
				//this adds a table row for six times for six different rows in the calendar
				addView(tr);			
			}		
			DisplayMonthsGrid();			
		}
		private TextView tv;


		//Called when a day is clicked.
		private OnClickListener dayClickedListener = new OnClickListener(){
			@Override
			public void onClick(View v) 
			{			
				dayClickedListenerMethod(v);
			}
		};	

		public void dayClickedListenerMethod(View v)
		{
			if(tv!=null)
			{
				try{	

					if(isEvent[day])
					{
						tv.setBackgroundResource(R.drawable.dayinmonth);						
					}
					else
						tv.setBackgroundResource(R.drawable.rectgrad);

				}catch(Exception ex)
				{
					tv.setBackgroundResource(R.drawable.rectgrad);
				}
				tv.setPadding(8,8,8,8);				
			}
			if(tv.getText().toString().trim().equals(String.valueOf(today.get(Calendar.DATE))))
			{
				tv.setBackgroundResource(R.drawable.selectedgrad);
			}

			day = Integer.parseInt(v.getTag().toString());			
			selected_day = day;
			tv = (TextView)v;
			//Toast.makeText(getContext(),"Date Selected: "+day+"/"+month+"/"+year, Toast.LENGTH_LONG).show();
			SelectedDay =day;
			SelectedMonth=month-1;
			SelectedYear=year;

			tv.setBackgroundResource(R.drawable.selectedgrad);
			DisplayMonth(false);

			/*save the day,month and year in the public int variables day,month and year
		 so that they can be used when the calendar is closed */

			cal.set(Calendar.DAY_OF_MONTH, day);					
		}
		private class MySimpleOnGestureListener extends SimpleOnGestureListener 
		{
			private final int swipeMinDistance;
			private final int swipeThresholdVelocity;

			public MySimpleOnGestureListener(Context context) {
				final ViewConfiguration viewConfig = ViewConfiguration.get(context);
				swipeMinDistance = viewConfig.getScaledTouchSlop();
				swipeThresholdVelocity = viewConfig.getScaledMinimumFlingVelocity();
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				if (e1.getX() - e2.getX() > swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) 
				{
					//onPreviousMonth();

					//Toast.makeText(context, "Left Data", Toast.LENGTH_LONG).show();
					btn1.setTag("<");
					Arrow=false;
					ChangeMonth(btn1,Arrow);
					return true;
				}
				else if (e2.getX() - e1.getX() > swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) 
				{
					// onNextMonth();
					//Toast.makeText(context, "Right Data", Toast.LENGTH_LONG).show();
					btn1.setTag(">");
					Arrow=false;
					ChangeMonth(btn1,Arrow);
					return true;	            
				}
				return false;  
			}
		}

		int width,height;
		public void ChangeMonth(View v,boolean arrow)
		{
			int value1,value2;
			if(arrow==true)
			{
				value1=-1;
				value2=1;
			}
			else
			{
				value1=1;
				value2=-1;
			}
			ImageView tv = (ImageView)v;
			//cal = (Calendar) today.clone();
			//If previous month is to be displayed subtract one from current month.			
			if(tv.getTag().equals("<"))
			{								
				cal.add(Calendar.MONTH, value1); 
				animFlag = false;
			}
			//If next month is to be displayed add one to the current month
			else
			{
				cal.add(Calendar.MONTH, value2);	
				animFlag = true;
			}
			//selected_day = day;
			DisplayMonth(true);
		}

		@SuppressWarnings("deprecation")
		public void DisplayMonthsGrid()
		{    	       
			gv = new GridView(context);    	
			months_data = new ArrayList<String>();
			MonthsInit();
			gv.setLayoutParams(new GridView.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));        
			gv.setNumColumns(14);            
			//   gv.setBackgroundDrawable(getResources().getDrawable(R.drawable.gridgb));
			gv.setColumnWidth(GridView.AUTO_FIT);
			gv.setPadding(0, 10, 0,10);   

			//gv.setVerticalSpacing(20);
			//gv.setHorizontalSpacing(5);               
			gv.setGravity(Gravity.CENTER);
			MonthAdapter = new CustomGridMonthAdapter(context, months_data,R.layout.calendar_title );
			gv.setAdapter(MonthAdapter);				
			addView(gv);

			gv.setOnItemClickListener(new OnItemClickListener()
			{
				public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) 
				{
					if(position==0)
					{
						cal.add(Calendar.YEAR, -1); 
						animFlag = false;
						DisplayMonth(true);
					}
					else if(position==13)
					{
						cal.add(Calendar.YEAR, 1);	
						animFlag = true;
						DisplayMonth(true);
					}
					else 
					{
						cal.set(Calendar.DAY_OF_MONTH, selected_day);
						cal.set(Calendar.MONTH,position-1);
						cal.set(Calendar.YEAR, year);
						DisplayMonth(true);
					}
					//int x=arg0.getPositionForView(arg1);
					//Toast.makeText(context,"data: "+x,Toast.LENGTH_LONG).show();
				}

			});

			gv.setOnTouchListener(new OnTouchListener()
			{		
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) 
				{
					if(arg1.getAction()==MotionEvent.ACTION_MOVE)
						return true;				
					return false;
				}
			});
		}

		public GridView gv;
		public ArrayList<String> months_data;
		public CustomGridMonthAdapter MonthAdapter;

		public RelativeLayout layout;

		public void MonthsInit()
		{
			months_data.add("<<");
			months_data.add("Jan");
			months_data.add("Feb");
			months_data.add("Mar");
			months_data.add("Apr");
			months_data.add("May");
			months_data.add("Jun");
			months_data.add("Jul");
			months_data.add("Aug");
			months_data.add("Sep");
			months_data.add("Oct"); 
			months_data.add("Nov");
			months_data.add("Dec");
			months_data.add(">>");
		}    

		public String DisplayDate()
		{
			String[] monthName = {"January", "February",
					"March", "April", "May", "June", "July",
					"August", "September", "October", "November",
			"December"};
			String[] Days = {"Sunday","Monday","Tuesday","Wednesday",
					"Thursday","Friday","Saturday"};
			Calendar c = Calendar.getInstance(); 
			c.setTime(GetDate());
			c.clear(Calendar.HOUR);
			c.clear(Calendar.MINUTE);
			c.clear(Calendar.MILLISECOND); 
			c.clear(Calendar.SECOND);
			c.clear(Calendar.HOUR_OF_DAY);
			c.clear(Calendar.ZONE_OFFSET);
			c.clear(Calendar.DST_OFFSET);
			String Date=Days[c.get(Calendar.DAY_OF_WEEK)-1] +", "+c.get(Calendar.DATE)+ monthName[c.get(Calendar.MONTH)]+" "+c.get(Calendar.YEAR);
			return Date;
		}		
}