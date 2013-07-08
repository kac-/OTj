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

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.bouncycastle.jcajce.provider.util.DigestFactory;

/**
 * Calculates and verifies OT compatible signature from digest <br>
 * Based on
 * {@link org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi.nonePSS}
 * <br>
 * found at <a href=
 * "https://github.com/bcgit/bc-java/blob/2b976f5364cfdbc37d3086019d93483c983eb80b/prov/src/main/java/org/bouncycastle/jcajce/provider/asymmetric/rsa/PSSSignatureSpi.java"
 * >github</a>
 * 
 * @author Piotr Kopeć (kactech)
 */
public class OTPssSignature extends Signature {
	PSSSigner pss;
	Digest mgfDigest;
	Digest contentDigest;

	public OTPssSignature() {
		super("OpenTransactions PSS");
		mgfDigest = DigestFactory.getDigest("SHA-256");
		contentDigest = new NullPssDigest(mgfDigest);
	}

	@Override
	protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
		if (!(publicKey instanceof RSAPublicKey))
			throw new InvalidKeyException("Supplied key is not a RSAPublicKey instance");
		pss = signer((RSAKey) publicKey);
		pss.init(false, generatePublicKeyParameter((RSAPublicKey) publicKey));
	}

	PSSSigner signer(RSAKey key) {
		return new PSSSigner(new RSABlindedEngine()
				, contentDigest
				, mgfDigest
				// max salt length, openssl -2 equivalent
				, (key.getModulus().toByteArray().length - 1) - 32 - 2
				, PSSSigner.TRAILER_IMPLICIT);
	}

	static RSAKeyParameters generatePublicKeyParameter(
			RSAPublicKey key) {
		return new RSAKeyParameters(false, key.getModulus(), key.getPublicExponent());
	}

	static RSAKeyParameters generatePrivateKeyParameter(
			RSAPrivateKey key) {
		if (key instanceof RSAPrivateCrtKey) {
			RSAPrivateCrtKey k = (RSAPrivateCrtKey) key;

			return new RSAPrivateCrtKeyParameters(k.getModulus(),
					k.getPublicExponent(), k.getPrivateExponent(),
					k.getPrimeP(), k.getPrimeQ(), k.getPrimeExponentP(), k.getPrimeExponentQ(),
					k.getCrtCoefficient());
		} else {
			RSAPrivateKey k = key;

			return new RSAKeyParameters(true, k.getModulus(), k.getPrivateExponent());
		}
	}

	@Override
	protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
		if (!(privateKey instanceof RSAPrivateKey))
			throw new InvalidKeyException("Supplied key is not a RSAPublicKey instance");
		pss = signer((RSAKey) privateKey);
		pss.init(true, generatePrivateKeyParameter((RSAPrivateKey) privateKey));
	}

	@Override
	protected void engineInitSign(PrivateKey privateKey, SecureRandom random) throws InvalidKeyException {
		pss = signer((RSAKey) privateKey);
		pss.init(true, new ParametersWithRandom(generatePrivateKeyParameter((RSAPrivateKey) privateKey), random));
	}

	@Override
	protected void engineUpdate(byte b) throws SignatureException {
		pss.update(b);

	}

	@Override
	protected void engineUpdate(byte[] b, int off, int len) throws SignatureException {
		pss.update(b, off, len);

	}

	@Override
	protected byte[] engineSign() throws SignatureException {
		try {
			return pss.generateSignature();
		} catch (Exception e) {
			throw new SignatureException(e);
		}
	}

	@Override
	protected boolean engineVerify(byte[] sigBytes) throws SignatureException {
		return pss.verifySignature(sigBytes);
	}

	@Override
	@Deprecated
	protected void engineSetParameter(String param, Object value) throws InvalidParameterException {

	}

	@Override
	@Deprecated
	protected Object engineGetParameter(String param) throws InvalidParameterException {
		return null;
	}

	/**
	 * Copy-paste from <a href=
	 * "https://github.com/bcgit/bc-java/blob/2b976f5364cfdbc37d3086019d93483c983eb80b/prov/src/main/java/org/bouncycastle/jcajce/provider/asymmetric/rsa/PSSSignatureSpi.java#L329"
	 * >github<a>
	 * 
	 * @author kac
	 * 
	 */
	private class NullPssDigest
			implements Digest
	{
		private ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		private Digest baseDigest;
		private boolean oddTime = true;

		public NullPssDigest(Digest mgfDigest)
		{
			this.baseDigest = mgfDigest;
		}

		@Override
		public String getAlgorithmName()
		{
			return "NULL";
		}

		@Override
		public int getDigestSize()
		{
			return baseDigest.getDigestSize();
		}

		@Override
		public void update(byte in)
		{
			bOut.write(in);
		}

		@Override
		public void update(byte[] in, int inOff, int len)
		{
			bOut.write(in, inOff, len);
		}

		@Override
		public int doFinal(byte[] out, int outOff)
		{
			byte[] res = bOut.toByteArray();

			if (oddTime)
			{
				System.arraycopy(res, 0, out, outOff, res.length);
			}
			else
			{
				baseDigest.update(res, 0, res.length);

				baseDigest.doFinal(out, outOff);
			}

			reset();

			oddTime = !oddTime;

			return res.length;
		}

		@Override
		public void reset()
		{
			bOut.reset();
			baseDigest.reset();
		}

		public int getByteLength()
		{
			return 0;
		}
	}
}
