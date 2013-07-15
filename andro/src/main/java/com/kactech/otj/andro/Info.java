package com.kactech.otj.andro;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.skeletonapp.R;

public class Info extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		final OTjApplication app = (OTjApplication) getApplication();
		((EditText) findViewById(R.id.info_server)).setText(app.getServer());
		((EditText) findViewById(R.id.info_nym)).setText(app.getNym());
		((EditText) findViewById(R.id.info_asset)).setText(app.getAsset());
		((EditText) findViewById(R.id.info_account)).setText(app.getAccount());
		((Button) findViewById(R.id.info_acc_qr)).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String url = "http://qrfree.kaywa.com/?l=1&s=12&d=" + app.getAccount();
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
	}

}
