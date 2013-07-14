package com.kactech.otj.andro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.skeletonapp.R;

public class NewTransaction extends Activity {
	static final int PICK = 100008;
	static final int PICK_ACCT = 100009;

	EditText amount;
	EditText account;
	ProgressDialog dialog;
	Button pick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_transaction);

		amount = (EditText) findViewById(R.id.new_tx_amount);
		account = (EditText) findViewById(R.id.new_tx_account);
		pick = (Button) findViewById(R.id.new_tx_pick);

		((Button) findViewById(R.id.new_tx_send)).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Long l = null;
				try {
					l = new Long(amount.getText().toString());
				} catch (Exception e) {
					Log.w("amount", e.toString());
				}
				if (l == null || l <= 0) {
					Toast.makeText(getApplicationContext(), "invalid amount", 500).show();
					return;
				}
				final Long amount = l;
				final String acc = account.getText().toString().trim();
				if (acc.length() < 5) {
					Toast.makeText(getApplicationContext(), "invalid account", 500).show();
					return;
				}
				dialog = ProgressDialog.show(NewTransaction.this, "sending", "sending");
				dialog.show();
				new Thread() {
					@Override
					public void run() {
						final String ret = ((OTjApplication) getApplication()).send(acc, amount);
						runOnUiThread(new Runnable() {
							public void run() {
								dialog.dismiss();
								Toast toast = Toast.makeText(NewTransaction.this, ret, 1000);
								toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
								toast.show();
							}
						});

					};
				}.start();
			}
		});
		pick.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TreeSet<String> ts = new TreeSet<String>();
				ts.addAll(((OTjApplication) getApplication()).getAccountDisplayNames());
				ArrayList<String> list = new ArrayList<String>(ts);
				Collections.sort(list);

				Intent intent = new Intent(NewTransaction.this, PickString.class);
				intent.putExtra("values", list);
				intent.putExtra("title", "pick contact");
				startActivityForResult(intent, PICK);
			}
		});

		account.setText("5m0F1N9n68J07kowFAUm3Eto7hnl0R2jnVEhf99LVno");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == PICK) {
				String name = data.getExtras().getString("selected");
				Log.d("newtx", "picked '" + name + "'");
				ArrayList<String> list = new ArrayList<String>(
						((OTjApplication) getApplication()).getAccountIdsForName(name));
				if (list.size() == 1) {
					setAccount(name, list.get(0));
					return;
				}
				Collections.sort(list);
				Intent intent = new Intent(NewTransaction.this, PickString.class);
				intent.putExtra("values", list);
				intent.putExtra("hold", name);
				intent.putExtra("title", name + " accounts");
				startActivityForResult(intent, PICK_ACCT);

			} else if (requestCode == PICK_ACCT) {
				String acct = data.getExtras().getString("selected");
				setAccount(data.getExtras().getString("hold"), acct);
			}
		}
	}

	public void setAccount(String name, String account) {
		this.account.setText(account);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_transaction, menu);
		return true;
	}

}
