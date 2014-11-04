package org.bluetooth.bleswitch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class SwitchListActivity extends Activity {
	 MyAdapter adapter;
	 ListView lv;
	 List<Map<String, Object>> switchList = new ArrayList<Map<String, Object>>();  
	 TextView searchDevice;
	 Button btAutherInfo;
	private static final long SCANNING_TIMEOUT = 5 * 1000; /* 5 seconds */
	private static final int ENABLE_BT_REQUEST_ID = 1;
	private ArrayList<BluetoothDevice> mDevices;
	private boolean mScanning = false;
	private Handler mHandler = new Handler();
	private BleWrapper mBleWrapper = null;
   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.switch_list);       
       mDevices  = new ArrayList<BluetoothDevice>();
       lv = (ListView)findViewById(R.id.btdevice_list_view);
       btAutherInfo = (Button)findViewById(R.id.info);
       btAutherInfo.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(SwitchListActivity.this,AutherInfo.class);
				SwitchListActivity.this.startActivity(i);
			}
       });
       searchDevice = (TextView)findViewById(R.id.search_switch);
       searchDevice.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
		  if(mScanning == false)
		  {
			  mScanning = true;
		
			// remember to add timeout for scanning to not run it forever and drain the battery
			  addScanningTimeout();    	
			  mBleWrapper.startScanning();
		  }
		}
		});
		mBleWrapper = new BleWrapper(this, new BleWrapperUiCallbacks.Null() {
        	@Override
        	public void uiDeviceFound(final BluetoothDevice device, final int rssi, final byte[] record) {
        		handleFoundDevice(device, rssi, record);
        		Log.v(" "+device.getName(),"mike");
        	}
        });
        
        // check if we have BT and BLE on board
        if(mBleWrapper.checkBleHardwareAvailable() == false) {
        	bleMissing();
        }
		// ListView 中某项被选中
		/*lv.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> a, View v,
					int position, long id) {
				// TODO Auto-generated method stub
				  Log.v("Select","ListItemSelected:"+ a.toString()+"查看："
		            + v.toString()+"位置:"+"ID："+ id);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			
			}
		});*/
       lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Log.v("item click", "mike");
				Intent i = new Intent(SwitchListActivity.this,RoomSwitchList.class);
				i.putExtra("btdevice_address", mDevices.get(position).getAddress());
				SwitchListActivity.this.startActivity(i);
			}
       	
       });
   }
   
   @Override
   protected void onResume() {
   	super.onResume();
   	
   	// on every Resume check if BT is enabled (user could turn it off while app was in background etc.)
   	if(mBleWrapper.isBtEnabled() == false) {
			// BT is not turned on - ask user to make it enabled
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
		    // see onActivityResult to check what is the status of our request
		}
   	
   	// initialize BleWrapper object
   	mBleWrapper.initialize();
   	adapter = new MyAdapter(this); 
   	lv.setAdapter(adapter);
   	
       // Automatically start scanning for devices
   	mScanning = true;
		// remember to add timeout for scanning to not run it forever and drain the battery
	addScanningTimeout();    	
	mBleWrapper.startScanning();
		
   }
   
   @Override
   protected void onPause() {
   	super.onPause();
   	mScanning = false;    	
   	mBleWrapper.stopScanning();
   	
   }
 
   
	public final class ViewHolder{        
		public ImageView onOffSwitch;        
		public TextView roomName; 
		public ImageView arraw_desc;
		
	  }                 
	public class MyAdapter extends BaseAdapter{    
		private LayoutInflater mInflater;      
		ViewHolder holder = null; 
		
		public MyAdapter(Context context){    
			this.mInflater = LayoutInflater.from(context);  			
			}       
		
		public int getCount() {   
			// TODO Auto-generated method stub        
			return mDevices.size();      
			}          
		public Object getItem(int arg0) {       
			// TODO Auto-generated method stub        
			return null;        
			}        
		public long getItemId(int arg0) { 
			// TODO Auto-generated method stub     
			return 0;       
			}     
		public BluetoothDevice getDevice(int index) {
			return mDevices.get(index);
		}
		public View getView(int position, View convertView, ViewGroup parent) {  
			   
			if (convertView == null) {        
				holder=new ViewHolder();   
				convertView = mInflater.inflate(R.layout.switch_list_item, null);  
				convertView.setTag(position);
				holder.onOffSwitch = (ImageView)convertView.findViewById(R.id.turn_on_off_roomlight);  
				//holder.onOffSwitch.setTag(position);
				holder.roomName = (TextView)convertView.findViewById(R.id.room_name);		
				holder.roomName.setTag(position);
				holder.arraw_desc = (ImageView) convertView.findViewById(R.id.arraw_description);  
				convertView.setTag(holder);   
				}else {                  
					holder = (ViewHolder)convertView.getTag();  
					}                                       
			//holder.onOffSwitch.setImageResource((Integer)mData.get(position).get("img"));      
			holder.roomName.setText((String)switchList.get(position).get("title")); 
			holder.roomName.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//holder.roomName.
					final int pos = (Integer)v.getTag();
					View vv = lv.getChildAt(pos - lv.getFirstVisiblePosition());
					holder.roomName = (TextView)vv.findViewById(R.id.room_name);
					Log.v("edittext click","mike");
					
					
					
					LayoutInflater factory = LayoutInflater.from(SwitchListActivity.this);
					final View changeView = factory.inflate(R.layout.change_device_name, null);
					new AlertDialog.Builder(SwitchListActivity.this)//弹出登陆对话框
					.setTitle("请输入新的名字")
					.setView(changeView)
					.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog,int whichButton){
						//跳转至注册
	
						EditText et = (EditText)changeView.findViewById(R.id.change_name);
	
						String str = et.getText().toString();
						holder.roomName.setText(str);
						switchList.get(pos).put("title", str);
						StoreDataInFile.storeData(SwitchListActivity.this, 
								mDevices.get(pos).getAddress().toString(),str);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {					
							       	adapter.notifyDataSetChanged();		
							}
						});
					}
					})
					.setNegativeButton("取消", null)
					.show();
				}
			});
			//Log.v("R","R:"+(String) mData.get(position).get("rating"));
			//holder.rating.setRating((Float)mData.get(position).get("rating"));
			
			  
			return convertView;      
			}             
	}
	 public void showInfo(){        
		 new AlertDialog.Builder(this)     
		 .setTitle("我的listview")    
		 .setMessage("介绍...")      
		 .setPositiveButton("确定", new DialogInterface.OnClickListener() {     
			 @Override           
			 public void onClick(DialogInterface dialog, int which) {    
				 
			 }         })         .show();               } 
	 
		/* make sure that potential scanning will take no longer
		 * than <SCANNING_TIMEOUT> seconds from now on */
		private void addScanningTimeout() {
			Runnable timeout = new Runnable() {
	            @Override
	            public void run() {
	            	if(mBleWrapper == null) return;
	                mScanning = false;
	                mBleWrapper.stopScanning();	               
	            }
	        };
	        mHandler.postDelayed(timeout, SCANNING_TIMEOUT);
		}    

		/* add device to the current list of devices */
	    private void handleFoundDevice(final BluetoothDevice device,
	            final int rssi,
	            final byte[] scanRecord)
		{
			// adding to the UI have to happen in UI thread
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if((device.getName().compareTo("HNT_Small_Switch")==0 ) && (mDevices.contains(device)==false))
					{
						Map<String, Object> map = new HashMap<String, Object>();    
				       	map.put("switch_img", R.drawable.switch_off_des); 
				       	String s = StoreDataInFile.readData(SwitchListActivity.this, device.getAddress().toString());
				       	if(s.compareTo("")==0)
				       	{
				       		map.put("title", "开关"); 
				       	}
				       	else
				       	{
				       		
				       		map.put("title", s);
				       	}
				       	
				       	map.put("arraw_img",R.drawable.small_arraw_not_selected);
				       	switchList.add(map); 
				       	mDevices.add(device);
				       	adapter.notifyDataSetChanged();
					}
				}
			});
		}	

	    private void btDisabled() {
	    	Toast.makeText(this, "Sorry, BT has to be turned ON for us to work!", Toast.LENGTH_LONG).show();
	        finish();    	
	    }
	    
	    private void bleMissing() {
	    	Toast.makeText(this, "BLE Hardware is required but not available!", Toast.LENGTH_LONG).show();
	        finish();    	
	    }
	    
	    
	    /* check if user agreed to enable BT */
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        // user didn't want to turn on BT
	        if (requestCode == ENABLE_BT_REQUEST_ID) {
	        	if(resultCode == Activity.RESULT_CANCELED) {
			    	btDisabled();
			        return;
			    }
	        }
	        super.onActivityResult(requestCode, resultCode, data);
	    }
}