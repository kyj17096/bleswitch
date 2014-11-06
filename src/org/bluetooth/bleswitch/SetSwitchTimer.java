package org.bluetooth.bleswitch;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class SetSwitchTimer extends Activity implements OnClickListener{
	Button btBack,btEnableTimer,btOnTime,btOffTime, btTimerOnOffEnable;
	TextView tvBack,btSave,tvTimeValue,tvChoose;
	int shooseVaue;
	CheckBox[] cb;
	
	boolean timerGlobalEnable = false;
	boolean enableLightOnTimer = false;
	boolean enableLightOffTimer = false;
	
	int lightOnHour = 0;
	int lightOnMinutes = 0;
	int lightOffHour = 0;
	int lightOffMinutes = 0;	
	
	boolean isLightOnPress = false;
	boolean isLightOffPress = false;
	byte weekBit;
	View v1,v2,v3,v4;
	public static Handler mHandler;
	int switchIndex = -1;
	public final static int GET_TIMER_RESPONSE = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_timer);
		switchIndex = getIntent().getExtras().getInt("switch_index");

		 btBack = (Button)findViewById(R.id.bt_back_to_switch_list);
		 btBack.setOnClickListener(this);
		 btSave = (TextView)findViewById(R.id.save_timer_setting);
		 btSave.setOnClickListener(this);
		 btEnableTimer = (Button)findViewById(R.id.switch_timer_enable);
		 btEnableTimer.setOnClickListener(this);
		 btOnTime = (Button)findViewById(R.id.switch_on_timer);
		 btOnTime.setOnClickListener(this);
		 btOffTime = (Button)findViewById(R.id.switch_off_timer);
		 btOffTime.setOnClickListener(this);
		 tvBack = (TextView)findViewById(R.id.tv_back_to_switch_list);
		 
		 tvBack.setOnClickListener(this);
		 tvTimeValue = (TextView)findViewById(R.id.switch_timer_time);
		 btTimerOnOffEnable= (Button)findViewById(R.id.enable_light_on_off);
		 btTimerOnOffEnable.setOnClickListener(this);
		 tvChoose = (TextView)findViewById(R.id.switch_timer_status);
		 
		 v1 = (View)findViewById(R.id.timer_v1);
		 v2 = (View)findViewById(R.id.timer_v2);
		 v3 = (View)findViewById(R.id.timer_v3);
		 v4 = (View)findViewById(R.id.timer_v4);
		 
		 
		
		 cb = new CheckBox[7];
		 cb[0] = (CheckBox)findViewById(R.id.CheckBox01);
		 cb[1] = (CheckBox)findViewById(R.id.CheckBox02);
		 cb[2] = (CheckBox)findViewById(R.id.CheckBox03);
		 cb[3] = (CheckBox)findViewById(R.id.CheckBox04);
		 cb[4] = (CheckBox)findViewById(R.id.CheckBox05);
		 cb[5] = (CheckBox)findViewById(R.id.CheckBox06);
		 cb[6] = (CheckBox)findViewById(R.id.CheckBox07);
		 initWheelH(SetSwitchTimer.this,wheelH,R.id.hours,0);
		 initWheelM(SetSwitchTimer.this,wheelM,R.id.minutes,0);
		 mHandler = new Handler(){@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what)
			{
			case GET_TIMER_RESPONSE:
				byte[] d = (byte[])msg.obj;
				int len = msg.arg1;
				weekBit = d[6];
				switchIndex = 0x0ff&d[5];
				lastOnH = lightOnHour = 0x0ff&d[8];
				lastOnM = lightOnMinutes = 0x0ff&d[7];
				lastOffH =lightOffHour = 0x0ff&d[10];				
				lastOffM = lightOffMinutes = 0x0ff&d[9];
				if((0x0ff&weekBit) == 0x0ff)
				{
					disableGlobalTimer();
					break;
				}
				else
				{
					enableGlobalTimer();									
				}
				
				
				if(timerGlobalEnable == true && enableLightOnTimer == true)
				{
					
					isLightOnPress = true;
						wheelH.setCurrentItem(lightOnHour);
						wheelM.setCurrentItem(lightOnMinutes);					
						tvTimeValue.setText(String.format("%02d",lightOnHour)+":"+String.format("%02d",lightOnMinutes));
						btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_on);
					
						
					
				}
				else if(timerGlobalEnable == true && enableLightOnTimer == false)
				{
					isLightOnPress = true;
					wheelH.setCurrentItem(0);
					wheelM.setCurrentItem(0);					
					tvTimeValue.setText(String.format("%02d",0)+":"+String.format("%02d",0));
					btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_off);
					
					
				}
								
				break;
			}
		}};
		RoomSwitchList.status = RoomSwitchList.REQUEST_TIMER_SETTING;

		SwitchUtils.requestTimerSetting(RoomSwitchList.mBleWrapper,RoomSwitchList.currentSetCharacteristic,RoomSwitchList.currentPassword);
	}
	
	
	public void disableGlobalTimer()
	{
		timerGlobalEnable = false;
		v1.setVisibility(View.GONE);
		v2.setVisibility(View.GONE);
		v3.setVisibility(View.GONE);
		v4.setVisibility(View.GONE);
		btEnableTimer.setBackgroundResource(R.drawable.switch_off);
	}
	public void enableGlobalTimer()
	{
		timerGlobalEnable = true;
		v1.setVisibility(View.VISIBLE);
		v2.setVisibility(View.VISIBLE);
		v3.setVisibility(View.VISIBLE);
		v4.setVisibility(View.VISIBLE);
		
		if((0x0ff&lastOnH) == 0x0ff)
		{
			enableLightOnTimer = false;
		}
		else
		{
			enableLightOnTimer = true;
			hours = lightOnHour;
			mintus = lightOnMinutes;
			updateStatus();
		}
		
		if((0x0ff&lastOffH) == 0x0ff)
		{
			enableLightOffTimer = false;
		}
		else
		{
			hours = lightOffHour;
			mintus = lightOffMinutes;
			enableLightOffTimer = true;
			updateStatus();
		}
		for(int i =0;i<7;i++)
		{
			if((weekBit&(0x01<<i)) != 0)
			{
				cb[i].setChecked(true);
			}
		}
		btEnableTimer.setBackgroundResource(R.drawable.switch_on);
	}

	boolean wheelScrolled = false;
	int hours = 0;
	 WheelView wheelH;
	 private void initWheelH(Context ctx,WheelView w,int id,int pos) {
	        WheelView wheel = (WheelView) this.findViewById(id);
	        wheelH = wheel;
	        wheel.setViewAdapter(new NumericWheelAdapter(ctx, 0, 23));
	        wheel.setCurrentItem(0);
	       
	        
	        wheel.addChangingListener( new OnWheelChangedListener() {
	        public void onChanged(WheelView wheel, int oldValue, int newValue) {
	        		hours = wheel.getCurrentItem();
	                updateStatus();
	            
	        }
	    });
	        wheel.addScrollingListener( new OnWheelScrollListener() {
		        public void onScrollingStarted(WheelView wheel) {
			            wheelScrolled = true;
			        }
			        public void onScrollingFinished(WheelView wheel) {
			            wheelScrolled = false;
			            hours = wheel.getCurrentItem();
			        	updateStatus();
			        }
			    });
	        
	        
	        wheel.setCyclic(true);
	        wheel.setInterpolator(new AnticipateOvershootInterpolator());
	    }
	 
	 
	 	int mintus = 0;
	 	WheelView wheelM;
	   private void initWheelM(Context ctx,WheelView w,int id,int pos) {
	        WheelView wheel = (WheelView) this.findViewById(id);
	        wheelM = wheel;
	        wheel.setViewAdapter(new NumericWheelAdapter(ctx, 0, 59));
	        wheel.setCurrentItem(0);
	        
	        wheel.addChangingListener( new OnWheelChangedListener() {
	        public void onChanged(WheelView wheel, int oldValue, int newValue) {
	        	mintus = wheel.getCurrentItem();
	            updateStatus();
	            
	        }
	        });
	        wheel.addScrollingListener( new OnWheelScrollListener() {
		        public void onScrollingStarted(WheelView wheel) {
			           wheelScrolled = true;
			        }
			        public void onScrollingFinished(WheelView wheel) {
			            wheelScrolled = false;
			        	
			        	mintus = wheel.getCurrentItem();
			        	updateStatus();
			        }
			    });
	        wheel.setCyclic(true);
	        wheel.setInterpolator(new AnticipateOvershootInterpolator());
	    }
	   	
	    // Wheel scrolled listener
	   // OnWheelScrollListener scrolledListener =;
	    
	   public void updateStatus()
	   {
		   tvTimeValue.setText(String.format("%02d",hours)+":"+String.format("%02d",mintus));
		   if(isLightOnPress == true)
		   {
			   lightOnHour = hours;
			   lightOnMinutes = mintus;
			   Log.v("lightOnHour = "+lightOnHour+"lightOnMinutes="+lightOnMinutes,"mike");
		   }
		   else
		   {
			   lightOffHour = hours;
			   lightOffMinutes = mintus;
			   Log.v("lightOffHour = "+lightOffHour+"lightOffMinutes="+lightOffMinutes,"mike");
		   }
	   }
	    
	   
	   int lastOnH = 0;
	   int lastOnM = 0;
	   int lastOffH = 0;
	   int lastOffM = 0;
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
			case R.id.back_to_switch_list:
			case R.id.tv_back_to_switch_list:
				SetSwitchTimer.this.finish();
				break;
			case R.id.save_timer_setting:
				saveWeekBit();
				if(isLightOnPress)
				{
					lastOnH = lightOnHour;
	                lastOnM = lightOnMinutes;
				}
				else
				{
	                lastOffH = lightOffHour;
	                lastOffM = lightOffMinutes;
				}
				Intent mIntent = new Intent(); 
				if(timerGlobalEnable == false)
					weekBit = (byte) 0xff;
				if(!enableLightOnTimer)
				{
					lastOnH = 0xff;
				}
				if(!enableLightOffTimer)
				{
					lastOffH = 0xff;
				}
				 Log.v("weekbit+"+weekBit+" lighoffh"+lightOffHour+"loffm"+lightOffMinutes
			        		+"lonh"+lightOnHour+"lonm "+lightOnMinutes,"mike");
		        mIntent.putExtra("lightWeekBit", weekBit);  
		        mIntent.putExtra("lightOffHour", lastOffH);  
		        mIntent.putExtra("lightOnHour", lastOnH);  
		        mIntent.putExtra("lightOnMinutes", lastOnM);  
		        mIntent.putExtra("lightOffMinutes", lastOffM); 
		        mIntent.putExtra("switch_index", switchIndex); 
		        
		       
		        // 设置结果，并进行传送  
		        this.setResult(0, mIntent); 
		        this.finish();
				
				break;
			case R.id.switch_on_timer:
				isLightOnPress = true;
                lastOffH = lightOffHour;
                lastOffM = lightOffMinutes;
				wheelH.setCurrentItem(lastOnH);
				wheelM.setCurrentItem(lastOnM);
				isLightOffPress = false;
				String status =  SetSwitchTimer.this.getResources().getString(R.string.timer_light_on_setting);
				tvChoose.setText(status);
				   Log.v("lastOffH = "+lastOffH+"lastOffM="+lastOffM,"mike");
				   
				   if(enableLightOnTimer == true)
					   btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_on);
				   else
					   btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_off);
				break;
				
			case R.id.switch_off_timer:
				   lastOnH = lightOnHour;
	                lastOnM = lightOnMinutes;
				isLightOffPress = true;
				wheelH.setCurrentItem(lastOffH);
				wheelM.setCurrentItem(lastOffM);
				isLightOnPress = false;
				status =  SetSwitchTimer.this.getResources().getString(R.string.timer_light_off_setting);
				tvChoose.setText(status);
				Log.v("lastOnH = "+lastOnH+"lastOnM="+lastOnM,"mike");
				 if(enableLightOffTimer == true)
					   btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_on);
				   else
					   btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_off);
				break;
			case R.id.switch_timer_enable:
				
				if(timerGlobalEnable == true)
				{
					timerGlobalEnable = false;
					disableGlobalTimer();
				}
				else
				{
					timerGlobalEnable = true;
					enableGlobalTimer();
				}
				break;
			case R.id.enable_light_on_off:
				Log.v("enable on off","mike ");
				if(isLightOnPress)
				{
					
					if(enableLightOnTimer == true)
					{
						Log.v("enable on off 1","mike ");
						wheelH.setCurrentItem(lightOnHour);
						wheelM.setCurrentItem(lightOnMinutes);					
						tvTimeValue.setText(String.format("%02d",lightOnHour)+":"+String.format("%02d",lightOnMinutes));
						btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_off);
						enableLightOnTimer = false;
					}
					else
					{
						Log.v("enable on off 2","mike ");
						wheelH.setCurrentItem(0);
						wheelM.setCurrentItem(0);					
						tvTimeValue.setText(String.format("%02d",0)+":"+String.format("%02d",0));
						btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_on);
						enableLightOnTimer = true;
					}					
					
				}
					
				else
				{
					if(enableLightOffTimer == true)
					{
						Log.v("enable on off 3","mike ");
						wheelH.setCurrentItem(lightOffHour);
						wheelM.setCurrentItem(lightOffMinutes);					
						tvTimeValue.setText(String.format("%02d",lightOffHour)+":"+String.format("%02d",lightOffMinutes));
						btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_off);
						enableLightOffTimer = false;
					}
					else
					{
						Log.v("enable on off 4","mike ");
						wheelH.setCurrentItem(0);
						wheelM.setCurrentItem(0);					
						tvTimeValue.setText(String.format("%02d",0)+":"+String.format("%02d",0));
						btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_on);
						enableLightOffTimer = true;
					}				
				}
				break;
			}
		}
	
		

		void saveWeekBit()
		{
			weekBit = 0;
			for(int i = 0;i<7;i++)
			{
				if(cb[i].isChecked())
					weekBit = (byte) (weekBit | (0x01<<i));
			}
		}
}
