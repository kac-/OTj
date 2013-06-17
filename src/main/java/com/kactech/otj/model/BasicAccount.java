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
 * OFFICIAL PROJECT PAGE:https://github.com/kactech/OTj
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
package com.kactech.otj.model;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.kactech.otj.Utils;

public class BasicAccount implements Account {
	String nymID;
	String nymIDSource;
	static final String[] keyNames = { "A", "E", "S" };
	Map<String, KeyPair> pairs = new LinkedHashMap<String, KeyPair>();
	Map<String, KeyPair> cpairs = new LinkedHashMap<String, KeyPair>();
	Map<String, String> sources = new LinkedHashMap<String, String>();
	Map<String, String> csources = new LinkedHashMap<String, String>();

	public BasicAccount generate() {
		KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		kpg.initialize(1024);
		for (String keyName : keyNames) {
			KeyPair kp = kpg.generateKeyPair();
			pairs.put(keyName, kp);
			sources.put(keyName, Utils.toPublicInfo(kp.getPublic()));
			kp = kpg.generateKeyPair();
			cpairs.put(keyName, kp);
			csources.put(keyName, Utils.toPublicInfo(kp.getPublic()));
		}
		nymIDSource = Utils.toNymIDSource(pairs.get("S").getPublic());
		nymID = Utils.toNymID(pairs.get("S").getPublic());
		return this;
	}

	@Override
	public Map<String, KeyPair> getPairs() {
		return pairs;
	}

	@Override
	public Map<String, KeyPair> getCpairs() {
		return cpairs;
	}

	@Override
	public Map<String, String> getSources() {
		return sources;
	}

	@Override
	public Map<String, String> getCsources() {
		return csources;
	}

	@Override
	public KeyPair getNymKeyPair() {
		return pairs.get("S");
	}

	@Override
	public String getNymID() {
		return nymID;
	}

	@Override
	public String getNymIDSource() {
		return nymIDSource;
	}
}
