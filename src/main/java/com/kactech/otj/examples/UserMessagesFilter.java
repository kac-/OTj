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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kactech.otj.Client;
import com.kactech.otj.MSG;
import com.kactech.otj.MSG.GetBoxReceiptResp;
import com.kactech.otj.MSG.GetNymboxResp;
import com.kactech.otj.OT;
import com.kactech.otj.Utils;

public class UserMessagesFilter implements Client.Filter<MSG.GetNymboxResp> {
	private static final Logger logger = LoggerFactory.getLogger(UserMessagesFilter.class);

	public static class UserMessage {
		public String from;
		public String text;
	}

	List<UserMessage> messages = new ArrayList<UserMessage>();

	@Override
	public GetNymboxResp filter(GetNymboxResp obj, Client client) {
		try {
			for (OT.BoxRecord rec : obj.getNymboxLedger().getNymboxRecords())
				if (rec.getType() == OT.Transaction.Type.message) {
					GetBoxReceiptResp receipt = client.getBoxReceipt(obj.getNymID(), obj
							.getNymboxLedger()
							.getType(), rec.getTransactionNum());
					OT.Transaction box = receipt.getBoxReceipt();
					MSG.SendUserMessage send = ((MSG.Message) box.getInReferenceToContent())
							.getSendUserMessage();

					byte[] data = send.getMessagePayload().getData();
					try {
						UserMessage umsg = new UserMessage();
						umsg.from = send.getNymID();
						umsg.text = Utils.open(data, client.getUserAccount().getCpairs().get("E")
								.getPrivate());
						messages.add(umsg);
					} catch (Exception e) {
						logger.error("reading message", e);
					}
				}
		} catch (Exception ex) {
		}
		return obj;
	}

	@Override
	public int getMask() {
		return Client.EVENT_STD;
	}

	public List<UserMessage> getAndClearMessages() {
		List<UserMessage> msgs = new ArrayList<UserMessage>(messages);
		messages.clear();
		return msgs;
	}
}
