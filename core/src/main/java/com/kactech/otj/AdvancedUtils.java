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
package com.kactech.otj;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.kactech.otj.model.BasicConnectionInfo;

public class AdvancedUtils {
	public static BasicConnectionInfo toConnectionInfo(OT.NotaryProviderContract notary) {
		String name = notary.getEntity().getShortname();
		String endpoint = "tcp://" + notary.getNotaryServer().getHostname() + ':' + notary.getNotaryServer().getPort();
		String id = Utils.samy62(Utils.bytes(notary.getSigned().trim(), Utils.UTF8));
		PublicKey publicKey = null;
		String nymID = null;
		if (notary.getSigner() != null) {
			nymID = notary.getSigner().getNymID();
			for (OT.MasterCredential c : notary.getSigner().getCredentials().values()) {
				if (c instanceof OT.KeyCredential) {
					OT.PublicContents pub = ((OT.KeyCredential) c).getMasterSigned().getPublicContents();
					String v = null;
					for (OT.KeyValue pubi : pub.getPublicInfos())
						if (pubi.getKey().equals("E"))
							v = pubi.getValue();
					publicKey = Utils.fromRawPublicInfo(v, false);
					break;
				}
			}
		} else {
			for (OT.NamedText txt : notary.getKeys()) {
				if ("contract".equals(txt.getName())) {
					String value = Utils.unarmor(txt.getText(), true);
					byte[] certBytes = Utils.base64Decode(value);
					X509Certificate cert = Utils.readX509Certificate(certBytes);
					publicKey = cert.getPublicKey();
					nymID = Utils.toNymID(publicKey);
				}
			}
		}
		return new BasicConnectionInfo(id, publicKey, endpoint, nymID, name);
	}

	public static char[] getMasterPassword(OT.SymmetricKey cachedKey, String password) {
		SecretKeyFactory f;
		try {
			f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

			KeySpec spec;
			int derivedKeyLength = 128;
			spec = new PBEKeySpec((password + '\0').toCharArray(), cachedKey.getSalt(), cachedKey.getIterationCount(),
					derivedKeyLength);
			byte[] derivedKey = f.generateSecret(spec).getEncoded();
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(derivedKey, "AES"),
					new IvParameterSpec(cachedKey.getIv()));

			final byte[] decKey = cipher.doFinal(cachedKey.getEncKey());
			char[] decChars = new char[decKey.length];
			for (int i = 0; i < decChars.length; i++)
				decChars[i] = (char) (0xff & decKey[i]);
			return decChars;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
