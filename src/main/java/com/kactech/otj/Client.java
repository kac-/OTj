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

package com.kactech.otj;

import java.io.Closeable;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import com.kactech.otj.model.Account;
import com.kactech.otj.model.Signed;

public class Client implements Closeable {
	Account account;
	String serverID;
	String serverNymID;
	RSAPublicKey serverPublicKey;
	JeromqTranport transport;

	public String send(String unsigned) {
		try {
			String signed = Utils.sign(unsigned, account.getCpairs().get("A").getPrivate());
			byte[] bytes = Utils.seal(signed, serverNymID, serverPublicKey);
			bytes = transport.send(bytes);
			if (bytes == null)
				throw new RuntimeException("no response");
			String str = new String(bytes, Utils.US_ASCII);
			if (str.contains("ENVELOPE")) {
				str = Utils.unarmor(str, false);
				byte[] by = Utils.base64Decode(str);
				by = Utils.unpack(by, byte[].class);
				str = Utils.open(by, (RSAPrivateKey) account.getCpairs().get("E").getPrivate());
				Signed signedContent = Utils.parseSigned(str);
				return signedContent.getUnsigned();
			} else if (str.contains("MESSAGE")) {
				str = Utils.unAsciiArmor(str, false);//set of additional operations
				str = Utils.parseSigned(str).getUnsigned();
				//return str;
				// it's failure, so throw
				throw new RuntimeException("not encoded envelope:\n" + str);
			} else
				throw new RuntimeException("unknown message type:\n" + str);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Client(Account account, String serverID, RSAPublicKey serverPublicKey, JeromqTranport transport) {
		super();
		this.account = account;
		this.serverID = serverID;
		this.serverPublicKey = serverPublicKey;
		this.transport = transport;
		this.serverNymID = Utils.toNymID(serverPublicKey);
	}

	@Override
	public void close() throws IOException {
		transport.close();
	}

	public Account getAccount() {
		return account;
	}

	public String getServerID() {
		return serverID;
	}

	public String getServerNymID() {
		return serverNymID;
	}

	public RSAPublicKey getServerPublicKey() {
		return serverPublicKey;
	}

	public JeromqTranport getTransport() {
		return transport;
	}

	public int getRequest() {
		String str;
		try {
			str = RequestTemplates.buildGetRequest(account.getNymID(), serverID);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		str = send(str);
		Messages.GetRequest get = Messages.parseResponse(str, Messages.GetRequest.class);
		if (!get.getContent().isSuccess())
			throw new RuntimeException("request not succesful");
		return get.getContent().getNewRequestNum();
	}

	public boolean createUserAccount() {
		String req;
		try {
			req = RequestTemplates.buildCreateUserAccountRequest(account, serverID);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		String resp = send(req);
		Messages.CreateUserAccount create = Messages.parseResponse(resp, Messages.CreateUserAccount.class);
		return create.getContent().isSuccess();
	}

	public Messages.CheckUser checkUser(String nymID, Integer reqNum) {
		if (reqNum == null)
			reqNum = getRequest();
		String req = RequestTemplates.buildCheckUser(account.getNymID(), getServerID(), reqNum,
				nymID);
		String resp = send(req);
		return Messages.parseResponse(resp, Messages.CheckUser.class);
	}

}