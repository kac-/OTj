package com.kactech.otj.andro;

import com.example.android.skeletonapp.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.EditText;

public class Info extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		OTjApplication app = (OTjApplication) getApplication();
		((EditText) findViewById(R.id.info_server)).setText(app.getServer());
		((EditText) findViewById(R.id.info_nym)).setText(app.getNym());
		((EditText) findViewById(R.id.info_asset)).setText(app.getAsset());
		((EditText) findViewById(R.id.info_account)).setText(app.getAccount());
	}

}
