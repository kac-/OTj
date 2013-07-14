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
package com.kactech.otj.model;

import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.kactech.otj.Utils;

public class OldServerContract extends BasicSigned {
	transient Document xml;
	Map<String, PublicKey> keys = new HashMap<String, PublicKey>();

	public void reset() {
		signatures.clear();
		keys.clear();
		xml = null;
	}

	public Map<String, PublicKey> getKeys() {
		return keys;
	}

	public void processXml() throws SAXException, IOException {
		if (getUnsigned() == null)
			throw new IllegalStateException("unsigned == null");
		if (xml == null)
			xml = Utils.parseXmlDocument(getUnsigned());
		Element root = xml.getDocumentElement();
		NodeList nl = root.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i) instanceof Element) {
				Element el = (Element) nl.item(i);
				if ("key".equals(el.getTagName())) {
					String name = el.getAttribute("name");
					String value = el.getTextContent();
					System.out.println("key " + name);
					if (value.contains("----BEGIN PGP PUBLIC KEY BLOCK")) {
						try {
							PublicKey key = Utils.pgpReadPublicKey(value);
							if (key != null)
								keys.put(name, key);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else if (value.contains("----BEGIN CERTIFICATE")) {
						try {
							value = Utils.unarmor(value, true);
							byte[] certBytes = Utils.base64Decode(value);
							X509Certificate cert = Utils.readX509Certificate(certBytes);
							keys.put(name, cert.getPublicKey());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					} else if (value.contains("-----BEGIN PUBLIC KEY")) {
						value = Utils.unarmor(value, true);
						byte[] packed = Utils.base64Decode(value);
						try {
							value = Utils.unpack(packed, String.class);
							RSAPublicKey pk = Utils.pemReadRSAPublicKey(value);
							keys.put(name, pk);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public Document getXml() {
		return xml;
	}
}
