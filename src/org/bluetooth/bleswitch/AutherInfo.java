package org.bluetooth.bleswitch;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class AutherInfo extends Activity{
	LinearLayout backToMain;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.autoinfo);
		backToMain = (LinearLayout)findViewById(R.id.back_back);
		backToMain.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AutherInfo.this.finish();
			}
		});
	}
}
