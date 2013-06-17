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

package com.kactech.otj.examples;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kactech.otj.Client;
import com.kactech.otj.JeromqTranport;
import com.kactech.otj.Messages;
import com.kactech.otj.Utils;
import com.kactech.otj.model.Account;
import com.kactech.otj.model.BasicAccount;
import com.thoughtworks.xstream.XStream;

public class DirtyClientExample {
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	private static final Path dirtyExampleAccountPath = Paths.get("testData", "acc", "dirtyExample.xml");
	private static Account testAccount;

	public static Account getAccount() {
		synchronized (dirtyExampleAccountPath) {
			if (testAccount == null) {
				Account acc = null;
				try {
					acc = (Account) new XStream().fromXML(Utils.read(dirtyExampleAccountPath));
				} catch (Exception e) {
				}
				if (acc == null) {
					BasicAccount bacc = new BasicAccount();
					bacc.generate();
					try {
						Utils.writeDirs(dirtyExampleAccountPath, new XStream().toXML(bacc));
					} catch (IOException e) {
						//throw it as runtime because... it's runtime for test example
						throw new RuntimeException(e);
					}
					acc = bacc;
				}
				testAccount = acc;
			}
		}
		return testAccount;
	}

	public static Client buildClient() throws JsonSyntaxException, InvalidKeySpecException, NoSuchAlgorithmException {
		// JSON serialized test-server RSAPublicKeySpec
		String serverKeyString = "{\"modulus\":157308124954637849769808055227589301287752744825558530150220825924565496309952110169467763525063683140481597445838436176231389850954363355832747286996253525968666484844394188835285816520152462516072618852511394863800122669965310355610221431146947161206966593783544166481213230593202598098997296223206388439283,\"publicExponent\":65537}";
		RSAPublicKey serverPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(
				new Gson().fromJson(serverKeyString, RSAPublicKeySpec.class));
		// generate serverNymID from public key
		String serverID = "tBy5mL14qSQXCJK7Uz3WlTOKRP9M0JZksA3Eg7EnnQ1";
		String serverEndpoint = "tcp://localhost:7085";

		Client client = new Client(getAccount(), serverID, serverPublicKey, new JeromqTranport(
				serverEndpoint));
		return client;
	}

	public static void main(String[] args) throws Exception {
		Client client = buildClient();
		try {
			int reqNum;
			/*
			 * try get request number
			 * if it throws exception the account isn't registered on the server
			 * so we'll register it
			 */
			try {
				reqNum = client.getRequest();
			} catch (Exception ex) {
				if (!client.createUserAccount())
					throw new RuntimeException("cannot create user account on server");
				reqNum = client.getRequest();
			}
			// check Trader Bob's nymID
			Messages.CheckUser checkUser = client.checkUser("SP8rPHc6GMRPL517UL5J8RK2yOiToyVqMaj3PUHvLzM", reqNum);
			// if user has credentials take his master
			if (checkUser.getContent().hasCredentials()) {
				String string = checkUser.getContent().getCredentialList().getUncompressed();
				// parse credentials list
				Messages.OTuser credentialList = Messages.parseResponse(string, Messages.OTuser.class);
				// and get master credential
				String masterCredential = checkUser.getContent().getCredentials()
						.get(credentialList.getMasterCredential().getID());
				System.out.println("got master credential\n" + masterCredential);
			} else { // he has only nymPublicKey
				System.out.println("got only public key\n" + checkUser.getContent().getNymPublicKey());
			}

		} finally {
			client.close();// close, else thread may hang
		}
	}
}
