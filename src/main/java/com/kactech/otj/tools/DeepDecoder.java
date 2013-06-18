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
package com.kactech.otj.tools;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kactech.otj.Utils;
import com.kactech.otj.model.OTSignature;
import com.kactech.otj.model.Signed;

/**
 * Decodes message deeply as can
 * 
 * @author Piotr Kopeć (kactech)
 * 
 */
public class DeepDecoder implements Flushable, Closeable {
	static final Pattern notB64Pat = Pattern.compile("[^a-zA-Z0-9+=/ \n]", Pattern.MULTILINE);
	PrintWriter writer;
	String indent;

	public DeepDecoder(PrintWriter writer, String indent) {
		super();
		this.writer = writer;
		this.indent = indent;
	}

	public void process(String str) throws Exception {
		process(str, "");
	}

	public void process(String str, String isum) throws Exception {
		String s = null;
		byte[] by = null;
		if (str.startsWith("-----BEGIN SIGNED")) {
			Signed signed = Utils.parseSigned(str);
			StringBuilder b = new StringBuilder();
			for (OTSignature sig : signed.getSignatures()) {
				b.append("(").append(sig.getValue().substring(4, 10));
				if (sig.getMeta() != null)
					b.append('[').append(sig.getMeta()).append(']');
				b.append(')');
			}
			writer.println(isum + "<!-- SIGNED " + b.toString() + " -->");
			//if it's signed, try get xml
			process(signed.getUnsigned(), isum);
			return;
		}
		if (str.startsWith("-----BEGIN") || str.startsWith("- -----BEGIN")) {
			boolean esc = str.charAt(1) == ' ';
			String name = str.substring(esc ? 12 : 10, str.indexOf('-', esc ? 12 : 10)).trim();
			List<String> stack = new LinkedList<String>();
			s = Utils.unarmor(str, esc);
			stack.add("ARMOR " + name);
			if (notBase64(s)) {
				printStack(stack, isum);
				process(s, isum);
				return;
			}
			by = Utils.base64Decode(s);
			stack.add("BASE64");
			if (isValidUtf8(by)) {
				printStack(stack, isum);
				process(new String(by, Utils.UTF8), isum);
				return;
			}
			if (!tryBytes(by, isum, indent, stack))
				writer.println(isum + "<!-- TEXT -->\n" + str + "\n" + isum + "<!-- !TEXT -->");
			return;
		}
		if (str.startsWith("<?xml ") || str.startsWith("<keyCredential")
				|| str.startsWith("<masterCredential") || str.startsWith("<transaction ")) {
			//str = str.replaceFirst("(<\\?xml .*?\\?>)", "<!-- $1 -->");
			str = str.replace("<?xml version=\"2.0\"", "<?xml version=\"1.0\"");
			str = str.replace("<@", "<").replace("</@", "</");
			Document doc = Utils.parseXmlDocument(str);
			process(doc.getDocumentElement(), isum);
			return;
		}
		if (str.length() > 0) {
			if (notBase64(str)) {
				writer.println(isum + "<!-- TEXT -->\n" + str + "\n" + isum + "<!-- !TEXT -->");
				return;
			}
			List<String> stack = new LinkedList<String>();
			by = Utils.base64Decode(str);
			stack.add("BASE64");
			if (!tryBytes(by, isum, indent, stack))
				writer.println(isum + "<!-- TEXT -->\n" + str + "\n" + isum + "<!-- !TEXT -->");

		}

	}

	boolean tryBytes(byte[] by, String isum, String ind, List<String> stack) {

		try {
			by = Utils.zlibDecompress(by);
			stack.add("COMPRESS");
			if (isValidUtf8(by)) {
				printStack(stack, isum);
				process(new String(by, Utils.UTF8), isum);
				return true;
			}
		} catch (Exception ex) {

		}
		try {
			Map<String, String> map = Utils.unpack(by, Map.class);
			stack.add("PACK MAP");
			printStack(stack, isum);
			for (String k : map.keySet()) {
				writer.println(isum + "<!-- KEY " + k + " -->");
				//writer.println(isum + ind + "<!-- VALUE -->");
				process(map.get(k), isum + ind);
			}
			return true;
		} catch (Exception ex) {

		}
		try {
			String unpacked = Utils.unpack(by, String.class);
			stack.add("PACK STRING");
			printStack(stack, isum);
			process(unpacked, isum);
			return true;
		} catch (Exception ex) {

		}
		return false;
	}

