package com.kactech.otj.andro;

import java.util.Collections;
import java.util.List;

import com.example.android.skeletonapp.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TransactionItemAdapter extends ArrayAdapter<Transaction> {

	List<Transaction> transactions;

	public TransactionItemAdapter(Context context, int textViewResourceId, List<Transaction> objects) {
		super(context, textViewResourceId, Collections.EMPTY_LIST);
		this.transactions = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.tx_listitem, null);
		}

		Transaction tx = transactions.get(position);
		if (tx != null) {
			String displayName = ((OTjApplication) getContext().getApplicationContext())
					.getAccountDisplayName(tx.account);
			TextView text = (TextView) v.findViewById(R.id.tx_listitem_account);
			if (text != null)
				text.setText(displayName != null ? displayName : tx.account);
			text = (TextView) v.findViewById(R.id.tx_listitem_amount);
			text.setText(Long.toString(tx.amount));
			text.setTextColor(tx.amount >= 0 ? Color.GREEN : Color.RED);
		}
		return v;
	}

	@Override
	public Transaction getItem(int position) {
		return transactions.get(position);
	}

	@Override
	public int getCount() {
		return transactions.size();
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
		notifyDataSetChanged();
	}
}
