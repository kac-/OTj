package com.kactech.otj.andro;

import java.util.List;

import com.example.android.skeletonapp.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PickString extends Activity {
	TextView title;
	ListView listView;
	List<String> values;
	String hold;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pick_string);
		listView = (ListView) findViewById(R.id.string_list_list);
		title = (TextView) findViewById(R.id.string_list_title);
		title.setText(getIntent().getExtras().getString("title"));

		hold = getIntent().getExtras().getString("hold");
		values = (List<String>) getIntent().getExtras().getStringArrayList("values");
		StringListAdapter sla = new StringListAdapter(this, values);
		listView.setAdapter(sla);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent resultData = new Intent();
				resultData.putExtra("selected", values.get(arg2));
				resultData.putExtra("hold", hold);
				setResult(Activity.RESULT_OK, resultData);
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	public static class StringListAdapter extends ArrayAdapter<String> {

		public StringListAdapter(Context context, List<String> objects) {
			super(context, R.layout.string_list_item, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.string_list_item, null);
			}
			String v = getItem(position);
			((TextView) convertView.findViewById(R.id.strig_list_item_value)).setText(v);
			return convertView;
		}

	}

}
