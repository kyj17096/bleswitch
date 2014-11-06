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
	TextView tvBack,btSave,tvTimeValue;
	int shooseVaue;
	CheckBox[] cb;
	boolean isPressLightOnTimerBt = false;
	boolean isPressLightOffTimerBt = false;
	int lightOnHour = -1;
	int lightOffHour = -1;
	int lightOnMinutes = -1;
	int lightOffMinutes = -1;
	
	int orgLightOnHour = -1;
	int orgLightOffHour = -1;
	int orgLightOnMinutes = -1;
	int orgLightOffMinutes = -1;
	boolean lightOnEnable = false;
	boolean lightOffEnable = false;
	boolean enableTimer = false;
	
	View v1,v2,v3;
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
				int len = msg.arg1;
				lightWeekBit = d[6];
				switchIndex = 0x0ff&d[5];
				orgLightOnHour = 0x0ff&d[8];
				orgLightOnMinutes = 0x0ff&d[7];
				orgLightOffHour = 0x0ff&d[10];				
				orgLightOffMinutes = 0x0ff&d[9];
				
				if((orgLightOnHour > 23&& orgLightOffHour>23)||(orgLightOnMinutes>59&&orgLightOnMinutes>59))
				{
					updateStatus();
					return;
				}
				
				if(orgLightOnHour <24 && orgLightOnMinutes <60)
				{
					lightOnEnable = true;
					//btOnTime.setPressed(true);
					
					 
					 hours = orgLightOnHour;
					 mintus = orgLightOnMinutes;
					 updateStatus();
					 btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_on);
				}
				
				if(orgLightOffHour <24 && orgLightOffMinutes <60)
				{
					lightOffEnable = true;
					//btOffTime.setPressed(true);
					 hours = orgLightOffHour;
					 mintus = orgLightOffMinutes;
					 updateStatus();
					 btTimerOnOffEnable.setBackgroundResource(R.drawable.switch_off);
				}
				wheelH.setCurrentItem(hours);
				wheelM.setCurrentItem(mintus);
				for(int i = 0;i<7;i++)
				{
					if((lightWeekBit & (0x01<<i))>0)
						cb[i].setChecked(true);
				}
				if((lightWeekBit&0x0ff) == 0)
					btEnableTimer.setBackgroundResource(R.drawable.switch_off);
				else
					btEnableTimer.setBackgroundResource(R.drawable.switch_on);
				
				
				break;
			}
		}};
		
		switchIndex = switchIndex+1;
		byte[] requestRoomSwitchQuantity = new byte[]{(byte) 0xFA,(byte) 0xF5,0x07,0x24,
				 0x00,0x00,(byte) 0xA1,(byte)switchIndex,(byte) 0xCC,0x0D};
		int checksum = 0;
		for(int i = 0;i<requestRoomSwitchQuantity.length-4;i++)
		{
			checksum = checksum + requestRoomSwitchQuantity[i+2];
		}
		requestRoomSwitchQuantity[8] = (byte)checksum;
		Log.v("checksum is "+ (0x0ff&checksum),"mike request");
		RoomSwitchList.status = RoomSwitchList.REQUEST_TIMER_SETTING;
		RoomSwitchList.mBleWrapper.writeDataToCharacteristic(RoomSwitchList.currentSetCharacteristic,
				requestRoomSwitchQuantity);
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
	    
	    
	    

	    byte lightWeekBit = 0;
	    void updateStatus()
	    {
	    	tvTimeValue.setText(String.format("%02d",hours)+":"+String.format("%02d",mintus));
	    	if(isPressLightOffTimerBt)
			{
				lightOffHour = hours;
				lightOffMinutes = mintus;
			}

			if(isPressLightOnTimerBt)
			{
				lightOnHour = hours;
				lightOnMinutes = mintus;
			}
			
	    }
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
				
				Intent mIntent = new Intent();  
		        mIntent.putExtra("lightWeekBit", lightWeekBit);  
		        mIntent.putExtra("lightOffHour", lightOffHour);  
		        mIntent.putExtra("lightOnHour", lightOnHour);  
		        mIntent.putExtra("lightOnMinutes", lightOnMinutes);  
		        mIntent.putExtra("lightOffMinutes", lightOffMinutes); 
		        mIntent.putExtra("switch_index", switchIndex); 
		        // 设置结果，并进行传送  
		        this.setResult(0, mIntent); 
		        this.finish();
				
				break;
			case R.id.switch_on_timer:
				//saveOffStatus();
				//saveWeekBit();
				btOnTime.setPressed(true);
				isPressLightOnTimerBt = true;
				wheelH.setCurrentItem(orgLightOnHour);
				wheelM.setCurrentItem(orgLightOnMinutes);
				isPressLightOffTimerBt = false;
				break;
				
			case R.id.switch_off_timer:
				//saveOnStatus();
				//saveWeekBit();
				btOffTime.setPressed(true);
				isPressLightOffTimerBt = true;
				wheelH.setCurrentItem(orgLightOffHour);
				wheelM.setCurrentItem(orgLightOffMinutes);
				isPressLightOnTimerBt = false;
				break;
			case R.id.switch_timer_enable:
				if(enableTimer)
				{
					enableTimer = false;
					btEnableTimer.setBackgroundResource(R.drawable.switch_off);
					v1.setVisibility(View.GONE);
					v2.setVisibility(View.GONE);
					v3.setVisibility(View.GONE);
					
				}
					
				else
				{
					enableTimer = true;
					btEnableTimer.setBackgroundResource(R.drawable.switch_on);
					v1.setVisibility(View.VISIBLE);
					v2.setVisibility(View.VISIBLE);
					v3.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.enable_light_on_off:
				
			}
		}
		

		void saveWeekBit()
		{
			lightWeekBit = 0;
			for(int i = 0;i<7;i++)
			{
				if(cb[i].isChecked())
					lightWeekBit = (byte) (lightWeekBit | (0x01<<i));
			}
		}
}