	void printStack(List<String> l, String isum) {
		for (String s : l)
			writer.println(isum + "<!-- " + s + " -->");
	}

	void process(Element el, String is) throws DOMException, Exception {

		NamedNodeMap nnm = el.getAttributes();
		if (nnm.getLength() > 0) {
			writer.print(is + "<" + el.getTagName());
			for (int i = 0; i < nnm.getLength(); i++) {
				Node n = nnm.item(i);
				if (i == nnm.getLength() - 1)
					writer.println((i == 0 ? " " : is + "  ") + n + ">");
				else
					writer.println((i == 0 ? " " : is + "  ") + n);
			}
		}
		else
			writer.println(is + "<" + el.getTagName() + ">");
		NodeList nl = el.getChildNodes();
		boolean hasElements = false;
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n instanceof Element) {
				hasElements = true;
				process((Element) n, is + indent);
			}
		}
		if (!hasElements) {
			process(el.getTextContent(), is + indent);
		}
		writer.println(is + "</" + el.getTagName() + ">");
	}

	String toStartString(Element el, String is) {
		StringBuilder b = new StringBuilder();
		b.append("<" + el.getTagName() + " ");
		NamedNodeMap nnm = el.getAttributes();
		for (int i = 0; i < nnm.getLength(); i++) {
			Node n = nnm.item(i);
			writer.println(n);
		}

		return b.toString();
	}

	public static boolean notBase64(String str) {
		return notB64Pat.matcher(str).find();
	}

	/*
	 * from https://code.google.com/p/protobuf/source/browse/trunk/java/src/main/java/com/google/protobuf/Utf8.java?spec=svn425&r=425#143
	 */

	public static boolean isValidUtf8(byte[] byteString) {
		int index = 0;
		int size = byteString.length;
		// To avoid the masking, we could change this to use bytes;
		// Then X > 0xC2 gets turned into X < -0xC2; X < 0x80
		// gets turned into X >= 0, etc.

		while (index < size) {
			int byte1 = byteString[index++] & 0xFF;
			if (byte1 < 0x80) {
				// fast loop for single bytes
				continue;

				// we know from this point on that we have 2-4 byte forms
			} else if (byte1 < 0xC2 || byte1 > 0xF4) {
				// catch illegal first bytes: < C2 or > F4
				return false;
			}
			if (index >= size) {
				// fail if we run out of bytes
				return false;
			}
			int byte2 = byteString[index++] & 0xFF;
			if (byte2 < 0x80 || byte2 > 0xBF) {
				// general trail-byte test
				return false;
			}
			if (byte1 <= 0xDF) {
				// two-byte form; general trail-byte test is sufficient
				continue;
			}

			// we know from this point on that we have 3 or 4 byte forms
			if (index >= size) {
				// fail if we run out of bytes
				return false;
			}
			int byte3 = byteString[index++] & 0xFF;
			if (byte3 < 0x80 || byte3 > 0xBF) {
				// general trail-byte test
				return false;
			}
			if (byte1 <= 0xEF) {
				// three-byte form. Vastly more frequent than four-byte forms
				// The following has an extra test, but not worth restructuring
				if (byte1 == 0xE0 && byte2 < 0xA0 ||
						byte1 == 0xED && byte2 > 0x9F) {
					// check special cases of byte2
					return false;
				}

			} else {
				// four-byte form

				if (index >= size) {
					// fail if we run out of bytes
					return false;
				}
				int byte4 = byteString[index++] & 0xFF;
				if (byte4 < 0x80 || byte4 > 0xBF) {
					// general trail-byte test
					return false;
				}
				// The following has an extra test, but not worth restructuring
				if (byte1 == 0xF0 && byte2 < 0x90 ||
						byte1 == 0xF4 && byte2 > 0x8F) {
					// check special cases of byte2
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}
}
