package com.kactech.otj.andro;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

public class Pseudonym {
	public static final String MIME = "ot_pseudonym";
	public String contact;
	public String _id;
	public String nym;
	public String lookup;

	@Override
	public String toString() {
		return "Pseudonym [contact=" + contact + ", _id=" + _id + ", nym=" + nym + ", lookup=" + lookup + "]";
	}

	public static List<Pseudonym> getAll(ContentResolver cr) {
		return get(cr, null, null, null);
	}

	public static List<Pseudonym> get(ContentResolver cr, String contact, String nym, String id) {
		List<Pseudonym> list = new ArrayList<Pseudonym>();
		List<String> args = new ArrayList<String>();
		String where = where(args, contact, nym, id);
		Cursor cursor = cr.query(ContactsContract.Data.CONTENT_URI, null,
				where, args.toArray(new String[args.size()]), null);
		while (cursor.moveToNext())
			list.add(from(cursor));
		return list;
	}

	public static String where(List<String> args, String contact, String nym, String id) {
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

		if (nym != null) {
			where += " AND " + ContactsContract.Data.DATA1 + " = ?";
			args.add(nym);
		}
		return where;
	}

	public static Pseudonym from(Cursor c) {
		Pseudonym a = new Pseudonym();
		a.contact = c.getString(c.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
		a._id = c.getString(c.getColumnIndex(BaseColumns._ID));
		a.nym = c.getString(c.getColumnIndex(ContactsContract.Data.DATA1));
		a.lookup = c.getString(c.getColumnIndex(ContactsContract.Data.LOOKUP_KEY));
		return a;
	}

	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(ContactsContract.Data.RAW_CONTACT_ID, contact);
		values.put(ContactsContract.Data.DATA1, nym);
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
			List<String> p = dataUri.getPathSegments();
			_id = p.get(p.size() - 1);
			lookup = p.get(p.size() - 2);
			return true;
		}
		return false;

	}
}
