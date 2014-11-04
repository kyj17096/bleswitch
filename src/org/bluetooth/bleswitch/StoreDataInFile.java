package org.bluetooth.bleswitch;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class StoreDataInFile {

	public static void storeData(Context ctx,String key, String value)
	{
		//实例化SharedPreferences对象（第一步）
		SharedPreferences mySharedPreferences= ctx.getSharedPreferences("btSwitch",
				Activity.MODE_PRIVATE);
		//实例化SharedPreferences.Editor对象（第二步）
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		//用putString的方法保存数据
		editor.putString(key, value);
		//提交当前数据
		editor.commit(); 	
	}
	
	public static String readData(Context ctx,String key)
	{
		SharedPreferences sharedPreferences= ctx.getSharedPreferences("btSwitch",
				Activity.MODE_PRIVATE);
		
		return sharedPreferences.getString(key, ""); 
		
	}
}
