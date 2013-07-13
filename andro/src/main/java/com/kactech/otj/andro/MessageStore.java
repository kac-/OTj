package com.kactech.otj.andro;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MessageStore {
	public static final String KEY_ID = "_id";
	public static final String KEY_DATE = "date";
	public static final String KEY_NYM = "nym";
	public static final String KEY_SUBJECT = "subject";
	public static final String KEY_TEXT = "msg";
	public static final String KEY_READ = "read";

	static final String MESSAGES_TABLE_NAME = "message_store";
	static final String MESSAGES_TABLE_CREATE =
			"CREATE TABLE " + MESSAGES_TABLE_NAME + " (" +
					KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
					KEY_DATE + " INTEGER, " +
					KEY_NYM + " TEXT, " +
					KEY_SUBJECT + " TEXT, " +
					KEY_TEXT + " TEXT, " +
					KEY_READ + " INTEGER);";
	DBHelper h;

	public MessageStore(DBHelper h) {
		super();
		this.h = h;
	}

	public List<Message> getAll() {
		return getAll(null);
	}

	public List<Message> getAll(String limit) {
		List<Message> list = new ArrayList<Message>();
		SQLiteDatabase db = h.getReadableDatabase();
		Cursor c = db.query(MESSAGES_TABLE_NAME, null, null, null, null, null, "date DESC", limit);
		int rowid = c.getColumnIndex(KEY_ID);
		int date = c.getColumnIndex(KEY_DATE);
		int nym = c.getColumnIndex(KEY_NYM);
		int subject = c.getColumnIndex(KEY_SUBJECT);
		int txt = c.getColumnIndex(KEY_TEXT);
		int read = c.getColumnIndex(KEY_READ);

		while (c.moveToNext())
			list.add(new Message(c.getLong(rowid), c.getLong(date), c.getString(nym), c.getString(subject), c
					.getString(txt), c.getInt(read) == 1));

		c.close();
		db.close();
		return list;
	}

	public boolean insert(Message msg) {
		ContentValues v = new ContentValues();
		v.put(KEY_DATE, msg.date);
		v.put(KEY_NYM, msg.nym);
		v.put(KEY_SUBJECT, msg.subject);
		v.put(KEY_TEXT, msg.text);
		v.put(KEY_READ, msg.read ? 1 : 0);
		SQLiteDatabase db = h.getWritableDatabase();
		long id = db.insertOrThrow(MESSAGES_TABLE_NAME, null, v);
		db.close();
		msg._id = id;
		return true;
	}

}
