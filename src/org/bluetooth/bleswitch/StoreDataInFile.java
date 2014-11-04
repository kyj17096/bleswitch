package org.bluetooth.bleswitch;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class StoreDataInFile {

	public static void storeData(Context ctx,String key, String value)
	{
		//ʵ����SharedPreferences���󣨵�һ����
		SharedPreferences mySharedPreferences= ctx.getSharedPreferences("btSwitch",
				Activity.MODE_PRIVATE);
		//ʵ����SharedPreferences.Editor���󣨵ڶ�����
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		//��putString�ķ�����������
		editor.putString(key, value);
		//�ύ��ǰ����
		editor.commit(); 	
	}
	
	public static String readData(Context ctx,String key)
	{
		SharedPreferences sharedPreferences= ctx.getSharedPreferences("btSwitch",
				Activity.MODE_PRIVATE);
		
		return sharedPreferences.getString(key, ""); 
		
	}
}
