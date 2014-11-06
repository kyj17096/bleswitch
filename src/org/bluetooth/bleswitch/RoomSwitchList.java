package org.bluetooth.bleswitch;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.bluetooth.bleswitch.PeripheralActivity.ListType;
import org.bluetooth.bleswitch.RoomListActivity.MyAdapter;
import org.bluetooth.bleswitch.RoomListActivity.ViewHolder;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RoomSwitchList extends Activity implements BleWrapperUiCallbacks {	
	    public static final String EXTRAS_DEVICE_NAME    = "BLE_DEVICE_NAME";
	    public static final String EXTRAS_DEVICE_ADDRESS = "BLE_DEVICE_ADDRESS";
	    public static final String EXTRAS_DEVICE_RSSI    = "BLE_DEVICE_RSSI";
	    final static public UUID SWITCH_SERVICE = UUID.fromString("0000fee9-0000-1000-8000-00805f9b34fb");
	    final static public UUID SWITCH_SET_CHARACTERISTIC = UUID.fromString("d44bc439-abfd-45a2-b575-925416129600");	   
	    final static public UUID SWITCH_NOTIFY_CHARACTERISTIC = UUID.fromString("d44bc439-abfd-45a2-b575-925416129601");	   
	    public static int status = 0;
	    public  final static int REQUEST_SWITCH_COUNT_AND_ONOFF_STATUS = 1;
	    public  final static int SET_NOTIFY_ENABLE = 3;
	    public  final  static int SET_SWITCH_ONOFF = 2;
	    public  final static int REQUEST_TIMER_SETTING = 5;
	    public  final static int CHANGE_PWD = 6;
	    public  final static int SET_PWD= 7;
	    public  static int currentSetSwitch = -1;
	    MyAdapter adapter;
		ListView lv;
		Button backToMain;
		
		byte[] commandCache;
		
		TextView tvBackToMain;
		TextView switchTitle;
		TextView changePassword;
		List<Map<String, Object>> switchList = new ArrayList<Map<String, Object>>();  
	    
	    public enum ListType {
	    	GATT_SERVICES,
	    	GATT_CHARACTERISTICS,
	    	GATT_CHARACTERISTIC_DETAILS
	    }
	    public static BluetoothGattCharacteristic currentSetCharacteristic;
	    BluetoothGattCharacteristic currentNotifyCharacteristic;
	    private ListType mListType = ListType.GATT_SERVICES;
	    private String mDeviceName;
	    private String mDeviceAddress;
	    private String mDeviceRSSI;

	    public static BleWrapper mBleWrapper;
	    
//	    private TextView mDeviceNameView;
//	    private TextView mDeviceAddressView;
//	    private TextView mDeviceRssiView;
//	    private TextView mDeviceStatus;
//	    private ListView mListView;
//	    private View     mListViewHeader;
//	    private TextView mHeaderTitle;
//	    private TextView mHeaderBackButton;
//	    private ServicesListAdapter mServicesListAdapter = null;
//	    private CharacteristicsListAdapter mCharacteristicsListAdapter = null; 
//	    private CharacteristicDetailsAdapter mCharDetailsAdapter = null;  
	    
	    public void uiDeviceConnected(final BluetoothGatt gatt,
				                      final BluetoothDevice device)
	    {
	    	runOnUiThread(new Runnable() {
				@Override
				public void run() {
					//mDeviceStatus.setText("connected");
					//invalidateOptionsMenu();
					//mBleWrapper.getCharacteristicsForService(mBleWrapper.getCachedService());
		
				}
	    	});
	    }
	    
	    public void uiDeviceDisconnected(final BluetoothGatt gatt,
				                         final BluetoothDevice device)
	    {
	    	runOnUiThread(new Runnable() {
				@Override
				public void run() {
//					mDeviceStatus.setText("disconnected");
//					mServicesListAdapter.clearList();
//					mCharacteristicsListAdapter.clearList();
//					mCharDetailsAdapter.clearCharacteristic();
//					
//					invalidateOptionsMenu();
//					
//					mHeaderTitle.setText("");
//					mHeaderBackButton.setVisibility(View.INVISIBLE);
//					mListType = ListType.GATT_SERVICES;
//					mListView.setAdapter(mServicesListAdapter);
				}
	    	});    	
	    }
	    
	    public void uiNewRssiAvailable(final BluetoothGatt gatt,
	    							   final BluetoothDevice device,
	    							   final int rssi)
	    {
	    	runOnUiThread(new Runnable() {
		    	@Override
				public void run() {
					//mDeviceRSSI = rssi + " db";
					//mDeviceRssiView.setText(mDeviceRSSI);
				}
			});    	
	    }
	    
	    public void uiAvailableServices(final BluetoothGatt gatt,
	    						        final BluetoothDevice device,
	    							    final List<BluetoothGattService> services)
	    {
	    	runOnUiThread(new Runnable() {
				@Override
				public void run() {
					
//					mServicesListAdapter.clearList();
//					mListType = ListType.GATT_SERVICES;
//					mListView.setAdapter(mServicesListAdapter);
//					mHeaderTitle.setText(mDeviceName + "\'s services:");
//					mHeaderBackButton.setVisibility(View.INVISIBLE);
//					
	    			for(BluetoothGattService service : mBleWrapper.getCachedServices()) {
	            		//mServicesListAdapter.addService(service);
	    				Log.v("service "+service.getUuid().toString(),"mike");
	    				if(service.getUuid().compareTo(SWITCH_SERVICE)==0)
	    				{
	    					mBleWrapper.getCharacteristicsForService(service);
	    					Log.v("service is "+ service.getUuid().toString(),"mike");
	    					break;
	    				}

	            	}				
//	    			mServicesListAdapter.notifyDataSetChanged();
				}    		
	    	});
	    }
	   
	    public void uiCharacteristicForService(final BluetoothGatt gatt,
	    				 					   final BluetoothDevice device,
	    									   final BluetoothGattService service,
	    									   final List<BluetoothGattCharacteristic> chars)
	    {
	    	runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Log.v("currentSetCharacteristic callback","mike");
//					mCharacteristicsListAdapter.clearList();
//			    	mListType = ListType.GATT_CHARACTERISTICS;
//			    	mListView.setAdapter(mCharacteristicsListAdapter);
//			    	mHeaderTitle.setText(BleNamesResolver.resolveServiceName(service.getUuid().toString().toLowerCase(Locale.getDefault())) + "\'s characteristics:");
//			    	mHeaderBackButton.setVisibility(View.VISIBLE);
//			    	
			    	for(BluetoothGattCharacteristic ch : chars) {
			    		//mCharacteristicsListAdapter.addCharacteristic(ch);
			    		Log.v("ch is "+ ch.getUuid().toString(),"mike");
			    		if(ch.getUuid().compareTo(SWITCH_SET_CHARACTERISTIC)==0)
			    		{
			    			currentSetCharacteristic = ch;
			    			Log.v("currentSetCharacteristic is "+ currentSetCharacteristic.getUuid().toString(),"mike");	    			
			    			
			    		}
			    		if(ch.getUuid().compareTo(SWITCH_NOTIFY_CHARACTERISTIC)==0)
			    		{
			    			currentNotifyCharacteristic = ch;
			    			Log.v("currentNotifyCharacteristic is "+ currentNotifyCharacteristic.getUuid().toString(),"mike");
			    		}
			    		
			    		
			    		
			    	}
			    	status = SET_NOTIFY_ENABLE;
			    	mBleWrapper.setNotificationForCharacteristic(currentNotifyCharacteristic, true);
	    			
//			    	mCharacteristicsListAdapter.notifyDataSetChanged();
				}
	    	});
	    }
	    
	    public void uiCharacteristicsDetails(final BluetoothGatt gatt,
						 					 final BluetoothDevice device,
											 final BluetoothGattService service,
											 final BluetoothGattCharacteristic characteristic)
	    {
	    	runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mListType = ListType.GATT_CHARACTERISTIC_DETAILS;
//					mListView.setAdapter(mCharDetailsAdapter);
//			    	mHeaderTitle.setText(BleNamesResolver.resolveCharacteristicName(characteristic.getUuid().toString().toLowerCase(Locale.getDefault())) + "\'s details:");
//			    	mHeaderBackButton.setVisibility(View.VISIBLE);
//			    	
//			    	mCharDetailsAdapter.setCharacteristic(characteristic);
//			    	mCharDetailsAdapter.notifyDataSetChanged();
				}
	    	});
	    }

	    public void uiNewValueForCharacteristic(final BluetoothGatt gatt,
												final BluetoothDevice device,
												final BluetoothGattService service,
												final BluetoothGattCharacteristic characteristic,
												final String strValue,
												final int intValue,
												final byte[] rawValue,
												final String timestamp)
	    {
	    	Log.v("new value","mike");
	    	String mAsciiValue = "";
    		if (rawValue != null && rawValue.length > 0) {
    	           // final StringBuilder stringBuilder = new StringBuilder(mRawValue.length);
    	            for(byte byteChar : rawValue)
    	                //stringBuilder.append(String.format("%02X", byteChar));
    	            	mAsciiValue = mAsciiValue +"  0x" +(Integer.toHexString(0x0ff&byteChar));// stringBuilder.toString();
    	     }
    	     else 
    	    	 mAsciiValue = "";
    	    Log.v(""+mAsciiValue," mike");
    	    handlerReceiveData(rawValue);
	    	/*if(status == REQUEST_SWITCH_COUNT_AND_ONOFF_STATUS)
	    	{
	    		handlerReceiveData(rawValue);
	    		
	    	}
	    	else if(status == REQUEST_TIMER_SETTING)
	    	{
	    		handlerReceiveData(rawValue);
	    	}*/
//	    	runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					//mCharDetailsAdapter.newValueForCharacteristic(characteristic, strValue, intValue, rawValue, timestamp);
//					//mCharDetailsAdapter.notifyDataSetChanged();
//				}
//	    	});
	    }
	 
		public void uiSuccessfulWrite(final BluetoothGatt gatt,
	            					  final BluetoothDevice device,
	            					  final BluetoothGattService service,
	            					  final BluetoothGattCharacteristic ch,
	            					  final String description)
		{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(status == SET_SWITCH_ONOFF)
					{
						if((Integer)switchList.get(currentSetSwitch).get("switch_onoff_status") == 0)
							switchList.get(currentSetSwitch).put("switch_onoff_status", 255);
						else
							switchList.get(currentSetSwitch).put("switch_onoff_status", 0);
						adapter.notifyDataSetChanged();
					}	
					
					Log.v("write sucessful","mike");
					//Toast.makeText(getApplicationContext(), "Writing to " + description + " was finished successfully!", Toast.LENGTH_LONG).show();
				}
			});
		}
		
		public void uiFailedWrite(final BluetoothGatt gatt,
								  final BluetoothDevice device,
								  final BluetoothGattService service,
								  final BluetoothGattCharacteristic ch,
								  final String description)
		{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "Writing to " + description + " FAILED!", Toast.LENGTH_LONG).show();
				}
			});	
		}

		public void uiSucessfulWriteDescription(final BluetoothGatt gatt,
	            final BluetoothDevice device,
	            final BluetoothGattService service,
	            final BluetoothGattDescriptor des,
	            final String description){
			
			status = REQUEST_SWITCH_COUNT_AND_ONOFF_STATUS;
			SwitchUtils.requestRoomSwitchCount(mBleWrapper,currentSetCharacteristic);
						
		}
		
		public void uiFailedWriteDescription(final BluetoothGatt gatt,
	            final BluetoothDevice device,
	            final BluetoothGattService service,
	            final BluetoothGattDescriptor des,
	            final String description){
			
		}
		public void uiGotNotification(final BluetoothGatt gatt,
									  final BluetoothDevice device,
									  final BluetoothGattService service,
									  final BluetoothGattCharacteristic ch)
		{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// at this moment we only need to send this "signal" do characteristic's details view
					//mCharDetailsAdapter.setNotificationEnabledForService(ch);
				}			
			});
		}

		@Override
		public void uiDeviceFound(BluetoothDevice device, int rssi, byte[] record) {
			// no need to handle that in this Activity (here, we are not scanning)
		}  	
		
	
	    private String inputOldPwd;
	    private String oldPassword;
	    private String inputNewPwd;
	    private String newPassword;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.room_switch_list);
			backToMain = (Button)findViewById(R.id.back_to_switch_list);
			mDeviceAddress = this.getIntent().getExtras().getString("btdevice_address");
			
			backToMain.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					RoomSwitchList.this.finish();
				}
			});
			tvBackToMain = (TextView)findViewById(R.id.tv_back_to_switch_list);
			tvBackToMain.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					RoomSwitchList.this.finish();
				}
			});
			
			String roomTitle = StoreDataInFile.readData(this, mDeviceAddress);
			if(roomTitle == null)
			{
				roomTitle = RoomSwitchList.this.getResources().getString(R.string.room_room);
			}
			
			switchTitle = (TextView)findViewById(R.id.room_title);
			switchTitle.setText(roomTitle);
			changePassword = (TextView)findViewById(R.id.change_password);
			changePassword.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					class rightInput extends CallBackForDialog
					{
						public void func(String str)
						{
							oldPassword = inputOldPwd = str;
							status = CHANGE_PWD;
							SwitchUtils.requestPwd(mBleWrapper, currentSetCharacteristic);
						}
					}
					String title = RoomSwitchList.this.getResources().getString(R.string.input_old_pwd);
					String errorHint = RoomSwitchList.this.getResources().getString(R.string.input_not_enough_length_cahrs);
					SwitchUtils.alertDialogForInput(RoomSwitchList.this,title,errorHint,4,4,new rightInput(),SwitchUtils.INPUT_OLD_PASSWORD);
					
				}
			});
			
			lv = (ListView)findViewById(R.id.room_switch_list);
		   	adapter = new MyAdapter(this); 
		   	lv.setAdapter(adapter);
			
			
			if(mBleWrapper == null) mBleWrapper = new BleWrapper(this, this);
			
			if(mBleWrapper.initialize() == false) {
				finish();
			}
			
	    	mBleWrapper.connect(mDeviceAddress);
		}
		
		@Override
		protected void onResume() {
			super.onResume();
			
		}
		
		@Override
		protected void onPause() {
			super.onPause();
			

		}
		protected void onDestroy() {
			super.onDestroy();
			mBleWrapper.stopMonitoringRssiValue();
			mBleWrapper.diconnect();
			mBleWrapper.close();
		}

		public final class ViewHolder{        
			public ImageView onOffSwitchDesc;        
			public TextView switchName; 
			public Button switchTimer;
			public Button switchToggle;
			
		  }                 
		public class MyAdapter extends BaseAdapter{    
			private LayoutInflater mInflater;      
			ViewHolder holder = null; 
			
			public MyAdapter(Context context){    
				this.mInflater = LayoutInflater.from(context);  			
				}       
			
			public int getCount() {   
				// TODO Auto-generated method stub        
				return switchList.size();      
				}          
			public Object getItem(int arg0) {       
				// TODO Auto-generated method stub        
				return null;        
				}        
			public long getItemId(int arg0) { 
				// TODO Auto-generated method stub     
				return 0;       
				}     
 
			public View getView(int position, View convertView, ViewGroup parent) {  
				   Log.v("get view position "+position,"mike get view");
				if (convertView == null) {        
					holder=new ViewHolder();   
					convertView = mInflater.inflate(R.layout.room_switch_list_item, null);  
					convertView.setTag(position);
					holder.onOffSwitchDesc = (ImageView)convertView.findViewById(R.id.switch_on_off_description);  					
					holder.switchName = (TextView)convertView.findViewById(R.id.switch_name);
					holder.switchName.setTag(position);
					holder.switchTimer = (Button) convertView.findViewById(R.id.settimer);  
					holder.switchTimer.setTag(position);
					holder.switchToggle = (Button) convertView.findViewById(R.id.switc_action);  
					holder.switchToggle.setTag(position);
					convertView.setTag(holder);  
					 
					}else {                  
						holder = (ViewHolder)convertView.getTag();  
						}   
				
				if(switchList.size()>0)
				{
					if((Integer)switchList.get(position).get("switch_onoff_status") >0)
					{
						if(holder.onOffSwitchDesc !=null)
							holder.onOffSwitchDesc.setImageResource(R.drawable.switch_on_des);
						if(holder.switchToggle !=null)
							holder.switchToggle.setBackgroundResource(R.drawable.switch_on);
						
					}
					else
					{
						if(holder.onOffSwitchDesc !=null)
							holder.onOffSwitchDesc.setImageResource(R.drawable.switch_off_des);
						if(holder.switchToggle !=null)
							holder.switchToggle.setBackgroundResource(R.drawable.switch_off);
					}
					holder.switchName = (TextView)convertView.findViewById(R.id.switch_name);
					Log.v("set index "+position+"  "+(String)switchList.get(position).get("title"),"mike");
					if(holder.switchName !=null)
					{
						
						holder.switchName.setText((String)switchList.get(position).get("title"));
					}
					
				}
				
				
				if(holder.switchName !=null)
				{
					
					holder.switchName.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						//holder.roomName.
						final int pos = (Integer)v.getTag();
						View vv = lv.getChildAt(pos - lv.getFirstVisiblePosition());
						holder.switchName = (TextView)vv.findViewById(R.id.room_name);
						
						Log.v("edittext click","mike");
						class rightInput extends CallBackForDialog
						{
							public void func(String str)
							{
								holder.switchName.setText(str);
								switchList.get(pos).put("title", str);
								StoreDataInFile.storeData(RoomSwitchList.this, 
										mDeviceAddress+pos,str);
								runOnUiThread(new Runnable() {
									@Override
									public void run() {					
									       	adapter.notifyDataSetChanged();		
									}
								});
							}
						}
						String title = RoomSwitchList.this.getResources().getString(R.string.change_room_name);
						String errorHint = RoomSwitchList.this.getResources().getString(R.string.input_not_enough_length_cahrs);
						SwitchUtils.alertDialogForInput(RoomSwitchList.this,title,errorHint,5,10,new rightInput(),SwitchUtils.INPUT_ROOM_SWITCH_NAME);
						
					}
				});
				}
				//holder.onOffSwitch.setImageResource((Integer)mData.get(position).get("img"));      
				//holder.title.setText((String)mData.get(position).get("title")); 
				//Log.v("R","R:"+(String) mData.get(position).get("rating"));
				//holder.rating.setRating((Float)mData.get(position).get("rating"));
				if(holder.switchTimer!=null)
				{holder.switchTimer.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						//showInfo();
						int pos = (Integer) arg0.getTag();
						
						Intent i = new Intent(RoomSwitchList.this,SetSwitchTimer.class);  
						i.putExtra("switch_index", pos);
						RoomSwitchList.this.startActivityForResult(i, 1);
//						int position = (Integer) arg0.getTag();
//						Log.v("lv position is "+position,"mike");
//						View v = lv.getChildAt(position - lv.getFirstVisiblePosition());
//						Log.v("v info "+ v.getTag(),"mike");
//						holder.img = (ImageView)v.findViewById(R.id.img);
//						
//						Log.v("holder.img info "+ holder.img.getTag(),"mike");
//						holder.img.setBackgroundColor(Color.WHITE);
//						holder.img.setImageResource(R.drawable.ednijbxw);
//						holder.title = (TextView)v.findViewById(R.id.title); 
//						Log.v("holder.title info "+ holder.title.getTag(),"mike");
//						holder.title.setText("改变了");  
						//notifyDataSetChanged();
					}});
				}
				if(holder.switchToggle!=null)
				{holder.switchToggle.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						//showInfo();
						
						int position = (Integer) arg0.getTag();
						int onoff= -1;
						if((Integer)switchList.get(position).get("switch_onoff_status") == 0)
						{
							onoff = 255;
						}
						else
						{
							onoff = 0;
						}
//						
//						String test = "1234";
//						byte[] testb = SwitchUtils.myStringToBcd(test);
//						Log.v("test+"+testb[0]+" "+testb[1], "mike");
						
						Log.v("set onoff "+ onoff, "mike");

						status = SET_SWITCH_ONOFF;
						
						currentSetSwitch = position;
						SwitchUtils.setOnOff(mBleWrapper, currentSetCharacteristic, (byte)onoff, position);
//						Log.v("lv position is "+position,"mike");
						View v = lv.getChildAt(position - lv.getFirstVisiblePosition());
//						Log.v("v info "+ v.getTag(),"mike");
//						holder.img = (ImageView)v.findViewById(R.id.img);
//						
//						Log.v("holder.img info "+ holder.img.getTag(),"mike");
//						holder.img.setBackgroundColor(Color.WHITE);
//						holder.img.setImageResource(R.drawable.ednijbxw);
//						holder.title = (TextView)v.findViewById(R.id.title); 
//						Log.v("holder.title info "+ holder.title.getTag(),"mike");
//						holder.title.setText("改变了");  
						//notifyDataSetChanged();
					}});
				}
				return convertView;      
				}             
		}
  
		
		    byte[] byteBuffer = new byte[255];
		    int bufferIndex = 0;
		    int length = 0;
		    void handlerReceiveData(byte[] d)
		    {
		    	Log.v("receivd raw data","mike");
		    	System.arraycopy(d, 0, byteBuffer, bufferIndex, d.length);
		    	bufferIndex = bufferIndex+d.length;
		    	if(bufferIndex >=4)
		    	{
		    		if(byteBuffer[0] == (byte) 0xfa&&byteBuffer[1] == (byte) 0xf5)
		    		length = byteBuffer[2];
		    		Log.v("length is " + length, "mike");
		    	}
		    	if(bufferIndex >= length+3)
		    	{
		    		int sum = 0;
		    		for(int i = 0;i<length-1;i++)
		    		{
		    			sum = sum+byteBuffer[i+2];
		    			
		    		}
		    		Log.v("sum is " + (0x0ff&sum)+" check sum = "+(0x0ff&byteBuffer[length+1]), "mike");
		    		if((0x0ff&sum)== (0x0ff&byteBuffer[length+1]))
		    		{	
		    			dealData(byteBuffer, bufferIndex);
		    			bufferIndex = 0;
		    		}
		    		else 
		    			bufferIndex = 0;
		    	}
		    	
		    }
		    void dealData(byte[] b, int len)
		    {
		    	Log.v("deal data is " + (0x0ff&b[3]), "mike");
		    	if((0x0ff&b[4]) != 0)
		    	{
		    		 SwitchUtils.wrongPassword(RoomSwitchList.this, REQUEST_SWITCH_COUNT_AND_ONOFF_STATUS,
		    		    		mBleWrapper,currentSetCharacteristic);
		    	}
		    	switch(0x0ff&b[3])
		    	{
		    	case 0xA0:
		    		int num = 0x0ff&b[5];
		    		switchList.clear();
		    		for(int i =0;i< num;i++)
		    		{
		    			Log.v("num is "+num,"mike");
			    		Map<String, Object> map = new HashMap<String, Object>();    
				       	map.put("switch_img", R.drawable.switch_off_des); 
				       	
				       	String s = StoreDataInFile.readData(RoomSwitchList.this, mDeviceAddress+i);
				       	if(s.compareTo("")==0)
				       	{
				       		map.put("title", "开关"); 
				       	}
				       	else
				       	{
				       		
				       		map.put("title", s);
				       	} 
				       	map.put("timer_button",R.drawable.switch_timer_selected);
				       	map.put("toggle_switch",R.drawable.switch_off);
				       	map.put("switch_onoff_status", (0x0ff&b[6+i]));
				       	switchList.add(map); 
				       	    	
		    		}
		    		Log.v("switchList.size "+switchList.size(),"mike");
		    		runOnUiThread(new Runnable() {
						@Override
						public void run() {
							adapter.notifyDataSetChanged();
						}
			    	});
		    		
		    		break;
		    		
		    	case 0xA1:
		    		if(SetSwitchTimer.mHandler!=null)
		    			SetSwitchTimer.mHandler.obtainMessage(SetSwitchTimer.GET_TIMER_RESPONSE,
		    					len,0,b).sendToTarget();
		    		break;
		    	case 0xA3:
		    		final byte h = b[5];
		    		final byte l = b[6];
		    		String strpwd = SwitchUtils.myBcdToString(new byte[]{h,l},2);
		    		Log.v("old pwd is "+ strpwd+"inputOldPwd "+inputOldPwd,"mike");
		    		
		    		if(strpwd.compareTo(inputOldPwd)== 0 && status == CHANGE_PWD)
		    		{
		    			Log.v("another popup","mike");
		    		 	class rightInput extends CallBackForDialog
		    			{
		    				public void func(String str)
		    				{
		    					
		    					status = SET_PWD;
		    					byte[] newbcd = SwitchUtils.myStringToBcd(str);
		    					SwitchUtils.setNewPwd(mBleWrapper, currentSetCharacteristic, 
		    							h, l, newbcd[0], newbcd[1]);
		    				}
		    			}
		    			String title = RoomSwitchList.this.getResources().getString(R.string.input_new_pwd);
		    			String errorHint = RoomSwitchList.this.getString(R.string.input_not_enough_length_cahrs);
		    			SwitchUtils.alertDialogForInput(RoomSwitchList.this,title,errorHint,4,4,new rightInput(),SwitchUtils.INPUT_NEW_PASSWORD);
		    			
		    			
		    		}
		    		else
		    		{
		    			class rightInput extends CallBackForDialog
						{
							public void func(String str)
							{
								oldPassword = inputOldPwd = str;
								status = CHANGE_PWD;
								SwitchUtils.requestPwd(mBleWrapper, currentSetCharacteristic);
							}
						}
						String title = RoomSwitchList.this.getResources().getString(R.string.input_old_pwd);
						String errorHint = RoomSwitchList.this.getResources().getString(R.string.input_not_enough_length_cahrs);
						SwitchUtils.alertDialogForInput(RoomSwitchList.this,title,errorHint,4,4,new rightInput(),SwitchUtils.INPUT_OLD_PASSWORD);
		    		}
		    		
		    	}
		    }
		    

		    @Override  
		    protected void onActivityResult(int requestCode, int resultCode, Intent data)  
		    {  
		        //可以根据多个请求代码来作相应的操作  
		        switch (requestCode) {  
		        case 1:  
		            Log.v("onActivityResult","mike");
		            if(data == null)
		            	return;
		            byte lightWeekBit = data.getByteExtra("lightWeekBit", (byte) 0);
		            int lightOffHour = data.getIntExtra("lightOffHour", (byte) 0);
		            int lightOnHour = data.getIntExtra("lightOnHour", (byte) 0);
		            int lightOnMinutes = data.getIntExtra("lightOnMinutes", (byte) 0);
		            int lightOffMinutes = data.getIntExtra("lightOffMinutes", (byte) 0);
		            int switchIndex = data.getIntExtra("switch_index", (byte) 0);
		            
		            Log.v("set tiem "+lightOnHour+" "+lightOnMinutes+" "+lightOffHour+" "+lightOffMinutes,"mike");
		            byte[] setSwitchTimer = new byte[]{(byte) 0xFA,(byte) 0xF5,0x0B,0x22,0x00,0x00,
		            		(byte) switchIndex,lightWeekBit,(byte) lightOnMinutes,
		            		(byte) lightOnHour,(byte) lightOffMinutes,(byte) lightOffHour,0x00,	0x0d};
		            int checksum = 0;
		    		for(int i = 0;i<setSwitchTimer.length-4;i++)
		    		{
		    			checksum = checksum + setSwitchTimer[i+2];
		    		}
		    		setSwitchTimer[12] = (byte)checksum;
		    		Log.v("checksum is "+ (0x0ff&checksum),"mike request");
		    		RoomSwitchList.status = RoomSwitchList.REQUEST_TIMER_SETTING;
		    		RoomSwitchList.mBleWrapper.writeDataToCharacteristic(RoomSwitchList.currentSetCharacteristic,
		    				setSwitchTimer);
		            break;    
		        default:  
		            break;  
		        }  
		        super.onActivityResult(requestCode, resultCode, data);  
		    } 
		    
		    

	}
