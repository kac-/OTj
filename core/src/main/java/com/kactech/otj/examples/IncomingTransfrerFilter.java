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
import java.util.Map;
import java.util.TreeMap;

import com.kactech.otj.Client;
import com.kactech.otj.MSG;
import com.kactech.otj.OT;
import com.kactech.otj.OT.Transaction.Type;

public class IncomingTransfrerFilter implements Client.Filter<Object> {
	public static class Tx {
		public Long date;
		public String account;
		public Long amount;

		@Override
		public String toString() {
			return "Tx [date=" + date + ", account=" + account + ", amount=" + amount + "]";
		}

	}

	Map<Long, Tx> pending = new TreeMap<Long, IncomingTransfrerFilter.Tx>();

	Map<Long, Tx> acknowleded = new TreeMap<Long, IncomingTransfrerFilter.Tx>();

	@Override
	public Object filter(Object obj, Client client) {
		if (obj instanceof MSG.GetInboxResp) {
			MSG.GetInboxResp gir = (MSG.GetInboxResp) obj;
			if (!gir.getSuccess() || gir.getInboxLedger().getNumPartialRecords() == 0)
				return obj;
			//System.out.println(Engines.gson.toJson(obj));
			for (OT.BoxRecord rec : gir.getInboxLedger().getInboxRecords())
				if (rec.getType() == Type.pending) {
					MSG.GetBoxReceiptResp boxResp = client.getBoxReceipt(gir.getAccountID(),
							gir.getInboxLedger().getType(), rec.getTransactionNum());
					OT.Item it = (OT.Item) boxResp.getBoxReceipt().getInReferenceToContent();
					Tx tx = new Tx();
					tx.amount = it.getAmount();
					tx.account = it.getFromAccountID();
					tx.date = boxResp.getBoxReceipt().getDateSigned();
					pending.put(it.getTransactionNum(), tx);
					//System.out.println(Engines.gson.toJson(boxResp));

				}
		} else if (obj instanceof MSG.ProcessInboxResp) {
			MSG.ProcessInboxResp pir = (MSG.ProcessInboxResp) obj;
			if (!pir.getSuccess())
				return obj;
			//System.out.println(Engines.gson.toJson(obj));
			List<Long> acknowledged = new ArrayList<Long>();
			for (OT.Item item : pir.getResponseLedger().getTransactions().get(0).getItems())
				if (item.getType() == OT.Item.Type.atBalanceStatement
						&& item.getStatus() != OT.Item.Status.acknowledgement)
					return obj;
				else if (item.getType() == OT.Item.Type.atAcceptPending
						&& item.getStatus() == OT.Item.Status.acknowledgement) {
					acknowledged.add(item.getNumberOfOrigin());
				}
			for (Long l : acknowledged) {
				Tx tx = pending.remove(l);
				this.acknowleded.put(tx.date, tx);
			}
		}
		return obj;
	}

	@Override
	public int getMask() {
		return Client.EVENT_STD;
	}

	public List<Tx> getAndClearAcknowledged() {
		List<Tx> l = new ArrayList<Tx>(this.acknowleded.values());
		this.acknowleded.clear();
		return l;
	}
}
