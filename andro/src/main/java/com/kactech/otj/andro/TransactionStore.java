package com.kactech.otj.andro;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TransactionStore {

	public static final String KEY_ID = "_id";
	public static final String KEY_DATE = "date";
	public static final String KEY_ACCOUNT = "account";
	public static final String KEY_AMOUNT = "amount";

	static final String TRANSACTIONS_TABLE_NAME = "transaction_store";
	static final String TRANSACTIONS_TABLE_CREATE =
			"CREATE TABLE " + TRANSACTIONS_TABLE_NAME + " (" +
					KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
					KEY_DATE + " INTEGER, " +
					KEY_ACCOUNT + " TEXT, " +
					KEY_AMOUNT + " INTEGER);";

	DBHelper h;

	public TransactionStore(DBHelper h) {
		this.h = h;
	}

	public boolean insert(Transaction tx) {
		ContentValues v = new ContentValues();
		v.put(KEY_DATE, tx.getDate());
		v.put(KEY_ACCOUNT, tx.getAccount());
		v.put(KEY_AMOUNT, tx.getAmount());
		SQLiteDatabase db = h.getWritableDatabase();
		long id = db.insertOrThrow(TRANSACTIONS_TABLE_NAME, null, v);
		db.close();
		tx.set_id(id);
		return true;
	}

	public List<Transaction> getAll() {
		return getAll(null);
	}

	public List<Transaction> getAll(String limit) {
		List<Transaction> list = new ArrayList<Transaction>();
		SQLiteDatabase db = h.getReadableDatabase();
		Cursor c = db.query(TRANSACTIONS_TABLE_NAME, null, null, null, null, null, "date DESC", limit);
		int rowid = c.getColumnIndex(KEY_ID);
		int date = c.getColumnIndex(KEY_DATE);
		int acc = c.getColumnIndex(KEY_ACCOUNT);
		int amount = c.getColumnIndex(KEY_AMOUNT);

		while (c.moveToNext())
			list.add(new Transaction(c.getLong(rowid), c.getLong(date), c.getString(acc), c.getLong(amount)));
		c.close();
		db.close();
		return list;
	}

	public Transaction find(String id) {
		Transaction tx = null;
		SQLiteDatabase db = h.getReadableDatabase();
		Cursor c = db.query(TRANSACTIONS_TABLE_NAME, null, KEY_ID + " =?", new String[] { id }, null, null, null, "1");
		if (c.moveToFirst()) {
			int rowid = c.getColumnIndex(KEY_ID);
			int date = c.getColumnIndex(KEY_DATE);
			int acc = c.getColumnIndex(KEY_ACCOUNT);
			int amount = c.getColumnIndex(KEY_AMOUNT);
			tx = new Transaction(c.getLong(rowid), c.getLong(date), c.getString(acc), c.getLong(amount));
		}
		c.close();
		db.close();
		return tx;
	}
}