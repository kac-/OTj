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

import java.io.StringWriter;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.stringtemplate.v4.NoIndentWriter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.STWriter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.kactech.otj.model.Account;

public class RequestTemplates {
	public static String buildCreateUserAccountRequest(Account acc, String serverID)
			throws InvalidProtocolBufferException, InvalidKeyException, SignatureException {
		String str;
		byte[] by;

		Map<String, String> credentials = new HashMap<String, String>();

		STGroup group = new STGroupFile("com/kactech/otj/stg/createUserAccount.stg", "UTF-8", '$', '$');

		ST st = group.getInstanceOf("masterCredential");
		st.add("keys", acc.getSources());
		st.add("nymID", acc.getNymID());
		st.add("nymIDSource", acc.getNymIDSource());
		str = render(st);
		str = Utils.sign(str, acc.getPairs().get("S").getPrivate());
		String masterCredentialID = Utils.samy62(str.trim().getBytes(Utils.UTF8));//AHHH FUCK TRIM
		credentials.put(masterCredentialID, str);
		String masterPublic = Utils.base64EncodeString(Utils.zlibCompress(Utils.pack(str)), true);

		st = group.getInstanceOf("keyCredential");
		st.add("nymID", acc.getNymID());
		st.add("masterCredentialID", masterCredentialID);
		st.add("nymIDSource", acc.getNymIDSource());
		st.add("masterPublic", masterPublic);
		st.add("keys", acc.getCsources());
		str = render(st);
		str = Utils.sign(str, acc.getPairs().get("S").getPrivate());
		String masterSigned = Utils.base64EncodeString(Utils.zlibCompress(Utils.pack(str)), true);

		st = group.getInstanceOf("keyCredentialSigned");
		st.add("nymID", acc.getNymID());
		st.add("masterCredentialID", masterCredentialID);
		st.add("nymIDSource", acc.getNymIDSource());
		st.add("masterSigned", masterSigned);
		str = render(st);
		str = Utils.sign(str, acc.getCpairs().get("S").getPrivate());
		String keyCredentialID = Utils.samy62(str.trim().getBytes(Utils.UTF8));
		credentials.put(keyCredentialID, str);

		by = Utils.pack(credentials);
		String credentialsContent = Utils.base64EncodeString(by, true);

		st = group.getInstanceOf("credentialList");
		st.add("nymID", acc.getNymID());
		st.add("nymIDSource", acc.getNymIDSource());
		st.add("masterCredentialID", masterCredentialID);
		st.add("keyCredentialID", keyCredentialID);
		str = render(st);
		by = Utils.pack(str);
		by = Utils.zlibCompress(by);
		String credentialList = Utils.base64EncodeString(by, true);

		st = group.getInstanceOf("createUserAccount");
		st.add("requestNum", "1");
		st.add("nymID", acc.getNymID());
		st.add("serverID", serverID);
		st.add("credentialList", credentialList);
		st.add("credentials", credentialsContent);
		str = render(st);

		return str;
	}

	static String render(ST st) {
		StringWriter out = new StringWriter();
		STWriter wr;
		wr = new NoIndentWriter(out);
		wr.setLineWidth(STWriter.NO_WRAP);//have to check
		//wr = new AutoIndentWriter(out);
		//wr.setLineWidth(lineWidth);
		st.write(wr, Locale.getDefault());
		return out.toString();
	}

	public static String buildGetRequest(String nymID, String serverID) throws InvalidKeyException,
			SignatureException {
		StringBuilder b = new StringBuilder()
				.append("<?xml version=\"1.0\"?>\n")
				.append("<OTmessage version=\"1.0\">\n")
				.append("<getRequest\n")
				.append(" nymID=\"").append(nymID).append("\"\n")
				.append(" serverID=\"").append(serverID).append("\"\n")
				.append(" requestNum=\"1\">\n\n")
				.append("</getRequest>")
				.append("</OTmessage>\n");
		return b.toString();
	}

	public static String buildCheckUser(String nymID, String serverID, int requestNum, String nymID2) {
		return new StringBuilder()
				.append("<?xml version=\"1.0\"?>\n")
				.append("<OTmessage version=\"1.0\">\n")
				.append("<checkUser\n")
				.append(" nymID=\"").append(nymID).append("\"\n")
				.append(" serverID=\"").append(serverID).append("\"\n")
				.append(" requestNum=\"").append(requestNum).append("\"\n")
				.append(" nymID2=\"").append(nymID2).append("\">\n")
				.append("</checkUser>\n")
				.append("</OTmessage>\n")
				.toString();
	}
}
