package org.bluetooth.bleswitch;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class SetSwitchTimer extends Activity implements OnClickListener{
	Button btOnTime,btOffTime, btTimerOnOffEnable;
	TextView tvBack,btSave,tvTimeValue,tvChoose,tvSetSleepTimer,tvDelayOnOff;
	int shooseVaue;
	CheckBox[] cb;
	LinearLayout btBack;
	
	boolean enableLightOnTimer = false;
	boolean enableLightOffTimer = false;
//	int setSleepTimerOnTime = 0;
//	int setSleepTimerOffTime = 0;	
//	int lastSetSleepTimerOnTime = 0;
//	int lastSetSleepTimerOffTime = 0;
	int setDelayTime = 0;
	int lightOnHour = 0;
	int lightOnMinutes = 0;
	int lightOffHour = 0;
	int lightOffMinutes = 0;	
	private SeekBar setSleepTimer;
	boolean lightOnOffSetChoose = true;
	byte weekBit;
	View v1,v2,v3;
	public static Handler mHandler;
	int switchIndex = -1;
	byte switchOnOffStatus = -1;
	public final static int GET_TIMER_RESPONSE = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_timer);
		switchIndex = getIntent().getExtras().getInt("switch_index");
		switchOnOffStatus = getIntent().getExtras().getByte("swtich_onoff_status");
		 btBack = (LinearLayout)findViewById(R.id.back_back);
		 btBack.setOnClickListener(this);
		 btSave = (TextView)findViewById(R.id.save_timer_setting);
		 btSave.setOnClickListener(this);
		
		 btOnTime = (Button)findViewById(R.id.switch_on_timer);
		 btOnTime.setOnClickListener(this);
		 btOffTime = (Button)findViewById(R.id.switch_off_timer);
		 btOffTime.setOnClickListener(this);
		//tvBack = (TextView)findViewById(R.id.tv_back_to_switch_list);
		 setSleepTimer=(SeekBar)findViewById(R.id.sb_set_sleep_timer);
		 //tvBack.setOnClickListener(this);
		 tvTimeValue = (TextView)findViewById(R.id.switch_timer_time);
		 btTimerOnOffEnable= (Button)findViewById(R.id.enable_light_on_off);
		 btTimerOnOffEnable.setOnClickListener(this);
		 tvChoose = (TextView)findViewById(R.id.switch_timer_status);
		 tvSetSleepTimer = (TextView)findViewById(R.id.setting_sleep_timer_value);
		 tvDelayOnOff =(TextView)findViewById(R.id.tv_delay_on_off); 
		 setSleepTimer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

	            @Override
	            public void onStopTrackingTouch(SeekBar seekBar) {
	            }

	            @Override
	            public void onStartTrackingTouch(SeekBar seekBar) {
	            	
	            }

	            @Override
	            public void onProgressChanged(SeekBar seekBar, int progress,
	                    boolean fromUser) {
//	            	if(lightOnOffSetChoose == true)
//	            		setSleepTimerOnTime = progress;
//	            	else
//	            		setSleepTimerOffTime = progress;
	            	setDelayTime = progress;
	            	tvSetSleepTimer.setText(""+progress);
	        
	            
	            }
	        });
		 setSleepTimer.setMax(59);
		 //setSleepTimer.setProgress(lastSetSleepTimerOnTime);
		 Log.v("setDelayTime="+setDelayTime,"mike setDelayTime");

		 v1 = (View)findViewById(R.id.timer_v1);
		 v2 = (View)findViewById(R.id.timer_v2);
		 v3 = (View)findViewById(R.id.timer_v3);

		 
		 
		
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
				weekBit = d[6];
				switchIndex = 0x0ff&d[5];
				lastOnH = lightOnHour = 0x0ff&d[7];
				lastOnM = lightOnMinutes = 0x0ff&d[8];
				lastOffH =lightOffHour = 0x0ff&d[9];				
				lastOffM = lightOffMinutes = 0x0ff&d[10];
				if(switchOnOffStatus == 0)
				{
					setDelayTime = 0x0ff&d[12];
				}
				else
				{
					setDelayTime = 0x0ff&d[11];
				}
				
