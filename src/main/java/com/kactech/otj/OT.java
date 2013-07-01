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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.protobuf.InvalidProtocolBufferException;
import com.kactech.otj.MSG.AsciiEntity;
import com.kactech.otj.Utils.PackerException;
import com.kactech.otj.model.BasicSigningSupport;
import com.kactech.otj.model.XmlEntity;
import com.kactech.otj.model.annot.GsonExclude;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class OT {
	/*
	transaction
	- accountID
	- dateSigned
	- inReferenceTo
	- serverID
	- totalListOfNumbers
	- transactionNum
	- type
	- userID
	-- inReferenceTo
	-- item
	-- transaction

	item
	- amount
	- fromAccountID
	- inReferenceTo
	- outboxNewTransNum
	- serverID
	- status
	- toAccountID
	- totalListOfNumbers
	- transactionNum
	- type
	- userID
	-- attachment
	-- inReferenceTo
	-- item
	-- note
	-- transactionReport

	accountLedger
	- accountID
	- numPartialRecords
	- serverID
	- type
	- userID
	- version
	-- accountLedger
	-- inboxRecord
	-- nymboxRecord
	-- outboxRecord
	-- transaction

	nymboxRecord
	- dateSigned 
	- inRefDisplay
	- inReferenceTo
	- receiptHash
	- requestNumber
	- totalListOfNumbers +
	- transSuccess +
	- transactionNum
	- type

	inboxRecord
	- adjustment 
	- closingNum +outbox
	- dateSigned
	- displayValue
	- inRefDisplay
	- inReferenceTo
	- receiptHash
	- transactionNum
	- type

	outboxRecord
	- adjustment
	- dateSigned
	- displayValue
	- inRefDisplay
	- inReferenceTo
	- receiptHash
	- transactionNum
	- type

	transactionReport
	- accountID
	- adjustment
	- closingTransactionNum
	- inReferenceTo
	- serverID
	- transactionNum
	- type
	- userID
	
	assetAccount
	- accountID
	- assetTypeID
	- serverID
	- type
	- userID
	- version
	-- assetAccount
	-- balance
	-- inboxHash
	-- outboxHash

	 */

	public static class BoxRecord {
		@XStreamAsAttribute
		Long adjustment;
		@XStreamAsAttribute
		Long closingNum;//inbox
		@XStreamAsAttribute
		Long dateSigned;
		@XStreamAsAttribute
		Long displayValue;
		@XStreamAsAttribute
		Long inRefDisplay;
		@XStreamAsAttribute
		Long inReferenceTo;
		@XStreamAsAttribute
		String receiptHash;
		@XStreamAsAttribute
		Long requestNumber;
		@XStreamAsAttribute
		NumListAttribute totalListOfNumbers;
		@XStreamAsAttribute
		Boolean transSuccess;
		@XStreamAsAttribute
		Long transactionNum;
		@XStreamAsAttribute
		Transaction.Type type;

		// get/set

		public Long getAdjustment() {
			return adjustment;
		}

		public void setAdjustment(Long adjustment) {
			this.adjustment = adjustment;
		}

		public Long getClosingNum() {
			return closingNum;
		}

		public void setClosingNum(Long closingNum) {
			this.closingNum = closingNum;
		}

		public Long getDateSigned() {
			return dateSigned;
		}

		public void setDateSigned(Long dateSigned) {
			this.dateSigned = dateSigned;
		}

		public Long getDisplayValue() {
			return displayValue;
		}

		public void setDisplayValue(Long displayValue) {
			this.displayValue = displayValue;
		}

		public Long getInRefDisplay() {
			return inRefDisplay;
		}

		public void setInRefDisplay(Long inRefDisplay) {
			this.inRefDisplay = inRefDisplay;
		}

		public Long getInReferenceTo() {
			return inReferenceTo;
		}

		public void setInReferenceTo(Long inReferenceTo) {
			this.inReferenceTo = inReferenceTo;
		}

		public String getReceiptHash() {
			return receiptHash;
		}

		public void setReceiptHash(String receiptHash) {
			this.receiptHash = receiptHash;
		}

		public Long getRequestNumber() {
			return requestNumber;
		}

		public void setRequestNumber(Long requestNumber) {
			this.requestNumber = requestNumber;
		}

		public NumListAttribute getTotalListOfNumbers() {
			return totalListOfNumbers;
		}

		public void setTotalListOfNumbers(NumListAttribute totalListOfNumbers) {
			this.totalListOfNumbers = totalListOfNumbers;
		}

		public Boolean getTransSuccess() {
			return transSuccess;
		}

		public void setTransSuccess(Boolean transSuccess) {
			this.transSuccess = transSuccess;
		}

		public Long getTransactionNum() {
			return transactionNum;
		}

		public void setTransactionNum(Long transactionNum) {
			this.transactionNum = transactionNum;
		}

		public Transaction.Type getType() {
			return type;
		}

		public void setType(Transaction.Type type) {
			this.type = type;
		}

	}

	public static class Contract extends BasicSigningSupport implements XmlEntity {
		String name;
		String ID;
		String contractType;
		// Map<String,Nym> nyms
		@XStreamAsAttribute
		String version;
		Entity entity;
		@XStreamImplicit(itemFieldName = "condition")
		List<NamedText> conditions;

		// get/set

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getID() {
			return ID;
		}

		public void setID(String iD) {
			ID = iD;
		}

		public String getContractType() {
			return contractType;
		}

		public void setContractType(String contractType) {
			this.contractType = contractType;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public Entity getEntity() {
			return entity;
		}

		public void setEntity(Entity entity) {
			this.entity = entity;
		}

		public List<NamedText> getConditions() {
			return conditions;
		}

		public void setConditions(List<NamedText> conditions) {
			this.conditions = conditions;
		}

	}

	public static class TransactionType extends OT.Contract {
		@XStreamAsAttribute
		String accountID;
		@XStreamAsAttribute
		String serverID;
		@XStreamAsAttribute
		String userID;
		@XStreamAsAttribute
		Long transactionNum;
		@XStreamAsAttribute
		Long inReferenceTo;
		@XStreamAlias("inReferenceTo")
		OT.Contract inReferenceToContent;
		//String inReferenceToContent;
		OT.NumList numList; //TODO not used as data part, probably needs to be removed

		// get/set

		public String getAccountID() {
			return accountID;
		}

		public void setAccountID(String accountID) {
			this.accountID = accountID;
		}

		public String getServerID() {
			return serverID;
		}

		public void setServerID(String serverID) {
			this.serverID = serverID;
		}

		public String getUserID() {
			return userID;
		}

		public void setUserID(String userID) {
			this.userID = userID;
		}

		public Long getTransactionNum() {
			return transactionNum;
		}

		public void setTransactionNum(Long transactionNum) {
			this.transactionNum = transactionNum;
		}

		public Long getInReferenceTo() {
			return inReferenceTo;
		}

		public void setInReferenceTo(Long inReferenceTo) {
			this.inReferenceTo = inReferenceTo;
		}

		public OT.Contract getInReferenceToContent() {
			return inReferenceToContent;
		}

		public void setInReferenceToContent(OT.Contract inReferenceToContent) {
			this.inReferenceToContent = inReferenceToContent;
		}

		public OT.NumList getNumList() {
			return numList;
		}

		public void setNumList(OT.NumList numList) {
			this.numList = numList;
		}
	}

	@XStreamAlias("accountLedger")
	public static class Ledger extends OT.TransactionType {
		@XStreamImplicit
		List<OT.Transaction> transactions;
		@XStreamImplicit(itemFieldName = "nymboxRecord")
		List<OT.BoxRecord> nymboxRecords;
		@XStreamImplicit(itemFieldName = "inboxRecord")
		List<OT.BoxRecord> inboxRecords;
		@XStreamImplicit(itemFieldName = "outboxRecord")
		List<OT.BoxRecord> outboxRecords;
		@XStreamAsAttribute
		Ledger.Type type;
		@XStreamAsAttribute
		Integer numPartialRecords;
		@XStreamOmitField
		//TODO
		Boolean loadedLegacyData;

		enum Type {
			nymbox, // the nymbox is per user account (versus per asset account) and is used to receive new transaction numbers (and messages.)
			inbox, // each asset account has an inbox, with pending transfers as well as receipts inside.
			outbox, // if you SEND a pending transfer, it sits in your outbox until it's accepted, rejected, or canceled.
			message, // used in OTMessages, to send various lists of transactions back and forth.
			paymentInbox, // Used for client-side-only storage of incoming cheques, invoices, payment plan requests, etc. (Coming in from the Nymbox.)
			recordBox, // Used for client-side-only storage of completed items from the inbox, and the paymentInbox.
			error_state
		}

		// get/set 

		public List<OT.Transaction> getTransactions() {
			return transactions;
		}

		public void setTransactions(List<OT.Transaction> transactions) {
			this.transactions = transactions;
		}

		public List<OT.BoxRecord> getNymboxRecords() {
			return nymboxRecords;
		}

		public void setNymboxRecords(List<OT.BoxRecord> nymboxRecords) {
			this.nymboxRecords = nymboxRecords;
		}

		public List<OT.BoxRecord> getInboxRecords() {
			return inboxRecords;
		}

		public void setInboxRecords(List<OT.BoxRecord> inboxRecords) {
			this.inboxRecords = inboxRecords;
		}

		public List<OT.BoxRecord> getOutboxRecords() {
			return outboxRecords;
		}

		public void setOutboxRecords(List<OT.BoxRecord> outboxRecords) {
			this.outboxRecords = outboxRecords;
		}

		public Ledger.Type getType() {
			return type;
		}

		public void setType(Ledger.Type type) {
			this.type = type;
		}

		public Integer getNumPartialRecords() {
			return numPartialRecords;
		}

		public void setNumPartialRecords(Integer numPartialRecords) {
			this.numPartialRecords = numPartialRecords;
		}

		public Boolean getLoadedLegacyData() {
			return loadedLegacyData;
		}

		public void setLoadedLegacyData(Boolean loadedLegacyData) {
			this.loadedLegacyData = loadedLegacyData;
		};
	}

	/*
	transaction
	- accountID
	- dateSigned
	- inReferenceTo
	- serverID
	- totalListOfNumbers
	- transactionNum
	- type
	- userID
	-- inReferenceTo
	-- item
	-- transaction
	 */
	@XStreamAlias("transaction")
	public static class Transaction extends OT.TransactionType {
		//Ledger parent;
		@XStreamAsAttribute
		Long dateSigned;
		@XStreamAsAttribute
		Transaction.Type type;
		@XStreamImplicit(itemFieldName = "item")
		List<OT.Item> items;
		@XStreamAsAttribute
		NumListAttribute totalListOfNumbers;//TODO is it in item.. i'm confused

		public enum Type
		{
			// ***** INBOX / OUTBOX / NYMBOX

			// --------------------------------------------------------------------------------------
			// NYMBOX
			blank, // freshly issued transaction number, not used yet
			// (the server drops these into the nymbox.)
			message, // A message from one user to another, also in the nymbox.
			notice, // A notice from the server. Used in Nymbox.
			replyNotice, // A copy of a server reply to a previous request you sent. (To make SURE you get the reply.)
			successNotice, // A transaction # has successfully been signed out.
			// --------------------------------------------------------------------------------------

			// INBOX / OUTBOX (pending transfer)
			pending, // Server puts this in your outbox (when sending) and recipient's inbox.

			// INBOX / receipts
			transferReceipt, // the server drops this into your inbox, when someone accepts your transfer.

			chequeReceipt, // the server drops this into your inbox, when someone cashes your cheque.
			marketReceipt, // server periodically drops this into your inbox if an offer is live.
			paymentReceipt, // the server drops this into people's inboxes, every time a payment processes. (from a payment plan or a smart contract)

			finalReceipt, // the server drops this into your in/nym box(es), when a CronItem expires or is canceled.
			basketReceipt, // the server drops this into your inboxes, when a basket exchange is processed.
			// --------------------------------------------------------------------------------------

			// PAYMENT INBOX / PAYMENT OUTBOX / RECORD BOX

			instrumentNotice, // Receive these in paymentInbox (by way of Nymbox), and send in Outpayments (like Outmail).) (When done, they go to recordBox to await deletion.)
			instrumentRejection, // When someone rejects your invoice from his paymentInbox, you get one of these in YOUR paymentInbox.

			// --------------------------------------------------------------------------------------

			// **** MESSAGES ****

			processNymbox, // process nymbox transaction // comes from client
			atProcessNymbox, // process nymbox reply // comes from server
			// --------------------------------------------------------------------------------------
			processInbox, // process inbox transaction // comes from client
			atProcessInbox, // process inbox reply // comes from server
			// --------------------------------------------------------------------------------------
			transfer, // or "spend". This transaction is a request to transfer from one account to another
			atTransfer, // reply from the server regarding a transfer request
			// --------------------------------------------------------------------------------------
			deposit, // this transaction is a deposit (cash or cheque)
			atDeposit, // reply from the server regarding a deposit request
			// --------------------------------------------------------------------------------------
			withdrawal, // this transaction is a withdrawal (cash or voucher)
			atWithdrawal, // reply from the server regarding a withdrawal request
			// --------------------------------------------------------------------------------------
			marketOffer, // this transaction is a market offer
			atMarketOffer, // reply from the server regarding a market offer
			// --------------------------------------------------------------------------------------
			paymentPlan, // this transaction is a payment plan
			atPaymentPlan, // reply from the server regarding a payment plan
			// --------------------------------------------------------------------------------------
			smartContract, // this transaction is a smart contract
			atSmartContract, // reply from the server regarding a smart contract
			// --------------------------------------------------------------------------------------
			cancelCronItem, // this transaction is intended to cancel a market offer or payment plan.
			atCancelCronItem, // reply from the server regarding said cancellation.
			// --------------------------------------------------------------------------------------
			exchangeBasket, // this transaction is an exchange in/out of a basket currency.
			atExchangeBasket, // reply from the server regarding said exchange.
			// --------------------------------------------------------------------------------------
			payDividend, // this transaction is dividend payment (to all shareholders...)
			atPayDividend, // reply from the server regarding said dividend payment.
			// --------------------------------------------------------------------------------------
			error_state
		}

		// get/set

		public Long getDateSigned() {
			return dateSigned;
		}

		public void setDateSigned(Long dateSigned) {
			this.dateSigned = dateSigned;
		}

		public Transaction.Type getType() {
			return type;
		}

		public void setType(Transaction.Type type) {
			this.type = type;
		}

		public List<OT.Item> getItems() {
			return items;
		}

		public void setItems(List<OT.Item> items) {
			this.items = items;
		}

		public NumListAttribute getTotalListOfNumbers() {
			return totalListOfNumbers;
		}

		public void setTotalListOfNumbers(NumListAttribute totalListOfNumbers) {
			this.totalListOfNumbers = totalListOfNumbers;
		};

	}

	/*
	 item
		- amount
		- fromAccountID
		- inReferenceTo
		- outboxNewTransNum
		- serverID
		- status
		- toAccountID
		- totalListOfNumbers
		- transactionNum
		- type
		- userID
		-- attachment
		-- inReferenceTo
		-- item
		-- note
		-- transactionReport
	 */
	@XStreamAlias("item")
	public static class Item extends OT.TransactionType {
		@XStreamAsAttribute
		Long amount;
		@XStreamAsAttribute
		//TODO handle it, fuck
		String fromAccountID;
		@XStreamAsAttribute
		Long outboxNewTransNum;
		@XStreamAsAttribute
		Item.Status status;
		@XStreamAsAttribute
		String toAccountID;
		@XStreamAsAttribute
		NumListAttribute totalListOfNumbers;//TODO is it in Transaction?
		@XStreamAsAttribute
		Item.Type type;

		OT.ArmoredString note;
		OT.ArmoredString attachment;
		@XStreamImplicit(itemFieldName = "item")
		List<Item> items;
		@XStreamImplicit(itemFieldName = "transactionReport")
		List<TransactionReport> transactionReport;

		public enum Type
		{
			// ------------------------------------------------------------------------------
			// TRANSFER
			transfer, // this item is an outgoing transfer, probably part of an outoing transaction.
			atTransfer, // Server reply.
			// ------------------------------------------------------------------------------

			// NYMBOX RESOLUTION

			acceptTransaction, // this item is a client-side acceptance of a transaction number (a blank) in my Nymbox
			atAcceptTransaction,
			acceptMessage, // this item is a client-side acceptance of a message in my Nymbox
			atAcceptMessage,
			acceptNotice, // this item is a client-side acceptance of a server notification in my Nymbox
			atAcceptNotice,

			// ------------------------------------------------------------------------------

			// INBOX RESOLUTION

			acceptPending, // this item is a client-side acceptance of a pending transfer
			atAcceptPending,
			rejectPending, // this item is a client-side rejection of a pending transfer
			atRejectPending,

			// RECEIPT ACKNOWLEDGMENT / DISPUTE
			acceptCronReceipt, // this item is a client-side acceptance of a cron receipt in his inbox.
			atAcceptCronReceipt, // this item is a server reply to that acceptance.

			acceptItemReceipt, // this item is a client-side acceptance of an item receipt in his inbox.
			atAcceptItemReceipt, // this item is a server reply to that acceptance.

			disputeCronReceipt, // this item is a client dispute of a cron receipt in his inbox.
			atDisputeCronReceipt, // Server reply to dispute message.

			disputeItemReceipt, // this item is a client dispute of an item receipt in his inbox.
			atDisputeItemReceipt, // Server reply to dispute message.

			// Sometimes the attachment will be an OTItem, and sometimes it will be
			// an OTPaymentPlan or OTTrade. These different types above help the
			// code to differentiate.
			// --------------------------------------------

			acceptFinalReceipt, // this item is a client-side acceptance of a final receipt in his inbox. (All related receipts must also be closed!)
			atAcceptFinalReceipt, // server reply

			acceptBasketReceipt, // this item is a client-side acceptance of a basket receipt in his inbox.
			atAcceptBasketReceipt, // server reply

			disputeFinalReceipt, // this item is a client-side rejection of a final receipt in his inbox. (All related receipts must also be closed!)
			atDisputeFinalReceipt, // server reply

			disputeBasketReceipt, // this item is a client-side rejection of a basket receipt in his inbox.
			atDisputeBasketReceipt, // server reply

			// ------------------------------------------------------------------------------

			// FEEs
			serverfee, // this item is a fee from the transaction server (per contract)
			atServerfee,
			issuerfee, // this item is a fee from the issuer (per contract)
			atIssuerfee,
			// ------------------------------------------------------------------------------
			// INFO (BALANCE, HASH, etc) these are still all messages with replies.
			balanceStatement, // this item is a statement of balance. (For asset account.)
			atBalanceStatement,
			transactionStatement, // this item is a transaction statement. (For Nym -- which numbers are assigned to him.)
			atTransactionStatement,
			// ------------------------------------------------------------------------------
			// CASH WITHDRAWAL / DEPOSIT
			withdrawal, // this item is a cash withdrawal (of chaumian blinded tokens)
			atWithdrawal,
			deposit, // this item is a cash deposit (of a purse containing blinded tokens.)
			atDeposit,
			// ------------------------------------------------------------------------------
			// CHEQUES AND VOUCHERS
			withdrawVoucher, // this item is a request to purchase a voucher (a cashier's cheque)
			atWithdrawVoucher,
			depositCheque, // this item is a request to deposit a cheque
			atDepositCheque, // this item is a server response to that request.
			// ------------------------------------------------------------------------------
			// PAYING DIVIDEND ON SHARES OF STOCK
			payDividend, // this item is a request to pay a dividend.
			atPayDividend, // the server reply to that request.
			// ------------------------------------------------------------------------------
			// TRADING ON MARKETS
			marketOffer, // this item is an offer to be put on a market.
			atMarketOffer, // server reply or updated notification regarding a market offer.
			// ------------------------------------------------------------------------------
			// PAYMENT PLANS
			paymentPlan, // this item is a new payment plan
			atPaymentPlan, // server reply or updated notification regarding a payment plan.
			// ------------------------------------------------------------------------------
			// SMART CONTRACTS
			smartContract, // this item is a new smart contract
			atSmartContract, // server reply or updated notification regarding a smart contract.
			// ------------------------------------------------------------------------------
			// CANCELLING: Market Offers and Payment Plans.
			cancelCronItem, // this item is intended to cancel a market offer or payment plan.
			atCancelCronItem, // reply from the server regarding said cancellation.
			// --------------------------------------------------------------------------------------
			// EXCHANGE IN/OUT OF A BASKET CURRENCY
			exchangeBasket, // this item is an exchange in/out of a basket currency.
			atExchangeBasket, // reply from the server regarding said exchange.
			// ------------------------------------------------------------------------------
			// Now these three receipts have a dual use: as the receipts in the inbox, and also
			// as the representation for transactions in the inbox report (for balance statements.)
			// Actually chequeReceipt is ONLY used for inbox report, and otherwise is not actually
			// needed for real cheque receipts. marketReceipt and paymentReceipt are used as real
			// receipts, and also in inbox reports to represent transaction items in an inbox.
			chequeReceipt, // Currently don't create an OTItem for cheque receipt in inbox. Not needed.
			// I also don't create one for the transfer receipt, currently.
			// (Although near the top, I do have item types to go in a processInbox message and
			// clear those transaction types out of my inbox.)
			marketReceipt, // server receipt dropped into inbox as result of market trading.
			paymentReceipt, // server receipt dropped into an inbox as result of payment occuring.
			transferReceipt, // server receipt dropped into an inbox as result of transfer being accepted.
			// ------------------------------------------------------------------------------
			finalReceipt, // server receipt dropped into inbox / nymbox as result of cron item expiring or being canceled.
			basketReceipt, // server receipt dropped into inbox as result of a basket exchange.
			// ------------------------------------------------------------------------------
			replyNotice, // server notice of a reply that nym should have already received as a response to a request.
			// (Some are so important, a copy of the server reply is dropped to your nymbox, to make SURE you got it and processed it.)
			successNotice, // server notice dropped into nymbox as result of a transaction# being successfully signed out.
			notice, // server notice dropped into nymbox as result of a smart contract processing.
			// Also could be used for ballots / elections, corporate meetings / minutes, etc.
			// finalReceipt is also basically a notice (in the Nymbox, anyway) but it still is
			// information that you have to act on as soon as you receive it, whereas THIS kind
			// of notice isn't so hardcore. It's more laid-back.
			// ------------------------------------------------------------------------------
			error_state // error state versus error status
		};

		// FOR EXAMPLE: A client may send a TRANSFER request, setting type to Transfer and status to Request.
		// The server may respond with type atTransfer and status Acknowledgment.
		// Make sense?

		public enum Status
		{
			request, // This item is a request from the client
			acknowledgement, // This item is an acknowledgment from the server. (The server has signed it.)
			rejection, // This item represents a rejection of the request by the server. (Server will not sign it.)
			error_status // error status versus error state
		};

		// get/set

		public Long getAmount() {
			return amount;
		}

		public void setAmount(Long amount) {
			this.amount = amount;
		}

		public String getFromAccountID() {
			return fromAccountID;
		}

		public void setFromAccountID(String fromAccountID) {
			this.fromAccountID = fromAccountID;
		}

		public Long getOutboxNewTransNum() {
			return outboxNewTransNum;
		}

		public void setOutboxNewTransNum(Long outboxNewTransNum) {
			this.outboxNewTransNum = outboxNewTransNum;
		}

		public Item.Status getStatus() {
			return status;
		}

		public void setStatus(Item.Status status) {
			this.status = status;
		}

		public String getToAccountID() {
			return toAccountID;
		}

		public void setToAccountID(String toAccountID) {
			this.toAccountID = toAccountID;
		}

		public NumListAttribute getTotalListOfNumbers() {
			return totalListOfNumbers;
		}

		public void setTotalListOfNumbers(NumListAttribute totalListOfNumbers) {
			this.totalListOfNumbers = totalListOfNumbers;
		}

		public Item.Type getType() {
			return type;
		}

		public void setType(Item.Type type) {
			this.type = type;
		}

		public OT.ArmoredString getNote() {
			return note;
		}

		public void setNote(OT.ArmoredString note) {
			this.note = note;
		}

		public OT.ArmoredString getAttachment() {
			return attachment;
		}

		public void setAttachment(OT.ArmoredString attachment) {
			this.attachment = attachment;
		}

		public List<OT.Item> getItems() {
			return items;
		}

		public void setItems(List<OT.Item> items) {
			this.items = items;
		}

		public List<OT.TransactionReport> getTransactionReport() {
			return transactionReport;
		}

		public void setTransactionReport(List<OT.TransactionReport> transactionReport) {
			this.transactionReport = transactionReport;
		}

	}

	/*
	 assetAccount
	- accountID
	- assetTypeID
	- serverID
	- type
	- userID
	- version
	-- assetAccount
	-- balance
	-- inboxHash
	-- outboxHash
	 */
	@XStreamAlias("assetAccount")
	public static class Account extends TransactionType {
		@XStreamAsAttribute
		String assetTypeID;
		@XStreamAsAttribute
		Type type;

		Hash inboxHash;
		Hash outboxHash;
		Balance balance;

		public static enum Type {
			simple, // used by users
			issuer, // used by issuers      (these can only go negative.)
			basket, // issuer acct used by basket currencies (these can only go negative)
			basketsub, // used by the server (to store backing reserves for basket sub-accounts)
			mint, // used by mints (to store backing reserves for cash)
			voucher, // used by the server (to store backing reserves for vouchers)
			stash, // used by the server (to store backing reserves for stashes, for smart contracts.)
			err_acct
		}

		// get/set

		public String getAssetTypeID() {
			return assetTypeID;
		}

		public void setAssetTypeID(String assetTypeID) {
			this.assetTypeID = assetTypeID;
		}

		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		public Hash getInboxHash() {
			return inboxHash;
		}

		public void setInboxHash(Hash inboxHash) {
			this.inboxHash = inboxHash;
		}

		public Hash getOutboxHash() {
			return outboxHash;
		}

		public void setOutboxHash(Hash outboxHash) {
			this.outboxHash = outboxHash;
		}

		public Balance getBalance() {
			return balance;
		}

		public void setBalance(Balance balance) {
			this.balance = balance;
		}
	}

	@XStreamAlias("OTuser")
	public static class User implements XmlEntity {
		@XStreamAsAttribute
		String nymID;
		@XStreamAsAttribute
		Version version;
		NumList transactionNums;
		NumList issuedNums;
		ArmoredString nymIDSource;
		RequestNum requestNum;
		@XStreamImplicit(itemFieldName = "ownsAssetAcct")
		List<Identifier> assetAccounts;
		Hash nymboxHash;
		CredentialIdentifier masterCredential;
		CredentialIdentifier keyCredential;

		@Override
		public String toString() {
			return "User [nymID=" + nymID + ", transactionNums=" + transactionNums + ", issuedNums="
					+ issuedNums + "]";
		}

		//TODO multiple servers
		public void addAllNums(String serverID, List<Long> num) {
			if (transactionNums == null)
				transactionNums = new NumList();
			for (Long l : num)
				if (!transactionNums.contains(l))
					transactionNums.add(l);
			transactionNums.serverID = serverID;
			if (issuedNums == null)
				issuedNums = new NumList();
			for (Long l : num)
				if (!issuedNums.contains(l))
					issuedNums.add(l);
			issuedNums.serverID = serverID;
		}

		// get/set

		public String getNymID() {
			return nymID;
		}

		public void setNymID(String nymID) {
			this.nymID = nymID;
		}

		public Version getVersion() {
			return version;
		}

		public void setVersion(Version version) {
			this.version = version;
		}

		public NumList getTransactionNums() {
			return transactionNums;
		}

		public void setTransactionNums(NumList transactionNums) {
			this.transactionNums = transactionNums;
		}

		public NumList getIssuedNums() {
			return issuedNums;
		}

		public void setIssuedNums(NumList issuedNums) {
			this.issuedNums = issuedNums;
		}

		public ArmoredString getNymIDSource() {
			return nymIDSource;
		}

		public void setNymIDSource(ArmoredString nymIDSource) {
			this.nymIDSource = nymIDSource;
		}

		public RequestNum getRequestNum() {
			return requestNum;
		}

		public void setRequestNum(RequestNum requestNum) {
			this.requestNum = requestNum;
		}

		public List<Identifier> getAssetAccounts() {
			return assetAccounts;
		}

		public void setAssetAccounts(List<Identifier> assetAccounts) {
			this.assetAccounts = assetAccounts;
		}

		public Hash getNymboxHash() {
			return nymboxHash;
		}

		public void setNymboxHash(Hash nymboxHash) {
			this.nymboxHash = nymboxHash;
		}

		public CredentialIdentifier getMasterCredential() {
			return masterCredential;
		}

		public void setMasterCredential(CredentialIdentifier masterCredential) {
			this.masterCredential = masterCredential;
		}

		public CredentialIdentifier getKeyCredential() {
			return keyCredential;
		}

		public void setKeyCredential(CredentialIdentifier keyCredential) {
			this.keyCredential = keyCredential;
		}

	}

	public static class CredentialIdentifier {
		@XStreamAsAttribute
		String ID;
		@XStreamAsAttribute
		String masterID;
		@XStreamAsAttribute
		Boolean valid;

		public CredentialIdentifier() {
		}

		public CredentialIdentifier(String iD, String masterID, Boolean valid) {
			super();
			ID = iD;
			this.masterID = masterID;
			this.valid = valid;
		}

		// get/set

		public String getID() {
			return ID;
		}

		public void setID(String iD) {
			ID = iD;
		}

		public String getMasterID() {
			return masterID;
		}

		public void setMasterID(String masterID) {
			this.masterID = masterID;
		}

		public Boolean getValid() {
			return valid;
		}

		public void setValid(Boolean valid) {
			this.valid = valid;
		}

	}

	@XStreamAlias("masterCredential")
	public static class MasterCredential extends Contract {
		@XStreamAsAttribute
		String nymID;
		ArmoredString nymIDSource;
		PublicContents publicContents;

		// get/set

		public String getNymID() {
			return nymID;
		}

		public void setNymID(String nymID) {
			this.nymID = nymID;
		}

		public ArmoredString getNymIDSource() {
			return nymIDSource;
		}

		public void setNymIDSource(ArmoredString nymIDSource) {
			this.nymIDSource = nymIDSource;
		}

		public PublicContents getPublicContents() {
			return publicContents;
		}

		public void setPublicContents(PublicContents publicContents) {
			this.publicContents = publicContents;
		}
	}

	@XStreamAlias("keyCredential")
	public static class KeyCredential extends MasterCredential {
		@XStreamAsAttribute
		String masterCredentialID;
		KeyCredential masterSigned;
		MasterCredential masterPublic;

		// get/set

		public String getMasterCredentialID() {
			return masterCredentialID;
		}

		public void setMasterCredentialID(String masterCredentialID) {
			this.masterCredentialID = masterCredentialID;
		}

		public KeyCredential getMasterSigned() {
			return masterSigned;
		}

		public void setMasterSigned(KeyCredential masterSigned) {
			this.masterSigned = masterSigned;
		}

		public MasterCredential getMasterPublic() {
			return masterPublic;
		}

		public void setMasterPublic(MasterCredential masterPublic) {
			this.masterPublic = masterPublic;
		}

	}

	/*
	 transactionReport
	- accountID
	- adjustment
	- closingTransactionNum
	- inReferenceTo
	- serverID
	- transactionNum
	- type
	- userID
	 */
	@XStreamAlias("transactionReport")
	public static class TransactionReport {
		@XStreamAsAttribute
		String serverID;
		@XStreamAsAttribute
		String accountID;
		@XStreamAsAttribute
		String userID;

		@XStreamAsAttribute
		Long adjustment;
		@XStreamAsAttribute
		Long closingTransactionNum;
		@XStreamAsAttribute
		Long inReferenceTo;
		@XStreamAsAttribute
		Long transactionNum;
		@XStreamAsAttribute
		Item.Type type;

		// get/set

		public String getServerID() {
			return serverID;
		}

		public void setServerID(String serverID) {
			this.serverID = serverID;
		}

		public String getAccountID() {
			return accountID;
		}

		public void setAccountID(String accountID) {
			this.accountID = accountID;
		}

		public String getUserID() {
			return userID;
		}

		public void setUserID(String userID) {
			this.userID = userID;
		}

		public Long getAdjustment() {
			return adjustment;
		}

		public void setAdjustment(Long adjustment) {
			this.adjustment = adjustment;
		}

		public Long getClosingTransactionNum() {
			return closingTransactionNum;
		}

		public void setClosingTransactionNum(Long closingTransactionNum) {
			this.closingTransactionNum = closingTransactionNum;
		}

		public Long getInReferenceTo() {
			return inReferenceTo;
		}

		public void setInReferenceTo(Long inReferenceTo) {
			this.inReferenceTo = inReferenceTo;
		}

		public Long getTransactionNum() {
			return transactionNum;
		}

		public void setTransactionNum(Long transactionNum) {
			this.transactionNum = transactionNum;
		}

		public Item.Type getType() {
			return type;
		}

		public void setType(Item.Type type) {
			this.type = type;
		}

	}

	@XStreamAlias("publicContents")
	public static class PublicContents {
		@XStreamAsAttribute
		Integer count;//TODO make it implicit
		@XStreamImplicit(itemFieldName = "publicInfo", keyFieldName = "key")
		Map<String, PublicInfo> publicInfos = new HashMap<String, OT.PublicInfo>();

		public void put(PublicInfo info) {
			publicInfos.put(info.getKey(), info);
		}

		// get/set

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

		public Map<String, PublicInfo> getPublicInfos() {
			return publicInfos;
		}

		public void setPublicInfos(Map<String, PublicInfo> publicInfos) {
			this.publicInfos = publicInfos;
		}
	}

	@XStreamAlias("publicInfo")
	public static class PublicInfo {
		@XStreamAsAttribute
		@GsonExclude
		String key;
		String value;

		public PublicInfo() {
		}

		public PublicInfo(String key, String value) {
			super();
			this.key = key;
			this.value = value;
		}

		// get/set

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		// converter

		public static final Converter converter = new Converter() {

			@Override
			public boolean canConvert(Class type) {
				return type == PublicInfo.class;
			}

			@Override
			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
				PublicInfo info = new PublicInfo();
				info.key = reader.getAttribute("key");
				info.value = AsciiA.getString(reader.getValue().trim());
				return info;
			}

			@Override
			public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
				writer.addAttribute("key", ((PublicInfo) source).key);
				writer.setValue('\n' + AsciiA.setString(((PublicInfo) source).value));
			}
		};
	}

	public static class CredentialMap extends HashMap<String, MasterCredential> {

		public static final Converter converter = new Converter() {

			@Override
			public boolean canConvert(Class type) {
				return CredentialMap.class == type;
			}

			@Override
			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
				byte[] by = Utils.base64Decode(reader.getValue().trim());
				Map<String, String> map;
				try {
					map = Utils.unpack(by, Map.class);
				} catch (PackerException e) {
					throw new RuntimeException(e);
				}
				CredentialMap cmap = new CredentialMap();
				for (java.util.Map.Entry<String, String> e : map.entrySet()) {
					BasicSigningSupport s = new BasicSigningSupport();
					s.setSigned(e.getValue());
					s.parseFromSigned();
					OT.Contract ct = (OT.Contract) Engines.xstream.fromXML(s.getUnsigned());
					ct.copyFrom(s);
					cmap.put(e.getKey(), (MasterCredential) ct);
				}
				return cmap;
			}

			@Override
			public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
				Map<String, String> map = new HashMap<String, String>();
				CredentialMap cmap = (CredentialMap) source;
				for (java.util.Map.Entry<String, MasterCredential> e : cmap.entrySet())
					map.put(e.getKey(), e.getValue().getSigned());
				byte[] by;
				try {
					by = Utils.pack(map);
				} catch (InvalidProtocolBufferException e1) {
					throw new RuntimeException(e1);
				}
				writer.setValue('\n' + Utils.base64EncodeString(by, true));
			}
		};
	}

	@XStreamAlias("notaryProviderContract")
	public static class NotaryProviderContract extends Contract {
		public static class NotaryServer {
			@XStreamAsAttribute
			String hostname;
			@XStreamAsAttribute
			Integer port;
			@XStreamAsAttribute
			String URL;

			// get/set

			public String getHostname() {
				return hostname;
			}

			public void setHostname(String hostname) {
				this.hostname = hostname;
			}

			public Integer getPort() {
				return port;
			}

			public void setPort(Integer port) {
				this.port = port;
			}

			public String getURL() {
				return URL;
			}

			public void setURL(String uRL) {
				URL = uRL;
			}
		}

		NotaryServer notaryServer;
		Signer signer;
		@XStreamImplicit(itemFieldName = "key")
		List<NamedText> keys;

		// get/set

		public NotaryServer getNotaryServer() {
			return notaryServer;
		}

		public void setNotaryServer(NotaryServer notaryServer) {
			this.notaryServer = notaryServer;
		}

		public Signer getSigner() {
			return signer;
		}

		public void setSigner(Signer signer) {
			this.signer = signer;
		}

		public List<NamedText> getKeys() {
			return keys;
		}

		public void setKeys(List<NamedText> keys) {
			this.keys = keys;
		}

	}

	public static class Entity {
		@XStreamAsAttribute
		String shortname;
		@XStreamAsAttribute
		String longname;
		@XStreamAsAttribute
		String email;
		@XStreamAsAttribute
		String serverURL;

		// get/set

		public String getShortname() {
			return shortname;
		}

		public void setShortname(String shortname) {
			this.shortname = shortname;
		}

		public String getLongname() {
			return longname;
		}

		public void setLongname(String longname) {
			this.longname = longname;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getServerURL() {
			return serverURL;
		}

		public void setServerURL(String serverURL) {
			this.serverURL = serverURL;
		}
	}

	public static class Signer {
		@XStreamAsAttribute
		Boolean hasCredentials;
		@XStreamAsAttribute
		String nymID;
		@XStreamAsAttribute
		String altLocation;

		ArmoredString nymIDSource;
		AsciiEntity<User> credentialList;
		CredentialMap credentials;

		// get/set

		public Boolean getHasCredentials() {
			return hasCredentials;
		}

		public void setHasCredentials(Boolean hasCredentials) {
			this.hasCredentials = hasCredentials;
		}

		public String getNymID() {
			return nymID;
		}

		public void setNymID(String nymID) {
			this.nymID = nymID;
		}

		public String getAltLocation() {
			return altLocation;
		}

		public void setAltLocation(String altLocation) {
			this.altLocation = altLocation;
		}

		public ArmoredString getNymIDSource() {
			return nymIDSource;
		}

		public void setNymIDSource(ArmoredString nymIDSource) {
			this.nymIDSource = nymIDSource;
		}

		public AsciiEntity<User> getCredentialList() {
			return credentialList;
		}

		public void setCredentialList(AsciiEntity<User> credentialList) {
			this.credentialList = credentialList;
		}

		public CredentialMap getCredentials() {
			return credentials;
		}

		public void setCredentials(CredentialMap credentials) {
			this.credentials = credentials;
		}

	}

	@XStreamAlias("notaryServer")
	public static class NotaryServer {
		@XStreamAsAttribute
		String version;
		String serverID;
		String serverUserID;
		long transactionNum;

		String cachedKey;
		@XStreamImplicit(itemFieldName = "assetType")
		List<AssetType> assetTypes;
		@XStreamImplicit(itemFieldName = "basketInfo")
		List<BasketInfo> basketInfos;
		@XStreamImplicit(itemFieldName = "accountList")
		List<AccountList> accountLists;

		// get/set

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getServerID() {
			return serverID;
		}

		public void setServerID(String serverID) {
			this.serverID = serverID;
		}

		public String getServerUserID() {
			return serverUserID;
		}

		public void setServerUserID(String serverUserID) {
			this.serverUserID = serverUserID;
		}

		public long getTransactionNum() {
			return transactionNum;
		}

		public void setTransactionNum(long transactionNum) {
			this.transactionNum = transactionNum;
		}

		public String getCachedKey() {
			return cachedKey;
		}

		public void setCachedKey(String cachedKey) {
			this.cachedKey = cachedKey;
		}

		public List<AssetType> getAssetTypes() {
			return assetTypes;
		}

		public void setAssetTypes(List<AssetType> assetTypes) {
			this.assetTypes = assetTypes;
		}

		public List<BasketInfo> getBasketInfos() {
			return basketInfos;
		}

		public void setBasketInfos(List<BasketInfo> basketInfos) {
			this.basketInfos = basketInfos;
		}

		public List<AccountList> getAccountLists() {
			return accountLists;
		}

		public void setAccountLists(List<AccountList> accountLists) {
			this.accountLists = accountLists;
		}

	}

	public static class AssetType {
		@XStreamAsAttribute
		String name;
		@XStreamAsAttribute
		String assetTypeID;

		// get/set

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAssetTypeID() {
			return assetTypeID;
		}

		public void setAssetTypeID(String assetTypeID) {
			this.assetTypeID = assetTypeID;
		}

	}

	public static class BasketInfo {
		@XStreamAsAttribute
		String basketID;
		@XStreamAsAttribute
		String basketAcctID;
		@XStreamAsAttribute
		String basketContractID;

		// get/set

		public String getBasketID() {
			return basketID;
		}

		public void setBasketID(String basketID) {
			this.basketID = basketID;
		}

		public String getBasketAcctID() {
			return basketAcctID;
		}

		public void setBasketAcctID(String basketAcctID) {
			this.basketAcctID = basketAcctID;
		}

		public String getBasketContractID() {
			return basketContractID;
		}

		public void setBasketContractID(String basketContractID) {
			this.basketContractID = basketContractID;
		}

	}

	public static class AccountList {
		@XStreamAsAttribute
		Account.Type type;
		@XStreamAsAttribute
		Integer count;
		@XStreamImplicit(itemFieldName = "accountEntry")
		List<AccountEntry> accounts;

		// get/set

		public Account.Type getType() {
			return type;
		}

		public void setType(Account.Type type) {
			this.type = type;
		}

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

		public List<AccountEntry> getAccounts() {
			return accounts;
		}

		public void setAccounts(List<AccountEntry> accounts) {
			this.accounts = accounts;
		}

	}

	public static class AccountEntry {
		@XStreamAsAttribute
		String assetTypeID;
		@XStreamAsAttribute
		String accountID;

		// get/set

		public String getAssetTypeID() {
			return assetTypeID;
		}

		public void setAssetTypeID(String assetTypeID) {
			this.assetTypeID = assetTypeID;
		}

		public String getAccountID() {
			return accountID;
		}

		public void setAccountID(String accountID) {
			this.accountID = accountID;
		}

	}

	public static class NamedText {
		@XStreamAsAttribute
		String name;
		String text;

		public static final Converter converter = new Converter() {

			@Override
			public boolean canConvert(Class type) {
				return type == NamedText.class;
			}

			@Override
			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
				NamedText c = new NamedText();
				c.name = reader.getAttribute("name");
				c.text = reader.getValue().trim();
				return c;
			}

			@Override
			public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
				writer.addAttribute("name", ((NamedText) source).name);
				writer.setValue(((NamedText) source).text + '\n');
			}
		};

		// get/set

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

	}

	public static class ArmoredString {
		@GsonExclude
		String armored;
		String unarmored;

		public ArmoredString() {
		}

		public ArmoredString(String unarmored) {
			this.unarmored = unarmored;
		}

		public static final Converter converter = new Converter() {
			@Override
			public boolean canConvert(Class type) {
				return type == OT.ArmoredString.class;
			}

			@Override
			public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
				OT.ArmoredString string = (OT.ArmoredString) source;
				string.armored = AsciiA.setString(string.unarmored, true);
				writer.setValue('\n' + string.armored);
			}

			@Override
			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
				OT.ArmoredString cs = new ArmoredString();
				cs.armored = reader.getValue();
				cs.unarmored = AsciiA.getString(cs.armored.trim());
				return cs;
			}
		};

		// get/set

		public String getRaw() {
			return armored;
		}

		public void setRaw(String raw) {
			this.armored = raw;
		}

		public String getUnarmored() {
			return unarmored;
		}

		public void setUnarmored(String uncompressed) {
			this.unarmored = uncompressed;
		}
	}

	public static class NumListAttribute extends ArrayList<Long> {
		public static final SingleValueConverter converter = new SingleValueConverter() {

			@Override
			public boolean canConvert(Class type) {
				return type == NumListAttribute.class;
			}

			@Override
			public String toString(Object obj) {
				Iterator<Long> li = ((NumListAttribute) obj).iterator();
				StringBuilder b = new StringBuilder();
				while (li.hasNext()) {
					b.append(li.next());
					if (li.hasNext())
						b.append(',');
				}
				return b.toString();
			}

			@Override
			public Object fromString(String str) {
				NumListAttribute nl = new NumListAttribute();
				for (String s : str.split(","))
					nl.add(new Long(s));
				return nl;
			}
		};
	}

	@SuppressWarnings({ "rawtypes", "serial" })
	public static class NumList extends LinkedList<Long> {
		String serverID;
		String armored;

		public NumList() {
		}

		public NumList(String serverID) {
			this.serverID = serverID;
		}

		public NumList(NumList list) {
			super(list);
			serverID = list.serverID;
		}

		public NumList(String serverID, List<Long> nums) {
			super(nums);
			this.serverID = serverID;
		}

		public boolean removeNum(Long num) {
			Iterator<Long> it = iterator();
			while (it.hasNext())
				if (it.next().equals(num)) {
					it.remove();
					return true;
				}
			return false;
		}

		public static final Converter converter = new Converter() {

			@Override
			public boolean canConvert(Class type) {
				return type == NumList.class;
			}

			@Override
			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
				NumList nl = new NumList();
				nl.serverID = reader.getAttribute("serverID");
				nl.armored = reader.getValue();
				String str = AsciiA.getString(nl.armored.trim());
				for (String s : str.split(","))
					if (!s.isEmpty())
						nl.add(new Long(s));
				return nl;
			}

			@Override
			public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
				NumList nl = (NumList) source;
				writer.addAttribute("serverID", nl.serverID);
				StringBuilder b = new StringBuilder();
				Iterator<Long> li = nl.iterator();
				while (li.hasNext()) {
					b.append(li.next());
					if (li.hasNext())
						b.append(',');
				}
				nl.armored = AsciiA.setString(b.toString());
				writer.setValue('\n' + nl.armored);
			}
		};

		@Override
		public String toString() {
			return "NumList[serverID=" + serverID + ", nums:" + new ArrayList<Long>(this).toString() + "]";
		};
	}

	@SuppressWarnings("serial")
	public static class StringMap extends LinkedHashMap<String, String> {
		public StringMap() {

		}

		public StringMap(Map<String, String> map) {
			super(map);
		}

		// converter

		public static final Converter converter = new Converter() {

			@Override
			public boolean canConvert(Class type) {
				return type == StringMap.class;
			}

			@SuppressWarnings("unchecked")
			@Override
			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
				byte[] by = Utils.base64Decode(reader.getValue().trim());
				Map<String, String> map;
				try {
					map = Utils.unpack(by, Map.class);
				} catch (PackerException e) {
					throw new RuntimeException(e);
				}
				return new StringMap(map);
			}

			@SuppressWarnings("unchecked")
			@Override
			public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
				byte[] by;
				try {
					by = Utils.pack((Map) source);
				} catch (InvalidProtocolBufferException e) {
					throw new RuntimeException(e);
				}
				writer.setValue("\n" + Utils.base64EncodeString(by, true));
			}
		};
	}

	public static class Version {
		private String value;

		public Version() {
		}

		public Version(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		// converter

		public static final SingleValueConverter converter = new SingleValueConverter() {

			@Override
			public boolean canConvert(Class type) {
				return type == Version.class;
			}

			@Override
			public String toString(Object obj) {
				return obj == null ? null : ((Version) obj).value;
			}

			@Override
			public Object fromString(String str) {
				Version version = new Version();
				version.setValue(str);
				return version;
			}
		};
	}

	@SuppressWarnings("rawtypes")
	public static class ArmoredData {
		String raw;
		byte[] data;

		public ArmoredData() {

		}

		public ArmoredData(byte[] data) {
			this.data = data;
		}

		public byte[] getData() {
			return data;
		}

		public String getRaw() {
			return raw;
		}

		// converter

		public static final Converter converter = new Converter() {

			@Override
			public boolean canConvert(Class type) {
				return type == ArmoredData.class;
			}

			@Override
			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
				ArmoredData data = new ArmoredData();
				data.raw = reader.getValue();
				data.data = Utils.base64Decode(data.raw.trim());
				try {
					data.data = Utils.unpack(data.data, byte[].class);
				} catch (PackerException e) {
					throw new RuntimeException(e);
				}
				return data;
			}

			@Override
			public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
				ArmoredData data = (ArmoredData) source;
				byte[] by = Utils.pack(ByteBuffer.wrap(data.data));
				data.raw = Utils.base64EncodeString(by, true);
				writer.setValue("\n" + data.raw);
			}
		};
	}

	public static class Hash {
		@XStreamAsAttribute
		String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	public static class Balance {
		@XStreamAsAttribute
		Long amount;
		@XStreamAsAttribute
		Long date;

		// get/set

		public Long getAmount() {
			return amount;
		}

		public void setAmount(Long amount) {
			this.amount = amount;
		}

		public Long getDate() {
			return date;
		}

		public void setDate(Long date) {
			this.date = date;
		}
	}

	public static class RequestNum {
		@XStreamAsAttribute
		String serverID;
		@XStreamAsAttribute
		Long currentRequestNum;

		public String getServerID() {
			return serverID;
		}

		public void setServerID(String serverID) {
			this.serverID = serverID;
		}

		public Long getCurrentRequestNum() {
			return currentRequestNum;
		}

		public void setCurrentRequestNum(Long currentRequestNum) {
			this.currentRequestNum = currentRequestNum;
		}

	}

	public static class Identifier {
		@XStreamAsAttribute
		String ID;

		public Identifier() {
		}

		public Identifier(String ID) {
			this.ID = ID;
		}

		public String getID() {
			return ID;
		}

		public void setID(String iD) {
			ID = iD;
		}
	}

}
