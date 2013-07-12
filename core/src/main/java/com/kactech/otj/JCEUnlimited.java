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

import java.io.IOException;
import java.io.StringReader;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.spongycastle.openssl.PEMReader;
import org.spongycastle.openssl.PasswordFinder;

import com.kactech.otj.model.BasicPrivateInfo;

public class JCEUnlimited {
	public static BasicPrivateInfo getPrivateInfo(String privateInfo, final char[] masterPassword) {
		BasicPrivateInfo bpi = new BasicPrivateInfo();
		PEMReader p = new PEMReader(new StringReader(privateInfo), new PasswordFinder() {

			@Override
			public char[] getPassword() {

				return masterPassword;
			}
		});
		Object o;
		try {
			while ((o = p.readObject()) != null) {
				if (o instanceof PrivateKey)
					bpi.setPrivateKey((PrivateKey) o);
				else if (o instanceof X509Certificate)
					bpi.setCertificate((X509Certificate) o);
				else
					System.err.println(o.getClass());
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			try {
				p.close();
			} catch (IOException e) {
			}
		}
		return bpi;
	}
}
