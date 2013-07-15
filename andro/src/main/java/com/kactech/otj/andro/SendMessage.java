package com.kactech.otj.andro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.skeletonapp.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class SendMessage extends BaseActivity {
	static final String TAG = "send msg";
	EditText nym;
	EditText subject;
	EditText text;
	Button scan;
	Button send;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_message);
		nym = (EditText) findViewById(R.id.send_msg_nym);
		subject = (EditText) findViewById(R.id.send_msg_subject);
		text = (EditText) findViewById(R.id.send_msg_text);
		scan = (Button) findViewById(R.id.send_msg_scan);
		send = (Button) findViewById(R.id.send_msg_send);

		handler = new BaseHandler(this);

		send.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final String sNym = nym.getText().toString().trim();
				if (sNym.length() < 5) {
					Log.w(TAG, "invalid nym");
					toast("invalid nym");
					return;
				}
				String sSubject = subject.getText().toString().trim();
				String sText = text.getText().toString().trim();
				if (sSubject.length() == 0 && sText.length() == 0) {
					Log.w(TAG, "empty message");
					toast("empty message");
					return;
				}
				final String msg = sSubject.length() > 0 ? sText = "Subject: " + sSubject + '\n' + sText : sText;
				work(new Runnable() {

					@Override
					public void run() {
						boolean sent;
						try {
							sent = ((OTjApplication) getApplication()).sendUserMessage(sNym, msg);
						} catch (Exception e) {
							Log.e(TAG, "sending", e);
							msg("send error");
							return;
						}
						msg(sent ? "message sent" : "message NOT sent");
					}
				}, "sending");
			}
		});

		scan.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				IntentIntegrator integrator = new IntentIntegrator(SendMessage.this);
				integrator.initiateScan();
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == IntentIntegrator.REQUEST_CODE) {
				IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
				if (scanResult != null) {
					nym.setText(scanResult.getContents());
					toast("scan done");
				} else
					toast("invalid scan");
			}
		}
	}
}
