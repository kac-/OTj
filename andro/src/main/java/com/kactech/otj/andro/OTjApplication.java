package com.kactech.otj.andro;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import com.kactech.otj.Client;
import com.kactech.otj.EClient;
import com.kactech.otj.MSG;
import com.kactech.otj.Utils;
import com.kactech.otj.examples.ExamplesUtils;
import com.kactech.otj.examples.IncomingTransfrerFilter;
import com.kactech.otj.examples.UserMessagesFilter;

public class OTjApplication extends Application {
	static final String TAG = "app";
	public Map<String, String> contactIdToName = new HashMap<String, String>();
	public Map<String, AssetAccount> accountCache = new HashMap<String, AssetAccount>();
	public Map<String, Pseudonym> nymCache = new HashMap<String, Pseudonym>();

	DBHelper dbHelper;
	TransactionStore txStore;
	MessageStore msgStore;

	List<Transaction> transactions;
	List<Message> messages;
	UserMessagesFilter messagesFilter = new UserMessagesFilter();
	IncomingTransfrerFilter transfersFilter = new IncomingTransfrerFilter();

	EClient client;

	@Override
	public void onCreate() {
		Log.i(TAG, "create");
		dbHelper = new DBHelper(this);
		txStore = new TransactionStore(dbHelper);
		msgStore = new MessageStore(dbHelper);

		transactions = txStore.getAll();
		messages = msgStore.getAll();

		Utils.init();
	}

	@Override
	public void onTerminate() {
		Log.i("app", "terminate");
		unload();
	}

	public String getAccountDisplayName(String account) {
		AssetAccount acc = accountCache.get(account);
		return acc == null ? null : contactIdToName.get(acc.contact);
	}

	public String getNymDisplayName(String nym) {
		Pseudonym pnym = nymCache.get(nym);
		return pnym == null ? null : contactIdToName.get(pnym.contact);
	}

	public Collection<String> getAccountDisplayNames() {
		Set<String> names = new HashSet<String>();
		for (AssetAccount acct : accountCache.values())
			names.add(contactIdToName.get(acct.contact));
		return names;
	}

	public Collection<String> getNymDisplayNames() {
		Set<String> names = new HashSet<String>();
		for (Pseudonym nym : nymCache.values())
			names.add(contactIdToName.get(nym.contact));
		return names;
	}

	public String assignAcct(String account, Uri contact) {
		AssetAccount acc = accountCache.get(account);
		if (acc != null) {
			int r = acc.delete(getContentResolver());
			Log.i("assign", "delete: " + r);
			if (r > 0) {
				accountCache.remove(account);
			}

		} else {
			acc = new AssetAccount();
			acc.account = account;
			acc.asset = getAsset();
			acc.server = getServer();
		}
		acc.contact = contact.getLastPathSegment();
		boolean r = acc.insert(getContentResolver());
		Log.i("assign", "insert: " + r);
		if (r) {
			String name = _displayName(contact);
			accountCache.put(acc.account, acc);
			return name;
		}
		return null;
	}

	public String assignNym(String nym, Uri contact) {
		Pseudonym acc = nymCache.get(nym);
		if (acc != null) {
			int r = acc.delete(getContentResolver());
			Log.i("assign", "delete: " + r);
			if (r > 0) {
				nymCache.remove(nym);
			}

		} else {
			acc = new Pseudonym();
			acc.nym = nym;
		}
		acc.contact = contact.getLastPathSegment();
		boolean r = acc.insert(getContentResolver());
		Log.i("assign", "insert: " + r);
		if (r) {
			String name = _displayName(contact);
			nymCache.put(acc.nym, acc);
			return name;
		}
		return null;
	}

	String getServer() {
		return client == null ? null : client.getConnInfo().getID();
	}

	String getAsset() {
		return client == null ? null : client.getCachedAccount() == null ? null : client.getCachedAccount()
				.getAssetTypeID();
	}

	String getAccount() {
		return client == null ? null : client.getCachedAccount() == null ? null : client.getCachedAccount()
				.getAccountID();
	}

	String getNym() {
		return client == null ? null : client.getClient().getUserAccount().getNymID();
	}

