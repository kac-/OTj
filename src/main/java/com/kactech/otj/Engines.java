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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.kactech.otj.model.BasicSigningSupport;
import com.kactech.otj.model.SigningSupport;
import com.kactech.otj.model.XmlEntity;
import com.kactech.otj.model.annot.GsonExclude;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriterNewlineAttribute;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class Engines {

	public static void render(SigningSupport sign, PrivateKey key) {
		String str = xstream.toXML(sign);
		if (false)// not-needed
			if (sign instanceof MSG.Message)
				str = "<?xml version=\"1.0\"?>\n" + str;
		str += '\n';//TODO newline signature problem
		sign.setUnsigned(str);
		try {
			Utils.sign(sign, key);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@SuppressWarnings("unchecked")
	public static void parse(SigningSupport sign) {
		sign.parseFromSigned();
		String unsigned = sign.getUnsigned();
		unsigned = cleanXML(unsigned);
		xstream.fromXML(unsigned, sign);
	}

	public static SigningSupport parse(String signed) {
		BasicSigningSupport bss = new BasicSigningSupport();
		bss.setSigned(signed);
		bss.parseFromSigned();
		String unsigned = cleanXML(bss.getUnsigned());
		SigningSupport ss = (SigningSupport) xstream.fromXML(unsigned);
		ss.copyFrom(bss);
		return ss;
	}

	static String cleanXML(String unsigned) {
		return unsigned.replace("<@", "<re_").replace("</@", "</re_")
				.replace("<?xml version=\"2.0\"", "<?xml version=\"1.0\"");
	}

	public static final XStream xstream;
	static {
		HierarchicalStreamDriver driver = new XppDriver() {
			@Override
			public HierarchicalStreamWriter createWriter(OutputStream out) {
				return createWriter(new OutputStreamWriter(out));
			}

			@Override
			public HierarchicalStreamWriter createWriter(Writer out) {
				return new PrettyPrintWriterNewlineAttribute(out, new char[] {});//empty lineIndentier
			}
		};
		xstream = new XStream(driver);
		//xstream.aliasAttribute(OT.Item.class, "accountID", "fromAccountID");

		//xstream.processAnnotations(OT.TransactionType.class);

		if (false)
			xstream.processAnnotations(new Class[] { OT.Ledger.class, OT.Transaction.class, OT.Item.class,
					OT.User.class, OT.BoxRecord.class,

			});
		else
			xstream.processAnnotations(OT.class.getDeclaredClasses());

		xstream.processAnnotations(MSG.class.getDeclaredClasses());
		//xstream.alias("nymboxRecord", OT.Transaction.class);
		final Converter superConverter = xstream.getConverterLookup().lookupConverterForType(OT.Transaction.class);

		Converter contractConverter = new Converter() {

			@Override
			public boolean canConvert(Class type) {
				boolean can = XmlEntity.class.isAssignableFrom(type) && SigningSupport.class.isAssignableFrom(type);
				//System.out.println("CAN A " + type + " " + can);
				return can;
			}

			@Override
			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
				if (true) {
					//if (SigningSupport.class.isAssignableFrom(context.getRequiredType())) {
					if (context.get("raw") != null) {
						//System.out.println("AA " + context.getRequiredType() + " " + reader.getNodeName());
						String raw = reader.getValue();
						String una = AsciiA.getString(raw.trim());
						try {
							if (reader.getNodeName().equals("inReferenceTo"))
								return Engines.parse(una);
							Object inst = context.getRequiredType().newInstance();
							((SigningSupport) inst).setSigned(una);
							Engines.parse((SigningSupport) inst);
							return inst;
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					} else {
						//System.out.println("A " + context.getRequiredType() + " " + reader.getNodeName());
						context.put("raw", true);
						return superConverter.unmarshal(reader, context);
					}
				} else {
					System.err.println("ENT " + context.getRequiredType());
					String raw = reader.getValue();
					String una = AsciiA.getString(raw.trim());
					return xstream.fromXML(una);
				}
			}

			@Override
			public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
				//System.out.println("marsh " + source.getClass());

				if (context.get("raw") != null) {
					writer.setValue('\n' + AsciiA.setString(((SigningSupport) source).getSigned()));
				} else {
					context.put("raw", true);
					superConverter.marshal(source, writer, context);
				}
			}
		};
		Converter entityConverter = new Converter() {

			@Override
			public boolean canConvert(Class type) {

				boolean can = (MSG.AsciiEntity.class.isAssignableFrom(type));
				//System.out.println("CAN B " + type + " " + can);
				return can;
			}

			@Override
			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
				String raw = reader.getValue();
				String una = AsciiA.getString(raw.trim());
				una = cleanXML(una);
				//System.out.println(una);
				Object o = xstream.fromXML(una);
				MSG.AsciiEntity ae = new MSG.AsciiEntity();
				ae.entity = o;
				return ae;
			}

			@Override
			public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
				writer.setValue('\n' + AsciiA.setString(xstream.toXML(((MSG.AsciiEntity) source).entity)));
			}
		};
		xstream.registerConverter(contractConverter, XStream.PRIORITY_VERY_HIGH);
		xstream.registerConverter(entityConverter, XStream.PRIORITY_LOW);
		xstream.registerConverter(OT.StringMap.converter, XStream.PRIORITY_NORMAL);
		xstream.registerConverter(OT.ArmoredString.converter, XStream.PRIORITY_NORMAL);
		xstream.registerConverter(OT.NumList.converter, XStream.PRIORITY_NORMAL);
		xstream.registerConverter(OT.NumListAttribute.converter, XStream.PRIORITY_NORMAL);
		xstream.registerConverter(OT.Version.converter, XStream.PRIORITY_NORMAL);
		xstream.registerConverter(OT.ArmoredData.converter, XStream.PRIORITY_NORMAL);
		xstream.registerConverter(OT.PublicInfo.converter, XStream.PRIORITY_NORMAL);
		xstream.registerConverter(OT.CredentialMap.converter, XStream.PRIORITY_NORMAL);
		xstream.registerConverter(OT.NamedText.converter, XStream.PRIORITY_NORMAL);
	}

	// GSON

	public static final Gson gson;
	static {
		GsonBuilder builder = new GsonBuilder();
		builder.setExclusionStrategies(new ExclusionStrategy() {

			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				return f.getAnnotation(GsonExclude.class) != null;
			}

			@Override
			public boolean shouldSkipClass(Class<?> clazz) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		// make compact NumList even in prettyPrinting
		builder.registerTypeAdapter(OT.NumList.class, new TypeAdapter<OT.NumList>() {
			@Override
			public OT.NumList read(JsonReader in) throws IOException {
				if (in.peek() == JsonToken.NULL) {
					in.nextNull();
					return null;
				}

				OT.NumList list = new OT.NumList();
				in.beginArray();
				while (in.hasNext())
					list.add(in.nextLong());
				in.endArray();

				return list;
			}

			@Override
			public void write(JsonWriter out, OT.NumList value) throws IOException {
				if (value == null) {
					out.nullValue();
					return;
				}
				out.beginArray();
				out.setIndent("");
				for (Long l : value)
					out.value(l);
				out.endArray();
				out.setIndent("  ");
			}
		});
		// make compact NumList even in prettyPrinting
		builder.registerTypeAdapter(OT.NumListAttribute.class, new TypeAdapter<OT.NumListAttribute>() {
			@Override
			public OT.NumListAttribute read(JsonReader in) throws IOException {
				if (in.peek() == JsonToken.NULL) {
					in.nextNull();
					return null;
				}

				OT.NumListAttribute list = new OT.NumListAttribute();
				in.beginArray();
				while (in.hasNext())
					list.add(in.nextLong());
				in.endArray();

				return list;
			}

			@Override
			public void write(JsonWriter out, OT.NumListAttribute value) throws IOException {
				if (value == null) {
					out.nullValue();
					return;
				}
				out.beginArray();
				out.setIndent("");
				for (Long l : value)
					out.value(l);
				out.endArray();
				out.setIndent("  ");
			}
		});

		builder.registerTypeAdapter(OT.ArmoredString.class, new TypeAdapter<OT.ArmoredString>() {
			@Override
			public OT.ArmoredString read(JsonReader in) throws IOException {
				throw new UnsupportedOperationException();
			}

			@Override
			public void write(JsonWriter out, OT.ArmoredString value) throws IOException {
				if (value == null)
					out.nullValue();
				else if (value.getUnarmored() != null)
					out.value(value.getUnarmored());
				else {
					out.beginObject();
					out.name("armored");
					out.value(value.getRaw());
					out.endObject();
				}
			}
		});
		// print PublicInfo only as its value
		builder.registerTypeAdapter(OT.PublicInfo.class, new TypeAdapter<OT.PublicInfo>() {
			@Override
			public OT.PublicInfo read(JsonReader in) throws IOException {
				throw new UnsupportedOperationException();
			}

			@Override
			public void write(JsonWriter out, OT.PublicInfo value) throws IOException {
				out.value(value.getValue());
			}
		});

		// print Identifier only as its ID
		builder.registerTypeAdapter(OT.Identifier.class, new TypeAdapter<OT.Identifier>() {
			@Override
			public OT.Identifier read(JsonReader in) throws IOException {
				throw new UnsupportedOperationException();
			}

			@Override
			public void write(JsonWriter out, OT.Identifier value) throws IOException {
				out.value(value.getID());
			}
		});

		// print contents as simple map TODO remove Identifier type adapter
		builder.registerTypeAdapter(OT.PublicContents.class, new TypeAdapter<OT.PublicContents>() {
			@Override
			public OT.PublicContents read(JsonReader in) throws IOException {
				throw new UnsupportedOperationException();
			}

			@Override
			public void write(JsonWriter out, OT.PublicContents value) throws IOException {
				if (value == null || value.getPublicInfos() == null)
					out.nullValue();
				else {
					out.beginObject();
					for (java.util.Map.Entry<String, OT.PublicInfo> e : value.getPublicInfos().entrySet()) {
						out.name(e.getKey());
						out.value(e.getValue().getValue());
					}
					out.endObject();
				}
			};
		});
		// ah
		builder.registerTypeAdapter(OT.Version.class, new TypeAdapter<OT.Version>() {
			@Override
			public OT.Version read(JsonReader in) throws IOException {
				throw new UnsupportedOperationException();
			}

			@Override
			public void write(JsonWriter out, OT.Version value) throws IOException {
				if (value == null || value.getValue() == null)
					out.nullValue();
				else
					out.value(value.getValue());
			}
		});
		// keypairs!!! 
		builder.registerTypeAdapter(KeyPair.class, new TypeAdapter<KeyPair>() {
			@Override
			public KeyPair read(JsonReader in) throws IOException {
				in.beginObject();
				BigInteger mod = null, privExp = null, pubExp = null;
				for (int i = 0; i < 3; i++) {
					String name = in.nextName();
					if ("modulus".equals(name))
						mod = new BigInteger(in.nextString());
					else if ("privateExponent".equals(name))
						privExp = new BigInteger(in.nextString());
					else if ("publicExponent".equals(name))
						pubExp = new BigInteger(in.nextString());
				}
				in.endObject();
				try {
					KeyFactory kf = KeyFactory.getInstance("RSA");
					return new KeyPair(kf.generatePublic(new RSAPublicKeySpec(mod, pubExp))
							, kf.generatePrivate(new RSAPrivateKeySpec(mod, privExp)));
				} catch (InvalidKeySpecException e) {
					throw new RuntimeException(e);
				} catch (NoSuchAlgorithmException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void write(JsonWriter out, KeyPair value) throws IOException {
				out.beginObject();
				out.name("modulus");
				out.value(((RSAPrivateKey) value.getPrivate()).getModulus());
				out.name("privateExponent");
				out.value(((RSAPrivateKey) value.getPrivate()).getPrivateExponent());
				out.name("publicExponent");
				out.value(((RSAPublicKey) value.getPublic()).getPublicExponent());
				out.endObject();
			}
		});
		builder.registerTypeAdapter(PublicKey.class, new TypeAdapter<PublicKey>() {
			@Override
			public PublicKey read(JsonReader in) throws IOException {
				in.beginObject();
				BigInteger mod = null, pubExp = null;
				for (int i = 0; i < 2; i++) {
					String name = in.nextName();
					if ("modulus".equals(name))
						mod = new BigInteger(in.nextString());
					else if ("publicExponent".equals(name))
						pubExp = new BigInteger(in.nextString());
				}
				in.endObject();
				try {
					KeyFactory kf = KeyFactory.getInstance("RSA");
					return kf.generatePublic(new RSAPublicKeySpec(mod, pubExp));
				} catch (InvalidKeySpecException e) {
					throw new RuntimeException(e);
				} catch (NoSuchAlgorithmException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void write(JsonWriter out, PublicKey value) throws IOException {
				if (value == null)
					out.nullValue();
				else {
					out.beginObject();
					out.name("modulus");
					out.value(((RSAPublicKey) value).getModulus());
					out.name("publicExponent");
					out.value(((RSAPublicKey) value).getPublicExponent());
					out.endObject();
				}
			}
		});

		builder.setPrettyPrinting();

		gson = builder.create();
	}

}
