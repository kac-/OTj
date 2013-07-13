package com.kactech.otj.andro;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "otj";
	private static final int DATABASE_VERSION = 2;

	DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TransactionStore.TRANSACTIONS_TABLE_CREATE);
		db.execSQL(MessageStore.MESSAGES_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		Log.w("debug", "onUpgrade not implemented by " + TransactionStore.class);
	}
}
