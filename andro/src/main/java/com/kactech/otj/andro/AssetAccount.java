package com.kactech.otj.andro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;

public class AssetAccount {
	public static final String MIME = "ot_asset_account";
	public String contact;
	public String _id;
	public String server;
	public String asset;
	public String account;
	public String lookup;

	@Override
	public String toString() {
		return "AssetAccount [contact=" + contact + ", _id=" + _id + ", server=" + server + ", asset=" + asset
				+ ", account=" + account + ", lookup=" + lookup + "]";
	}

	public static List<AssetAccount> getAll(ContentResolver cr) {
		return get(cr, null, null, null, null, null);
	}

	public static List<AssetAccount> get(ContentResolver cr, String contact, String server,
			String asset, String account, String id) {
		List<AssetAccount> list = new ArrayList<AssetAccount>();
		List<String> args = new ArrayList<String>();
		String where = where(args, contact, server, asset, account, id);
		//Log.i("get", where + " " + args);
		Cursor cursor = cr.query(ContactsContract.Data.CONTENT_URI, null,
				where, args.toArray(new String[args.size()]), null);
		Log.d("aa", "get count " + cursor.getCount());
		while (cursor.moveToNext())
			list.add(from(cursor));
		return list;
	}

	public static String where(List<String> args, String contact, String server,
			String asset, String account, String id) {
		String where = ContactsContract.Data.MIMETYPE + " = ?";
		args.add(MIME);

		if (id != null) {
			where += " AND " + BaseColumns._ID + " = ?";
			args.add(id);
		}

		if (contact != null) {
			where += " AND " + ContactsContract.Data.CONTACT_ID + " = ?";
			args.add(contact);
		}

		if (server != null) {
			where += " AND " + ContactsContract.Data.DATA1 + " = ?";
			args.add(server);
		}

		if (asset != null) {
			where += " AND " + ContactsContract.Data.DATA2 + " = ?";
			args.add(asset);
		}

		if (account != null) {
			where += " AND " + ContactsContract.Data.DATA3 + " = ?";
			args.add(account);
		}
		return where;
	}

	public static AssetAccount from(Cursor c) {
		AssetAccount a = new AssetAccount();
		a.contact = c.getString(c.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
		a._id = c.getString(c.getColumnIndex(BaseColumns._ID));
		a.server = c.getString(c.getColumnIndex(ContactsContract.Data.DATA1));
		a.asset = c.getString(c.getColumnIndex(ContactsContract.Data.DATA2));
		a.account = c.getString(c.getColumnIndex(ContactsContract.Data.DATA3));
		a.lookup = c.getString(c.getColumnIndex(ContactsContract.Data.LOOKUP_KEY));
		return a;
	}

	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(ContactsContract.Data.RAW_CONTACT_ID, contact);
		values.put(ContactsContract.Data.DATA1, server);
		values.put(ContactsContract.Data.DATA2, asset);
		values.put(ContactsContract.Data.DATA3, account);
		values.put(ContactsContract.Data.MIMETYPE, MIME);
		return values;
	}

	public int update(ContentResolver cr) {
		if (_id == null)
			throw new IllegalStateException("trying to update entity with null _id");
		String where = BaseColumns._ID + "=?";
		return cr.update(ContactsContract.Data.CONTENT_URI, toContentValues(), where, new String[] { _id });
	}

	public int delete(ContentResolver cr) {
		if (_id == null)
			throw new IllegalStateException("trying to delete entity with null _id");
		String where = BaseColumns._ID + "=?";
		return cr.delete(ContactsContract.Data.CONTENT_URI, where, new String[] { _id });
	}

	public boolean insert(ContentResolver cr) {
		Uri dataUri = cr.insert(ContactsContract.Data.CONTENT_URI, toContentValues());
		if (dataUri != null) {
			_id = dataUri.getLastPathSegment();
			Log.d("aa", "this inserted " + this);
			/*
			Cursor c = cr.query(dataUri, new String[] { ContactsContract.Data.LOOKUP_KEY }, null, null, null);
			c.moveToFirst();
			lookup = c.getString(c.getColumnIndex(ContactsContract.Data.LOOKUP_KEY));
			if (false) {
				Log.d("aa", "this inserted " + this);
				Log.d("aa", "col " + Arrays.toString(c.getColumnNames()));
				String s = "";
				for (int i = 0; i < c.getColumnCount(); i++)
					s += c.getString(i) + ", ";
				Log.d("aa", s);
			}
			*/
			return true;
		}
		return false;

	}
}