	private String _displayName(Uri contact) {
		String contactId = contact.getLastPathSegment();
		String name;
		name = contactIdToName.get(contactId);
		if (name != null)
			return name;
		Cursor c = getContentResolver().query(contact, new String[] { ContactsContract.Data.DISPLAY_NAME }, null,
				null, null);
		Log.d("beta", "disp move to " + c.moveToFirst());
		name = c.getString(c.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
		c.close();
		contactIdToName.put(contactId, name);
		return name;
	}

	public void loadAccountsAndNyms() {
		contactIdToName.clear();
		accountCache.clear();
		nymCache.clear();
		for (AssetAccount acc : AssetAccount.getAll(getContentResolver())) {
			//Uri uri = ContactsContract.Contacts.getLookupUri(Long.parseLong(acc.contact), acc.lookup);
			Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, acc.contact);
			_displayName(uri);
			accountCache.put(acc.account, acc);
		}
		for (Pseudonym nym : Pseudonym.getAll(getContentResolver())) {
			//Uri uri = ContactsContract.Contacts.getLookupUri(Long.parseLong(nym.contact), "invalid");
			Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, nym.contact);
			_displayName(uri);
			nymCache.put(nym.nym, nym);
		}
		//Log.d("load()", nymCache.toString());
		if (client == null) {
			EClient c = new EClient(new File(Environment.getExternalStorageDirectory(), "client"),
					ExamplesUtils.findServer("OT 8coin"));
			c.setAssetType(ExamplesUtils.findAsset("silver").assetID);
			c.init();
			c.getClient().addFilter((Client.Filter) transfersFilter, MSG.ProcessInboxResp.class, 0);
			c.getClient().addFilter((Client.Filter) transfersFilter, MSG.GetInboxResp.class, 0);
			client = c;
		}
		client.getAccount();

		Log.d("beta", "acc " + AssetAccount.getAll(getContentResolver()));
		Log.d("beta", "cache " + accountCache);
		Log.d("beta", "nam " + contactIdToName);
	}

	public Collection<String> getAccountIdsForName(String name) {
		Collection<String> l = new ArrayList<String>();
		for (Map.Entry<String, String> e : contactIdToName.entrySet())
			if (name.equals(e.getValue())) {
				for (AssetAccount acc : accountCache.values())
					if (acc.contact.equals(e.getKey()))
						l.add(acc.account);
			}
		return l;
	}

	public ArrayList<Message> getMessages() {
		return new ArrayList<Message>(messages);
	}

	public List<Transaction> getTransactions() {
		return new ArrayList<Transaction>(transactions);
	}

	public int getMessagesCount() {
		return messages.size();
	}

	public void delete(Message msg) {
		//TODO
	}

	public void unload() {
		if (client != null) {
			client.saveState();
			try {
				client.close();
			} catch (IOException e) {
			}
			client = null;
		}
	}

	public void refreshClient() {
		client.processInbox();
		client.getAccount();
		for (IncomingTransfrerFilter.Tx tx : transfersFilter.getAndClearAcknowledged()) {
			Transaction trans = new Transaction(null, tx.date, tx.account, tx.amount);
			if (txStore.insert(trans)) {
				Log.i("beta", "inc 	" + trans);
				transactions.add(0, trans);
			}
		}
	}

	public String send(String account, Long amount) {
		try {
			if (!client.notarizeTransaction(account, amount)) {
				Log.e("beta", "notarize unsuccessful");
				return "notarize unsuccessful";
				//Toast.makeText(this, "not sent", 500).show();
			} else {
				Transaction tx = new Transaction(null, System.currentTimeMillis() / 1000, account, -amount);
				if (txStore.insert(tx)) {
					Log.i("beta", "added " + tx);
					transactions.add(0, tx);
					client.getAccount();
				}
				//Toast.makeText(this, "sent " + amount, 500).show();
				Log.e("info", "sent " + amount);
				return "sent " + amount;
			}
		} catch (Exception e) {
			Log.e("beta", "notarize", e);
			//Toast.makeText(this, "notarize exception", 1000).show();
			return "notarize exception";
		}
	}

	public long getBalance() {
		return client == null ? 0 : client.getCachedAccount() == null ? 0 : client.getCachedAccount().getBalance()
				.getAmount();
	}

	public void reloadNym() {
		client.reloadState();
	}

	public boolean sendUserMessage(String nym, String msg) {
		try {
			return client.sendUserMessage(nym, msg);
		} catch (Exception e) {
			Log.e(TAG, "send message", e);
			return false;
		}
	}
}