//				lastSetSleepTimerOffTime = setSleepTimerOffTime = 0x0ff&d[11];
//				lastSetSleepTimerOnTime = setSleepTimerOnTime = 0x0ff&d[12];
				Log.v("lastOnH ="+lastOnH+ " lastOnM="+lastOnM+" lastOffH="+
				lastOffH+" lastOffM="+lastOffM,"mike");
				 setSleepTimer.setProgress(setDelayTime);
				 
				 tvSetSleepTimer.setText(""+setDelayTime);
				enableGlobalTimer();									
				//setSleepTimer.setProgress(setDelayTime);
				
				lightOnOffSetChoose = true;
				if(enableLightOnTimer == true)
				{
					
					
						wheelH.setCurrentItem(lastOnH);
						wheelM.setCurrentItem(lastOnM);		
						
						
						tvTimeValue.setText(String.format("%02d",lightOnHour)+":"+String.format("%02d",lightOnMinutes));
						btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_on);
					
						
					
				}
				else if( enableLightOnTimer == false)
				{
					
					wheelH.setCurrentItem(0);
					wheelM.setCurrentItem(0);	
			
					tvTimeValue.setText(String.format("%02d",0)+":"+String.format("%02d",0));
					btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_off);
					
					
				}
								
				break;
			}
		}};
		RoomSwitchList.status = RoomSwitchList.REQUEST_TIMER_SETTING;

		SwitchUtils.requestTimerSetting(RoomSwitchList.mBleWrapper,RoomSwitchList.currentSetCharacteristic,RoomSwitchList.currentPassword ,switchIndex);
	}
	
	
	public void disableGlobalTimer()
	{

		v1.setVisibility(View.GONE);
		v2.setVisibility(View.GONE);
		v3.setVisibility(View.GONE);
	}
	public void enableGlobalTimer()
	{

		v1.setVisibility(View.VISIBLE);
		v2.setVisibility(View.VISIBLE);
		v3.setVisibility(View.VISIBLE);
		if(switchOnOffStatus == 0)
		{
			String s = SetSwitchTimer.this.getResources().getString(R.string.delay_on);
			tvDelayOnOff.setText(s);
		}
		else
		{
			String s = SetSwitchTimer.this.getResources().getString(R.string.delay_off);
			tvDelayOnOff.setText(s);
		}
		
		
		if((0x0ff&lastOnH) == 0x0ff)
		{
			enableLightOnTimer = false;
			btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_off);
		}
		else
		{
			enableLightOnTimer = true;
			hours = lastOnH;
			mintus = lastOnM;
			btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_on);
			tvTimeValue.setText(String.format("%02d",hours)+":"+String.format("%02d",mintus));

			//updateStatus();
		}
		
		if((0x0ff&lastOffH) == 0x0ff)
		{
			enableLightOffTimer = false;
		}
		else
		{
//			hours = lastOffH;
//			mintus = lastOffM;
			enableLightOffTimer = true;
			//btOffTime.setBackgroundColor(Color.YELLOW);
			//btOnTime.setBackgroundColor(Color.GRAY);
			//updateStatus();
		}
		for(int i =0;i<7;i++)
		{
			if((weekBit&(0x01<<i)) != 0)
			{
				cb[i].setChecked(true);
			}
		}
		btOnTime.setBackgroundColor(Color.YELLOW);
		btOffTime.setBackgroundColor(Color.GRAY);
		lightOnOffSetChoose = true;
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
		   if(lightOnOffSetChoose == true)
		   {
			   lightOnHour = hours;
			   lightOnMinutes = mintus;
			   Log.v("lightOnHour = "+lightOnHour+"update      lightOnMinutes="+lightOnMinutes,"mike");
		   }
		   else
		   {
			   lightOffHour = hours;
			   lightOffMinutes = mintus;
			   Log.v("lightOffHour = "+lightOffHour+"update     lightOffMinutes="+lightOffMinutes,"mike");
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
			case R.id.back_back:
				SetSwitchTimer.this.finish();
				break;
			case R.id.save_timer_setting:
				saveWeekBit();
				if(lightOnOffSetChoose)
				{
					lastOnH = lightOnHour;
	                lastOnM = lightOnMinutes;
	               // lastSetSleepTimerOnTime = setSleepTimerOnTime;
				}
				else
				{
	                lastOffH = lightOffHour;
	                lastOffM = lightOffMinutes;
	                //lastSetSleepTimerOffTime = setSleepTimerOffTime;
				}
	
				Intent mIntent = new Intent(); 

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
		        if(switchOnOffStatus == 0)
				{
					mIntent.putExtra("lastSetSleepTimerOnTime", setDelayTime); 
					mIntent.putExtra("lastSetSleepTimerOffTime", 0); 
				} 
		        else
		        {
					mIntent.putExtra("lastSetSleepTimerOnTime", 0); 
					mIntent.putExtra("lastSetSleepTimerOffTime", setDelayTime); 
		        }
		        
		       
		        // 设置结果，并进行传送  
		        this.setResult(0, mIntent); 
		        this.finish();
				
				break;
			case R.id.switch_on_timer:
				btOnTime.setBackgroundColor(Color.YELLOW);
				btOffTime.setBackgroundColor(Color.GRAY);
				lightOnOffSetChoose = true;
                lastOffH = lightOffHour;
                lastOffM = lightOffMinutes;
                lastOnH = lightOnHour;
                lastOnM = lightOnMinutes;
                //lastSetSleepTimerOffTime = setSleepTimerOffTime;
				wheelH.setCurrentItem(lastOnH);
				wheelM.setCurrentItem(lastOnM);
				//setSleepTimer.setProgress(lastSetSleepTimerOnTime);
				//tvSetSleepTimer.setText(""+lastSetSleepTimerOnTime);
				String status =  SetSwitchTimer.this.getResources().getString(R.string.timer_light_on_setting);
				tvChoose.setText(status);
				   Log.v("lastOffH = "+lastOffH+"lastOffM="+lastOffM,"mike");
				   
				   if(enableLightOnTimer == true)
					   btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_on);
				   else
					   btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_off);
				break;
				
			case R.id.switch_off_timer:
				btOffTime.setBackgroundColor(Color.YELLOW);
				btOnTime.setBackgroundColor(Color.GRAY);
				   lastOnH = lightOnHour;
	                lastOnM = lightOnMinutes;
	                lastOffH = lightOffHour;
	                lastOffM = lightOffMinutes;
	                //lastSetSleepTimerOnTime = setSleepTimerOnTime;
	                lightOnOffSetChoose = false;
				wheelH.setCurrentItem(lastOffH);
				wheelM.setCurrentItem(lastOffM);
				//setSleepTimer.setProgress(lastSetSleepTimerOffTime);
				//tvSetSleepTimer.setText(""+lastSetSleepTimerOffTime);
				status =  SetSwitchTimer.this.getResources().getString(R.string.timer_light_off_setting);
				tvChoose.setText(status);
				Log.v("lastOnH = "+lastOnH+"lastOnM="+lastOnM,"mike");

				break;
			
			case R.id.enable_light_on_off:
				Log.v("enable on off","mike ");
				if(lightOnOffSetChoose)
				{
					
					if(enableLightOnTimer == false)
					{
						Log.v("enable on off 1","mike ");
						wheelH.setCurrentItem(lastOnH);
						wheelM.setCurrentItem(lastOnM);	
						//setSleepTimer.setProgress(lastSetSleepTimerOnTime);
						//tvSetSleepTimer.setText(""+lastSetSleepTimerOnTime);
						if(lastOnH == 0xff)
						{
							lastOnH = 0;
							lastOnM = 0;
						}
						tvTimeValue.setText(String.format("%02d",lastOnH)+":"+String.format("%02d",lastOnM));
						btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_on);
						enableLightOnTimer = true;
					}
					else
					{
						Log.v("enable on off 2","mike ");
						wheelH.setCurrentItem(0);
						wheelM.setCurrentItem(0);			
						//setSleepTimer.setProgress(0);
						//tvSetSleepTimer.setText(""+0);
						tvTimeValue.setText(String.format("%02d",0)+":"+String.format("%02d",0));
						btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_off);
						enableLightOnTimer = false;
					}					
					
				}
					
				else
				{
					if(enableLightOffTimer == false)
					{
						Log.v("enable on off 3","mike ");
						wheelH.setCurrentItem(lastOffH);
						wheelM.setCurrentItem(lastOffM);
						//setSleepTimer.setProgress(lastSetSleepTimerOffTime);
						//tvSetSleepTimer.setText(""+lastSetSleepTimerOffTime);
						if(lastOffH == 0xff)
						{
							lastOffH = 0;
							lastOffM = 0;
						}
						tvTimeValue.setText(String.format("%02d",lastOffH)+":"+String.format("%02d",lastOffM));
						btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_on);
						enableLightOffTimer = true;
					}
					else
					{
						Log.v("enable on off 4","mike ");
						wheelH.setCurrentItem(0);
						wheelM.setCurrentItem(0);
						//setSleepTimer.setProgress(0);
						//tvSetSleepTimer.setText(""+0);
						tvTimeValue.setText(String.format("%02d",0)+":"+String.format("%02d",0));
						btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_off);
						enableLightOffTimer = false;
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
