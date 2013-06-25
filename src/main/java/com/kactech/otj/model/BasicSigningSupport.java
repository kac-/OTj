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
package com.kactech.otj.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.kactech.otj.model.annot.GsonExclude;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class BasicSigningSupport implements SigningSupport {
	@XStreamOmitField
	@GsonExclude
	List<OTSignature> signatures;
	@XStreamOmitField
	@GsonExclude
	String signed;
	@XStreamOmitField
	@GsonExclude
	String unsigned;
	@XStreamOmitField
	@GsonExclude
	String hashType;

	@Override
	public List<OTSignature> getSignatures() {
		return signatures == null ? null : new ArrayList<OTSignature>(signatures);
	}

	@Override
	public String getSigned() {
		return signed;
	}

	@Override
	public String getUnsigned() {
		return unsigned;
	}

	@Override
	public void setUnsigned(String unsigned) {
		setSigned(null);//reset
		if (!unsigned.endsWith("\n"))
			throw new IllegalArgumentException("unsigned doesn't end with '\n'");
		this.unsigned = unsigned;
	}

	@Override
	public String getHashType() {
		return hashType;
	}

	@Override
	public void addSignature(OTSignature signature, String hashType) {
		if (unsigned == null)
			throw new IllegalStateException("unsigned == null");
		if (hashType == null)
			throw new IllegalArgumentException("hashType == null");
		if (signature == null)
			throw new IllegalArgumentException("signature == null");
		if (this.hashType != null && !hashType.equals(this.hashType))
			throw new IllegalArgumentException("hashType is different that previously supplied signatures");
		if (this.signatures == null)
			this.signatures = new ArrayList<OTSignature>();
		if (this.signatures.size() == 0) {
			this.hashType = hashType;
			signed = "-----BEGIN SIGNED MESSAGE-----\n"
					+ "Hash: " + this.hashType + "\n\n"
					+ unsigned;
		}
		this.signatures.add(signature);
		signed += "-----BEGIN MESSAGE SIGNATURE-----\n";
		if (signature.getVersion() != null)
			signed += "Version: " + signature.getVersion() + "\n";
		if (signature.getMeta() != null)
			signed += "Meta: " + signature.getMeta() + "\n";
		if (signature.getComment() != null)
			signed += "Comment: " + signature.getComment() + "\n";
		signed += '\n' + signature.getValue() + "\n-----END MESSAGE SIGNATURE-----\n";
	}

	@Override
	public void setSigned(String signed) {
		this.signed = signed;
		this.hashType = null;
		this.signatures = null;
		this.unsigned = null;

	}

	@Override
	public void parseFromSigned() {
		if (signed == null)
			throw new IllegalStateException("signed == null");
		setSigned(signed);//reset
		this.signatures = new ArrayList<OTSignature>();
		BufferedReader r = new BufferedReader(new StringReader(signed));

		String line;
		BasicOTSignature sign = null;
		StringBuilder bSign = new StringBuilder();
		StringBuilder bContent = new StringBuilder();
		boolean mSign = false;//signature mode
		boolean mContent = false;
		boolean mEnteredContent = false;
		int lineN = 0;

		try {
			while ((line = r.readLine()) != null) {
				lineN++;
				if (line.length() < 2) {
					if (mSign)
						continue;
				} else if (line.charAt(0) == '-') {
					if (mSign) {
						mSign = false;
						sign.setValue(bSign.toString().trim());
						continue;
					}
					if (!mEnteredContent) {
						if (line.length() > 3 && line.startsWith("----") && line.contains("BEGIN")) {
							mEnteredContent = true;
							mContent = true;
							continue;
						} else
							continue;
					} else if (line.length() > 3 && line.startsWith("----") && line.contains("SIGNATURE")) {
						mSign = true;
						mContent = false;
						bSign = new StringBuilder();
						signatures.add(sign = new BasicOTSignature());
						continue;
					} else if (line.length() < 3 || line.charAt(1) != ' ' || line.charAt(2) != '-')
						//throw new ParseException(line, lineN);
						throw new RuntimeException("line " + lineN + ":\n" + line);
					else
						;
				} else {
					if (mEnteredContent) {
						if (mSign) {
							if (line.length() < 2)
								continue;
							else if (line.startsWith("Version:")) {
								sign.setVersion(line.substring("Version:".length()).trim());
								continue;
							}
							else if (line.startsWith("Comment:")) {
								sign.setComment(line.substring("Comment:".length()).trim());
								continue;
							} else if (line.startsWith("Meta:")) {
								if (line.length() != 13)
									throw new RuntimeException("incorrect meta length, line " + lineN + ":\n" + line);
								sign.setMeta(line.substring(9, 13));
								continue;
							}
						}
						if (mContent) {
							if (line.startsWith("Hash:")) {
								this.hashType = line.substring("Hash: ".length()).trim().toUpperCase();
								r.readLine();
								continue;
							}
						}
					}

				}
				if (mSign)
					bSign.append(line);
				else if (mContent)
					bContent.append(line).append('\n');
			}

			r.close();
		} catch (IOException e) {
			//not normal with reader
			throw new RuntimeException(e);
		}

		this.unsigned = bContent.toString();

		if (mSign)
			throw new IllegalStateException("still in signature mode");
		if (mContent)
			throw new IllegalStateException("still in content mode");
		if (!mEnteredContent)
			throw new IllegalStateException("never entered content mode");
	}

	@Override
	public void copyFrom(SigningSupport signing) {
		signed = signing.getSigned();
		unsigned = signing.getUnsigned();
		signatures = signing.getSignatures();
		hashType = signing.getHashType();
	}

}
