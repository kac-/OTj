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

import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.google.gson.Gson;
import com.kactech.otj.JeromqTranport;
import com.kactech.otj.RequestTemplates;
import com.kactech.otj.Utils;
import com.kactech.otj.model.Account;
import com.kactech.otj.model.BasicAccount;
import com.kactech.otj.model.Signed;
/**
 * Generate account and register it on the server
 * @author Piotr Kopeć (kactech)
 *
 */
public class CreateUserAccountExample {

	public static void main(String[] args) throws Exception {
		// add BC for WHIRLPOOL and raw PSS signatures
		Security.addProvider(new BouncyCastleProvider());

		// JSON serialized test-server RSAPublicKeySpec
		String serverKeyString = "{\"modulus\":157308124954637849769808055227589301287752744825558530150220825924565496309952110169467763525063683140481597445838436176231389850954363355832747286996253525968666484844394188835285816520152462516072618852511394863800122669965310355610221431146947161206966593783544166481213230593202598098997296223206388439283,\"publicExponent\":65537}";
		RSAPublicKey serverPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(
				new Gson().fromJson(serverKeyString, RSAPublicKeySpec.class));
		// generate serverNymID from public key
		String serverNymID = Utils.toNymID(serverPublicKey);
		String serverID = "tBy5mL14qSQXCJK7Uz3WlTOKRP9M0JZksA3Eg7EnnQ1";
		// server ZMQ endpoint
		String serverEndpoint = "tcp://localhost:7085";

		// generate account
		Account account = new BasicAccount().generate();

		String unsigned = RequestTemplates.buildCreateUserAccountRequest(account, serverID);
		System.out.println("---- request ----\n" + unsigned + "---- !request ----");
		String signed = Utils.sign(unsigned, account.getCpairs().get("A").getPrivate());

		// seal message
		byte[] envelope = Utils.seal(signed, serverNymID, serverPublicKey);

		JeromqTranport zmq = new JeromqTranport(serverEndpoint);
		byte[] response = zmq.send(envelope);
		zmq.close();

		if (response == null)
			System.out.println("no reply");
		else {
			String str = new String(response, Utils.US_ASCII);
			/*
			 handle
			 -----BEGIN OT ARMORED MESSAGE-----
			 or
			 -----BEGIN OT ARMORED ENVELOPE-----
			 */
			if (str.contains("ENVELOPE")) {
				str = Utils.unarmor(str, false);
				byte[] by = Utils.base64Decode(str);
				by = Utils.unpack(by, byte[].class);
				str = Utils.open(by, (RSAPrivateKey) account.getCpairs().get("E").getPrivate());
				Signed signedContent = Utils.parseSigned(str);
				str = signedContent.getUnsigned();
			} else if (str.contains("MESSAGE")) {
				str = Utils.unAsciiArmor(str, false);//set of additional operations
				str = Utils.parseSigned(str).getUnsigned();
			} else
				throw new RuntimeException("don't know what is it\n" + str);
			System.out.println("---- reply ----\n" + str + "---- !reply ----");
		}

	}
}
