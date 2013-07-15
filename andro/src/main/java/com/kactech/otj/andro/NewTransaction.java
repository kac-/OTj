package com.kactech.otj.andro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.skeletonapp.R;

public class NewTransaction extends BaseActivity {
	static final String TAG = "new tx";
	static final int PICK = 100008;
	static final int PICK_ACCT = 100009;

	EditText amount;
	EditText account;
	Button pick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_transaction);

		amount = (EditText) findViewById(R.id.new_tx_amount);
		account = (EditText) findViewById(R.id.new_tx_account);
		pick = (Button) findViewById(R.id.new_tx_pick);

		handler = new BaseHandler(this);

		((Button) findViewById(R.id.new_tx_send)).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Long l = null;
				try {
					l = new Long(amount.getText().toString());
				} catch (Exception e) {
					Log.w(TAG, e.toString());
				}
				if (l == null || l <= 0) {
					toast("invalid amount");
					return;
				}
				final Long amount = l;
				final String acc = account.getText().toString().trim();
				if (acc.length() < 5) {
					toast("invalid account");
					return;
				}
				work(new Runnable() {

					@Override
					public void run() {
						try {
							String ret = ((OTjApplication) getApplication()).send(acc, amount);
							msg(ret);
						} catch (Exception e) {
							Log.e(TAG, "send error", e);
							msg("send error");
						}
					}
				}, "sending");
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
				Log.d(TAG, "picked '" + name + "'");
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
