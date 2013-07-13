package com.kactech.otj.andro;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.example.android.skeletonapp.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MessagesView extends Activity {
	static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static final int PICK_TO_ASSIGN = 0x30001;
	ListView list;
	MessageItemAdapter adapter;
	Message current;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages_view);
		list = (ListView) findViewById(R.id.msgs_view_list);
		registerForContextMenu(list);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.msgs_view_list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			current = adapter.getItem(info.position);
			Log.d("msg view", "" + current);
			menu.setHeaderTitle("message");
			String[] menuItems;
			//menuItems = getResources().getStringArray(R.array.menu);
			menuItems = new String[] { "assign", "delete" };
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if ("assign".equals(item.getTitle())) {
			Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, PICK_TO_ASSIGN);
		} else if ("delete".equals(item.getTitle())) {
			((OTjApplication) getApplication()).delete(current);
			refreshList();
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			if (PICK_TO_ASSIGN == requestCode) {
				Log.i("assign", "picked " + data);
				Uri contactData = data.getData();
				if (contactData != null) {
					((OTjApplication) getApplication()).assignNym(current.nym, contactData);
					refreshList();
				}
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("msg list", "resume");
		refreshList();
	}

	void refreshList() {
		list.setAdapter(adapter = new MessageItemAdapter(this, R.id.msgs_view_list,
				((OTjApplication) getApplication()).getMessages()));
	}

	static class MessageItemAdapter extends ArrayAdapter<Message> {

		List<Message> Messages;

		public MessageItemAdapter(Context context, int textViewResourceId, List<Message> objects) {
			super(context, textViewResourceId, objects);
			this.Messages = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.msg_listitem, null);
			}

			Message tx = Messages.get(position);
			if (tx != null) {
				TextView text;
				OTjApplication app = (OTjApplication) getContext().getApplicationContext();
				((TextView) v.findViewById(R.id.msg_listitem_date)).setText(DF.format(new Date(tx.date * 1000)));
				((TextView) v.findViewById(R.id.msg_listitem_nym)).setText(tx.nym);
				String displayName = app.getNymDisplayName(tx.nym);
				((TextView) v.findViewById(R.id.msg_listitem_name)).setText(displayName);
				((TextView) v.findViewById(R.id.msg_listitem_subject)).setText(tx.subject);
				((TextView) v.findViewById(R.id.msg_listitem_text)).setText(tx.text);
			}
			return v;
		}

	}

}
