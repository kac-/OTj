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
import com.kactech.otj.JeromqTransport;
import com.kactech.otj.MSG;
import com.kactech.otj.OT;
import com.kactech.otj.Utils;
import com.kactech.otj.model.UserAccount;
import com.kactech.otj.model.BasicUserAccount;

public class DirtyClientExample {
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	private static final Path dirtyExampleAccountPath = Paths.get("testData", "acc", "dirtyExample.ser");
	private static UserAccount testAccount;

	public static UserAccount getAccount() {
		synchronized (dirtyExampleAccountPath) {
			if (testAccount == null) {
				UserAccount acc = null;
				try {
					acc = (UserAccount) ExamplesUtils.deserializeJava(dirtyExampleAccountPath);
				} catch (Exception e) {
				}
				if (acc == null) {
					BasicUserAccount bacc = new BasicUserAccount();
					bacc.generate();
					ExamplesUtils.serializeJava(dirtyExampleAccountPath, bacc);
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

		UserAccount account = getAccount();
		System.out.println("client nymID: " + getAccount().getNymID());
		Client client = new Client(account, serverID, serverPublicKey, new JeromqTransport(
				serverEndpoint));
		return client;
	}

	public static void main(String[] args) throws Exception {
		Client client = buildClient();
		// send message to Trader Bob
		String recipientNymID = "SP8rPHc6GMRPL517UL5J8RK2yOiToyVqMaj3PUHvLzM";
		String message = "Subject: hello from Java\ngood morning!";
		try {
			Long reqNum;
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

			MSG.CheckUserResp cu = client.checkUser(recipientNymID);
			// if user has credentials take his nymIDSource, for public key
			RSAPublicKey recipientPublicKey;
			if (cu.getHasCredentials()) {
				OT.Pseudonym credentialList = cu.getCredentialList().getEntity();
				String nymIDSource = credentialList.getNymIDSource().getRaw();
				recipientPublicKey = (RSAPublicKey) Utils.fromIDSource(nymIDSource);
			} else {
				String str = Utils.unarmor(cu.getNymPublicKey(), true);
				byte[] packed = Utils.base64Decode(str);
				str = Utils.unpack(packed, String.class);
				recipientPublicKey = Utils.pemReadRSAPublicKey(str);
			}
			MSG.SendUserMessageResp msg = client.sendUserMessage(message,
					recipientNymID, recipientPublicKey);
			System.out.println("message sent: " + msg.getSuccess());

		} finally {
			client.close();// close, else thread may hang
		}
	}
}
