/*******************************************************************************
 *              OTj
 * Low-level client-side library for Open Transactions in Java
 * 
 * Copyright (C) 2013 by Piotr Kopeć (kactech)
 * 
 * EMAIL: pepe.kopec@gmail.com
 * 
 * BITCOIN: 1ESADvST7ubsFce7aEi2B6c6E2tYd4mHQp
 * 
 * OFFICIAL PROJECT PAGE: https://github.com/kactech/OTj
 * 
 * -------------------------------------------------------
 * 
 * LICENSE:
 * This program is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * ADDITIONAL PERMISSION under the GNU Affero GPL version 3
 * section 7: If you modify this Program, or
 * any covered work, by linking or combining it with other
 * code, such other code is not for that reason alone subject
 * to any of the requirements of the GNU Affero GPL version 3.
 * (==> This means if you are only using the OTj, then you
 * don't have to open-source your code--only your changes to
 * OTj itself must be open source. Similar to
 * LGPLv3, except it applies to software-as-a-service, not
 * just to distributing binaries.)
 * Anyone using my library is given additional permission
 * to link their software with any BSD-licensed code.
 * 
 * -----------------------------------------------------
 * 
 * You should have received a copy of the GNU Affero General
 * Public License along with this program. If not, see:
 * http://www.gnu.org/licenses/
 * 
 * If you would like to use this software outside of the free
 * software license, please contact Piotr Kopeć.
 * 
 * DISCLAIMER:
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU Affero General Public License for
 * more details.
 ******************************************************************************/
package com.kactech.otj.examples;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.reflect.TypeToken;
import com.kactech.otj.Engines;
import com.kactech.otj.Utils;
import com.kactech.otj.model.BasicConnectionInfo;
import com.kactech.otj.model.ConnectionInfo;
import com.thoughtworks.xstream.XStream;

public class ExamplesUtils {
	public static void serializeJava(File path, Object obj) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			Utils.writeDirs(path, bos.toByteArray());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Object deserializeJava(File path) {
		Object obj;
		try {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(Utils.readBytes(path)));
			obj = ois.readObject();
			ois.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return obj;
	}

	public static void serializeXStream(File path, Object obj) {
		try {
			Utils.writeDirs(path, new XStream().toXML(obj));
		} catch (IOException e) {
			// test environment- just hide it
			throw new RuntimeException(e);
		}
	}

	public static Object deserializeXStream(File path) {
		try {
			return new XStream().fromXML(Utils.read(path));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static List<SampleAccount> sampleAccounts;

	public static List<SampleAccount> getSampleAccounts() {
		if (sampleAccounts == null)
			sampleAccounts = Engines.gson.fromJson(new InputStreamReader(Engines.class
					.getResourceAsStream("/com/kactech/otj/examples/sample-accounts.json")),
					new TypeToken<List<SampleAccount>>() {
					}.getType());
		return new ArrayList<SampleAccount>(sampleAccounts);
	}

	private static Map<String, ConnectionInfo> servers;

	public static Map<String, ConnectionInfo> getServers() {
		if (servers == null) {
			servers = Engines.gson.fromJson(new InputStreamReader(Engines.class
					.getResourceAsStream("/com/kactech/otj/examples/sample-servers.json")),
					new TypeToken<Map<String, BasicConnectionInfo>>() {
					}.getType());
		}
		return servers;
	}

	public static ConnectionInfo findServer(String part) {
		Map<String, ConnectionInfo> map = getServers();
		for (Entry<String, ConnectionInfo> e : map.entrySet()) {
			if (e.getKey().startsWith(part))
				return e.getValue();
			if (e.getValue().getID().startsWith(part))
				return e.getValue();
		}
		return null;
	}

	public static SampleAccount findAsset(String part) {
		for (SampleAccount acct : getSampleAccounts())
			if (acct.assetName.startsWith(part) || acct.assetID.startsWith(part))
				return acct;
		return null;
	}

	private static Map<String, String> contractsURI = new HashMap<String, String>();
	static {
		// Transactions.com localhost test server
		contractsURI
				.put("tBy5mL14qSQXCJK7Uz3WlTOKRP9M0JZksA3Eg7EnnQ1",
						"https://raw.github.com/FellowTraveler/Open-Transactions/master/sample-data/ot-sample-data/server_data/contracts/tBy5mL14qSQXCJK7Uz3WlTOKRP9M0JZksA3Eg7EnnQ1");
		// vancouver bitcoin test server
		contractsURI.put("4x3jrBs4OZ8DN7rOTSbdqb8bdrG0O5sMFzArgZP7NXO",
				"https://raw.github.com/stretch/OT-BitcoinServer/master/test-contracts/SERVER-vanbtc.otc");
		// OT 8coin
		contractsURI.put("8bPtJo8pmJ5eG992ccbfrl06DsDi6aqxr7fNhAK2PuW",
				"https://raw.github.com/kactech/OTj/master/sample-data/SERVER-ot.8coin.org.otc");
		// silver grams asset
		contractsURI
				.put("CvHGtfOOKzQKL5hFL7J4iF5yAodVKhS1rxPzME5R9XA",
						"https://raw.github.com/FellowTraveler/Open-Transactions/master/sample-data/ot-sample-data/server_data/contracts/CvHGtfOOKzQKL5hFL7J4iF5yAodVKhS1rxPzME5R9XA");
		// kactech LOC
		contractsURI
				.put("3SSQuTikpv7H9KlPNvnJ5ttmjqIwQc60ySvoXfYRBc8",
						"https://github.com/kactech/OTj/blob/master/sample-data/ASSET-ktLOC.otc");

	}

	public static String getContractURI(String id) {
		return contractsURI.get(id);
	}
}
