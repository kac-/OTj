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

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.protobuf.InvalidProtocolBufferException;
import com.kactech.otj.Utils.PackerException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class Messages {
	public static class OTMessage {
		@XStreamAsAttribute
		String version;

		public String getVersion() {
			return version;
		}
	}

	public static class OTRequest {
		@XStreamAsAttribute
		int requestNum;
		@XStreamAsAttribute
		boolean success;
		@XStreamAsAttribute
		String nymID;
		@XStreamAsAttribute
		String serverID;

		public int getRequestNum() {
			return requestNum;
		}

		public boolean isSuccess() {
			return success;
		}

		public String getNymID() {
			return nymID;
		}

		public String getServerID() {
			return serverID;
		}

	}

	@XStreamAlias("OTmessage")
	public static class CreateUserAccount extends OTMessage {
		@XStreamAlias("createUserAccount")
		Content content;

		public static class Content extends OTRequest {
			CompressedString nymfile;
			CompressedString inReferenceTo;

			public CompressedString getNymfile() {
				return nymfile;
			}

			public CompressedString getInReferenceTo() {
				return inReferenceTo;
			}

		}

		public Content getContent() {
			return content;
		}

	}

	@XStreamAlias("OTmessage")
	public static class GetRequest extends OTMessage {
		@XStreamAlias("getRequest")
		Content content;

		public static class Content extends OTRequest {
			@XStreamAsAttribute
			String nymboxHash;
			@XStreamAsAttribute
			int newRequestNum;

			public int getNewRequestNum() {
				return newRequestNum;
			}

			public String getNymboxHash() {
				return nymboxHash;
			}
		}

		public Content getContent() {
			return content;
		}
	}

	@XStreamAlias("OTmessage")
	public static class CheckUser extends OTMessage {
		@XStreamAlias("checkUser")
		Content content;

		public static class Content extends OTRequest {
			@XStreamAsAttribute
			String nymID2;
			@XStreamAsAttribute
			boolean hasCredentials;
			String nymPublicKey;
			CompressedString credentialList;
			StringMap credentials;

			public String getNymID2() {
				return nymID2;
			}

			public boolean hasCredentials() {
				return hasCredentials;
			}

			public String getNymPublicKey() {
				return nymPublicKey;
			}

			public CompressedString getCredentialList() {
				return credentialList;
			}

			public StringMap getCredentials() {
				return credentials;
			}
		}

		public Content getContent() {
			return content;
		}
	}

	@XStreamAlias("OTmessage")
	public static class SendUserMessage extends OTMessage {
		@XStreamAlias("sendUserMessage")
		Content content;
		String ackReplies;//TODO

		public static class Content extends OTRequest {
			@XStreamAsAttribute
			String nymID2;
			PackedData messagePayload;

			public String getNymID2() {
				return nymID2;
			}

			public PackedData getMessagePayload() {
				return messagePayload;
			}
		}

		public Content getContent() {
			return content;
		}
	}

	@XStreamAlias("OTmessage")
	public static class GetNymbox extends OTMessage {
		@XStreamAlias("getNymbox")
		Content content;

		public static class Content extends OTRequest {
			@XStreamAsAttribute
			String nymboxHash;
			CompressedString nymboxLedger;

			public String getNymboxHash() {
				return nymboxHash;
			}

			public CompressedString getNymboxLedger() {
				return nymboxLedger;
			}
		}

		public Content getContent() {
			return content;
		}
	}

	@XStreamAlias("OTmessage")
	public static class GetBoxReceipt extends OTMessage {
		@XStreamAlias("getBoxReceipt")
		Content content;

		public static class Content extends OTRequest {
			@XStreamAsAttribute
			String accountID;
			@XStreamAsAttribute
			String boxType;
			@XStreamAsAttribute
			int transactionNum;
			CompressedString inReferenceTo;
			CompressedString boxReceipt;

			public String getAccountID() {
				return accountID;
			}

			public String getBoxType() {
				return boxType;
			}

			public int getTransactionNum() {
				return transactionNum;
			}

			public CompressedString getInReferenceTo() {
				return inReferenceTo;
			}

			public CompressedString getBoxReceipt() {
				return boxReceipt;
			}
		}

		public Content getContent() {
			return content;
		}
	}

	public static class CompressedString {
		String raw;
		String uncompressed;

		public String getRaw() {
			return raw;
		}

		public void setRaw(String raw) {
			this.raw = raw;
		}

		public String getUncompressed() {
			return uncompressed;
		}

		public void setUncompressed(String uncompressed) {
			this.uncompressed = uncompressed;
		}

		public static final Converter converter = new Converter() {
			@Override
			public boolean canConvert(Class type) {
				return type == CompressedString.class;
			}

			@Override
			public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
				CompressedString string = (CompressedString) source;
				byte[] by = Utils.pack(string.uncompressed);
				by = Utils.zlibCompress(by);
				string.raw = Utils.base64EncodeString(by, true);
				writer.setValue('\n' + string.raw);
			}

			@Override
			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
				CompressedString cs = new CompressedString();
				cs.raw = reader.getValue();
				byte[] by = Utils.base64Decode(cs.raw.trim());
				try {
					by = Utils.zlibDecompress(by);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				try {
					cs.uncompressed = Utils.unpack(by, String.class);
				} catch (PackerException e) {
					throw new RuntimeException(e);
				}
				return cs;
			}
		};
	}

	@SuppressWarnings("serial")
	public static class StringMap extends LinkedHashMap<String, String> {
		public StringMap() {

		}

		public StringMap(Map<String, String> map) {
			super(map);
		}

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

	@SuppressWarnings("serial")
	public static class PackedData {
		String raw;
		byte[] data;
		public static final Converter converter = new Converter() {

			@Override
			public boolean canConvert(Class type) {
				return type == PackedData.class;
			}

			@Override
			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
				PackedData data = new PackedData();
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
				PackedData data = (PackedData) source;
				byte[] by = Utils.pack(ByteBuffer.wrap(data.data));
				data.raw = Utils.base64EncodeString(by, true);
				writer.setValue("\n" + data.raw);
			}
		};

		public byte[] getData() {
			return data;
		}

		public String getRaw() {
			return raw;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T parseResponse(String unsigned, Class<T> clazz) {
		unsigned = unsigned.replace("<@", "<").replace("</@", "</");
		unsigned = unsigned.replace("<?xml version=\"2.0\"", "<?xml version=\"1.0\"");
		XStream xstream = new XStream();
		xstream.registerConverter(CompressedString.converter);
		xstream.registerConverter(StringMap.converter);
		xstream.registerConverter(PackedData.converter);
		xstream.processAnnotations(clazz);
		return (T) xstream.fromXML(unsigned);
	}

	@XStreamAlias("OTuser")
	public static class OTuser {
		@XStreamAsAttribute
		String nymID;
		CompressedString nymIDSource;
		MasterCredential masterCredential;
		KeyCredential keyCredential;

		public static class MasterCredential {
			String ID;
			boolean valid;

			public String getID() {
				return ID;
			}

			public boolean isValid() {
				return valid;
			}
		}

		public static class KeyCredential extends MasterCredential {
			String masterID;

			public String getMasterID() {
				return masterID;
			}
		}

		public String getNymID() {
			return nymID;
		}

		public CompressedString getNymIDSource() {
			return nymIDSource;
		}

		public MasterCredential getMasterCredential() {
			return masterCredential;
		}

		public KeyCredential getKeyCredential() {
			return keyCredential;
		}

	}

	@XStreamAlias("accountLedger")
	public static class AccountLedger {
		@XStreamAsAttribute
		String version;
		@XStreamAsAttribute
		String type;
		@XStreamAsAttribute
		int numPartialRecords;
		@XStreamAsAttribute
		String accountID;
		@XStreamAsAttribute
		String userID;
		@XStreamAsAttribute
		String serverID;
		NymboxRecord nymboxRecord;

		public static class NymboxRecord {
			@XStreamAsAttribute
			String type;
			@XStreamAsAttribute
			long dateSigned;
			@XStreamAsAttribute
			String receiptHash;
			@XStreamAsAttribute
			int transactionNum;
			@XStreamAsAttribute
			int inRefDisplay;
			@XStreamAsAttribute
			int inReferenceTo;

			public String getType() {
				return type;
			}

			public long getDateSigned() {
				return dateSigned;
			}

			public String getReceiptHash() {
				return receiptHash;
			}

			public int getTransactionNum() {
				return transactionNum;
			}

			public int getInRefDisplay() {
				return inRefDisplay;
			}

			public int getInReferenceTo() {
				return inReferenceTo;
			}

			@Override
			public String toString() {
				return "NymboxRecord [type=" + type + ", dateSigned=" + dateSigned + ", receiptHash=" + receiptHash
						+ ", transactionNum=" + transactionNum + ", inRefDisplay=" + inRefDisplay + ", inReferenceTo="
						+ inReferenceTo + "]";
			}

		}

		public String getVersion() {
			return version;
		}

		public String getType() {
			return type;
		}

		public int getNumPartialRecords() {
			return numPartialRecords;
		}

		public String getAccountID() {
			return accountID;
		}

		public String getUserID() {
			return userID;
		}

		public String getServerID() {
			return serverID;
		}

		public NymboxRecord getNymboxRecord() {
			return nymboxRecord;
		}

		@Override
		public String toString() {
			return "AccountLedger [version=" + version + ", type=" + type + ", numPartialRecords=" + numPartialRecords
					+ ", accountID=" + accountID + ", userID=" + userID + ", serverID=" + serverID + ", nymboxRecord="
					+ nymboxRecord + "]";
		}

	}

	@XStreamAlias("transaction")
	public static class Transaction extends AccountLedger.NymboxRecord {
		CompressedString inReferenceTo;

		public CompressedString getInReferenceToContent() {
			return inReferenceTo;
		}
	}
}
