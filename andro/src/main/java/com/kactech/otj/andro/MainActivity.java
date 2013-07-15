/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kactech.otj.andro;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.skeletonapp.R;

/**
 * This class provides a basic demonstration of how to write an Android
 * activity. Inside of its window, it places a single view: an EditText that
 * displays and edits some internal text.
 */
public class MainActivity extends BaseActivity {
	static final String TAG = "main";
	static final int CONTACT_PICKER_RESULT = 1001;
	static final int ACCOUNT_PICKER_RESULT = 1002;

	Cursor cursor;

	ListView txList;
	TransactionItemAdapter txAdapter;

	Button newTransaction;
	Button messages;

	TextView balance;

	public MainActivity() {
	}

	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//System.out.println(new BasicUserAccount().generate());

		// Inflate our UI from its XML layout description.
		setContentView(R.layout.main_activity);
		//Log.i("accList", AssetAccount.getAll(getContentResolver()).toString());
		newTransaction = (Button) findViewById(R.id.new_transaction);
		messages = (Button) findViewById(R.id.show_messages);
		txList = (ListView) findViewById(R.id.string_list_list);
		balance = (TextView) findViewById(R.id.balance_value);

		txList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Transaction tx;
				//= txStore.find(Long.toString(arg3));
				tx = txAdapter.transactions.get(arg2);
				Intent intent = new Intent(MainActivity.this, TransactionView.class);
				intent.putExtra("tx", tx);
				startActivity(intent);
			}

		});
		newTransaction.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, NewTransaction.class);
				startActivity(intent);
			}
		});
		messages.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, MessagesView.class);
				startActivity(intent);
			}
		});
		((Button) findViewById(R.id.refresh_button)).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				work(new Runnable() {

					@Override
					public void run() {
						try {
							((OTjApplication) getApplication()).refreshClient();
							msg("refresh done");
						} catch (Exception e) {
							Log.e(TAG, "refreshClient()", e);
							msg("refresh error");
						}
					}
				}, "refreshing");
			}
		});
		((Button) findViewById(R.id.show_info_button)).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, Info.class);
				startActivity(intent);
			}
		});

		((Button) findViewById(R.id.reload_nym_button)).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				work(new Runnable() {

					@Override
					public void run() {
						try {
							((OTjApplication) getApplication()).reloadNym();
							msg("reload done");
						} catch (Exception e) {
							Log.e(TAG, "reloading nym", e);
							msg("reload error");
						}
					}
				}, "reloading nym");
			}
		});

		txAdapter = new TransactionItemAdapter(this, R.layout.tx_listitem,
				Collections.EMPTY_LIST);
		txList.setAdapter(txAdapter);

		messages.setVisibility(View.INVISIBLE);

		handler = new BaseHandler(this) {
			public void handleMessage(Message msg) {
				refreshView();
				super.handleMessage(msg);
			};
		};

		work(new Runnable() {

			@Override
			public void run() {
				try {
					((OTjApplication) getApplication()).loadAccountsAndNyms();
				} catch (Exception e) {
					Log.e(TAG, "init", e);
					((OTjApplication) getApplication()).unload();
					finish();
					return;
				}
				msg("init done");
			}
		}, "init", "init");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");
		refreshView();
	}

	void refreshView() {
		txAdapter.setTransactions(((OTjApplication) getApplication()).getTransactions());
		messages.setText(Integer.toString(((OTjApplication) getApplication()).getMessagesCount()));
		balance.setText(Long.toString(((OTjApplication) getApplication()).getBalance()));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CONTACT_PICKER_RESULT:
				Log.i(TAG, "pick " + data.toString());
				String contact = data.getData().getLastPathSegment();
				AssetAccount a;

				List<AssetAccount> l = AssetAccount.get(getContentResolver(), contact,
						getServer(),
						getAsset(), null, null);
				if (l.size() > 0) {
					a = l.get(l.size() - 1);
					a.account = new SimpleDateFormat("HH:mm:ss").format(new Date());
					int u = a.update(getContentResolver());
					Log.i(TAG, "rows updated " + u);
				} else {
					a = new AssetAccount();
					a.contact = contact;
					a.server = getServer();
					a.asset = getAsset();
					a.account = "newacct123";
					boolean saved = a.insert(getContentResolver());
					Log.i(TAG, "account saved: " + saved);
				}
				break;
			case ACCOUNT_PICKER_RESULT:
				break;
			}
		} else {
			Log.w(TAG, "Warning: activity result not ok");
		}
	};

	String getServer() {
		return "srv123";
	}

	String getAsset() {
		return "ass123";
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "destroy");
		((OTjApplication) getApplication()).unload();
	}

}
