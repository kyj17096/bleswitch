package org.bluetooth.bleswitch;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AutherInfo extends Activity{
	Button backToMain;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.autoinfo);
		backToMain = (Button)findViewById(R.id.back_to_switch_list);
		backToMain.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AutherInfo.this.finish();
			}
		});
	}
}
