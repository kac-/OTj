package com.kactech.otj.andro;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.android.skeletonapp.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class TransactionView extends Activity {
	SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static final int PICK_FOR_ASSIGNING = 10101;
	Transaction tx;
	TextView name;
	TextView account;
	TextView date;
	TextView amount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transaction_view);
		tx = (Transaction) getIntent().getExtras().get("tx");
		Log.i("view", tx == null ? "NULL" : tx.toString());

		name = (TextView) findViewById(R.id.tx_view_name);
		date = (TextView) findViewById(R.id.tx_view_date);
		amount = (TextView) findViewById(R.id.tx_view_amount);
		account = (TextView) findViewById(R.id.tx_view_account);
		account.setClickable(true);
		registerForContextMenu(account);

		name.setText(((OTjApplication) getApplication())
				.getAccountDisplayName(tx.account));
		account.setText(tx.account);
		amount.setText(Long.toString(tx.amount));
		amount.setTextColor(tx.amount >= 0 ? Color.GREEN : Color.RED);
		date.setText(DF.format(new Date(tx.date * 1000)));
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("view", "resume");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.transaction_view, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.tx_view_account) {
			//Log.i("context", menu != null ? menu.toString() : "NULL");
			menu.setHeaderTitle("accountID");
			String[] menuItems;
			//menuItems = getResources().getStringArray(R.array.menu);
			menuItems = new String[] { "assign" };
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if ("assign".equals(item.getTitle())) {
			Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, PICK_FOR_ASSIGNING);
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			if (PICK_FOR_ASSIGNING == requestCode) {
				Log.i("assign", "picked " + data);
				Uri contactData = data.getData();
				if (contactData != null) {
					String name = ((OTjApplication) getApplication()).assignAcct(tx.account, contactData);
					this.name.setText(name);
				}
			}
		}
	}
}
