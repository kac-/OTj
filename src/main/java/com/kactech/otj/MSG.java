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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.kactech.otj.OT.ArmoredData;
import com.kactech.otj.OT.NumList;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class MSG {
	/*
	OTmessage
	- version
	-- ackReplies
	-- createAccount
	-- createUserAccount
	-- getAccount
	-- getBoxReceipt
	-- getInbox
	-- getNymbox
	-- getOutbox
	-- getRequest
	-- getTransactionNum
	-- notarizeTransactions
	-- processInbox
	-- processNymbox
	-- sendUserMessage
	 */
	@XStreamAlias("OTmessage")
	public static class Message extends OT.Contract {
		public Message() {
			version = "2.0";
		}

		CheckUser checkUser;
		@XStreamAlias("re_checkUser")
		CheckUserResp checkUserResp;
		CreateAccount createAccount;
		@XStreamAlias("re_createAccount")
		CreateAccountResp createAccountResp;
		CreateUserAccount createUserAccount;
		@XStreamAlias("re_createUserAccount")
		CreateUserAccountResp createUserAccountResp;
		GetAccount getAccount;
		@XStreamAlias("re_getAccount")
		GetAccountResp getAccountResp;
		GetBoxReceipt getBoxReceipt;
		@XStreamAlias("re_getBoxReceipt")
		GetBoxReceiptResp getBoxReceiptResp;
		GetInbox getInbox;
		@XStreamAlias("re_getInbox")
		GetInboxResp getInboxResp;
		GetNymbox getNymbox;
		@XStreamAlias("re_getNymbox")
		GetNymboxResp getNymboxResp;
		GetOutbox getOutbox;
		@XStreamAlias("re_getOutbox")
		GetOutboxResp getOutboxResp;
		GetRequest getRequest;
		@XStreamAlias("re_getRequest")
		GetRequestResp getRequestResp;
		GetTransactionNum getTransactionNum;
		@XStreamAlias("re_getTransactionNum")
		GetTransactionNumResp getTransactionNumResp;
		NotarizeTransactions notarizeTransactions;
		@XStreamAlias("re_notarizeTransactions")
		NotarizeTransactionsResp notarizeTransactionsResp;
		ProcessInbox processInbox;
		@XStreamAlias("re_processInbox")
		ProcessInboxResp processInboxResp;
		ProcessNymbox processNymbox;
		@XStreamAlias("re_processNymbox")
		ProcessNymboxResp processNymboxResp;
		SendUserMessage sendUserMessage;
		@XStreamAlias("re_sendUserMessage")
		SendUserMessageResp sendUserMessageResp;

		NumList ackReplies;

		/*
		 * get/set
		 */

		@Override
		public String getVersion() {
			return version;
		}

		@Override
		public void setVersion(String version) {
			this.version = version;
		}

		public CheckUser getCheckUser() {
			return checkUser;
		}

		public void setCheckUser(CheckUser checkUser) {
			this.checkUser = checkUser;
		}

		public CheckUserResp getCheckUserResp() {
			return checkUserResp;
		}

		public void setCheckUserResp(CheckUserResp checkUserResp) {
			this.checkUserResp = checkUserResp;
		}

		public CreateAccount getCreateAccount() {
			return createAccount;
		}

		public void setCreateAccount(CreateAccount createAccount) {
			this.createAccount = createAccount;
		}

		public CreateAccountResp getCreateAccountResp() {
			return createAccountResp;
		}

		public void setCreateAccountResp(CreateAccountResp createAccountResp) {
			this.createAccountResp = createAccountResp;
		}

		public CreateUserAccount getCreateUserAccount() {
			return createUserAccount;
		}

		public void setCreateUserAccount(CreateUserAccount createUserAccount) {
			this.createUserAccount = createUserAccount;
		}

		public CreateUserAccountResp getCreateUserAccountResp() {
			return createUserAccountResp;
		}

		public GetAccount getGetAccount() {
			return getAccount;
		}

		public void setGetAccount(GetAccount getAccount) {
			this.getAccount = getAccount;
		}

		public GetAccountResp getGetAccountResp() {
			return getAccountResp;
		}

		public void setGetAccountResp(GetAccountResp getAccountResp) {
			this.getAccountResp = getAccountResp;
		}

		public GetBoxReceipt getGetBoxReceipt() {
			return getBoxReceipt;
		}

		public void setGetBoxReceipt(GetBoxReceipt getBoxReceipt) {
			this.getBoxReceipt = getBoxReceipt;
		}

		public GetBoxReceiptResp getGetBoxReceiptResp() {
			return getBoxReceiptResp;
		}

		public void setGetBoxReceiptResp(GetBoxReceiptResp getBoxReceiptResp) {
			this.getBoxReceiptResp = getBoxReceiptResp;
		}

		public GetInbox getGetInbox() {
			return getInbox;
		}

		public void setGetInbox(GetInbox getInbox) {
			this.getInbox = getInbox;
		}

		public GetInboxResp getGetInboxResp() {
			return getInboxResp;
		}

		public void setGetInboxResp(GetInboxResp getInboxResp) {
			this.getInboxResp = getInboxResp;
		}

		public GetNymbox getGetNymbox() {
			return getNymbox;
		}

		public void setGetNymbox(GetNymbox getNymbox) {
			this.getNymbox = getNymbox;
		}

		public GetNymboxResp getGetNymboxResp() {
			return getNymboxResp;
		}

		public void setGetNymboxResp(GetNymboxResp getNymboxResp) {
			this.getNymboxResp = getNymboxResp;
		}

		public GetOutbox getGetOutbox() {
			return getOutbox;
		}

		public void setGetOutbox(GetOutbox getOutbox) {
			this.getOutbox = getOutbox;
		}

		public GetOutboxResp getGetOutboxResp() {
			return getOutboxResp;
		}

		public void setGetOutboxResp(GetOutboxResp getOutboxResp) {
			this.getOutboxResp = getOutboxResp;
		}

		public GetRequest getGetRequest() {
			return getRequest;
		}

		public void setGetRequest(GetRequest getRequest) {
			this.getRequest = getRequest;
		}

		public GetRequestResp getGetRequestResp() {
			return getRequestResp;
		}

		public void setGetRequestResp(GetRequestResp getRequestResp) {
			this.getRequestResp = getRequestResp;
		}

		public GetTransactionNum getGetTransactionNum() {
			return getTransactionNum;
		}

		public void setGetTransactionNum(GetTransactionNum getTransactionNum) {
			this.getTransactionNum = getTransactionNum;
		}

		public GetTransactionNumResp getGetTransactionNumResp() {
			return getTransactionNumResp;
		}

		public void setGetTransactionNumResp(GetTransactionNumResp getTransactionNumResp) {
			this.getTransactionNumResp = getTransactionNumResp;
		}

		public NotarizeTransactions getNotarizeTransactions() {
			return notarizeTransactions;
		}

		public void setNotarizeTransactions(NotarizeTransactions notarizeTransactions) {
			this.notarizeTransactions = notarizeTransactions;
		}

		public NotarizeTransactionsResp getNotarizeTransactionsResp() {
			return notarizeTransactionsResp;
		}

		public void setNotarizeTransactionsResp(NotarizeTransactionsResp notarizeTransactionsResp) {
			this.notarizeTransactionsResp = notarizeTransactionsResp;
		}

		public ProcessInbox getProcessInbox() {
			return processInbox;
		}

		public void setProcessInbox(ProcessInbox processInbox) {
			this.processInbox = processInbox;
		}

		public ProcessInboxResp getProcessInboxResp() {
			return processInboxResp;
		}

		public void setProcessInboxResp(ProcessInboxResp processInboxResp) {
			this.processInboxResp = processInboxResp;
		}

		public ProcessNymbox getProcessNymbox() {
			return processNymbox;
		}

		public void setProcessNymbox(ProcessNymbox processNymbox) {
			this.processNymbox = processNymbox;
		}

		public ProcessNymboxResp getProcessNymboxResp() {
			return processNymboxResp;
		}

		public void setProcessNymboxResp(ProcessNymboxResp processNymboxResp) {
			this.processNymboxResp = processNymboxResp;
		}

		public SendUserMessage getSendUserMessage() {
			return sendUserMessage;
		}

		public void setSendUserMessage(SendUserMessage sendUserMessage) {
			this.sendUserMessage = sendUserMessage;
		}

		public SendUserMessageResp getSendUserMessageResp() {
			return sendUserMessageResp;
		}

		public void setSendUserMessageResp(SendUserMessageResp sendUserMessageResp) {
			this.sendUserMessageResp = sendUserMessageResp;
		}

		public NumList getAckReplies() {
			return ackReplies;
		}

		public void setAckReplies(NumList ackReplies) {
			this.ackReplies = ackReplies;
		}

		/*
		 * helper
		 */
		static Map<Class, Field> fields = new HashMap<Class, Field>();
		static
		{
			for (Field f : Message.class.getDeclaredFields())
				fields.put(f.getType(), f);
		}

		public Message set(Object requestOrResponse) {
			try {
				fields.get(requestOrResponse.getClass()).set(this, requestOrResponse);
				return this;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}
	}

	public static class Request {
		@XStreamAsAttribute
		Long requestNum;
		@XStreamAsAttribute
		String nymID;
		@XStreamAsAttribute
		String serverID;

		// get/set

		public Long getRequestNum() {
			return requestNum;
		}

		public void setRequestNum(Long requestNum) {
			this.requestNum = requestNum;
		}

		public String getNymID() {
			return nymID;
		}

		public void setNymID(String nymID) {
			this.nymID = nymID;
		}

		public String getServerID() {
			return serverID;
		}

		public void setServerID(String serverID) {
			this.serverID = serverID;
		}

	}

	public static class Response extends Request {
		@XStreamAsAttribute
		Boolean success;

		public Boolean getSuccess() {
			return success;
		}

		public void setSuccess(Boolean success) {
			this.success = success;
		}

	}

	@XStreamAlias("checkUser")
	public static class CheckUser extends Request {
		@XStreamAsAttribute
		String nymID2;

		public String getNymID2() {
			return nymID2;
		}

		public void setNymID2(String nymID2) {
			this.nymID2 = nymID2;
		}

	}

	public static class AsciiEntity<T> {
		T entity;

		public AsciiEntity() {

		}

		public AsciiEntity(T entity) {
			this.entity = entity;
		}

		public T getEntity() {
			return entity;
		}

		public void setEntity(T entity) {
			this.entity = entity;
		}
	}

	@XStreamAlias("re_checkUser")
	public static class CheckUserResp extends Response {
		@XStreamAsAttribute
		String nymID2;
		@XStreamAsAttribute
		Boolean hasCredentials;

		String nymPublicKey;
		Message inReferenceTo;

		AsciiEntity<OT.Pseudonym> credentialList;
		OT.StringMap credentials;//TODO CredentialsMap

		// get/set

		public String getNymID2() {
			return nymID2;
		}

		public void setNymID2(String nymID2) {
			this.nymID2 = nymID2;
		}

		public Boolean getHasCredentials() {
			return hasCredentials;
		}

		public void setHasCredentials(Boolean hasCredentials) {
			this.hasCredentials = hasCredentials;
		}

		public String getNymPublicKey() {
			return nymPublicKey;
		}

		public void setNymPublicKey(String nymPublicKey) {
			this.nymPublicKey = nymPublicKey;
		}

		public Message getInReferenceTo() {
			return inReferenceTo;
		}

		public void setInReferenceTo(Message inReferenceTo) {
			this.inReferenceTo = inReferenceTo;
		}

		public OT.StringMap getCredentials() {
			return credentials;
		}

		public void setCredentials(OT.StringMap credentials) {
			this.credentials = credentials;
		}

		public AsciiEntity<OT.Pseudonym> getCredentialList() {
			return credentialList;
		}

		public void setCredentialList(AsciiEntity<OT.Pseudonym> credentialList) {
			this.credentialList = credentialList;
		}
	}

	@XStreamAlias("createAccount")
	public static class CreateAccount extends Request {
		@XStreamAsAttribute
		String assetType;

		// get/set

		public String getAssetType() {
			return assetType;
		}

		public void setAssetType(String assetType) {
			this.assetType = assetType;
		}

	}

	@XStreamAlias("re_createAccount")
	public static class CreateAccountResp extends Response {
		@XStreamAsAttribute
		String accountID;

		Message inReferenceTo;
		OT.Account newAccount;

		// get/set

		public String getAccountID() {
			return accountID;
		}

		public void setAccountID(String accountID) {
			this.accountID = accountID;
		}

		public Message getInReferenceTo() {
			return inReferenceTo;
		}

		public void setInReferenceTo(Message inReferenceTo) {
			this.inReferenceTo = inReferenceTo;
		}

		public OT.Account getNewAccount() {
			return newAccount;
		}

		public void setNewAccount(OT.Account newAccount) {
			this.newAccount = newAccount;
		}
	}

	@XStreamAlias("createUserAccount")
	public static class CreateUserAccount extends Request {
		AsciiEntity<OT.Pseudonym> credentialList;
		OT.CredentialMap credentials;

		// get/set

		public AsciiEntity<OT.Pseudonym> getCredentialList() {
			return credentialList;
		}

		public void setCredentialList(AsciiEntity<OT.Pseudonym> credentialList) {
			this.credentialList = credentialList;
		}

		public OT.CredentialMap getCredentials() {
			return credentials;
		}

		public void setCredentials(OT.CredentialMap credentials) {
			this.credentials = credentials;
		}
	}

	@XStreamAlias("re_createUserAccount")
	public static class CreateUserAccountResp extends Response {
		AsciiEntity<OT.Pseudonym> nymfile;
		Message inReferenceTo;

		//TODO more
		public AsciiEntity<OT.Pseudonym> getNymfile() {
			return nymfile;
		}

		public void setNymfile(AsciiEntity<OT.Pseudonym> nymfile) {
			this.nymfile = nymfile;
		}

		public Message getInReferenceTo() {
			return inReferenceTo;
		}

		public void setInReferenceTo(Message inReferenceTo) {
			this.inReferenceTo = inReferenceTo;
		}

	}

	@XStreamAlias("getAccount")
	public static class GetAccount extends Request {
		@XStreamAsAttribute
		String accountID;

		public String getAccountID() {
			return accountID;
		}

		public void setAccountID(String accountID) {
			this.accountID = accountID;
		}

	}

	@XStreamAlias("re_getAccount")
	public static class GetAccountResp extends Response {
		@XStreamAsAttribute
		String accountID;

		OT.Account assetAccount;

		// get/set

		public String getAccountID() {
			return accountID;
		}

		public void setAccountID(String accountID) {
			this.accountID = accountID;
		}

		public OT.Account getAssetAccount() {
			return assetAccount;
		}

		public void setAssetAccount(OT.Account assetAccount) {
			this.assetAccount = assetAccount;
		}

	}

	@XStreamAlias("getBoxReceipt")
	public static class GetBoxReceipt extends Request {
		@XStreamAsAttribute
		String accountID;
		@XStreamAsAttribute
		Long transactionNum;
		@XStreamAsAttribute
		OT.Ledger.Type boxType;

		// get/set

		public String getAccountID() {
			return accountID;
		}

		public void setAccountID(String accountID) {
			this.accountID = accountID;
		}

		public Long getTransactionNum() {
			return transactionNum;
		}

		public void setTransactionNum(Long transactionNum) {
			this.transactionNum = transactionNum;
		}

		public OT.Ledger.Type getBoxType() {
			return boxType;
		}

		public void setBoxType(OT.Ledger.Type boxType) {
			this.boxType = boxType;
		}
	}

	@XStreamAlias("re_getBoxReceipt")
	public static class GetBoxReceiptResp extends Response {
		@XStreamAsAttribute
		String accountID;
		@XStreamAsAttribute
		Long transactionNum;
		@XStreamAsAttribute
		OT.Ledger.Type boxType;

		Message inReferenceTo;
		OT.Transaction boxReceipt;

		// get/set

		public String getAccountID() {
			return accountID;
		}

		public void setAccountID(String accountID) {
			this.accountID = accountID;
		}

		public Long getTransactionNum() {
			return transactionNum;
		}

		public void setTransactionNum(Long transactionNum) {
			this.transactionNum = transactionNum;
		}

		public OT.Ledger.Type getBoxType() {
			return boxType;
		}

		public void setBoxType(OT.Ledger.Type boxType) {
			this.boxType = boxType;
		}

		public Message getInReferenceTo() {
			return inReferenceTo;
		}

		public void setInReferenceTo(Message inReferenceTo) {
			this.inReferenceTo = inReferenceTo;
		}

		public OT.Transaction getBoxReceipt() {
			return boxReceipt;
		}

		public void setBoxReceipt(OT.Transaction boxReceipt) {
			this.boxReceipt = boxReceipt;
		}

	}

	@XStreamAlias("getInbox")
	public static class GetInbox extends Request {
		@XStreamAsAttribute
		String accountID;

		public String getAccountID() {
			return accountID;
		}

		public void setAccountID(String accountID) {
			this.accountID = accountID;
		}
	}

	/*
	 getInbox
	- accountID
	- inboxHash
	- nymID
	- requestNum
	- serverID
	- success
	-- inboxLedger
	 */
	@XStreamAlias("re_getInbox")
	public static class GetInboxResp extends Response {
		@XStreamAsAttribute
		String accountID;
		@XStreamAsAttribute
		String inboxHash;

		OT.Ledger inboxLedger;

		// get/set

		public String getAccountID() {
			return accountID;
		}

		public void setAccountID(String accountID) {
			this.accountID = accountID;
		}

		public String getInboxHash() {
			return inboxHash;
		}

		public void setInboxHash(String inboxHash) {
			this.inboxHash = inboxHash;
		}

		public OT.Ledger getInboxLedger() {
			return inboxLedger;
		}

		public void setInboxLedger(OT.Ledger inboxLedger) {
			this.inboxLedger = inboxLedger;
		}

	}

	@XStreamAlias("getNymbox")
	public static class GetNymbox extends Request {
	}

	@XStreamAlias("re_getNymbox")
	public static class GetNymboxResp extends Response {
		@XStreamAsAttribute
		String nymboxHash;
		OT.Ledger nymboxLedger;

		// get/set

		public String getNymboxHash() {
			return nymboxHash;
		}

		public void setNymboxHash(String nymboxHash) {
			this.nymboxHash = nymboxHash;
		}

		public OT.Ledger getNymboxLedger() {
			return nymboxLedger;
		}

		public void setNymboxLedger(OT.Ledger nymboxLedger) {
			this.nymboxLedger = nymboxLedger;
		}

	}

	@XStreamAlias("getOutbox")
	public static class GetOutbox extends Request {
		@XStreamAsAttribute
		String accountID;

		public String getAccountID() {
			return accountID;
		}

		public void setAccountID(String accountID) {
			this.accountID = accountID;
		}
	}

	@XStreamAlias("re_getOutbox")
	public static class GetOutboxResp extends Response {
		@XStreamAsAttribute
		String accountID;
		@XStreamAsAttribute
		String outboxHash;

		OT.Ledger outboxLedger;

		// get/set

		public String getAccountID() {
			return accountID;
		}

		public void setAccountID(String accountID) {
			this.accountID = accountID;
		}

		public String getOutboxHash() {
			return outboxHash;
		}

		public void setOutboxHash(String outboxHash) {
			this.outboxHash = outboxHash;
		}

		public OT.Ledger getOutboxLedger() {
			return outboxLedger;
		}

		public void setOutboxLedger(OT.Ledger outboxLedger) {
			this.outboxLedger = outboxLedger;
		}
	}

	@XStreamAlias("getRequest")
	public static class GetRequest extends Request {
		public GetRequest() {
			requestNum = 1l;
		}
	}

	@XStreamAlias("re_getRequest")
	public static class GetRequestResp extends Response {
		@XStreamAsAttribute
		String nymboxHash;
		@XStreamAsAttribute
		Long newRequestNum;

		/* get/set */

		public String getNymboxHash() {
			return nymboxHash;
		}

		public void setNymboxHash(String nymboxHash) {
			this.nymboxHash = nymboxHash;
		}

		public Long getNewRequestNum() {
			return newRequestNum;
		}

		public void setNewRequestNum(Long newRequestNum) {
			this.newRequestNum = newRequestNum;
		}

	}

	@XStreamAlias("getTransactionNum")
	public static class GetTransactionNum extends Request {
		@XStreamAsAttribute
		String nymboxHash;

		public String getNymboxHash() {
			return nymboxHash;
		}

		public void setNymboxHash(String nymboxHash) {
			this.nymboxHash = nymboxHash;
		}
	}

	@XStreamAlias("re_getTransactionNum")
	public static class GetTransactionNumResp extends Response {
	}

	@XStreamAlias("notarizeTransactions")
	public static class NotarizeTransactions extends Request {
		@XStreamAsAttribute
		String accountID;
		@XStreamAsAttribute
		String nymboxHash;

		OT.Ledger accountLedger;

		// get/set

		public String getAccountID() {
			return accountID;
		}

		public void setAccountID(String accountID) {
			this.accountID = accountID;
		}

		public String getNymboxHash() {
			return nymboxHash;
		}

		public void setNymboxHash(String nymboxHash) {
			this.nymboxHash = nymboxHash;
		}

		public OT.Ledger getAccountLedger() {
			return accountLedger;
		}

		public void setAccountLedger(OT.Ledger accountLedger) {
			this.accountLedger = accountLedger;
		}
	}

	@XStreamAlias("re_notarizeTransactions")
	public static class NotarizeTransactionsResp extends Response {
		@XStreamAsAttribute
		String accountID;

		OT.Ledger responseLedger;
		Message inReferenceTo;

		// get/set

		public String getAccountID() {
			return accountID;
		}

		public void setAccountID(String accountID) {
			this.accountID = accountID;
		}

		public OT.Ledger getResponseLedger() {
			return responseLedger;
		}

		public void setResponseLedger(OT.Ledger responseLedger) {
			this.responseLedger = responseLedger;
		}

		public Message getInReferenceTo() {
			return inReferenceTo;
		}

		public void setInReferenceTo(Message inReferenceTo) {
			this.inReferenceTo = inReferenceTo;
		}

	}

	@XStreamAlias("processInbox")
	public static class ProcessInbox extends Request {
		@XStreamAsAttribute
		String nymboxHash;
		@XStreamAsAttribute
		String accountID;

		OT.Ledger processLedger;

		// get/set

		public String getNymboxHash() {
			return nymboxHash;
		}

		public void setNymboxHash(String nymboxHash) {
			this.nymboxHash = nymboxHash;
		}

		public String getAccountID() {
			return accountID;
		}

		public void setAccountID(String accountID) {
			this.accountID = accountID;
		}

		public OT.Ledger getProcessLedger() {
			return processLedger;
		}

		public void setProcessLedger(OT.Ledger processLedger) {
			this.processLedger = processLedger;
		}
	}

	@XStreamAlias("re_processInbox")
	public static class ProcessInboxResp extends Response {
		@XStreamAsAttribute
		String accountID;

		Message inReferenceTo;
		OT.Ledger responseLedger;

		// get/set

		public String getAccountID() {
			return accountID;
		}

		public void setAccountID(String accountID) {
			this.accountID = accountID;
		}

		public Message getInReferenceTo() {
			return inReferenceTo;
		}

		public void setInReferenceTo(Message inReferenceTo) {
			this.inReferenceTo = inReferenceTo;
		}

		public OT.Ledger getResponseLedger() {
			return responseLedger;
		}

		public void setResponseLedger(OT.Ledger responseLedger) {
			this.responseLedger = responseLedger;
		}

	}

	@XStreamAlias("processNymbox")
	public static class ProcessNymbox extends Request {
		@XStreamAsAttribute
		String nymboxHash;
		OT.Ledger processLedger;

		// get/set

		public String getNymboxHash() {
			return nymboxHash;
		}

		public void setNymboxHash(String nymboxHash) {
			this.nymboxHash = nymboxHash;
		}

		public OT.Ledger getProcessLedger() {
			return processLedger;
		}

		public void setProcessLedger(OT.Ledger processLedger) {
			this.processLedger = processLedger;
		}

	}

	@XStreamAlias("re_processNymbox")
	public static class ProcessNymboxResp extends Response {
		@XStreamAsAttribute
		String nymboxHash;

		Message inReferenceTo;
		OT.Ledger responseLedger;

		// get/set

		public String getNymboxHash() {
			return nymboxHash;
		}

		public void setNymboxHash(String nymboxHash) {
			this.nymboxHash = nymboxHash;
		}

		public Message getInReferenceTo() {
			return inReferenceTo;
		}

		public void setInReferenceTo(Message inReferenceTo) {
			this.inReferenceTo = inReferenceTo;
		}

		public OT.Ledger getResponseLedger() {
			return responseLedger;
		}

		public void setResponseLedger(OT.Ledger responseLedger) {
			this.responseLedger = responseLedger;
		}

	}

	@XStreamAlias("sendUserMessage")
	public static class SendUserMessage extends Request {
		@XStreamAsAttribute
		String nymID2;
		ArmoredData messagePayload;

		// get/set

		public String getNymID2() {
			return nymID2;
		}

		public void setNymID2(String nymID2) {
			this.nymID2 = nymID2;
		}

		public ArmoredData getMessagePayload() {
			return messagePayload;
		}

		public void setMessagePayload(ArmoredData messagePayload) {
			this.messagePayload = messagePayload;
		}
	}

	@XStreamAlias("re_sendUserMessage")
	public static class SendUserMessageResp extends Response {
	}

}
